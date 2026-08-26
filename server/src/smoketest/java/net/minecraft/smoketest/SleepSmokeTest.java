package net.minecraft.smoketest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.network.world.NetworkWorld;
import net.minecraft.client.network.player.RemotePlayer;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import net.minecraft.game.world.WorldType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SleepPacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateTimePacket;

/**
 * B-38: beds set a respawn point but never passed the night, and the moment one
 * player got into bed every other player on the server was dropped. Three
 * independent defects, each of which alone keeps the night from passing:
 *
 *   phase 0 (no server) - SleepPacket is id 17, which was mapped
 *                         CLIENT_TO_SERVER while only the server ever sends it.
 *                         Packet.readPacket refuses a clientbound id that is not
 *                         in the allowed set, so the *watcher's* connection dies
 *                         reading it. That is the kick. It is decided entirely
 *                         by the tables in :core, so it needs no server at all.
 *                         The same round trip carries bedY=150: it went over the
 *                         wire as a signed byte, and this world is 300+ blocks
 *                         tall.
 *   phase 1 (one sleeper)  - the clock advance. EntityPlayerMP.preventSleeping()
 *                         overrode "is this player holding a dream catcher" with
 *                         world.isDaytime(), so at night wakeUpPlayer always took
 *                         the dream-dimension branch - and Player.switchDimension
 *                         is an empty method on the server. Records how far one
 *                         sleeper moves the clock.
 *   phase 2 (two sleepers) - the same jump again, not twice it. The advance used
 *                         to run once per sleeper inside World.triggerSleep's
 *                         loop, so two people in bed moved a whole dayLength and
 *                         landed back on the same time of day. Phase 2 also
 *                         watches each bot receive the other's SleepPacket and
 *                         stay connected, which is phase 0 on the real wire.
 *
 * Needs a running server with BOTH sleepbot and dreambot in run/server/ops.txt -
 * each stocks itself with /give and clears its own rest timer with /fatigue,
 * which is self-targeted. It also needs the server to itself: the night only
 * passes when *every* player in the Overworld is asleep, so a human client
 * logged in will hang phases 1 and 2.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.SleepSmokeTest
 */
public class SleepSmokeTest {

    // The quiet chunk BlockBreakSmokeTest uses. The spawn is open ocean (B-01),
    // with nothing to stand on and nothing to put a bed on.
    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int STAND_A_Z = LAND_Z, STAND_B_Z = LAND_Z + 6;

    // Two blocks east of the sleeper's feet: in reach for both the place and the
    // activate, and clear of the roof below, which sits over the bed only.
    private static final int BED_X = LAND_X + 2;

    // Four blocks west of the pillow: inside handlePlace's 8 blocks of reach, outside goToSleep's
    // 3-block box. That gap is the only way to reach TOO_FAR_AWAY from a real click.
    private static final int FAR_X = BED_X - 4;

    private static final int STONE = 1;
    private static final int DIRT = 3;
    private static final int BED_ITEM = 4195;
    private static final int BED_BLOCK = 30;

    /** PlayerContainer lays the hotbar out at 40..48; the held-item index counts from there. */
    private static final int HOTBAR_START = 40;

    // The first /teleport into unexplored terrain generates the region on the
    // main thread and the server answers nothing at all while it runs - measured
    // at just over 30 seconds on a fresh world.
    private static final int ARRIVAL_MS = 60000;

    // sleepTimer only advances inside handlePlayerMovement's `if (hasMoved)`, one
    // step per movement packet, and Bot sends ten a second. isPlayerFullyAsleep
    // wants 100 of them, so the world cannot pass the night for ten seconds after
    // the last bot gets into bed.
    private static final int SLEEP_MS = 45000;

    private static final Checks checks = new Checks();

    /** The wake animation awaitTimeJump saw on its way past, or -1. */
    private static int wakeAnimation = -1;

    /** Where the bots first landed, so they can be parked there again. See finish(). */
    private static String home;

