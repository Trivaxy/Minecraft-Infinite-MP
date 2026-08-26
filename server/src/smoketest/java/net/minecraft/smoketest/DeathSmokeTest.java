package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.world.RespawnPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateSectionPacket;

/**
 * B-26: EntityPlayerMP.onDeath replaced Player.onDeath instead of extending it,
 * so on a server a death was three lines - drop everything, halve the score,
 * broadcast the message. No grave was ever placed though PlayerProperties.grave
 * defaults to true, and keepInventory was ignored. B-26b sits behind it:
 * ServerConfigurationManager.recreatePlayerEntity builds the replacement entity
 * and never calls Player.clone, so nothing survives the respawn either.
 *
 *   phase 1 (graves on)  - a grave block must take the block under the player
 *                          and no new item entity may appear.
 *   phase 2 (keepInv on) - nothing drops, no grave, and the stack is still in
 *                          the container after the respawn.
 *
 * Against the pre-fix build four checks go red: no grave block on the wire, the
 * stack on the floor instead, the same drop again with keepinventory on, and an
 * empty container after the respawn.
 *
 * Needs a running server with gravebot in run/server/ops.txt. A server-initiated
 * teleport clears hasMoved, and NetServerHandler.handlePlayerMovement does
 * everything - movement, chunk streaming, the health sync - inside one
 * `if (hasMoved)`, so every teleport here is followed by Bot.followTeleport;
 * without it the server stops listening and the checks measure nothing.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.DeathSmokeTest
 */
public class DeathSmokeTest {

    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int GRAVE = 282;  // BlockList.playerGrave
    private static final int STONE = 1;
    // One stack each, so a wiped inventory drops five separate item entities.
    // The count below is world-wide and a mob dying anywhere in dimension 0
    // moves it by one, which five cannot be confused with.
    private static final int[] STOCK = {3, 4, 5, 12, 13};
    private static final int DIRT = 3;
    private static final String ITEM_ENTITY = "Item";
    private static final int HOTBAR_CLICK_OFFSET = 2;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        // settle() sinks and then holds, which is the position the server has
        // accepted - hasMoved stays armed and the spawn chunks keep streaming.
        Bot bot = Bot.join(args, "gravebot");
        bot.settle(3000);
        if (bot.kickReason != null) {
            checks.abort("gravebot was kicked before the test began: " + bot.kickReason);
        }
        reviveIfDead(bot);
        standOnStone(bot, LAND_X, LAND_Y, LAND_Z);

        // ---------------- phase 1: graves on, keepInventory off
        bot.chat("/gamerule graves on");
        bot.chat("/gamerule keepinventory off");
        bot.drain(500);
        stock(bot);

        int itemsBefore = countItems(bot);
        Death buried = die(bot, 6000);
        int itemsAfter = countItems(bot);
        checks.check("the server processed the death, and still broadcasts it",
                buried.message != null && buried.message.contains("gravebot"),
                buried.message == null ? "no ChatPacket in 6s - the death never happened, or gravebot is not in "
                        + "run/server/ops.txt" : "the broadcast was \"" + buried.message + "\"");
        checks.check("a grave block is placed on death", buried.grave != null,
                "no block change to id " + GRAVE + " - Player.onDeath's grave branch never ran");
        if (buried.grave != null) {
            checks.check("the grave takes the block the player was standing on",
                    buried.grave[0] == LAND_X && buried.grave[1] == LAND_Y - 1 && buried.grave[2] == LAND_Z,
                    "grave at " + describe(buried.grave) + " but the player died standing at "
                            + LAND_X + "," + LAND_Y + "," + LAND_Z);
        }
        checks.check("the inventory goes into the grave, not onto the floor",
                dropped(itemsBefore, itemsAfter) == 0,
                itemsBefore < 0 || itemsAfter < 0 ? "/count Item did not answer"
                        : "item entities went from " + itemsBefore + " to " + itemsAfter
                                + " - the five stacks were scattered on the floor instead");
        if (bot.kickReason != null) {
            checks.abort("gravebot was kicked during phase 1: " + bot.kickReason);
        }

        respawn(bot);
        // The grave branch copies the stacks in and never clears the inventory
        // it copied from - what empties the player is the respawn handing them
        // a new one. So the clone() added for B-26b must not carry the
        // inventory over unless baby mode asked for it, or a grave death mints
        // a second copy of everything.
        ItemStack[] afterGrave = readContents(bot);
        checks.check("the buried inventory is not also still on the player", stocked(afterGrave) == 0,
                afterGrave == null ? "no ContainerContentsPacket came back to read"
                        : "the respawned player still holds " + stocked(afterGrave) + " of the "
                                + STOCK.length + " buried stacks, and the grave has a copy of them");

