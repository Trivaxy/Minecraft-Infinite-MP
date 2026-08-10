package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class FuzzyZoomLayer extends GenLayer {
    public FuzzyZoomLayer(long seed, GenLayer parent) {
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
        int offset = xOff << 1;

        for (int zPass = 0; zPass < zOff - 1; zPass++) {
            int i = zPass << 1;
            int index = i * offset;
            int option1 = areas[zPass * xOff];
            int option2 = areas[(zPass + 1) * xOff];

            for (int xPass = 0; xPass < xOff - 1; xPass++) {
                this.initChunkSeed((long)xPass + x << 1, (long)zPass + z << 1);
                int ai = areas[xPass + 1 + zPass * xOff];
                int ai1 = areas[xPass + 1 + (zPass + 1) * xOff];
                cached[index] = option1;
                cached[index++ + offset] = this.choose(option1, option2);
                cached[index] = this.choose(option1, ai);
                cached[index++ + offset] = this.modeOrRandom(option1, ai, option2, ai1);
                option1 = ai;
                option2 = ai1;
            }
        }

        int[] cacheFinal = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaHeight; i++) {
            System.arraycopy(cached, (i + (chunkZ & 1)) * (xOff << 1) + (chunkX & 1), cacheFinal, i * areaWidth, areaWidth);
        }

        return cacheFinal;
    }

    protected int choose(int x, int z) {
        return this.nextInt(2) == 0 ? x : z;
    }

    protected int modeOrRandom(int x1, int z1, int x2, int z2) {
        int chance = this.nextInt(4);
        return chance == 0 ? x1 : (chance == 1 ? z1 : (chance == 2 ? x2 : z2));
    }
}