    /** Whether the last getIntoBed actually took, read off the bed's own occupied bit. */
    private static boolean inBed;

    public static void main(String[] args) throws Exception {
        wireRoundTrip();
        restCooldown();

        Bot sleeper = Bot.join(args, "sleepbot");
        sleeper.settle(3000);
        if (sleeper.kickReason != null) {
            checks.abort("sleepbot was kicked before the test began: " + sleeper.kickReason);
        }

        home = " " + (int)sleeper.posX + " " + (int)sleeper.posY + " " + (int)sleeper.posZ;

        standOn(sleeper, LAND_X, LAND_Y, STAND_A_Z);
        placeFloor(sleeper, LAND_X, LAND_Y - 1, STAND_B_Z);
        buildBeds(sleeper);

        // ---------------- phase 1a: getting out of bed early
        sleeper.chat("/time night");
        sleeper.drain(1500);
        SleepPacket ownBed = getIntoBed(sleeper, STAND_A_Z);
        checks.check("the server tells the sleeping player itself, not just the onlookers",
                ownBed != null && ownBed.bedX == BED_X && ownBed.bedZ == STAND_A_Z + 1,
                ownBed == null
                        ? "no SleepPacket addressed to sleepbot - its client is handed nothing to act "
                                + "on, and stands next to a bed the server has it lying in"
                        : "got " + ownBed.bedX + "," + ownBed.bedY + "," + ownBed.bedZ);
        checks.check("and moves it onto the bed, so the position it echoes is the bed's",
                onBed(sleeper.posX, sleeper.posZ),
                "sleepbot's last accepted position is " + sleeper.posX + "," + sleeper.posY + "," + sleeper.posZ
                        + " and the bed is at " + (BED_X + 0.5) + ",_," + (STAND_A_Z + 1.5)
                        + " - lastPos* is still where it walked from, and handlePlayerMovement snaps it "
                        + "back there every packet");
        sleeper.sendAction(3);
        AnimationPacket cancelled = sleeper.await(AnimationPacket.class,
                p -> p.entityId == sleeper.entityId && (p.animate == 3 || p.animate == 6), 10000);
        checks.check("getting out of bed early tells the client it did not rest",
                cancelled != null && cancelled.animate == 6,
                cancelled == null
                        ? "no wake animation came back at all"
                        : "the server sent animate " + cancelled.animate + ", which the client turns into "
                                + "wakeUpPlayer(false, ...) - it charges itself the rest cooldown the server "
                                + "did not, and refuses the bed for the next half day of ticks");
        rehome(sleeper, LAND_X, LAND_Y, STAND_A_Z);

        // ---------------- phase 1b: one sleeper, and the clock moves at all
        sleeper.chat("/time night");
        sleeper.drain(1500);
        getIntoBed(sleeper, STAND_A_Z);
        long alone = awaitTimeJump(sleeper, SLEEP_MS);
        if (!checks.check("one sleeper passes the night", alone > 1000,
                "the world clock never jumped in " + SLEEP_MS / 1000 + "s - either sleepbot never got into the "
                        + "bed at " + BED_X + "," + LAND_Y + "," + (STAND_A_Z + 1) + ", or someone else is "
                        + "logged in and awake")) {
            finish(sleeper, null);
        }
        checks.note("one sleeper moved the clock " + alone + " ticks");
        checks.check("sleeping through the night tells the client it rested",
                wakeAnimation == 3,
                "the server sent animate " + wakeAnimation + " for a wake that passed the night, so the "
                        + "client does not charge the cooldown the server just charged");
        rehome(sleeper, LAND_X, LAND_Y, STAND_A_Z);

        // ---------------- phase 1c: the reason for a refusal has to reach the player
        // Straight back to the same bed with the rest timer still running from the sleep above.
        // The client does not work any of this out for itself any more, so a message the server
        // drops is a right-click that does nothing and explains nothing.
        sleeper.drain(2500);
        sleeper.send(new PlacePacket(BED_X, LAND_Y, STAND_A_Z + 1, 1, null));
        ChatPacket refused = sleeper.await(ChatPacket.class, 6000);
        if (checks.check("a bed the server refuses says why, in chat", refused != null,
                "nothing came back - Player.addChatMessage is a no-op that only LocalPlayer overrides, "
                        + "so every refusal the server writes goes nowhere in multiplayer")) {
            checks.note("sleepbot was told: " + refused.message);
        }
        sleeper.drain(1000);

        // ---------------- phase 1d: out of reach, the refusal that had no branch at all
        sleeper.chat("/fatigue");
        sleeper.drain(1000);
        placeFloor(sleeper, FAR_X, LAND_Y - 1, STAND_A_Z);
        rehome(sleeper, FAR_X, LAND_Y, STAND_A_Z);
        sleeper.drain(2500);
        sleeper.send(new PlacePacket(BED_X, LAND_Y, STAND_A_Z + 1, 1, null));
        String tooFarText = Translator.getInstance().translateKey("tile.bed.tooFar");
        ChatPacket tooFar = sleeper.await(ChatPacket.class, p -> p.message.equals(tooFarText), 6000);
        checks.check("a bed out of reach says so too", tooFar != null,
                "expected \"" + tooFarText + "\" and got nothing - goToSleep returns TOO_FAR_AWAY and "
                        + "blockActivated had no branch for it, so the click was silent");
        rehome(sleeper, LAND_X, LAND_Y, STAND_A_Z);

        // ---------------- phase 2: two sleepers, on the wire and on the clock
        Bot dreamer = Bot.join(args, "dreambot");
        dreamer.settle(3000);
        if (dreamer.kickReason != null) {
            checks.abort("dreambot was kicked before phase 2: " + dreamer.kickReason);
        }
        sleeper.chat("/teleport " + dreamer.name + " " + LAND_X + " " + LAND_Y + " " + STAND_B_Z);
        if (!dreamer.followTeleport(ARRIVAL_MS)) {
            checks.abort("dreambot was never teleported next to sleepbot");
        }
        // Once more now the region is loaded. The long hop draws corrective
        // teleports of its own, and Bot only adopts the one it is waiting for -
        // adopt a stale one and the echo never matches, so the server ignores
        // every packet this bot sends from then on, silently.
        rehome(dreamer, LAND_X, LAND_Y, STAND_B_Z);

        sleeper.chat("/time night");
        sleeper.drain(1500);

        SleepPacket seenByDreamer = watchGetIntoBed(sleeper, dreamer, STAND_A_Z);
        if (!checks.check("a player getting into bed reaches the other client instead of killing it",
                seenByDreamer != null,
                dreamer.kickReason != null
                        ? "dreambot's connection died: " + dreamer.kickReason
                        : "no SleepPacket for sleepbot (entity " + sleeper.entityId + ") in 20s")) {
            finish(sleeper, dreamer);
        }
        checks.check("it names the sleeper and the bed",
                seenByDreamer.entityID == sleeper.entityId
                        && seenByDreamer.bedX == BED_X && seenByDreamer.bedZ == STAND_A_Z + 1,
                "got entity " + seenByDreamer.entityID + " at " + seenByDreamer.bedX + ","
                        + seenByDreamer.bedY + "," + seenByDreamer.bedZ);
        checks.check("the bed's Y survives the wire above 127", seenByDreamer.bedY == LAND_Y,
                "got bedY " + seenByDreamer.bedY + " for a bed at y=" + LAND_Y
                        + ", so the remote player never lies down");

        // the reported symptom: the onlooker's client draws the sleeping model wherever the tracker
        // says the sleeper is, and putToBed does not touch the serverX/Y/Z that drives it
        dreamer.drain(2000);
        double[] seen = dreamer.positionOf(sleeper.entityId);
        if (checks.check("dreambot is being told where sleepbot is at all", seen != null,
                "no spawn or move for entity " + sleeper.entityId + " ever reached dreambot")) {
            checks.check("the onlooker sees the sleeper on the bed, not where it walked from",
                    onBed(seen[0], seen[2]),
                    "dreambot has sleepbot at " + seen[0] + "," + seen[1] + "," + seen[2]
                            + " and the bed at " + (BED_X + 0.5) + ",_," + (STAND_A_Z + 1.5)
                            + " - that is where sleepbot stood when it clicked, so the model lies "
                            + "on the floor beside the bed");
        }

        SleepPacket seenBySleeper = watchGetIntoBed(dreamer, sleeper, STAND_B_Z);
        checks.check("and it works in the other direction", seenBySleeper != null,
                sleeper.kickReason != null
                        ? "sleepbot's connection died: " + sleeper.kickReason
                        : "no SleepPacket for dreambot (entity " + dreamer.entityId + ") in 20s"
                                + (inBed
                                        ? " - and dreambot IS in the bed, so this is sleepbot not tracking it "
                                                + "rather than anything to do with sleeping (B-38c)"
                                        : " - dreambot never got into the bed"));

        long together = awaitTimeJump(sleeper, SLEEP_MS);
        if (!checks.check("two sleepers pass the night", together > 1000,
                "the world clock never jumped in " + SLEEP_MS / 1000 + "s with both bots in bed")) {
            finish(sleeper, dreamer);
        }
        checks.note("two sleepers moved the clock " + together + " ticks");
        // Run per-sleeper the advance is dayLength/2 each, so two of them is a
        // whole dayLength - the same time of day again, which is the report.
        checks.check("the night passes once, not once per sleeper",
                together < alone * 3 / 2,
                "two sleepers moved the clock " + together + " ticks against one sleeper's " + alone
                        + " - a whole day, so it is the same time of night it was");

        finish(sleeper, dreamer);
    }

