package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class ZoomLayer extends GenLayer {
    public ZoomLayer(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int x = chunkX >> 1;
        int z = chunkZ >> 1;
        int xOff = (areaWidth >> 1) + 3;
        int zOff = (areaHeight >> 1) + 3;
        int[] areas = this.parent.getInts(x, z, xOff, zOff);
        int[] cached = IntCache.getIntCache(xOff * 2 * zOff * 2);
        int zoomX = xOff << 1;

        for (int passZ = 0; passZ < zOff - 1; passZ++) {
            int zoomZ = passZ << 1;
            int zoom = zoomZ * zoomX;
            int topLeft = areas[passZ * xOff];
            int bottomLeft = areas[(passZ + 1) * xOff];

            for (int passX = 0; passX < xOff - 1; passX++) {
                this.initChunkSeed((long)passX + x << 1, (long)passZ + z << 1);
                int topRight = areas[passX + 1 + passZ * xOff];
                int bottomRight = areas[passX + 1 + (passZ + 1) * xOff];
                cached[zoom] = topLeft;
                cached[zoom++ + zoomX] = this.choose(topLeft, bottomLeft);
                cached[zoom] = this.choose(topLeft, topRight);
                cached[zoom++ + zoomX] = this.modeOrRandom(topLeft, topRight, bottomLeft, bottomRight);
                topLeft = topRight;
                bottomLeft = bottomRight;
            }
        }

        int[] zoomValue = IntCache.getIntCache(areaWidth * areaHeight);

        for (int index = 0; index < areaHeight; index++) {
            System.arraycopy(cached, (index + (chunkZ & 1)) * (xOff << 1) + (chunkX & 1), zoomValue, index * areaWidth, areaWidth);
        }

        return zoomValue;
    }

    protected int choose(int x, int z) {
        return this.nextInt(2) == 0 ? x : z;
    }

    protected int modeOrRandom(int x1, int z1, int x2, int z2) {
        if (z1 == x2 && x2 == z2) {
            return z1;
        }

        if (x1 == z1 && x1 == x2) {
            return x1;
        }

        if (x1 == z1 && x1 == z2) {
            return x1;
        }

        if (x1 == x2 && x1 == z2) {
            return x1;
        }

        if (x1 == z1 && x2 != z2) {
            return x1;
        }

        if (x1 == x2 && z1 != z2) {
            return x1;
        }

        if (x1 == z2 && z1 != x2) {
            return x1;
        }

        if (z1 == x1 && x2 != z2) {
            return z1;
        }

        if (z1 == x2 && x1 != z2) {
            return z1;
        }

        if (z1 == z2 && x1 != x2) {
            return z1;
        }

        if (x2 == x1 && z1 != z2) {
            return x2;
        }

        if (x2 == z1 && x1 != z2) {
            return x2;
        }

        if (x2 == z2 && x1 != z1) {
            return x2;
        }

        if (z2 == x1 && z1 != x2) {
            return x2;
        }

        if (z2 == z1 && x1 != x2) {
            return x2;
        }

        if (z2 == x2 && x1 != z1) {
            return x2;
        }

        int chance = this.nextInt(4);
        return chance == 0 ? x1 : (chance == 1 ? z1 : (chance == 2 ? x2 : z2));
    }

    public static GenLayer magnify(long seed, GenLayer generationLayer, int count) {
        GenLayer layer = generationLayer;

        for (int index = 0; index < count; index++) {
            layer = new ZoomLayer(seed + index, layer);
        }

        return layer;
    }
}
