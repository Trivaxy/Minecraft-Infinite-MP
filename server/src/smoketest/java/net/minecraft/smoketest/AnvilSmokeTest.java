package net.minecraft.smoketest;

import com.mojang.nbt.tag.special.CompoundTag;
import java.nio.charset.StandardCharsets;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.container.ContainerOpenPacket;
import net.minecraft.network.packet.container.CreativeSlotPacket;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;

/**
 * The anvil, end to end on the wire. Needs "anvilbot" in run/server/ops.txt.
 *
 * Three separate faults met here, all of them invisible from the client alone:
 *
 *   1. Renaming did nothing. The screen sends the field on "MC|ItemName" and
 *      NetServerHandler.handleCustomPayload knew only "MC|BEdit" and "MC|BSign",
 *      so the server's container never heard the name. Two more layers behind
 *      that would have swallowed it anyway: IOUtil.writeItemStack never wrote
 *      customName, and isItemStackEqual ignored it, so updateCraftResults saw a
 *      renamed stack as unchanged and sent nothing.
 *   2. One input was enough to produce an output. The rename branch compared the
 *      typed name against translateKey(getItemName(..)), which answers with the
 *      raw key ("item.pickaxeDiamond") because only translateNamedKey appends
 *      ".name" - so the untouched field the client echoes back never matched,
 *      every item looked renamed, and the output slot filled with an identical
 *      copy that cost score to take.
 *   3. Anvil.dropID returned 0, so breaking one dropped nothing.
 *
 * Phase 1 is the control and the precondition for phase 2: an input with no
 * second item and no name typed must leave the output empty. Against the
 * pre-fix build phase 1 goes red (a copy is offered) and phase 2 goes red too
 * (the copy that arrives carries no name).
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.AnvilSmokeTest
 */
public class AnvilSmokeTest {

    private static final int ANVIL = 175;
    private static final int PICKAXE = 4118;
    private static final int FLOOR_BLOCK = 1;

    private static final String DEFAULT_NAME = "Diamond Pickaxe";
    private static final String TYPED_NAME = "Bonker";

    // PlayerContainer puts the hotbar at slots 40-48 and AnvilContainer puts it
    // at 30-38, so the same hotbar index is addressed two different ways.
    private static final int FLOOR_HOTBAR = 0, PICK_HOTBAR = 3, ANVIL_HOTBAR = 4, TAG_HOTBAR = 5;
    private static final int PLAYER_HOTBAR_0 = 40, WINDOW_HOTBAR_0 = 30;
    private static final int PICK_WINDOW_SLOT = WINDOW_HOTBAR_0 + PICK_HOTBAR;

    private static final int INPUT_SLOT = 0, OUTPUT_SLOT = 2;

    // First slot of the main inventory, which this run never puts anything in.
    private static final int EMPTY_SLOT = 3;

    private static final Checks checks = new Checks();

    private static short action = 1;

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "anvilbot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);
        if (!bot.land(20000)) {
            checks.abort(username + " never landed, so it has no ground to build on");
        }

        bot.chat("/gamemode creative");
        bot.drain(1500);

        int[] floor = {(int)Math.floor(bot.posX), (int)Math.floor(bot.posY) + 2, (int)Math.floor(bot.posZ)};
        int[] spot = {floor[0], floor[1] + 1, floor[2]};

        bot.send(new ItemSwitchPacket(FLOOR_HOTBAR));
        bot.drain(400);
        clear(bot, spot);
        clear(bot, floor);
        if (!stock(bot, FLOOR_HOTBAR, FLOOR_BLOCK)) {
            checks.abort("the creative slot write never came back - either \"" + username
                    + "\" is not in run/server/ops.txt, so /gamemode did nothing, or it is not being ticked");
        }
        bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
        if (standing(bot, floor) != FLOOR_BLOCK) {
            checks.abort("could not stand a block in the air at " + fmt(floor) + ", so there is no floor to build on");
        }

        if (!stock(bot, PICK_HOTBAR, PICKAXE) || !stock(bot, ANVIL_HOTBAR, ANVIL)) {
            checks.abort("the creative slot writes for the pickaxe and the anvil never came back");
        }

