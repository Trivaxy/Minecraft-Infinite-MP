package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class IslandsLayer extends GenLayer {
    public IslandsLayer(long seed, GenLayer parent) {
        super(seed);
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

        for (int xPass = 0; xPass < areaHeight; xPass++) {
            for (int zPass = 0; zPass < areaWidth; zPass++) {
                int center = areas[zPass + xPass * xOff];
                int left = areas[zPass + 2 + xPass * xOff];
                int right = areas[zPass + (xPass + 2) * xOff];
                int front = areas[zPass + 2 + (xPass + 2) * xOff];
                int back = areas[zPass + 1 + (xPass + 1) * xOff];
                this.initChunkSeed(zPass + chunkX, xPass + chunkZ);
                if (back != 0 || center == 0 && left == 0 && right == 0 && front == 0) {
                    if (back == 1 && (center != 1 || left != 1 || right != 1 || front != 1)) {
                        cached[zPass + xPass * areaWidth] = 1 - this.nextInt(5) / 4;
                    } else {
                        cached[zPass + xPass * areaWidth] = back;
                    }
                } else {
                    cached[zPass + xPass * areaWidth] = this.nextInt(3) / 2;
                }
            }
        }

        return cached;
    }
}
