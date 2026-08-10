package net.minecraft.game.world.generation.layers;

import java.util.Arrays;
import net.minecraft.util.math.IntCache;

public class OceanLayer extends GenLayer {
    public OceanLayer(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] cached = IntCache.getIntCache(areaWidth * areaHeight);
        Arrays.fill(cached, 0);
        return cached;
    }
}
