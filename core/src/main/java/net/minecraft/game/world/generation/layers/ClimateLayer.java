package net.minecraft.game.world.generation.layers;

import java.util.Random;
import net.minecraft.game.world.generation.noise.OctaveSimplexNoise;
import net.minecraft.util.math.IntCache;

public class ClimateLayer extends GenLayer {
    private OctaveSimplexNoise temperatureNoise;
    private OctaveSimplexNoise humidityNoise;

    public ClimateLayer(long baseSeed) {
        super(baseSeed);
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);
        double[] temp = new double[areaWidth * areaHeight];
        double[] rainfall = new double[areaWidth * areaHeight];
        temp = this.temperatureNoise.generateNoiseOctaves(temp, chunkX, chunkZ, areaWidth, areaHeight, 0.55, 0.55, 0.6);
        rainfall = this.humidityNoise.generateNoiseOctaves(rainfall, chunkX, chunkZ, areaWidth, areaHeight, 0.55, 0.55, 0.6);

        for (int x = 0; x < areaHeight; x++) {
            for (int z = 0; z < areaWidth; z++) {
                double sampleTemp = temp[x + z * areaHeight];
                double sampleRainfall = rainfall[x + z * areaHeight];
                sampleTemp = Math.pow(Math.abs(sampleTemp / 4.5), 1.25) * Math.signum(sampleTemp) + 0.65;
                sampleRainfall = Math.pow(Math.abs(sampleRainfall / 4.5), 1.25) * Math.signum(sampleRainfall) + 0.65;
                if (sampleTemp > 1.0) {
                    sampleTemp = 1.0;
                }

                if (sampleRainfall > 1.0) {
                    sampleRainfall = 1.0;
                }

                if (sampleTemp < 0.0) {
                    sampleTemp = 0.0;
                }

                if (sampleRainfall < 0.0) {
                    sampleRainfall = 0.0;
                }

                byte climate = (byte)(((byte)(sampleTemp * 3.0) & 3) << 2 | (byte)(sampleRainfall * 3.0) & 3);
                out[z + x * areaWidth] = climate;
            }
        }

        return out;
    }

    @Override
    public void initWorldSeed(long worldSeed) {
        Random rand = new Random(worldSeed);
        this.temperatureNoise = new OctaveSimplexNoise(rand, 4);
        this.humidityNoise = new OctaveSimplexNoise(rand, 4);
        super.initWorldSeed(worldSeed);
    }
}
