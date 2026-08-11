package net.minecraft.util.math;

import java.util.ArrayList;
import java.util.List;

public class IntCache {
    private static int intCacheSize = 256;
    private static final List<int[]> freeSmallArrays = new ArrayList<>();
    private static final List<int[]> inUseSmallArrays = new ArrayList<>();
    private static final List<int[]> freeLargeArrays = new ArrayList<>();
    private static final List<int[]> inUseLargeArrays = new ArrayList<>();

    public static int[] getIntCache(int cacheSize) {
        if (cacheSize <= 256) {
            int[] cache;
            if (freeSmallArrays.isEmpty()) {
                cache = new int[256];
            } else {
                cache = freeSmallArrays.remove(freeSmallArrays.size() - 1);
            }

            inUseSmallArrays.add(cache);
            return cache;
        } else {
            int[] cache;
            if (cacheSize > intCacheSize) {
                intCacheSize = cacheSize;
                freeLargeArrays.clear();
                inUseLargeArrays.clear();
                cache = new int[intCacheSize];
            } else if (freeLargeArrays.isEmpty()) {
                cache = new int[intCacheSize];
            } else {
                cache = freeLargeArrays.remove(freeLargeArrays.size() - 1);
            }

            inUseLargeArrays.add(cache);
            return cache;
        }
    }

    public static void resetIntCache() {
        if (!freeLargeArrays.isEmpty()) {
            freeLargeArrays.remove(freeLargeArrays.size() - 1);
        }

        if (!freeSmallArrays.isEmpty()) {
            freeSmallArrays.remove(freeSmallArrays.size() - 1);
        }

        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }
}
