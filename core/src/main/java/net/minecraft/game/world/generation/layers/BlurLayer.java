package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class BlurLayer extends GenLayer {
    public BlurLayer(GenLayer genLayer) {
        super(0L);
        this.parent = genLayer;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] source = this.parent.getInts(chunkX - 1, chunkZ - 1, areaWidth + 2, areaHeight + 2);
        int[] dest = IntCache.getIntCache(areaWidth * areaHeight);

        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                int points = 0;
                int tempSum = 0;
                int humidSum = 0;

                for (int zz = z - 1; zz <= z + 1; zz++) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (zz >= -1 && zz <= areaWidth && xx >= -1 && xx <= areaHeight) {
                            int i = source[(zz + 1) * (areaWidth + 2) + xx + 1];
                            tempSum += i >> 2 & 3;
                            humidSum += i & 3;
                            points++;
                        }
                    }
                }

                if (points > 0) {
                    tempSum /= points;
                    humidSum /= points;
                    dest[z * areaWidth + x] = tempSum << 2 | humidSum;
                } else {
                    dest[z * areaWidth + x] = source[(z + 1) * (areaWidth + 2) + x + 1];
                }
            }
        }

        return dest;
    }
}
