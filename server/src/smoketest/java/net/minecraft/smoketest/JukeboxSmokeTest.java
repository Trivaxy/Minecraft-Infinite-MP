package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.container.CreativeSlotPacket;
import net.minecraft.network.packet.misc.MusicPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;

/**
 * The jukebox, end to end on the wire. Needs "jukebot" in run/server/ops.txt.
 *
 * The one fault behind all of it: MusicPacket wrote its fields in one order and
 * read them back in another - stop before the track name going out, after it
 * coming in. The track always decoded as "" and the boolean as whatever the
 * length prefix's high byte was, and the leftover bytes made the next packet id
 * garbage, so every client in the 64-block radius dropped its connection the
 * moment a record went in or came out. Everything else the report described -
 * the disc that would not come out, the jukebox that grew back when broken, the
 * drops that only appeared after a relogin - is a client that stopped being told
 * anything while the server carried on.
 *
 * Against the pre-fix build phase 1 goes red three ways (empty track name, stop
 * set on a start, connection gone) and nothing after it can run.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.JukeboxSmokeTest
 *
 * Persistence across a server restart is the same site in two halves, because
 * only a restart puts the tile entity through NBT:
 *
 *   ./gradlew :server:smokeTest -PsmokeClass=... -PsmokeArgs="jukebot load"
 *   (the load half ends with /stop; start the server again)
 *   ./gradlew :server:smokeTest -PsmokeClass=... -PsmokeArgs="jukebot eject"
 */
public class JukeboxSmokeTest {

    private static final int JUKEBOX = 88;
    private static final int FLOOR_BLOCK = 1;
    private static final int RECORD = 4096 + 103;
    private static final String RECORD_SONG = "Cobble Man";

    private static final int FLOOR_HOTBAR = 0, RECORD_HOTBAR = 1, JUKEBOX_HOTBAR = 2, EMPTY_HOTBAR = 3;
    private static final int PLAYER_HOTBAR_0 = 40;

