package net.minecraft.smoketest;

import java.util.List;

import net.minecraft.network.packet.player.AnimationPacket;

/**
 * B-32: a punch played no arm swing on any other client. The packet path was
 * never the problem - what was missing is the per-tick advance.
 * `Player.swingItem` only latches `swingProgress = -1, swinging = true`, and the
 * field the renderer reads is written by `Player.swingArms()`, which used to be
 * called from `Player.mobTick()` - a method `RemotePlayer` replaces outright.
 *
 * The client half of that is a play test: no bot can look at an arm. What a bot
 * can hold is the mechanism underneath it, on the one player object it can
 * observe. `EntityPlayerMP.swingItem` broadcasts only `if (!this.swinging)`, and
 * nothing clears `swinging` except `swingArms()` - so if the advance is
 * unreachable from the tick path, the server latches after the first punch and
 * every later one is silently swallowed. Counting broadcasts across several
 * punches asks exactly that question.
 *
 * This is a mechanism check, not a reproduction of the reported symptom: the
 * server was never broken, because `EntityPlayerMP` reaches `Player.onTick`
 * through `onUpdateEntity`. It goes red when `swingArms()` is unreachable, which
 * is the failure B-32 was on the client - so it is what stops the fix from being
 * put somewhere only two of the three player types can see. Both placements that
 * survive it are `Player.onTick` and `Player.mobTick`; only the former is also
 * reachable from `RemotePlayer`, and that half is the play test.
 *
 * **The puncher has to be standing on ground the server accepts.** Everything
 * measured here hangs off `onUpdateEntity`, which `handlePlayerMovement` only
 * reaches inside `if (hasMoved)` - and a bot that merely settles at the ocean
 * spawn never arms it: the fall draws a corrective teleport, `Bot.read` adopts
 * only the *first* teleport it sees, and the echo can never match again. Measured
 * on the first draft of this test: 80 consecutive movement packets with
 * `hasMoved=false`, `swingArms` called zero times, and a green-looking rig
 * reporting a bug that was not there. The teleport onto placed stone below is
 * what re-arms it, and it is why the puncher needs op.
 *
 * Needs a running server with punchbot in run/server/ops.txt.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.SwingSmokeTest
 */
public class SwingSmokeTest {

    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int WATCH_Z = LAND_Z + 3;
    private static final int STONE = 1;
    private static final int DIRT = 3;

    private static final int SWING_MAIN = 1;
    private static final int PUNCHES = 4;

    // swingArms needs 8 Player.onTick calls to clear `swinging`, and on the
    // server those come from onUpdateEntity - one per movement packet, which Bot
    // pumps at 10Hz. So a punch is spent after ~900ms and the gap has to clear
    // it, or the test measures the latch it is trying to rule out.
    private static final long PUNCH_GAP_MS = 2000;

    // The land is far from spawn, so the first run against a fresh world
    // generates that region synchronously and the server answers nothing for
    // tens of seconds. See BlockBreakSmokeTest.
    private static final int ARRIVAL_MS = 60000;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot puncher = Bot.join(args, "punchbot");
        Bot watcher = Bot.join(args, "swingwatcher");
        puncher.settle(3000);
        watcher.settle(3000);
        if (puncher.kickReason != null || watcher.kickReason != null) {
            checks.abort("a bot was kicked before the test began: punchbot=" + puncher.kickReason
                    + " swingwatcher=" + watcher.kickReason);
        }

        standOn(puncher, LAND_X, LAND_Y, LAND_Z);
        placeFloor(puncher, LAND_X, LAND_Y - 1, WATCH_Z);
        puncher.chat("/teleport " + watcher.name + " " + LAND_X + " " + LAND_Y + " " + WATCH_Z);
        if (!watcher.followTeleport(ARRIVAL_MS)) {
            checks.abort("swingwatcher was never teleported next to punchbot - is punchbot in run/server/ops.txt?");
        }
        // No awaitSpawnOf precondition here: the two bots spawn within tracking
        // range of each other, so that packet has already gone by before the
        // teleports and asking for it again just times out. The first punch is
        // the precondition - if it arrives, the watcher is tracking the puncher.
        punch(puncher);
        AnimationPacket first = watcher.await(AnimationPacket.class,
                p -> p.entityId == puncher.entityId && p.animate == SWING_MAIN, 10000);
        if (!checks.check("a punch is broadcast to other players at all", first != null,
                watcher.kickReason != null ? "watcher kicked: " + watcher.kickReason
                        : "no AnimationPacket(animate=1) for entity " + puncher.entityId + " within 10s")) {
            finish(puncher, watcher);
        }

        int seen = 1;
        for (int i = 1; i < PUNCHES; i++) {
            watcher.drain(PUNCH_GAP_MS);
            punch(puncher);
            List<AnimationPacket> swings = watcher.awaitAll(AnimationPacket.class,
                    p -> p.entityId == puncher.entityId && p.animate == SWING_MAIN, 2500);
            seen += swings.size();
        }

        checks.check("every punch is broadcast, not just the first", seen == PUNCHES,
                seen + " of " + PUNCHES + " punches reached the watcher - `swinging` is never cleared, so "
                        + "EntityPlayerMP.swingItem's `if (!this.swinging)` guard swallows everything after the "
                        + "first. Player.swingArms() is unreachable from this player's tick path");

        finish(puncher, watcher);
    }

    private static void punch(Bot bot) throws Exception {
        AnimationPacket swing = new AnimationPacket();
        swing.entityId = bot.entityId;
        swing.animate = SWING_MAIN;
        bot.send(swing);
    }

    private static void standOn(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/teleport " + x + " " + y + " " + z);
        if (!bot.followTeleport(ARRIVAL_MS)) {
            checks.abort("no teleport came back for /teleport " + x + " " + y + " " + z
                    + " - is " + bot.name + " in run/server/ops.txt?");
        }
        bot.drain(3000);
        placeFloor(bot, x, y - 1, z);
    }

    private static void placeFloor(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/setblock " + x + " " + y + " " + z + " " + DIRT);
        bot.drain(800);
        bot.chat("/setblock " + x + " " + y + " " + z + " " + STONE);
        bot.drain(1200);
    }

    private static void finish(Bot puncher, Bot watcher) {
        puncher.close();
        watcher.close();
        checks.done();
    }
}
