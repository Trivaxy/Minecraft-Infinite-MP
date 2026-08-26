package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.container.ContainerOpenPacket;
import net.minecraft.network.packet.container.CreativeSlotPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;

/**
 * Every block with a custom GUI, right-clicked on the wire. Needs "guibot" in
 * run/server/ops.txt.
 *
 * EntityPlayerMP overrode 6 of the 21 displayGUI* methods and Player's other 15
 * were empty bodies, so ten blocks reached the server, called a method that did
 * nothing, and sent no packet at all. Chest is the control: it went through the
 * whole time, so a run where chest passes and the rest fail is this bug and not
 * the rig.
 *
 * The slot-count check is the one that matters. Client and server each build
 * their own container instance and window clicks are resolved by slot index, so
 * two containers that disagree on how many slots they have desync every click -
 * the B-06 failure. The count here is the client container's, counted by hand,
 * and the server's has to match it.
 *
 * Usage: ContainerOpenSmokeTest <host> <port> [username]
 */
public class ContainerOpenSmokeTest {

    private static final int HOTBAR_SLOT = 40;

    private static final Case[] CASES = {
        new Case(54, ContainerOpenPacket.CHEST, 63, "chest"),
        new Case(175, ContainerOpenPacket.ANVIL, 39, "anvil"),
        new Case(284, ContainerOpenPacket.GRINDSTONE, 38, "grindstone"),
        new Case(228, ContainerOpenPacket.STONECUTTER, 55, "carver"),
        new Case(225, ContainerOpenPacket.SMOKER, 58, "smoker"),
        new Case(195, ContainerOpenPacket.HELLFORGE, 38, "hellforge"),
        new Case(231, ContainerOpenPacket.CRAFTER, 46, "crafter"),
        new Case(283, ContainerOpenPacket.BEACON, 38, "beacon"),
        new Case(101, ContainerOpenPacket.VACUUM, 45, "vacuum"),
        new Case(105, ContainerOpenPacket.DISPENSER, 45, "dispenser"),
        new Case(249, ContainerOpenPacket.ENDER_CHEST, 63, "obsidian chest"),
        new Case(61, ContainerOpenPacket.FURNACE, 39, "furnace"),
        new Case(77, ContainerOpenPacket.BLAST_FURNACE, 39, "blast furnace"),
    };

    private static final int FLOOR_BLOCK = 1;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "guibot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);

        if (!bot.land(20000)) {
            checks.abort(username + " never landed, so it has no ground to build on");
        }

        bot.chat("/gamemode creative");
        bot.drain(1500);

        int[] floor = {(int)Math.floor(bot.posX), (int)Math.floor(bot.posY) + 2, (int)Math.floor(bot.posZ)};
        int[] spot = {floor[0], floor[1] + 1, floor[2]};

        bot.send(new ItemSwitchPacket(0));
        bot.drain(400);
        clear(bot, spot);
        clear(bot, floor);
        if (!stock(bot, FLOOR_BLOCK)) {
            checks.abort("the creative slot write never came back - either \"" + username
                    + "\" is not in run/server/ops.txt, so /gamemode did nothing, or it is not being ticked (B-38c)");
        }
        if (!placeInAir(bot, floor, FLOOR_BLOCK)) {
            checks.abort("could not stand a block in the air at " + fmt(floor) + " over " + fmt(bot)
                    + ", so there is no floor to build the test blocks on");
        }
        checks.note("floor at " + fmt(floor) + ", building at " + fmt(spot));

        for (int index = 0; index < CASES.length; index++) {
            Case test = CASES[index];
            if (!stock(bot, test.blockId)) {
                checks.check(test.label + " opens a window", false, "the creative slot write for it never came back");
                continue;
            }
            if (!placeOnFloor(bot, floor, test.blockId)) {
                checks.check(test.label + " opens a window", false, "the block never landed on the test floor");
                continue;
            }

            run(bot, spot, test);
            clear(bot, spot);
        }

        clear(bot, spot);
        clear(bot, floor);
        bot.chat("/gamemode survival");
        bot.drain(1000);

        if (bot.kickReason != null) {
            checks.abort("kicked mid-run: " + bot.kickReason);
        }

