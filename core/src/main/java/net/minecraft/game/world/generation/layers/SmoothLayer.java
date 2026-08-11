package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class SmoothLayer extends GenLayer {
    public SmoothLayer(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
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
                int left = areas[passZ + (passX + 1) * xOff];
                int right = areas[passZ + 2 + (passX + 1) * xOff];
                int front = areas[passZ + 1 + passX * xOff];
                int back = areas[passZ + 1 + (passX + 2) * xOff];
                int center = areas[passZ + 1 + (passX + 1) * xOff];
                if (left == right && front == back) {
                    this.initChunkSeed(passZ + chunkX, passX + chunkZ);
                    if (this.nextInt(2) == 0) {
                        center = left;
                    } else {
                        center = front;
                    }
                } else {
                    if (left == right) {
                        center = left;
                    }

                    if (front == back) {
                        center = front;
                    }
                }

                cached[passZ + passX * areaWidth] = center;
            }
        }

        return cached;
    }
}
