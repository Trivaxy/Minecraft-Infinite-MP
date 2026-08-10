package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class LandLayer extends GenLayer {
    public LandLayer(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] cached = IntCache.getIntCache(areaWidth * areaHeight);

        for (int x = 0; x < areaHeight; x++) {
            for (int z = 0; z < areaWidth; z++) {
                this.initChunkSeed(chunkX + z, chunkZ + x);
                cached[z + x * areaWidth] = this.nextInt(8) == 0 ? 1 : 0;
            }
        }

        if (chunkX > -areaWidth && chunkX <= 0 && chunkZ > -areaHeight && chunkZ <= 0) {
            cached[-chunkX + -chunkZ * areaWidth] = 1;
        }

        return cached;
    }
}