    /**
     * Packet 17 through the client's own decoder, with no server anywhere. This
     * is the kick: Packet.readPacket(in, false) is exactly what NetworkManager
     * runs on the client, and it throws on an id that is not clientbound.
     */
    private static void wireRoundTrip() throws Exception {
        SleepPacket sent = new SleepPacket();
        sent.entityID = 4242;
        sent.state = 0;
        sent.bedX = 875;
        sent.bedY = LAND_Y;
        sent.bedZ = 769;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Packet.writePacket(sent, new DataOutputStream(bytes));

        Packet back = null;
        String failure = null;
        try {
            back = Packet.readPacket(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), false);
        } catch (Exception e) {
            failure = e.getMessage();
        }

        if (!checks.check("a client can decode the sleep packet the server sends it",
                back instanceof SleepPacket,
                failure != null ? "readPacket threw: " + failure : "decoded as " + back)) {
            return;
        }
        SleepPacket got = (SleepPacket) back;
        checks.check("the bed coordinates survive the round trip",
                got.entityID == sent.entityID && got.bedX == sent.bedX
                        && got.bedY == sent.bedY && got.bedZ == sent.bedZ,
                "sent " + sent.bedX + "," + sent.bedY + "," + sent.bedZ
                        + " got " + got.bedX + "," + got.bedY + "," + got.bedZ);
    }

    /**
     * The lockout the report describes, on real game objects with no server.
     * Waking with resetTimer=false is "you slept through": it charges
     * timeWithoutRest half a day, and goToSleep refuses until that runs down.
     * The client runs that path off the wake animation, so a *cancelled* sleep
     * that arrives as a slept-through wake locks the client out of a bed for
     * fifteen minutes while the server, which charged nothing, keeps putting it
     * to sleep - which is how you end up reading "This bed is occupied!" about
     * your own body lying in it.
     */
    private static void restCooldown() {
        checks.check("a cancelled wake leaves the player able to use the bed again",
                canSleepAfterWake(true), "goToSleep refused right after a wake that reset the timer");
        checks.check("a slept-through wake does not", !canSleepAfterWake(false),
                "goToSleep allowed a second sleep immediately after resting - the cooldown is not being charged");
    }

    /** One player, one bed, one wake of the given kind, then straight back to bed. */
    private static boolean canSleepAfterWake(boolean resetTimer) {
        NetworkWorld world = new NetworkWorld(null, 1L, 0, WorldType.DEFAULT, null);
        world.isOnline = true;
        world.doPreChunk(0, 0, true);
        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                world.updateBlocksAndNeighbors(x, 59, z, BlockList.stone.id, 0);
            }
        }
        // The pair the way BedItem lays it out: facing +Z, pillow carrying meta 8.
        world.updateBlocksAndNeighbors(2, 60, 2, BlockList.bed.id, 0);
        world.updateBlocksAndNeighbors(2, 60, 3, BlockList.bed.id, 8);

        RemotePlayer player = new RemotePlayer(world, "Sleeper");
        player.setPosition(2.5, 60.0, 2.5);
        if (player.goToSleep(2, 60, 3) != Bed.Status.OK) {
            checks.abort("the probe could not get its player into a bed at all - the rig is wrong, not the game");
        }

        player.wakeUpPlayer(resetTimer, false, false);
        return player.goToSleep(2, 60, 3) == Bed.Status.OK;
    }

    /**
     * Both beds, from one /give. Stocking twice used to be two calls and the
     * second one regularly came back empty: everything World.tickEntity drives -
     * the container sync included - stops for a player whose surrounding chunks
     * are not all loaded, and a bot that stands still far from the world spawn
     * gets there (B-38c). One trip through the inventory is one chance to lose.
     */
    private static void buildBeds(Bot bot) throws Exception {
        for (int standZ : new int[]{STAND_A_Z, STAND_B_Z}) {
            // Stand next to the bed being cleared. A place carries 8 blocks of
            // reach but a dig carries less, and digging the far bed from here
            // silently does nothing - which leaves last run's bed in place,
            // still flagged occupied, and blockActivated refuses an occupied
            // bed before it ever reaches goToSleep.
            rehome(bot, LAND_X, LAND_Y, standZ);
            placeFloor(bot, BED_X, LAND_Y - 1, standZ);
            placeFloor(bot, BED_X, LAND_Y - 1, standZ + 1);

            bot.send(new DigPacket(0, BED_X, LAND_Y, standZ, 1));
            bot.drain(500);
            bot.send(new DigPacket(2, BED_X, LAND_Y, standZ, 1));
            bot.drain(2500);
        }

        rehome(bot, LAND_X, LAND_Y, STAND_A_Z);

        int slot = stock(bot, BED_ITEM, 2);
        if (slot < 0) {
            checks.abort("no bed landed in " + bot.name + "'s hotbar. Either it is not in run/server/ops.txt, "
                    + "or its entity has stopped being ticked (B-38c) - delete "
                    + "run/server/world/players/" + bot.name + ".dat and run again");
        }
        bot.send(new ItemSwitchPacket(slot));
        bot.drain(500);

        for (int standZ : new int[]{STAND_A_Z, STAND_B_Z}) {
            // The bot never sends a look packet, so its yaw is 0, which BedItem
            // reads as facing +Z: pillow at standZ+1, carrying the meta 8.
            bot.send(new PlacePacket(BED_X, LAND_Y - 1, standZ, 1, new ItemStack(BED_ITEM, 1)));
            // handlePlace answers every place with the state of the clicked block
            // and the one the side points at, which is where the bed goes.
            final int at = standZ;
            UpdateBlockPacket landed = bot.awaitLast(UpdateBlockPacket.class,
                    p -> p.xPosition == BED_X && p.yPosition == LAND_Y && p.zPosition == at, 5000);
            if (landed == null || landed.type != BED_BLOCK) {
                checks.abort("no bed at " + BED_X + "," + LAND_Y + "," + standZ + " after placing one there - "
                        + (landed == null ? "the server never answered the place" : "found block id " + landed.type));
            }
            checks.note("bed at " + BED_X + "," + LAND_Y + "," + standZ + " meta " + landed.metadata);

            for (int x = BED_X - 1; x <= BED_X; x++) {
                for (int y = LAND_Y + 1; y <= LAND_Y + 2; y++) {
                    for (int z = standZ; z <= standZ + 1; z++) {
                        bot.chat("/setblock " + x + " " + y + " " + z + " " + STONE);
                        bot.drain(300);
                    }
                }
            }
        }
    }

    /**
     * Clicks the pillow half and waits to be told what came of it. The client does not decide any
     * of this any more - the SleepPacket addressed to the clicker *is* the answer, and without one
     * a real client sits there awake next to a bed the server has it lying in.
     */
    private static SleepPacket getIntoBed(Bot bot, int standZ) throws Exception {
        // goToSleep refuses for half a day after the last sleep, and TimeWithoutRest
        // is saved in the player file, so a second run would fail without this.
        bot.chat("/fatigue");
        bot.drain(2000);
        bot.send(new PlacePacket(BED_X, LAND_Y, standZ + 1, 1, null));
        SleepPacket own = bot.await(SleepPacket.class, p -> p.entityID == bot.entityId, 8000);
        inBed = own != null;
        if (inBed) {
            // the server teleports the sleeper onto the bed, and an unechoed teleport is a bot nobody listens to
            bot.followTeleport(4000);
        }
        checks.note(bot.name + (inBed ? " was put in the bed at " : " was NOT put in the bed at ")
                + BED_X + "," + LAND_Y + "," + (standZ + 1));
        if (!inBed) {
            // The server always says why now, so a refusal here names itself instead of
            // leaving the next reader guessing which of goToSleep's four gates shut.
            ChatPacket why = bot.await(ChatPacket.class, 3000);
            checks.note(why == null ? "  ...and said nothing about it" : "  ...because: " + why.message);
        }
        return own;
    }

    /** Within a block of the middle of bed A, which is as far off as the sleeping model can be and still be on it. */
    private static boolean onBed(double x, double z) {
        double offX = x - (BED_X + 0.5);
        double offZ = z - (STAND_A_Z + 1.5);
        return Math.sqrt(offX * offX + offZ * offZ) < 1.0;
    }

    /** The same, with the other bot listening for the announcement. */
    private static SleepPacket watchGetIntoBed(Bot bot, Bot watcher, int standZ) throws Exception {
        getIntoBed(bot, standZ);
        try {
            return watcher.await(SleepPacket.class, p -> p.entityID == bot.entityId, 20000);
        } catch (Exception e) {
            checks.note(watcher.name + " died reading the packet: " + e);
            return null;
        }
    }

    /**
     * The delta across the first UpdateTimePacket that moves the clock by more
     * than a second's worth of ticking. The server sends one every 20 ticks to
     * everyone in the dimension, so a jump of a few thousand cannot be mistaken
     * for the clock running normally.
     */
    private static long awaitTimeJump(Bot bot, long timeoutMs) throws Exception {
        long previous = -1;
        long jump = -1;
        wakeAnimation = -1;
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet next = bot.read();
            if (next instanceof AnimationPacket) {
                AnimationPacket animation = (AnimationPacket) next;
                if (animation.entityId == bot.entityId && (animation.animate == 3 || animation.animate == 6)) {
                    wakeAnimation = animation.animate;
                }
            }
            if (next instanceof UpdateTimePacket) {
                long time = ((UpdateTimePacket) next).time;
                if (previous >= 0 && time - previous > 1000) {
                    jump = time - previous;
                    // The wake animation and the clock land in the same tick but
                    // not in the same packet, and UpdateTimePacket only goes out
                    // every 20 ticks - so it can be either side. Keep reading.
                    deadline = Math.min(deadline, System.currentTimeMillis() + 3000);
                }
                previous = time;
            }
        }
        return jump;
    }

    /**
     * /give into the hotbar, returning the held-item index or -1. The drain
     * before the clear is not padding: digging the old bed out drops one, and a
     * pickup landing after the clear puts an item back in a slot this is about
     * to reason about.
     */
    private static int stock(Bot bot, int itemId, int count) throws Exception {
        bot.drain(2000);
        bot.chat("/clearinv");
        bot.drain(2000);
        bot.chat("/give " + itemId + " " + count);
        SlotPacket given = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == itemId, 6000);
        if (given == null) {
            java.util.List<SlotPacket> any = bot.awaitAll(SlotPacket.class, null, 3000);
            checks.note("no bed slot update; " + any.size() + " other slot packets in the window");
            return -1;
        }
        if (given.itemSlot < HOTBAR_START) {
            checks.note("the bed landed in slot " + given.itemSlot + ", outside the hotbar");
            return -1;
        }
        return given.itemSlot - HOTBAR_START;
    }

    /**
     * A server-initiated teleport clears hasMoved, and handlePlayerMovement does
     * all of its work inside one `if (hasMoved)` - so waking up leaves a bot the
     * server has stopped listening to until it echoes a position back.
     */
    private static void standOn(Bot bot, int x, int y, int z) throws Exception {
        rehome(bot, x, y, z);
        bot.drain(3000);
        placeFloor(bot, x, y - 1, z);
    }

    /**
     * Teleports and keeps adopting teleports until the one that comes back is
     * the one we asked for. It matters which: a wake-up sends a teleport of its
     * own, and a bot that adopts the wrong position echoes a position the server
     * never set, so hasMoved never re-arms and every packet it sends from then
     * on is dropped without a word.
     */
    private static void rehome(Bot bot, int x, int y, int z) throws Exception {
        bot.drain(1500);
        bot.chat("/teleport " + x + " " + y + " " + z);
        long deadline = System.currentTimeMillis() + ARRIVAL_MS;
        while (System.currentTimeMillis() < deadline) {
            if (!bot.followTeleport(15000)) {
                break;
            }
            if (Math.abs(bot.posX - x) < 1.5 && Math.abs(bot.posY - y) < 1.5 && Math.abs(bot.posZ - z) < 1.5) {
                bot.drain(1000);
                return;
            }
        }
        checks.abort("no teleport to " + x + "," + y + "," + z + " came back for " + bot.name
                + " (it is at " + bot.posX + "," + bot.posY + "," + bot.posZ
                + ") - is it in run/server/ops.txt?");
    }

    /** A setblock that changes nothing sends nothing, so always change it twice. */
    private static void placeFloor(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/setblock " + x + " " + y + " " + z + " " + DIRT);
        bot.drain(600);
        bot.chat("/setblock " + x + " " + y + " " + z + " " + STONE);
        bot.drain(900);
    }

    /**
     * Leaving a bot in bed leaves the bed flagged occupied, and the next run
     * cannot use it: the flag is cleared out of wakeUpPlayer against the
     * player's own bedPosition, and a reconnecting player rebuilds that from
     * where it was standing rather than from the bed (B-38b).
     */
    private static void finish(Bot sleeper, Bot dreamer) {
        try {
            sleeper.sendAction(3);
            if (dreamer != null) {
                dreamer.sendAction(3);
            }
            sleeper.drain(1500);
            // Park them back where they logged in. A bot saved out at the test
            // site logs back in there and is never ticked by the world at all
            // (B-38c), which makes the *next* run fail in the inventory for a
            // reason that has nothing to do with beds.
            if (home != null) {
                sleeper.chat("/teleport " + sleeper.name + home);
                if (dreamer != null) {
                    sleeper.chat("/teleport " + dreamer.name + home);
                }
                sleeper.drain(2500);
            }
        } catch (Exception ignored) {
        }
        sleeper.close();
        if (dreamer != null) {
            dreamer.close();
        }
        checks.done();
    }
}
