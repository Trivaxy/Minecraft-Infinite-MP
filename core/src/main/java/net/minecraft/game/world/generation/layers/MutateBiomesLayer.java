package net.minecraft.game.world.generation.layers;

import java.util.List;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.util.math.IntCache;

public class MutateBiomesLayer extends GenLayer {
    private final ClimateLayer climate;

    public MutateBiomesLayer(long seed, GenLayer parent, ClimateLayer climate) {
        super(seed);
        this.parent = parent;
        this.climate = climate;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] area = this.parent.getInts(chunkX - 1, chunkZ - 1, areaWidth + 2, areaHeight + 2);
        int[] climate = this.climate.getInts(chunkX, chunkZ, areaWidth, areaHeight);
        int[] cachedBiome = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                this.initChunkSeed(x + chunkX, z + chunkZ);
                int biomeID = area[x + 1 + (z + 1) * (areaWidth + 2)];
                int sampleClimate = climate[x + z * areaWidth];
                if (this.nextInt(4) == 0) {
                    List<Biome> variants = BiomeList.biomes[biomeID].variants;
                    if (biomeID == BiomeList.ocean.id) {
                        switch (this.nextInt(2)) {
                            case 0:
                                biomeID = BiomeList.crag.id;
                                break;
                            case 1:
                                if (sampleClimate >> 2 == Biome.Temperature.COLD.value) {
                                    biomeID = BiomeList.winterFloating.id;
                                } else if (sampleClimate >> 2 == Biome.Temperature.HOT.value) {
                                    biomeID = BiomeList.autumnFloating.id;
                                } else if (sampleClimate >> 2 == Biome.Temperature.WARM.value) {
                                    biomeID = BiomeList.summerFloating.id;
                                } else {
                                    biomeID = BiomeList.springFloating.id;
                                }
                        }
                    } else if (biomeID == BiomeList.frozenOcean.id) {
                        biomeID = BiomeList.winterFloating.id;
                    } else if (biomeID == BiomeList.warmOcean.id) {
                        biomeID = BiomeList.summerFloating.id;
                    } else if (BiomeList.biomes[biomeID].climateBiome && !variants.isEmpty()) {
                        biomeID = variants.get(this.nextInt(variants.size())).id;
                    }
                }

                cachedBiome[x + z * areaWidth] = biomeID;
            }
        }

        return cachedBiome;
    }
}