        checks.done();
    }

    private static void run(Bot bot, int[] spot, Case test) throws Exception {
        bot.drain(500);
        bot.send(new PlacePacket(spot[0], spot[1], spot[2], 1, null));

        ContainerOpenPacket open = bot.await(ContainerOpenPacket.class, 4000);
        if (!checks.check(test.label + " opens a window", open != null,
                "no ContainerOpenPacket - blockActivated called a displayGUI method the server never implemented")) {
            return;
        }

        checks.check(test.label + " opens the right kind of window", open.inventoryType == test.windowType,
                "expected inventoryType " + test.windowType + " and got " + open.inventoryType);

        ContainerContentsPacket contents = bot.await(ContainerContentsPacket.class,
                p -> p.windowId == open.windowId, 4000);
        if (checks.check(test.label + " sends its contents on open", contents != null,
                "the window opened but no ContainerContentsPacket followed it")) {
            checks.check(test.label + " server container matches the client's slot count",
                    contents.itemStack.length == test.slots,
                    "server built " + contents.itemStack.length + " slots, the client screen builds " + test.slots
                            + " - every click on this window would resolve against a different slot");
        }

        bot.send(new ContainerClosePacket(open.windowId));
        bot.drain(500);
    }

    /**
     * BlockItem.useOnBlock only shifts the target to the neighbouring cell when
     * the block clicked is a real one - clicking air places right where you
     * clicked. That is what gives this test a floor of known height without
     * caring what the terrain under the bot looks like.
     */
    private static boolean placeInAir(Bot bot, int[] at, int blockId) throws Exception {
        bot.drain(400);
        bot.send(new PlacePacket(at[0], at[1], at[2], 1, null));
        return standing(bot, at) == blockId;
    }

    /** Clicks the top of the floor, which puts the block in the cell above it. */
    private static boolean placeOnFloor(Bot bot, int[] floor, int blockId) throws Exception {
        bot.drain(400);
        bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
        int[] above = {floor[0], floor[1] + 1, floor[2]};
        int landed = standing(bot, above);
        if (landed != blockId) {
            checks.note("placing " + blockId + " at " + fmt(above) + " left block " + landed + " standing there");
            return false;
        }
        return true;
    }

    /**
     * handlePlace answers every click with the state of the block clicked and
     * then of the one its face points at, whether or not the place itself was
     * allowed - which is the only way to read a block off this server now that
     * /setblock is gone.
     */
    private static int standing(Bot bot, int[] at) throws Exception {
        UpdateBlockPacket landed = bot.awaitLast(UpdateBlockPacket.class,
                p -> p.xPosition == at[0] && p.yPosition == at[1] && p.zPosition == at[2], 4000, 700);
        return landed == null ? -1 : landed.type;
    }

    /** Creative makes useBlock break on the first dig packet, so one is enough. */
    private static void clear(Bot bot, int[] spot) throws Exception {
        bot.drain(300);
        bot.send(new DigPacket(0, spot[0], spot[1], spot[2], 1));
        bot.drain(800);
    }

    /**
     * A /give lands in the first free slot, so eleven of them run the hotbar out
     * and the twelfth is not held any more. Creative lets the bot write one slot
     * directly and overwrite it for every case instead.
     */
    private static boolean stock(Bot bot, int blockId) throws Exception {
        bot.drain(400);
        bot.send(new CreativeSlotPacket(HOTBAR_SLOT, new ItemStack(blockId, 1)));
        SlotPacket set = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.itemSlot == HOTBAR_SLOT && p.myItemStack != null && p.myItemStack.itemID == blockId,
                4000, 700);
        return set != null;
    }

    private static String fmt(Bot bot) {
        return (int)Math.floor(bot.posX) + "," + (int)Math.floor(bot.posY) + "," + (int)Math.floor(bot.posZ);
    }

    private static String fmt(int[] at) {
        return at[0] + "," + at[1] + "," + at[2];
    }

    private static final class Case {
        final int blockId;
        final int windowType;
        final int slots;
        final String label;

        Case(int blockId, int windowType, int slots, String label) {
            this.blockId = blockId;
            this.windowType = windowType;
            this.slots = slots;
            this.label = label;
        }
    }
}
