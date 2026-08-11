package net.minecraft.game.world.generation.layers;

import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.util.math.IntCache;

public class ShoreLayer extends GenLayer {
    public ShoreLayer(long baseSeed, GenLayer parent) {
        super(baseSeed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int x = chunkX - 1;
        int z = chunkZ - 1;
        int xOff = areaWidth + 2;
        int zOff = areaHeight + 2;
        int[] areas = this.parent.getInts(x, z, xOff, zOff);
        int[] cached = IntCache.getIntCache(areaWidth * areaHeight);

        for (int passX = 0; passX < areaHeight; passX++) {
            for (int passZ = 0; passZ < areaWidth; passZ++) {
                int left = areas[passZ + 1 + (passX + 1 - 1) * (areaWidth + 2)];
                int right = areas[passZ + 1 + 1 + (passX + 1) * (areaWidth + 2)];
                int front = areas[passZ + 1 - 1 + (passX + 1) * (areaWidth + 2)];
                int back = areas[passZ + 1 + (passX + 1 + 1) * (areaWidth + 2)];
                int center = areas[passZ + 1 + (passX + 1) * xOff];
                int offset = 0;
                if (left == 0) {
                    offset++;
                }

                if (right == 0) {
                    offset++;
                }

                if (front == 0) {
                    offset++;
                }

                if (back == 0) {
                    offset++;
                }

                if (center == 0 && offset <= 3) {
                    cached[passZ + passX * areaWidth] = BiomeList.shore.id;
                } else {
                    cached[passZ + passX * areaWidth] = center;
                }
            }
        }

        return cached;
    }
}