        bot.send(new ItemSwitchPacket(ANVIL_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
        if (standing(bot, spot) != ANVIL) {
            checks.abort("the anvil never landed at " + fmt(spot));
        }

        bot.send(new ItemSwitchPacket(PICK_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(spot[0], spot[1], spot[2], 1, null));
        ContainerOpenPacket open = bot.await(ContainerOpenPacket.class,
                p -> p.inventoryType == ContainerOpenPacket.ANVIL, 5000);
        if (open == null) {
            checks.abort("right-clicking the anvil opened no window");
        }
        int window = open.windowId;
        bot.drain(500);

        // Shift-click puts the held pickaxe in the first free input slot.
        ContainerContentsPacket loaded = click(bot, window, PICK_WINDOW_SLOT, true, null);
        if (loaded == null || loaded.itemStack.length <= OUTPUT_SLOT) {
            checks.abort("the shift-click into the anvil produced no container refresh to read");
        }
        if (!checks.check("the pickaxe reaches the anvil's input slot",
                loaded.itemStack[INPUT_SLOT] != null && loaded.itemStack[INPUT_SLOT].itemID == PICKAXE,
                "input slot holds " + describe(loaded.itemStack[INPUT_SLOT]) + " - nothing below this can be trusted")) {
            finish(bot, window, spot, floor);
        }

        // ---------------- phase 1: one input and no name typed is not a job
        checks.check("an anvil with only an input offers no output",
                loaded.itemStack[OUTPUT_SLOT] == null,
                "the output slot holds " + describe(loaded.itemStack[OUTPUT_SLOT])
                        + ", which costs score to take and changes nothing about the item");

        // The screen echoes the item's own name into the field whenever the
        // input changes, so the server has to read that as "no rename" too.
        checks.check("typing the item's own name back is not a job either",
                rename(bot, window, DEFAULT_NAME) == null,
                "the output slot filled for a name identical to the one the item already displays");

        // ---------------- phase 2: the rename itself
        ItemStack renamed = rename(bot, window, TYPED_NAME);
        if (checks.check("a typed name fills the output slot", renamed != null,
                "nothing arrived for slot " + OUTPUT_SLOT + " - handleCustomPayload ignores \"MC|ItemName\", or the "
                        + "renamed copy compared equal to the old one and was never sent")) {
            checks.check("the renamed item keeps its identity", renamed.itemID == PICKAXE,
                    "the output is " + describe(renamed) + " rather than the pickaxe that went in");
            checks.check("the name survives the wire", TYPED_NAME.equals(renamed.customName),
                    "the output arrived named \"" + renamed.customName + "\" - writeItemStack drops customName");
        }

        // ---------------- phase 3: the shared change the rename needed
        // Making customName count towards ItemStack equality meant making the
        // tags count too, and comparing tags needs structural equals on the NBT
        // classes - the library had none, so every CompoundTag was equal only to
        // itself. updateCraftResults compares each slot against its own copy, so
        // an item carrying a tag would have looked different from itself on every
        // tick and resent its slot twenty times a second, forever.
        if (stockTagged(bot, TAG_HOTBAR)) {
            int churn = bot.awaitAll(SlotPacket.class, p -> p.windowId == window, 3000).size();
            checks.check("a tagged item sitting in an open window is quiet",
                    churn <= 2,
                    churn + " slot updates in 3s with nothing happening - a tagged stack does not compare equal to "
                            + "its own copy, so the server resends it every tick");
        } else {
            checks.note("could not place a tagged item, so the idle-churn check did not run");
        }

        finish(bot, window, spot, floor);
    }

    /**
     * Phase 4, and the end of the run: survival, because creative digs drop
     * nothing, and with the pickaxe held because a bare hand harvests no metal.
     */
    private static void finish(Bot bot, int window, int[] spot, int[] floor) throws Exception {
        bot.send(new ContainerClosePacket(window));
        bot.drain(800);
        bot.chat("/gamemode survival");
        bot.drain(1500);
        bot.send(new ItemSwitchPacket(PICK_HOTBAR));
        bot.drain(400);

        bot.send(new DigPacket(0, spot[0], spot[1], spot[2], 1));
        bot.drain(1500);
        bot.send(new DigPacket(2, spot[0], spot[1], spot[2], 1));
        ItemPacket dropped = bot.await(ItemPacket.class,
                p -> p.itemStack != null && p.itemStack.itemID == ANVIL, 20000);
        if (!checks.check("breaking an anvil drops one", dropped != null,
                "no item entity carrying block " + ANVIL + " spawned within 20s of the dig - Anvil.dropID returns 0")) {
            bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
            checks.note("after the dig, " + fmt(spot) + " holds block " + standing(bot, spot)
                    + " (0 means the anvil did break and simply dropped nothing)");
        }

        bot.chat("/gamemode creative");
        bot.drain(1000);
        clear(bot, spot);
        clear(bot, floor);

        if (bot.kickReason != null) {
            checks.abort("kicked mid-run: " + bot.kickReason);
        }
        checks.done();
    }

    /**
     * Sends what the screen sends on every keystroke in the name field, and
     * answers with the output slot the server ends up holding.
     *
     * Reading it back needs the forced refresh rather than the SlotPacket the
     * rename itself triggers: "no output" and "no packet" look identical on the
     * wire, so a test that only listened would pass phase 1 whether the server
     * had understood the name or never received it.
     */
    private static ItemStack rename(Bot bot, int window, String name) throws Exception {
        bot.send(new CustomPayloadPacket("MC|ItemName", name.getBytes(StandardCharsets.UTF_8)));
        bot.drain(1000);
        ContainerContentsPacket state = refresh(bot, window);
        if (state == null) {
            checks.abort("the window stopped answering clicks after \"" + name + "\", so its output cannot be read");
        }
        return state.itemStack[OUTPUT_SLOT];
    }

    /**
     * The whole window, on demand. handleWindowClick answers a click whose
     * claimed result disagrees with the server's by resending every slot, so a
     * click that does nothing - an empty inventory slot, with nothing on the
     * cursor - reports a stack the server will always disagree with and costs
     * only the resend.
     */
    private static ContainerContentsPacket refresh(Bot bot, int window) throws Exception {
        return click(bot, window, EMPTY_SLOT, false, new ItemStack(FLOOR_BLOCK, 1));
    }

    /**
     * One window click, answered with the whole window.
     *
     * The claimed result is deliberately wrong, because handleWindowClick only
     * resends every slot when the client's claim disagrees with what the server
     * did - which is also the only way to read a container off this server.
     *
     * The acknowledgement is not optional. A disagreement puts the player in
     * Container.players, and canUseMP is `!players.contains(player)`, so the
     * server drops every later click on that window in silence until the client
     * answers the transaction. A bot that skips it gets exactly one click.
     */
    private static ContainerContentsPacket click(Bot bot, int window, int slot, boolean shift, ItemStack claim)
            throws Exception {
        bot.send(new ClickPacket(window, slot, 0, shift, claim, action++));
        ContainerContentsPacket state = null;
        TransactionPacket pending = null;
        long deadline = System.currentTimeMillis() + 4000;
        while (System.currentTimeMillis() < deadline) {
            Packet next = bot.read();
            if (next instanceof ContainerContentsPacket && ((ContainerContentsPacket)next).windowId == window) {
                state = (ContainerContentsPacket)next;
            } else if (next instanceof TransactionPacket && ((TransactionPacket)next).windowId == window) {
                pending = (TransactionPacket)next;
            }
            if (state != null && pending != null) {
                break;
            }
        }
        if (pending != null && !pending.transactionOngoing) {
            bot.send(new TransactionPacket(window, pending.transactionId, false));
            bot.drain(400);
        }
        return state;
    }

    /** A stack carrying NBT, which is what the churn check needs. */
    private static boolean stockTagged(Bot bot, int hotbarIndex) throws Exception {
        ItemStack tagged = new ItemStack(PICKAXE, 1);
        CompoundTag tag = new CompoundTag();
        tag.setString("SmokeTest", "tagged");
        tagged.setTag(tag);

        int slot = PLAYER_HOTBAR_0 + hotbarIndex;
        bot.drain(400);
        bot.send(new CreativeSlotPacket(slot, null));
        bot.drain(600);
        bot.send(new CreativeSlotPacket(slot, tagged));
        return bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.itemSlot == slot && p.myItemStack != null && p.myItemStack.hasTag(),
                4000, 700) != null;
    }

    /**
     * Empties the slot first. The server only answers a creative write that
     * changed something - updateCraftResults compares against what it already
     * sent - and this bot's inventory survives from the last run, so writing the
     * same pickaxe into the same slot twice would look like a dropped packet.
     */
    private static boolean stock(Bot bot, int hotbarIndex, int id) throws Exception {
        int slot = PLAYER_HOTBAR_0 + hotbarIndex;
        bot.drain(400);
        bot.send(new CreativeSlotPacket(slot, null));
        bot.drain(600);
        bot.send(new CreativeSlotPacket(slot, new ItemStack(id, 1)));
        return bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.itemSlot == slot && p.myItemStack != null && p.myItemStack.itemID == id,
                4000, 700) != null;
    }

    /** handlePlace answers every click with the state of the blocks it touched. */
    private static int standing(Bot bot, int[] at) throws Exception {
        UpdateBlockPacket landed = bot.awaitLast(UpdateBlockPacket.class,
                p -> p.xPosition == at[0] && p.yPosition == at[1] && p.zPosition == at[2], 4000, 700);
        return landed == null ? -1 : landed.type;
    }

    private static void clear(Bot bot, int[] spot) throws Exception {
        bot.drain(300);
        bot.send(new DigPacket(0, spot[0], spot[1], spot[2], 1));
        bot.drain(800);
    }

    private static String describe(ItemStack stack) {
        return stack == null ? "nothing" : stack.itemID + "x" + stack.stackSize
                + (stack.customName.isEmpty() ? "" : " named \"" + stack.customName + "\"");
    }

    private static String fmt(int[] at) {
        return at[0] + "," + at[1] + "," + at[2];
    }
}
