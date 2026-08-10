package net.minecraft.game.world.biomes;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.game.world.chunk.ChunkUtils;

public class BiomeCache {
    private final BiomeProvider provider;
    private long lastCleanupTime = 0L;
    private final Long2ObjectMap<BiomeCache.Block> cacheMap = new Long2ObjectOpenHashMap<>();
    private final List<BiomeCache.Block> cache = new ArrayList<>();

    public BiomeCache(BiomeProvider provider) {
        this.provider = provider;
    }

    private BiomeCache.Block getBiomeCacheBlock(int x, int z) {
        x >>= 4;
        z >>= 4;
        long coord = ChunkUtils.getPackedCoords(x, z);
        BiomeCache.Block biomecacheblock = this.cacheMap.get(coord);
        if (biomecacheblock == null) {
            biomecacheblock = new BiomeCache.Block(x, z);
            this.cacheMap.put(coord, biomecacheblock);
            this.cache.add(biomecacheblock);
        }

        biomecacheblock.lastAccessTime = System.currentTimeMillis();
        return biomecacheblock;
    }

    public Biome getBiomeAt(int x, int z) {
        return this.getBiomeCacheBlock(x, z).getBiomeAt(x, z);
    }

    public void cleanup() {
        long l = System.currentTimeMillis();
        long l1 = l - this.lastCleanupTime;
        if (l1 > 7500L || l1 < 0L) {
            this.lastCleanupTime = l;

            for (int i = 0; i < this.cache.size(); i++) {
                BiomeCache.Block biomecacheblock = this.cache.get(i);
                long l2 = l - biomecacheblock.lastAccessTime;
                if (l2 > 30000L || l2 < 0L) {
                    this.cache.remove(i--);
                    long l3 = biomecacheblock.xPosition & 4294967295L | (biomecacheblock.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(l3);
                }
            }
        }
    }

    public Biome[] getCachedBiomes(int x, int z) {
        return this.getBiomeCacheBlock(x, z).biomes;
    }

    private class Block {
        public byte[] temperatureValues = new byte[256];
        public byte[] rainfallValues = new byte[256];
        public Biome[] biomes = new Biome[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(int x, int z) {
            this.xPosition = x;
            this.zPosition = z;
            BiomeCache.this.provider.getTemperatureValues(this.temperatureValues, x << 4, z << 4, 16, 16);
            BiomeCache.this.provider.getHumidityValues(this.rainfallValues, x << 4, z << 4, 16, 16);
            BiomeCache.this.provider.getBiomeAt(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        public Biome getBiomeAt(int x, int z) {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}
