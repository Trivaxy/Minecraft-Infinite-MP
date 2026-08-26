package net.minecraft.smoketest;

import net.minecraft.game.block.BlockList;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.world.BlockEventPacket;

/**
 * B-31: ItemInWorldManager.hitBlock packed the break effect's data as
 * `id + meta * 256`, the layout from before block ids were widened past 255.
 * WorldRenderer.worldEvent case 2001 reads `id = data & 4095` and
 * `meta = data >> 12`, which is what every other emitter in the tree writes -
 * including the client's own GameMode. So a metadata-carrying block resolved to
 * a different id on every other client, BlockList.blocks[that id] is usually
 * null, and getStepSound threw out of the render loop.
 *
 *   phase 1 (control) - stone, metadata 0. Both layouts agree here, which is
 *                       why the crash looked intermittent. It is also the rig's
 *                       own precondition: if the watcher hears this one it is in
 *                       range and receiving 2001 events, so a silent phase 2 can
 *                       only mean the break never happened.
 *   phase 2 (the bug) - stained glass, metadata 1. The old layout sends 339,
 *                       BlockList.blocks[339] is null, and that is the report's
 *                       NullPointerException exactly.
 *
 * Against the pre-fix build phase 2 goes red three ways: the decoded id is 339
 * rather than 83, the decoded metadata is 0 rather than 1, and the id the client
 * would look up has no block behind it.
 *
 * Needs a running server with breakbot in run/server/ops.txt; the watcher rides
 * on breakbot's /teleport and needs no op of its own. Both bots stand on placed
 * stone at an integer Y - a bot holding a position in mid-air is kicked as
 * flying after four seconds and every check after that goes silently green.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.BlockBreakSmokeTest
 */
public class BlockBreakSmokeTest {

    // A quiet chunk. Around the spawn the churn is heavy enough that block
    // changes are swept into whole-chunk resends, and the spawn itself is open
    // ocean (B-01) with nothing to stand on.
    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int WATCH_Z = LAND_Z + 3;

    // One step to the +X side of the breaker's feet: inside the server's
    // 6-block dig reach, outside the player's own bounding box.
    private static final int TARGET_X = LAND_X + 1, TARGET_Y = LAND_Y, TARGET_Z = LAND_Z;

    private static final int STONE = 1;
    private static final int DIRT = 3;
    // Material.glass, so a bare hand harvests it: breakStrength is 1/0.3/30 per
    // tick held, which finishes in well under a second. A block needing a tool
    // is a hundredth of that and would take the dig past ten seconds.
    private static final int GLASS = 83, GLASS_META = 1;

    // The land below is not near the spawn, so the first run against a fresh
    // world generates that region synchronously on the main thread and the
    // server answers nothing at all while it does - measured at just over 30
    // seconds, during which commands queue up and every short wait expires.
    private static final int ARRIVAL_MS = 60000;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot breaker = Bot.join(args, "breakbot");
        Bot watcher = Bot.join(args, "watchbot");
        breaker.settle(3000);
        watcher.settle(3000);
        if (breaker.kickReason != null || watcher.kickReason != null) {
            checks.abort("a bot was kicked before the test began: breakbot=" + breaker.kickReason
                    + " watchbot=" + watcher.kickReason);
        }

        standOn(breaker, LAND_X, LAND_Y, LAND_Z);
        placeFloor(breaker, LAND_X, LAND_Y - 1, WATCH_Z);
        breaker.chat("/teleport " + watcher.name + " " + LAND_X + " " + LAND_Y + " " + WATCH_Z);
        if (!watcher.followTeleport(ARRIVAL_MS)) {
            checks.abort("watchbot was never teleported next to breakbot - is breakbot in run/server/ops.txt?");
        }

