package net.minecraft.smoketest;

import net.minecraft.network.packet.entity.EntitySpawnPacket;

/**
 * B-23: LightningBolt, LingeringCloud and VoidCrystal had no branch in
 * EntityTracker.trackEntity, so no EntityTrackerEntry was ever created and no
 * spawn packet was ever sent - the three lived out their lives server-side
 * only. Lightning was worse than the other two: World.addWeatherEffect does not
 * route through spawnEntity at all, so the storm path never even reached the
 * tracker.
 *
 * Each phase drives one of the three from a real trigger and asserts the spawn
 * packet on the wire. Against the pre-fix build every phase reports "no packet"
 * - the failure is total, not a wrong field.
 *
 * Needs a running server with boltbot in run/server/ops.txt. The bot teleports
 * to land first: the spawn point is open ocean (B-01), and a thrown Spear
 * cannot land a hit on a cow that is floating.
 */
public class EntityTrackingSmokeTest {

    private static final int LAND_X = 875, LAND_Y = 150, LAND_Z = 768;

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        Bot bot = Bot.join(args, "boltbot").sink();
        bot.settle(2000);
        if (bot.kickReason != null) {
            checks.abort("boltbot was kicked before the test began: " + bot.kickReason);
        }

        bot.chat("/teleport " + LAND_X + " " + LAND_Y + " " + LAND_Z);
        bot.drain(3000);

        EntitySpawnPacket crystal = summon(bot, "EndCrystal", EntitySpawnPacket.EntityType.CRYSTAL);
        checks.check("VoidCrystal reaches the client", crystal != null,
                "no CRYSTAL spawn packet in 5s - the entity exists server-side only");
        if (crystal != null) {
            checks.check("crystal carries playerPlaced=false", crystal.metadata == 0,
                    "metadata was " + crystal.metadata + ", expected 0 for a summoned crystal");
        }

        EntitySpawnPacket cloud = summon(bot, "LingeringCloud", EntitySpawnPacket.EntityType.CLOUD);
        checks.check("LingeringCloud reaches the client", cloud != null,
                "no CLOUD spawn packet in 5s - the entity exists server-side only");
        if (cloud != null) {
            checks.check("cloud carries the untinted marker", cloud.metadata == -1,
                    "metadata was " + cloud.metadata + ", expected -1 for a cloud with no effect");
        }

        if (crystal != null && cloud != null) {
            checks.check("both summons land in the player's column",
                    Math.abs(crystal.xPosition - cloud.xPosition) <= 32 && Math.abs(crystal.zPosition - cloud.zPosition) <= 32,
                    "crystal at " + crystal.xPosition / 32.0 + "," + crystal.zPosition / 32.0
                            + " but cloud at " + cloud.xPosition / 32.0 + "," + cloud.zPosition / 32.0);
        }

        EntitySpawnPacket bolt = strikeLightning(bot, checks);
        checks.check("LightningBolt reaches the client", bolt != null,
                "no LIGHTNING spawn packet - addWeatherEffect never reached the tracker");
        if (bolt != null && crystal != null) {
            checks.check("the bolt struck where the trident hit",
                    Math.abs(bolt.xPosition - crystal.xPosition) <= 128 && Math.abs(bolt.zPosition - crystal.zPosition) <= 128,
                    "bolt at " + bolt.xPosition / 32.0 + "," + bolt.zPosition / 32.0 + " is not the player's locale - "
                            + "this is a background storm strike, not the trident's");
        }

        bot.chat("/weather clear");
        bot.drain(500);
        bot.close();
        checks.done();
    }

    private static EntitySpawnPacket summon(Bot bot, String name, EntitySpawnPacket.EntityType type) throws Exception {
        bot.chat("/summon " + name);
        return bot.await(EntitySpawnPacket.class, p -> p.type == type, 5000);
    }

    /**
     * Trident.onEntityHit calls addWeatherEffect while it is raining, and
     * SummonCommand sets the summoned Spear's shooter to the caller. A spear
     * dropped onto a cow at the player's feet therefore strikes within a second
     * - deterministic, unlike a storm strike, which is one in 100000 per loaded
     * chunk per tick and only over a thunder-capable biome (measured at roughly
     * one a minute here, far too rare to assert on). The spear misses now and
     * then, so this retries.
     */
    private static EntitySpawnPacket strikeLightning(Bot bot, Checks checks) throws Exception {
        bot.chat("/weather clear");
        bot.drain(1000);
        bot.chat("/weather rain");
        bot.drain(2000);

        for (int attempt = 1; attempt <= 4; attempt++) {
            bot.chat("/summon Cow");
            bot.drain(500);
            bot.chat("/summon Spear");
            EntitySpawnPacket bolt = bot.await(EntitySpawnPacket.class,
                    p -> p.type == EntitySpawnPacket.EntityType.LIGHTNING, 6000);
            if (bolt != null) {
                checks.note("trident struck on attempt " + attempt);
                return bolt;
            }
        }

        return null;
    }
}
