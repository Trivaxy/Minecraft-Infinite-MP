package net.minecraft.game.world.generation.layers;

import java.util.Random;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.util.math.IntCache;

public class SkyLayer extends GenLayer {
    private Random random = new Random();
    private Biome[] biomes = new Biome[]{
        BiomeList.sky,
        BiomeList.skyGold,
        BiomeList.skySwampFrozen,
        BiomeList.skyTaigaFrozen,
        BiomeList.skyPlains,
        BiomeList.skySwamp,
        BiomeList.skyTaiga,
        BiomeList.skyMountain
    };

    public SkyLayer(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        this.random.setSeed(seed);
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] biome = IntCache.getIntCache(areaWidth * areaHeight);

        for (int x = 0; x < areaHeight; x++) {
            for (int z = 0; z < areaWidth; z++) {
                this.initChunkSeed(z + chunkX, x + chunkZ);
                biome[z + x * areaWidth] = this.biomes[this.nextInt(this.biomes.length)].id;
            }
        }

        return biome;
    }
}
