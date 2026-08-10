package net.minecraft.game.world.biomes.providers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.util.Position;

public class SingleProvider extends BiomeProvider {
    private final Biome biome;
    private final byte temperature;
    private final byte humidity;

    public SingleProvider(Biome biome) {
        this(biome, biome.getTemperatureEnum(), biome.getHumidityEnum());
    }

    public SingleProvider(Biome biome, Biome.Temperature temperature, Biome.Humidity humidity) {
        this.biome = biome;
        this.temperature = temperature.value;
        this.humidity = humidity.value;
    }

    @Override
    public Biome getBiomeAt(int x, int z) {
        return this.biome;
    }

    @Override
    public Biome[] getBiomes(Biome[] biomes, int x, int z, int width, int length) {
        if (biomes == null || biomes.length < width * length) {
            biomes = new Biome[width * length];
        }

        Arrays.fill(biomes, 0, width * length, this.biome);
        return biomes;
    }

    @Override
    public byte[] getTemperatureValues(byte[] values, int x, int z, int width, int length) {
        if (values == null || values.length < width * length) {
            values = new byte[width * length];
        }

        Arrays.fill(values, 0, width * length, this.temperature);
        return values;
    }

    @Override
    public byte[] getHumidityValues(byte[] values, int x, int z, int width, int length) {
        if (values == null || values.length < width * length) {
            values = new byte[width * length];
        }

        Arrays.fill(values, 0, width * length, this.humidity);
        return values;
    }

    @Override
    public Biome[] loadBiomes(Biome[] biomes, int x, int z, int width, int length) {
        if (biomes == null || biomes.length < width * length) {
            biomes = new Biome[width * length];
        }

        Arrays.fill(biomes, 0, width * length, this.biome);
        return biomes;
    }

    @Override
    public Biome[] getBiomeAt(Biome[] biomes, int x, int z, int width, int length, boolean cache) {
        return this.loadBiomes(biomes, x, z, width, length);
    }

    @Override
    public Position findSpawnBiome(int par1, int par2, int par3, List<Biome> spawnBiomes, Random rand) {
        return spawnBiomes.contains(this.biome) ? new Position(par1 - par3 + rand.nextInt(par3 * 2 + 1), 0, par2 - par3 + rand.nextInt(par3 * 2 + 1)) : null;
    }
}
