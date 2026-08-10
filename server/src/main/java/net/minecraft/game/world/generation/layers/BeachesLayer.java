package net.minecraft.game.world.generation.layers;

import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.warm.WarmBiome;
import net.minecraft.util.math.IntCache;

public class BeachesLayer extends GenLayer {
    public BeachesLayer(long seed, GenLayer layer) {
        super(seed);
        this.parent = layer;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] adjacent = this.parent.getInts(chunkX - 2, chunkZ - 2, areaWidth + 4, areaHeight + 4);
        int[] biome = IntCache.getIntCache(areaWidth * areaHeight);

        for (int x = 0; x < areaHeight; x++) {
            for (int z = 0; z < areaWidth; z++) {
                this.initChunkSeed(z + chunkX >> 2, x + chunkZ >> 2);
                int adjacentBiome = adjacent[z + 2 + (x + 2) * (areaWidth + 4)];
                Biome biomeCheck = BiomeList.biomes[adjacentBiome];
                if (biomeCheck.hasBeaches) {
                    int right = adjacent[z + 2 + (x + 2 - 2) * (areaWidth + 4)];
                    int left = adjacent[z + 2 + 2 + (x + 2) * (areaWidth + 4)];
                    int front = adjacent[z + 2 - 2 + (x + 2) * (areaWidth + 4)];
                    int back = adjacent[z + 2 + (x + 2 + 2) * (areaWidth + 4)];
                    if (right != BiomeList.shore.id
                        && left != BiomeList.shore.id
                        && front != BiomeList.shore.id
                        && back != BiomeList.shore.id
                        && right != BiomeList.frozenShore.id
                        && left != BiomeList.frozenShore.id
                        && front != BiomeList.frozenShore.id
                        && back != BiomeList.frozenShore.id
                        && right != BiomeList.warmShore.id
                        && left != BiomeList.warmShore.id
                        && front != BiomeList.warmShore.id
                        && back != BiomeList.warmShore.id) {
                        biome[z + x * areaWidth] = adjacentBiome;
                    } else if (this.nextInt(10) == 0) {
                        biome[z + x * areaWidth] = BiomeList.stoneBeach.id;
                    } else if (BiomeList.biomes[right].getTemperatureEnum() == Biome.Temperature.COLD
                        || BiomeList.biomes[left].getTemperatureEnum() == Biome.Temperature.COLD
                        || BiomeList.biomes[front].getTemperatureEnum() == Biome.Temperature.COLD
                        || BiomeList.biomes[back].getTemperatureEnum() == Biome.Temperature.COLD) {
                        biome[z + x * areaWidth] = BiomeList.snowyBeach.id;
                    } else if (BiomeList.biomes[right].getTemperatureEnum() != Biome.Temperature.HOT
                        && BiomeList.biomes[left].getTemperatureEnum() != Biome.Temperature.HOT
                        && BiomeList.biomes[front].getTemperatureEnum() != Biome.Temperature.HOT
                        && BiomeList.biomes[back].getTemperatureEnum() != Biome.Temperature.HOT) {
                        if (!(BiomeList.biomes[right] instanceof WarmBiome)
                            && !(BiomeList.biomes[left] instanceof WarmBiome)
                            && !(BiomeList.biomes[front] instanceof WarmBiome)
                            && !(BiomeList.biomes[back] instanceof WarmBiome)) {
                            biome[z + x * areaWidth] = BiomeList.beach.id;
                        } else {
                            biome[z + x * areaWidth] = BiomeList.lushBeach.id;
                        }
                    } else if (right != BiomeList.volcano.id && left != BiomeList.volcano.id && front != BiomeList.volcano.id && back != BiomeList.volcano.id) {
                        biome[z + x * areaWidth] = BiomeList.duneBeach.id;
                    } else {
                        biome[z + x * areaWidth] = BiomeList.stoneBeach.id;
                    }
                } else {
                    biome[z + x * areaWidth] = adjacentBiome;
                }
            }
        }

        return biome;
    }
}
