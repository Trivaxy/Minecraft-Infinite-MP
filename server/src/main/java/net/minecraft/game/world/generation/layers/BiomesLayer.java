package net.minecraft.game.world.generation.layers;

import java.util.Random;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.util.math.IntCache;

public class BiomesLayer extends GenLayer {
    private final ClimateLayer climate;
    private Random random = new Random();
    private boolean noOcean = false;

    public BiomesLayer(long seed, GenLayer parent, ClimateLayer climate) {
        super(seed);
        this.parent = parent;
        this.climate = climate;
        this.random.setSeed(seed);
    }

    public BiomesLayer(long seed, GenLayer parent, ClimateLayer climate, boolean noOcean) {
        super(seed);
        this.parent = parent;
        this.climate = climate;
        this.random.setSeed(seed);
        this.noOcean = noOcean;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] island = this.parent.getInts(chunkX, chunkZ, areaWidth, areaHeight);
        int[] climate = this.climate.getInts(chunkX, chunkZ, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                this.initChunkSeed(x + chunkX, z + chunkZ);
                int sampleClimate = climate[x + z * areaWidth];
                int sampleIsland = island[x + z * areaWidth];
                if (sampleIsland != 1 && !this.noOcean) {
                    if (sampleIsland == BiomeList.shore.id && sampleClimate >> 2 == Biome.Temperature.COLD.value) {
                        out[x + z * areaWidth] = BiomeList.frozenShore.id;
                    } else if (sampleIsland == BiomeList.ocean.id && sampleClimate >> 2 == Biome.Temperature.COLD.value) {
                        out[x + z * areaWidth] = BiomeList.frozenOcean.id;
                    } else if (sampleIsland == BiomeList.shore.id && sampleClimate >> 2 == Biome.Temperature.WARM.value) {
                        out[x + z * areaWidth] = BiomeList.warmShore.id;
                    } else if (sampleIsland == BiomeList.ocean.id && sampleClimate >> 2 == Biome.Temperature.WARM.value) {
                        out[x + z * areaWidth] = BiomeList.warmOcean.id;
                    } else if (sampleIsland == BiomeList.island.id && sampleClimate >> 2 == Biome.Temperature.COLD.value) {
                        out[x + z * areaWidth] = BiomeList.frozenIsland.id;
                    } else {
                        out[x + z * areaWidth] = island[x + z * areaWidth];
                    }
                } else {
                    Biome randomBiome = BiomeList.climateBiomes[sampleClimate].get(this.nextInt(BiomeList.climateBiomes[sampleClimate].size()));
                    if (randomBiome.id != BiomeList.biomeDefault.id && BiomeList.enabledBiomes[randomBiome.id]) {
                        out[x + z * areaWidth] = randomBiome.id;
                    } else {
                        out[x + z * areaWidth] = BiomeList.biomeDefault.id;
                    }
                }
            }
        }

        return out;
    }
}
