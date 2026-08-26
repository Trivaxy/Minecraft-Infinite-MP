package net.minecraft.smoketest;

import java.util.List;

import net.minecraft.game.entity.DataSyncer;
import net.minecraft.network.packet.entity.EntityHealthPacket;
import net.minecraft.network.packet.entity.EntityMetadataPacket;

/**
 * Two checks against an ALREADY-RUNNING server; no op needed.
 *
 * 1. The login EntityHealthPacket carries a food value a client can sprint
 *    with. The client forces sprint off every tick while its food stat is
 *    below 6 (LocalPlayer.handleSprinting), and it takes that stat verbatim
 *    from this packet, so food=0 means joining players can never sprint.
 *
 * 2. A player's sprint start/stop (EntityActionPacket states 4/5) reaches the
 *    server's player entity and propagates to nearby players as the sprint bit
 *    (flag 3) of metadata object 0. Without the server-side handler the flag
 *    never moves, so sprint knockback and sprint exhaustion never apply in
 *    multiplayer and other clients never learn the state.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.SprintSmokeTest
 */
public class SprintSmokeTest {

    private static final int SPRINT_FLAG = 3;

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        loginFoodCheck(args, checks);
        sprintStateCheck(args, checks);
        checks.done();
    }

    private static void loginFoodCheck(String[] args, Checks checks) throws Exception {
        Bot bot = Bot.join(args, "SprintBot").sink();
        EntityHealthPacket health = bot.await(EntityHealthPacket.class, 20000);
        bot.close();

        if (!checks.check("login EntityHealthPacket arrives", health != null, "never received one")) {
            return;
        }

        checks.note("EntityHealthPacket health=" + health.healthMP
                + " food=" + health.food + " saturation=" + health.foodSaturation);
        checks.check("health is positive", health.healthMP > 0, "health=" + health.healthMP);
        checks.check("food allows sprinting (>= 6)", health.food >= 6,
                "food=" + health.food + " - client forces sprint off below 6, so joining players can never sprint");
    }

    private static void sprintStateCheck(String[] args, Checks checks) throws Exception {
        Bot runner = Bot.join(args, "SprintBot").sink();
        Bot watcher = Bot.join(args, "SprintWatcher").sink();

        try {
            if (!checks.check("watcher sees the runner spawn", watcher.awaitSpawnOf(runner, 15000),
                    watcher.kickReason != null ? "watcher kicked: " + watcher.kickReason
                            : "no PlayerSpawnPacket for entity " + runner.entityId + " within 15s")) {
                return;
            }

            runner.sendAction(4);
            boolean sprintOn = awaitSprintFlag(watcher, runner, true);
            runner.sendAction(5);
            boolean sprintOff = sprintOn && awaitSprintFlag(watcher, runner, false);

            checks.check("sprint start reaches nearby players (metadata flag 3 set)", sprintOn,
                    "no EntityMetadataPacket for entity " + runner.entityId + " with the sprint bit set");
            checks.check("sprint stop reaches nearby players (metadata flag 3 cleared)", sprintOff,
                    "no EntityMetadataPacket for entity " + runner.entityId + " with the sprint bit cleared");
        } finally {
            runner.close();
            watcher.close();
        }
    }

    private static boolean awaitSprintFlag(Bot watcher, Bot runner, boolean expectSet) throws Exception {
        return watcher.await(EntityMetadataPacket.class,
                p -> p.entityId == runner.entityId && sprintFlagIs(p, expectSet), 10000) != null;
    }

    private static boolean sprintFlagIs(EntityMetadataPacket packet, boolean expectSet) {
        List<DataSyncer.DataItem> objects = packet.getObjects();
        if (objects == null) {
            return false;
        }
        for (DataSyncer.DataItem item : objects) {
            if (item.getId() == 0 && item.getObject() instanceof Byte) {
                boolean set = ((Byte) item.getObject() & 1 << SPRINT_FLAG) != 0;
                if (set == expectSet) {
                    return true;
                }
            }
        }
        return false;
    }
}