        // ---------------- phase 1: the layouts agree, and the rig works
        BlockEventPacket control = breakBlock(breaker, watcher, STONE, 0);
        if (control == null) {
            checks.abort("no break effect reached watchbot for plain stone - the dig never completed, or the "
                    + "watcher is out of the 64-block radius sendPacketToPlayerCoord uses");
        }
        checks.check("a metadata-free break still decodes to the block that was broken",
                id(control) == STONE && meta(control) == 0,
                "sent data=" + control.data + ", which decodes to id " + id(control) + " meta " + meta(control));

        // ---------------- phase 2: the reported crash
        BlockEventPacket stained = breakBlock(breaker, watcher, GLASS, GLASS_META);
        if (!checks.check("a break effect reaches other players at all", stained != null,
                "no BlockEventPacket(2001) at " + TARGET_X + "," + TARGET_Y + "," + TARGET_Z + " within 20s")) {
            finish(breaker, watcher);
        }

        checks.check("the block id survives the wire when the block carries metadata",
                id(stained) == GLASS,
                "sent data=" + stained.data + ", which decodes to " + describe(id(stained))
                        + " instead of " + describe(GLASS));
        checks.check("the metadata survives the wire", meta(stained) == GLASS_META,
                "decoded metadata " + meta(stained) + ", so the break particles use the wrong texture variant");
        // WorldRenderer.worldEvent:1107-1108 verbatim: it guards `index > 0` and
        // then dereferences whatever blocks[index] holds.
        checks.check("the id the client looks up has a block behind it",
                id(stained) > 0 && BlockList.blocks[id(stained)] != null,
                "BlockList.blocks[" + id(stained) + "] is null - getStepSound throws and the client dies in "
                        + "its render loop");

        finish(breaker, watcher);
    }

    /**
     * Places `id:meta` in reach and digs it out, returning the break effect the
     * watcher received. The dig is two packets because the server resolves it in
     * two places: status 0 arms ItemInWorldManager at these coordinates, status 2
     * asks it to finish, and whatever is left runs down in beginBlockRemoval one
     * tick at a time - which is why this waits after the second packet rather
     * than expecting the break to have already happened.
     */
    private static BlockEventPacket breakBlock(Bot breaker, Bot watcher, int id, int meta) throws Exception {
        // Something else first, so the block below is always a real change: a
        // setblock that changes nothing sends nothing, and the last phase (or
        // the last run) may have left this exact block here already.
        breaker.chat("/setblock " + TARGET_X + " " + TARGET_Y + " " + TARGET_Z + " " + DIRT);
        breaker.drain(1000);
        breaker.chat("/setblock " + TARGET_X + " " + TARGET_Y + " " + TARGET_Z + " " + id + ":" + meta);
        breaker.drain(1500);

        breaker.send(new DigPacket(0, TARGET_X, TARGET_Y, TARGET_Z, 1));
        breaker.drain(1000);
        breaker.send(new DigPacket(2, TARGET_X, TARGET_Y, TARGET_Z, 1));

        return watcher.await(BlockEventPacket.class,
                p -> p.sfx == 2001 && p.x == TARGET_X && p.y == TARGET_Y && p.z == TARGET_Z, 20000);
    }

    /**
     * A server-initiated teleport clears hasMoved, and handlePlayerMovement does
     * movement, chunk streaming and the health sync inside one `if (hasMoved)` -
     * so every teleport is followed by followTeleport or the server stops
     * listening to that bot entirely.
     */
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

    private static int id(BlockEventPacket effect) {
        return effect.data & 4095;
    }

    private static int meta(BlockEventPacket effect) {
        return effect.data >> 12 & 0xFF;
    }

    private static String describe(int id) {
        return id > 0 && BlockList.blocks[id] != null
                ? "id " + id + " (" + BlockList.blocks[id].getClass().getSimpleName() + ")"
                : "id " + id + " (no block)";
    }

    private static void finish(Bot breaker, Bot watcher) {
        breaker.close();
        watcher.close();
        checks.done();
    }
}