    private static final String WATCHER = "jukewatch";

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "jukebot";
        String mode = args.length > 3 ? args[3] : "full";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);
        if (!bot.land(20000)) {
            checks.abort(username + " never landed, so it has no ground to build on");
        }

        bot.chat("/gamemode creative");
        bot.drain(1500);

        int[] floor = {(int)Math.floor(bot.posX), (int)Math.floor(bot.posY) + 2, (int)Math.floor(bot.posZ)};
        int[] spot = {floor[0], floor[1] + 1, floor[2]};
        checks.note("building at " + fmt(spot));

        // The far half of the restart check: the jukebox at this site was
        // loaded by an earlier run, and the server has been through a full
        // shutdown and reload since, so the record it holds came back off disk.
        if ("eject".equals(mode)) {
            UpdateBlockPacket survived = blockAt(bot, spot);
            checks.check("a loaded jukebox is still loaded after a server restart",
                    survived != null && survived.type == JUKEBOX && survived.metadata == 1,
                    survived == null ? "no block update came back for " + fmt(spot)
                            : "block " + survived.type + " metadata " + survived.metadata
                                    + " - run the \"load\" half first, and against this same world");
            checks.check("the record still comes out after a server restart", eject(bot, spot) != null,
                    "no item entity carrying " + RECORD + " spawned within 6s - the tile entity's Record tag "
                            + "did not survive the save");
            checks.done();
        }

        clear(bot, spot);
        clear(bot, floor);
        if (!stock(bot, FLOOR_HOTBAR, FLOOR_BLOCK)) {
            checks.abort("the creative slot write never came back - either \"" + username
                    + "\" is not in run/server/ops.txt, so /gamemode did nothing, or it is not being ticked");
        }
        bot.send(new ItemSwitchPacket(FLOOR_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
        if (standing(bot, floor) != FLOOR_BLOCK) {
            checks.abort("could not stand a block in the air at " + fmt(floor) + ", so there is no floor to build on");
        }

        if (!stock(bot, JUKEBOX_HOTBAR, JUKEBOX) || !stock(bot, RECORD_HOTBAR, RECORD)) {
            checks.abort("the creative slot writes for the jukebox and the record never came back");
        }
        bot.send(new CreativeSlotPacket(PLAYER_HOTBAR_0 + EMPTY_HOTBAR, null));
        bot.drain(600);

        bot.send(new ItemSwitchPacket(JUKEBOX_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(floor[0], floor[1], floor[2], 1, null));
        if (standing(bot, spot) != JUKEBOX) {
            checks.abort("the jukebox never landed at " + fmt(spot));
        }

        // The report is that a jukebox kicks *everyone*, not just whoever
        // clicked it: the packet goes to every player within 64 blocks, so the
        // check that matters is on a bot that did nothing but stand there.
        Bot watcher = Bot.join(args, WATCHER);
        if (!watcher.land(20000)) {
            checks.abort(WATCHER + " never landed, so it cannot hold a position near the jukebox");
        }

        // ---------------- phase 1: putting a record in
        bot.send(new ItemSwitchPacket(RECORD_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(spot[0], spot[1], spot[2], 1, null));

        // The near half of the restart check: leave it loaded and stop here.
        if ("load".equals(mode)) {
            bot.drain(2000);
            checks.check("the record went into the jukebox this run leaves behind",
                    blockAtMeta(bot, spot) == 1,
                    "the jukebox at " + fmt(spot) + " is not loaded, so the \"eject\" half has nothing to read");
            // A killed server saves nothing, and the block itself would be gone
            // rather than the record - which reads as a persistence bug that
            // isn't one. /stop is the only shutdown that writes the chunk out.
            bot.chat("/stop");
            bot.drain(2000);
            checks.note("the server has been asked to stop; start it again and run this with mode \"eject\"");
            checks.done();
        }

        MusicPacket started = music(bot, spot, 6000);
        if (!checks.check("a record going in reaches the players around it", started != null,
                "no MusicPacket at " + fmt(spot) + (bot.kickReason == null ? "" : " (kicked: " + bot.kickReason + ")"))) {
            checks.done();
        }
        checks.check("the track that starts is the one that went in",
                RECORD_SONG.equals(started.string1),
                "the packet names the track \"" + started.string1 + "\" rather than \"" + RECORD_SONG + "\"");
        checks.check("a record going in is not a stop", !started.stop,
                "the packet asks the client to stop the music it is being told to start");
        // Not immediately after the MusicPacket: the mangled packet itself reads
        // back fine, and it is the bytes it left behind that make the *next*
        // packet id unplaceable. A survival check that does not read on past it
        // passes against the broken build.
        checks.check("the client survives being told to play a record", alive(bot, 1500),
                "the connection died just after the MusicPacket: " + bot.kickReason);

        MusicPacket heard = music(watcher, spot, 4000);
        checks.check("a bystander hears the record too", heard != null,
                "no MusicPacket reached " + WATCHER + " standing " + apart(bot, watcher) + " blocks away"
                        + (watcher.kickReason == null ? "" : " (kicked: " + watcher.kickReason + ")"));
        checks.check("a bystander survives it as well", alive(watcher, 1500),
                WATCHER + " did nothing but stand there and was dropped anyway: " + watcher.kickReason);
        watcher.close();

        UpdateBlockPacket loaded = blockAt(bot, spot);
        checks.check("the jukebox reports itself loaded", loaded != null && loaded.metadata == 1,
                loaded == null ? "no block update came back for " + fmt(spot)
                        : "metadata is " + loaded.metadata + ", so nothing will ever eject the record");

        // ---------------- phase 2: taking it back out
        ItemPacket ejected = eject(bot, spot);
        if (checks.check("an empty-handed click ejects the record", ejected != null,
                "no item entity carrying " + RECORD + " spawned within 6s"
                        + (bot.kickReason == null ? "" : " (kicked: " + bot.kickReason + ")"))) {
            UpdateBlockPacket empty = blockAt(bot, spot);
            checks.check("the emptied jukebox reports itself empty", empty != null && empty.metadata == 0,
                    empty == null ? "no block update came back for " + fmt(spot)
                            : "metadata is still " + empty.metadata);
        }

        // ---------------- phase 3: the same thing across a relogin
        if (!stock(bot, RECORD_HOTBAR, RECORD)) {
            checks.abort("could not restock the record for the relogin phase");
        }
        bot.send(new ItemSwitchPacket(RECORD_HOTBAR));
        bot.drain(400);
        bot.send(new PlacePacket(spot[0], spot[1], spot[2], 1, null));
        bot.drain(2000);
        if (bot.kickReason != null) {
            checks.abort("kicked reloading the jukebox for the relogin phase: " + bot.kickReason);
        }

        bot.close();
        Thread.sleep(2000);
        Bot back = Bot.join(args, username);
        if (!back.land(20000)) {
            checks.abort(username + " never landed after logging back in");
        }
        back.drain(2000);

        UpdateBlockPacket kept = blockAt(back, spot);
        checks.check("a loaded jukebox is still loaded after a relogin",
                kept != null && kept.type == JUKEBOX && kept.metadata == 1,
                kept == null ? "no block update came back for " + fmt(spot)
                        : "block " + kept.type + " metadata " + kept.metadata);

        ItemPacket afterRelog = eject(back, spot);
        checks.check("the record still comes out after a relogin", afterRelog != null,
                "no item entity carrying " + RECORD + " spawned within 6s"
                        + (back.kickReason == null ? "" : " (kicked: " + back.kickReason + ")"));

        // ---------------- phase 4: breaking one with a record still inside
        if (!stock(back, RECORD_HOTBAR, RECORD)) {
            checks.abort("could not restock the record for the break phase");
        }
        back.send(new ItemSwitchPacket(RECORD_HOTBAR));
        back.drain(400);
        back.send(new PlacePacket(spot[0], spot[1], spot[2], 1, null));
        back.drain(2000);
        if (back.kickReason != null) {
            checks.abort("kicked reloading the jukebox for the break phase: " + back.kickReason);
        }

        back.chat("/gamemode survival");
        back.drain(1500);
        back.send(new ItemSwitchPacket(EMPTY_HOTBAR));
        back.drain(400);
        back.send(new DigPacket(0, spot[0], spot[1], spot[2], 1));
        back.drain(1500);
        back.send(new DigPacket(2, spot[0], spot[1], spot[2], 1));

        boolean droppedBox = false, droppedRecord = false;
        for (ItemPacket drop : awaitDrops(back, 20000)) {
            droppedBox |= drop.itemStack != null && drop.itemStack.itemID == JUKEBOX;
            droppedRecord |= drop.itemStack != null && drop.itemStack.itemID == RECORD;
        }
        checks.check("breaking a loaded jukebox drops the jukebox", droppedBox,
                "no item entity carrying block " + JUKEBOX + " spawned within 20s of the dig"
                        + (back.kickReason == null ? "" : " (kicked: " + back.kickReason + ")"));
        checks.check("breaking a loaded jukebox drops the record too", droppedRecord,
                "no item entity carrying " + RECORD + " spawned within 20s of the dig");

        UpdateBlockPacket gone = blockAt(back, spot);
        checks.check("the broken jukebox is gone from the world the client is shown",
                gone == null || gone.type == 0,
                "the server still reports block " + (gone == null ? "?" : gone.type) + " at " + fmt(spot));

        back.chat("/gamemode creative");
        back.drain(1000);
        clear(back, spot);
        clear(back, floor);

        if (back.kickReason != null) {
            checks.abort("kicked mid-run: " + back.kickReason);
        }
        checks.done();
    }

    /**
     * The MusicPacket for this jukebox, or null. A stream that stops parsing is
     * the failure this test exists for, so a read that throws is reported rather
     * than thrown: the packet is written in one field order and read back in
     * another, so every byte after it is garbage and the client drops the
     * connection on the next packet id it cannot place.
     */
    private static MusicPacket music(Bot bot, int[] at, long timeoutMs) throws Exception {
        try {
            return bot.await(MusicPacket.class, p -> p.x == at[0] && p.y == at[1] && p.z == at[2], timeoutMs);
        } catch (Exception e) {
            checks.note("the connection stopped parsing while waiting for the MusicPacket: " + e);
            bot.kickReason = "stream desynced: " + e;
            return null;
        }
    }

    /** Reads on for a while, and answers whether the connection is still parsing. */
    private static boolean alive(Bot bot, long millis) throws Exception {
        try {
            bot.drain(millis);
        } catch (Exception e) {
            checks.note(bot.name + "'s connection stopped parsing: " + e);
            bot.kickReason = "stream desynced: " + e;
        }
        return bot.kickReason == null;
    }

    /** An empty-handed right-click, answered with the record it throws out. */
    private static ItemPacket eject(Bot bot, int[] at) throws Exception {
        try {
            bot.send(new ItemSwitchPacket(EMPTY_HOTBAR));
            bot.drain(400);
            bot.send(new PlacePacket(at[0], at[1], at[2], 1, null));
            return bot.await(ItemPacket.class, p -> p.itemStack != null && p.itemStack.itemID == RECORD, 6000);
        } catch (Exception e) {
            checks.note("the connection stopped parsing while waiting for the ejected record: " + e);
            bot.kickReason = "stream desynced: " + e;
            return null;
        }
    }

    private static java.util.List<ItemPacket> awaitDrops(Bot bot, long windowMs) throws Exception {
        try {
            return bot.awaitAll(ItemPacket.class, p -> p.itemStack != null, windowMs);
        } catch (Exception e) {
            checks.note("the connection stopped parsing while waiting for the drops: " + e);
            bot.kickReason = "stream desynced: " + e;
            return new java.util.ArrayList<ItemPacket>();
        }
    }

    /** handlePlace answers every click with the state of the blocks it touched. */
    private static UpdateBlockPacket blockAt(Bot bot, int[] at) throws Exception {
        try {
            bot.send(new ItemSwitchPacket(EMPTY_HOTBAR));
            bot.drain(300);
            bot.send(new PlacePacket(at[0], at[1] + 1, at[2], 0, null));
            return bot.awaitLast(UpdateBlockPacket.class,
                    p -> p.xPosition == at[0] && p.yPosition == at[1] && p.zPosition == at[2], 4000, 700);
        } catch (Exception e) {
            checks.note("the connection stopped parsing while reading the block at " + fmt(at) + ": " + e);
            bot.kickReason = "stream desynced: " + e;
            return null;
        }
    }

    private static int standing(Bot bot, int[] at) throws Exception {
        UpdateBlockPacket landed = bot.awaitLast(UpdateBlockPacket.class,
                p -> p.xPosition == at[0] && p.yPosition == at[1] && p.zPosition == at[2], 4000, 700);
        return landed == null ? -1 : landed.type;
    }

    /**
     * Empties the slot first: the server only answers a creative write that
     * changed something, and this bot's inventory survives from the last run.
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

    private static void clear(Bot bot, int[] spot) throws Exception {
        bot.drain(300);
        bot.send(new DigPacket(0, spot[0], spot[1], spot[2], 1));
        bot.drain(800);
    }

    private static int blockAtMeta(Bot bot, int[] at) throws Exception {
        UpdateBlockPacket at1 = blockAt(bot, at);
        return at1 == null ? -1 : at1.metadata;
    }

    private static String apart(Bot one, Bot other) {
        double dx = one.posX - other.posX, dy = one.posY - other.posY, dz = one.posZ - other.posZ;
        return String.valueOf(Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz)));
    }

    private static String fmt(int[] at) {
        return at[0] + "," + at[1] + "," + at[2];
    }
}
