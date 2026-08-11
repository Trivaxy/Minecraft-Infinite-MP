package net.minecraft.game.world.generation.layers;

import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.util.math.IntCache;

public class TropicalIslandsLayer extends IslandsLayer {
    public TropicalIslandsLayer(long seed, GenLayer genLayer) {
        super(seed, genLayer);
        this.parent = genLayer;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int chx = chunkX - 1;
        int chz = chunkZ - 1;
        int xOff = areaWidth + 2;
        int zOff = areaHeight + 2;
        int[] areas = this.parent.getInts(chx, chz, xOff, zOff);
        int[] cached = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                int center = areas[x + z * xOff];
                int left = areas[x + 2 + z * xOff];
                int right = areas[x + (z + 2) * xOff];
                int front = areas[x + 2 + (z + 2) * xOff];
                int back = areas[x + 1 + (z + 1) * xOff];
                this.initChunkSeed(x + chunkX, z + chunkZ);
                if (back == 0 && center == 0 && left == 0 && right == 0 && front == 0 && this.nextInt(32) == 0) {
                    cached[x + z * areaWidth] = BiomeList.island.id;
                } else {
                    cached[x + z * areaWidth] = back;
                }
            }
        }

        return cached;
    }
}
