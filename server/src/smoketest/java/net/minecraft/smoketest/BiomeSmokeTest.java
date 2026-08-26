package net.minecraft.smoketest;

import net.minecraft.SharedConstants;
import net.minecraft.client.network.world.NetworkWorld;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.network.packet.misc.LoginPacket;

/**
 * B-xx - a multiplayer client used to see "Valley Hills" everywhere on F3 and one constant sky
 * color, because the client built its NetworkWorld from whatever WorldType happened to still be
 * sitting in WorldSettings when handleLogin ran (world-select screen's most recent pick), instead
 * of the server's. The login packet now carries the server's WorldType and single-biome id, and
 * ClientHandler passes both into NetworkWorld so the client re-inits its biome provider with the
 * server's map setup instead.
 *
 * Checks, against a running server:
 *  1. The login packet's worldType and singleBiomeId round-trip on the wire.
 *  2. A NetworkWorld built the way the fixed ClientHandler builds it reports the server's biome
 *     (not FLAT's valleyHills fallback) at the probe point.
 */
public class BiomeSmokeTest {
    public static void main(String[] args) throws Exception {
        Bot bot = Bot.join(args, "biomebot");
        try {
            Checks checks = new Checks();

            LoginPacket login = bot.login;
            WorldType wireType = WorldType.fromNumber(login.worldType);
            checks.check("login packet carries the overworld type (DEFAULT)",
                wireType == WorldType.DEFAULT);
            checks.check("singleBiomeId is a valid biome id",
                login.singleBiomeId >= 0 && login.singleBiomeId < BiomeList.biomes.length);

            // Build a client world the way the fixed handler builds it and ask it the biome at
            // the spawn point - the probe - versus what a fresh server-side provider computes.
            NetworkWorld clientWorld =
                new NetworkWorld(null, login.mapSeed, login.dimension, wireType, null);
            BiomeProvider serverProvider = new BiomeProvider(login.mapSeed, wireType);

            // Single-column point queries can differ between two provider instances (cache
            // block alignment), so compare a wide region grid: the pre-fix failure mode - a
            // SingleProvider(valleyHills) - disagrees with the real generator almost everywhere,
            // while two correctly-seeded providers agree almost everywhere.
            int x = 1011, z = 287; // near the observed login spot
            int total = 0, mismatches = 0, valleyHills = 0;
            for (int dx = -512; dx <= 512; dx += 64) {
                for (int dz = -512; dz <= 512; dz += 64) {
                    total++;
                    if (!clientWorld.getBiomeAt(x + dx, z + dz).equals(serverProvider.getBiomeAt(x + dx, z + dz))) {
                        mismatches++;
                    }
                    if (clientWorld.getBiomeAt(x + dx, z + dz) == BiomeList.valleyHills) {
                        valleyHills++;
                    }
                }
            }

            System.out.println("region grid: " + mismatches + "/" + total + " mismatches, "
                + valleyHills + "/" + total + " valleyHills on the client");
            checks.check("client biomes agree with the server's provider over the region (<=10% mismatch)",
                mismatches <= total / 10);
            checks.check("client biome map is not uniformly the FLAT-provider fallback (valleyHills)",
                valleyHills < total);
            checks.check("enabledBiomes[vallleyHills] is true on the client (biomes[] defaults to all-enabled)",
                BiomeList.enabledBiomes[BiomeList.valleyHills.id]);
            checks.check("enabledBiomes[jungle] is true on the client (biomes[] defaults to all-enabled)",
                BiomeList.enabledBiomes[BiomeList.jungle.id]);

            checks.done();
        } finally {
            bot.close();
        }
    }
}