        // ---------------- phase 2: keepInventory on
        bot.chat("/gamerule keepinventory on");
        bot.drain(500);
        stock(bot);

        int keptBefore = countItems(bot);
        Death kept = die(bot, 6000);
        int keptAfter = countItems(bot);
        checks.check("keepInventory: nothing is dropped on death",
                dropped(keptBefore, keptAfter) == 0,
                keptBefore < 0 || keptAfter < 0 ? "/count Item did not answer"
                        : "item entities went from " + keptBefore + " to " + keptAfter
                                + " - baby mode was ignored and the inventory was cleared");
        checks.check("keepInventory: no grave is placed", kept.grave == null,
                "a grave went down at " + describe(kept.grave)
                        + " - baby mode must suppress the grave, not fill one");

        respawn(bot);
        ItemStack[] after = readContents(bot);
        checks.check("keepInventory: the container survives the respawn", stocked(after) == STOCK.length,
                after == null ? "no ContainerContentsPacket came back to read"
                        : "the respawned player holds " + stocked(after) + " of the " + STOCK.length
                                + " stacks - recreatePlayerEntity never copies the old player");

        bot.chat("/gamerule keepinventory off");
        bot.chat("/clearinv");
        bot.drain(500);
        bot.close();
        checks.done();
    }

    // ---------------- protocol helpers ----------------

    /**
     * A bot that was saved mid-death logs back in dead - health rides in the
     * player .dat - and the server then refuses the damage that would kill it
     * again, so every phase below would measure nothing. handleRespawn only
     * recreates the entity when health <= 0, which makes this a no-op for a
     * live player: no teleport comes back and there is nothing to follow.
     */
    private static void reviveIfDead(Bot bot) throws Exception {
        bot.send(new RespawnPacket((byte) 0));
        if (bot.followTeleport(2500)) {
            checks.note("gravebot logged in dead and was respawned before the test");
        }
    }

    /**
     * Both halves of this matter. The bot has to be somewhere the server keeps
     * listening to it: a player holding a position in mid-air is kicked as
     * flying after 80 packets, four seconds in, which turns every later
     * assertion silently green - so it stands on a block placed under its feet
     * at an integer Y, which is real support and also a known block for the
     * grave to replace. And it has to be somewhere discrete block updates
     * survive: around the spawn the chunk churn is heavy enough that changes
     * are swept into whole-chunk resends, where nothing here can see them. The
     * setblock coming back is the proof that both hold before anything is
     * measured.
     */
    private static void standOnStone(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/teleport " + x + " " + y + " " + z);
        if (!bot.followTeleport(5000)) {
            checks.abort("no teleport came back for /teleport " + x + " " + y + " " + z);
        }
        bot.drain(5000);

        // Something else first, so the stone below is always a real change: a
        // setblock that changes nothing sends nothing, and the last run of this
        // test left its own stone here. It cannot be air - BlockList.blocks[0]
        // is null, so id 0 fails checkBlockId and the command throws.
        bot.chat("/setblock " + x + " " + (y - 1) + " " + z + " " + DIRT);
        bot.drain(1500);

        bot.chat("/setblock " + x + " " + (y - 1) + " " + z + " " + STONE);
        boolean placed = false;
        long deadline = System.currentTimeMillis() + 6000;
        while (!placed && System.currentTimeMillis() < deadline && bot.kickReason == null) {
            int[] stone = blockChange(bot.read(), STONE);
            placed = stone != null && stone[0] == x && stone[1] == y - 1 && stone[2] == z;
        }
        if (!placed) {
            checks.abort("the setblock under gravebot never came back - without a block update this test "
                    + "cannot tell a missing grave from an unwatched chunk");
        }
    }

    private static void stock(Bot bot) throws Exception {
        bot.chat("/clearinv");
        bot.drain(500);
        for (int id : STOCK) {
            bot.chat("/give " + id + " 1");
            bot.drain(300);
        }
        bot.drain(700);
    }

    /**
     * One /kill and everything the wire says about it: the grave block if one
     * goes down, the dropped stacks if the inventory hits the floor instead,
     * and the broadcast message. They arrive interleaved in the same window, so
     * this reads the window once rather than awaiting each in turn.
     */
    private static Death die(Bot bot, long windowMs) throws Exception {
        Death death = new Death();
        bot.chat("/kill");
        long deadline = System.currentTimeMillis() + windowMs;
        while (System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet next = bot.read();
            int[] grave = blockChange(next, GRAVE);
            if (grave != null) {
                death.grave = grave;
            } else if (next instanceof ChatPacket) {
                death.message = ((ChatPacket) next).message;
            }
        }
        return death;
    }

    /**
     * The coordinates of a change to `type` carried by this packet, or null.
     * One block changing in a chunk in a tick is an UpdateBlockPacket and two
     * or more is an UpdateSectionPacket, and which one a grave arrives in
     * depends on whatever else moved that tick - water, in an ocean. Section
     * coordinates are packed x << 20 | z << 16 | y, chunk-relative in x and z.
     */
    private static int[] blockChange(Packet packet, int type) {
        if (packet instanceof UpdateBlockPacket) {
            UpdateBlockPacket single = (UpdateBlockPacket) packet;
            return single.type == type
                    ? new int[]{single.xPosition, single.yPosition, single.zPosition} : null;
        }
        if (packet instanceof UpdateSectionPacket) {
            UpdateSectionPacket many = (UpdateSectionPacket) packet;
            for (int i = 0; i < many.sizeOfAllArrays; i++) {
                if (many.typeArray[i] == type) {
                    int packed = many.coordinateArray[i];
                    return new int[]{
                        many.xPosition * 16 + (packed >> 20 & 15),
                        packed & 0xFFFF,
                        many.zPosition * 16 + (packed >> 16 & 15)
                    };
                }
            }
        }
        return null;
    }

    /**
     * How many DroppedItems the world holds, off /count's own reply ("Counted
     * N of Item."). Counting ItemPackets instead does not work: the tracker
     * sends a spawn packet for every item already lying around whenever the
     * player comes into range of it or is recreated by a respawn, so previous
     * runs of this test show up as this one's drops.
     */
    private static int countItems(Bot bot) throws Exception {
        bot.chat("/count " + ITEM_ENTITY);
        ChatPacket reply = bot.await(ChatPacket.class,
                p -> p.message != null && p.message.contains(" of " + ITEM_ENTITY), 4000);
        if (reply == null) {
            return -1;
        }
        String digits = reply.message.replaceAll("[^0-9]", " ").trim();
        return digits.isEmpty() ? -1 : Integer.parseInt(digits.split("\\s+")[0]);
    }

    private static void respawn(Bot bot) throws Exception {
        bot.send(new RespawnPacket((byte) 0));
        bot.drain(3000);
    }

    /**
     * The server answers a hotbar swap with a full contents refresh whether or
     * not the swap took (B-20), so an out-of-range hotbar index is a read of the
     * server's own inventory that changes nothing. Nothing else asks for one.
     */
    private static ItemStack[] readContents(Bot bot) throws Exception {
        ClickPacket probe = new ClickPacket();
        probe.window_Id = 0;
        probe.inventorySlot = 40;
        probe.mouseClick = 100 + HOTBAR_CLICK_OFFSET;
        probe.action = 1;
        probe.isActive = true;
        probe.itemStack = null;
        bot.send(probe);

        ContainerContentsPacket contents = bot.awaitLast(ContainerContentsPacket.class,
                p -> p.windowId == 0, 4000, 500);
        return contents == null ? null : contents.itemStack;
    }

    /**
     * Whether the death put the inventory on the floor. A wipe is five item
     * entities at once; anything smaller is a mob dying somewhere else in the
     * dimension while the window was open, which /count cannot tell apart and
     * does not need to.
     */
    private static int dropped(int before, int after) {
        if (before < 0 || after < 0) {
            return -1;
        }
        return after - before >= STOCK.length ? after - before : 0;
    }

    /** How many of the stocked ids the container still holds. */
    private static int stocked(ItemStack[] contents) {
        if (contents == null) {
            return -1;
        }
        int found = 0;
        for (int id : STOCK) {
            for (ItemStack stack : contents) {
                if (stack != null && stack.itemID == id) {
                    found++;
                    break;
                }
            }
        }
        return found;
    }

    private static String describe(int[] position) {
        return position == null ? "nowhere" : position[0] + "," + position[1] + "," + position[2];
    }

    private static final class Death {
        int[] grave;
        String message;
    }
}
