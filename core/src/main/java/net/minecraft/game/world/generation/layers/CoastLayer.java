package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class CoastLayer extends GenLayer {
    public CoastLayer(long l, GenLayer parent) {
        super(l);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] ai = this.parent.getInts(chunkX - 1, chunkZ - 1, areaWidth + 2, areaHeight + 2);
        int[] ai1 = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                int k = ai[x + 1 + (z + 1) * (areaWidth + 2)];
                if (k != 0) {
                    int l = ai[x + 1 + (z + 1 - 1) * (areaWidth + 2)];
                    int k1 = ai[x + 1 + 1 + (z + 1) * (areaWidth + 2)];
                    int j2 = ai[x + 1 - 1 + (z + 1) * (areaWidth + 2)];
                    int i3 = ai[x + 1 + (z + 1 + 1) * (areaWidth + 2)];
                    if (l == k && k1 == k && j2 == k && i3 == k) {
                        ai1[x + z * areaWidth] = k;
                    } else {
                        ai1[x + z * areaWidth] = l + k1 + j2 + i3 >> 2;
                    }
                } else {
                    ai1[x + z * areaWidth] = k;
                }
            }
        }

        return ai1;
    }
}
