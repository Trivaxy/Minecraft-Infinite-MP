package net.minecraft.smoketest;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.entity.EntityEffectPacket;
import net.minecraft.network.packet.entity.EntityHealthPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.world.RespawnPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;

public class WitherSmokeTest {

    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;
    private static final int STONE = 1;
    private static final int DIRT = 3;
    private static final int WITHER_EFFECT_ID = 26;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot bot = Bot.join(args, "witherbot");
        bot.settle(3000);
        if (bot.kickReason != null) {
            checks.abort("witherbot was kicked before the test began: " + bot.kickReason);
        }
        reviveIfDead(bot);
        standOnStone(bot, LAND_X, LAND_Y, LAND_Z);

        bot.chat("/gamerule keepinventory on");
        bot.chat("/effect clear witherbot");
        bot.drain(500);

        // --- Phase 1: Wither effect packet and ticking damage ---
        bot.chat("/effect wither 0 100 witherbot");

        EntityEffectPacket effectPacket = bot.await(EntityEffectPacket.class,
                p -> p.entityId == bot.entityId && (p.effectId & 0xFF) == WITHER_EFFECT_ID, 5000);
        checks.check("wither status effect packet received", effectPacket != null,
                "no EntityEffectPacket with effectId=" + WITHER_EFFECT_ID + " within 5s");

        // Initial health before taking wither damage
        int initialHealth = 20;
        EntityHealthPacket initialHealthPacket = bot.awaitLast(EntityHealthPacket.class, null, 2000, 500);
        if (initialHealthPacket != null) {
            initialHealth = initialHealthPacket.healthMP;
        }

        // Wait for wither damage tick (at 40 ticks = 2s)
        final int baselineHealth = initialHealth;
        EntityHealthPacket damagedHealthPacket = bot.await(EntityHealthPacket.class,
                p -> p.healthMP < baselineHealth, 5000);
        checks.check("wither deals ticking damage to player in multiplayer", damagedHealthPacket != null,
                "health never decreased from " + baselineHealth + " - Effect.performEffect did not execute wither damage");

        int healthAfterWither = damagedHealthPacket != null ? damagedHealthPacket.healthMP : baselineHealth;

        // --- Phase 2: Healing is suppressed while withered ---
        bot.chat("/effect heal 0 1 witherbot");
        bot.drain(1000);
        EntityHealthPacket healCheck = bot.awaitLast(EntityHealthPacket.class, null, 1000, 300);
        int healthAfterHealAttempt = healCheck != null ? healCheck.healthMP : healthAfterWither;
        checks.check("healing is blocked while withered", healthAfterHealAttempt <= healthAfterWither,
                "health increased from " + healthAfterWither + " to " + healthAfterHealAttempt + " despite wither effect");

        // --- Phase 3: Death by wither and custom death message ---
        // Apply fast wither (amplifier 3 = 40>>3 = 5 ticks per damage) to kill bot
        bot.chat("/effect wither 3 300 witherbot");

        ChatPacket deathBroadcast = null;
        long deadline = System.currentTimeMillis() + 15000;
        while (System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet p = bot.read();
            if (p instanceof ChatPacket) {
                ChatPacket cp = (ChatPacket) p;
                if (cp.message != null && !cp.message.startsWith("Applied") && cp.message.contains("witherbot")) {
                    deathBroadcast = cp;
                    break;
                }
            }
        }

        checks.check("death by wither is broadcast", deathBroadcast != null,
                "no ChatPacket broadcast for witherbot death within 15s");
        if (deathBroadcast != null) {
            checks.check("death message matches 'withered away'",
                    deathBroadcast.message.contains("withered away"),
                    "death broadcast was: \"" + deathBroadcast.message + "\" (expected death.wither message)");
        }

        // Respawn and cleanup
        bot.send(new RespawnPacket((byte) 0));
        bot.followTeleport(3000);
        bot.chat("/effect clear witherbot");
        bot.chat("/gamerule keepinventory off");
        bot.drain(500);
        bot.close();

        checks.done();
    }

    private static void reviveIfDead(Bot bot) throws Exception {
        bot.send(new RespawnPacket((byte) 0));
        if (bot.followTeleport(2500)) {
            checks.note("witherbot logged in dead and was respawned before the test");
        }
    }

    private static void standOnStone(Bot bot, int x, int y, int z) throws Exception {
        bot.chat("/teleport " + x + " " + y + " " + z);
        if (!bot.followTeleport(5000)) {
            checks.abort("no teleport came back for /teleport " + x + " " + y + " " + z);
        }
        bot.drain(5000);

        bot.chat("/setblock " + x + " " + (y - 1) + " " + z + " " + DIRT);
        bot.drain(1500);

        bot.chat("/setblock " + x + " " + (y - 1) + " " + z + " " + STONE);
        boolean placed = false;
        long deadline = System.currentTimeMillis() + 6000;
        while (!placed && System.currentTimeMillis() < deadline && bot.kickReason == null) {
            Packet next = bot.read();
            if (next instanceof UpdateBlockPacket) {
                UpdateBlockPacket single = (UpdateBlockPacket) next;
                if (single.type == STONE && single.xPosition == x && single.yPosition == y - 1 && single.zPosition == z) {
                    placed = true;
                }
            }
        }
        if (!placed) {
            checks.abort("the setblock under witherbot never came back");
        }
    }
}
