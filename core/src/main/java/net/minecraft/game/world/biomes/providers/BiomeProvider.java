package net.minecraft.game.world.biomes.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeCache;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.layers.GenLayer;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.math.IntCache;

public class BiomeProvider {
    protected GenLayer genLayer;
    protected GenLayer biomeLayer;
    protected final BiomeCache biomeCache = new BiomeCache(this);
    protected final List<Biome> spawnBiomes = new ArrayList<>();

    protected BiomeProvider() {
        this.spawnBiomes.add(BiomeList.beach);
        this.spawnBiomes.add(BiomeList.lushBeach);
        this.spawnBiomes.add(BiomeList.snowyBeach);
        this.spawnBiomes.add(BiomeList.duneBeach);
        this.spawnBiomes.add(BiomeList.stoneBeach);
        this.spawnBiomes.add(BiomeList.valleyHills);
        this.spawnBiomes.add(BiomeList.woodlandHills);
        this.spawnBiomes.add(BiomeList.snowyHills);
        this.spawnBiomes.add(BiomeList.snowyWoodland);
    }

    public BiomeProvider(long seed, WorldType type) {
        this();
        GenLayer[] initGenLayers = GenLayer.populateBiomes(seed, type);
        this.genLayer = initGenLayers[0];
        this.biomeLayer = initGenLayers[1];
    }

    public BiomeProvider(World world) {
        this(world.getSeed(), world.currDim.type);
    }

    public List<Biome> getSpawnBiomes() {
        return this.spawnBiomes;
    }

    public Biome getBiomeAt(int x, int z) {
        return this.biomeCache.getBiomeAt(x, z);
    }

    public byte[] getHumidityValues(byte[] values, int x, int z, int width, int length) {
        IntCache.resetIntCache();
        if (values == null || values.length < width * length) {
            values = new byte[width * length];
        }

        int[] ai = this.biomeLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; i++) {
            byte f = BiomeList.biomes[ai[i]].getRainfall();
            values[i] = f;
        }

        return values;
    }

    public byte[] getTemperatureValues(byte[] values, int x, int z, int width, int length) {
        IntCache.resetIntCache();
        if (values == null || values.length < width * length) {
            values = new byte[width * length];
        }

        int[] ai = this.biomeLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; i++) {
            byte f = BiomeList.biomes[ai[i]].getTemperature();
            values[i] = f;
        }

        return values;
    }

    public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length) {
        IntCache.resetIntCache();
        if (biomes == null || biomes.length < width * length) {
            biomes = new Biome[width * length];
        }

        int[] ai = this.genLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; i++) {
            biomes[i] = BiomeList.biomes[ai[i]];
        }

        return biomes;
    }

    public Biome[] loadBiomes(Biome[] biomes, int x, int z, int width, int length) {
        return this.getBiomeAt(biomes, x, z, width, length, true);
    }

    public Biome[] getBiomeAt(Biome[] biomes, int x, int z, int width, int length, boolean cache) {
        IntCache.resetIntCache();
        if (biomes == null || biomes.length < width * length) {
            biomes = new Biome[width * length];
        }

        if (cache && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
            Biome[] cachedBiomes = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(cachedBiomes, 0, biomes, 0, width * length);
            return biomes;
        }

        int[] ai = this.biomeLayer.getInts(x, z, width, length);

        for (int i = 0; i < width * length; i++) {
            biomes[i] = BiomeList.biomes[ai[i]];
        }

        return biomes;
    }

    public Position findSpawnBiome(int x, int z, int radius, List<Biome> spawnBiomes, Random rand) {
        int xNeg = x - radius >> 2;
        int zNeg = z - radius >> 2;
        int xPos = x + radius >> 2;
        int zPos = z + radius >> 2;
        int xOff = xPos - xNeg + 1;
        int zOff = zPos - zNeg + 1;
        int[] ai = this.genLayer.getInts(xNeg, zNeg, xOff, zOff);
        Position pos = null;
        int attempts = 0;

        for (int index = 0; index < ai.length; index++) {
            int posX = xNeg + index % xOff << 2;
            int posZ = zNeg + index / xOff << 2;
            Biome biome = BiomeList.biomes[ai[index]];
            if (spawnBiomes.contains(biome) && (pos == null || rand.nextInt(attempts + 1) == 0)) {
                pos = new Position(posX, 0, posZ);
                attempts++;
            }
        }

        return pos;
    }

    public Position findBiomeAt(int x, int z, int radius, Biome biome) {
        int xNeg = x - radius >> 2;
        int zNeg = z - radius >> 2;
        int xPos = x + radius >> 2;
        int zPos = z + radius >> 2;
        int xOff = xPos - xNeg + 1;
        int zOff = zPos - zNeg + 1;
        int[] ai = this.genLayer.getInts(xNeg, zNeg, xOff, zOff);
        Position pos = null;

        for (int index = 0; index < ai.length; index++) {
            int posX = xNeg + index % xOff << 2;
            int posZ = zNeg + index / xOff << 2;
            Biome biom = BiomeList.biomes[ai[index]];
            if (biom == biome && pos == null) {
                pos = new Position(posX, 0, posZ);
            }
        }

        return pos;
    }

    public void clearCache() {
        this.biomeCache.cleanup();
    }
}
