package net.minecraft.game.world.generation.noise;

import java.util.Random;

public class OctaveNoise extends NoiseGenerator {
    private PerlinNoise[] perlinGens;
    private int octaves;

    public OctaveNoise(Random random, int octaves) {
        this.octaves = octaves;
        this.perlinGens = new PerlinNoise[octaves];

        for (int octave = 0; octave < octaves; octave++) {
            this.perlinGens[octave] = new PerlinNoise(random);
        }
    }

    public double generateNoise2D(double x, double z) {
        double min = 0.0;
        double max = 1.0;

        for (int oct = 0; oct < this.octaves; oct++) {
            min += this.perlinGens[oct].generateNoise(x * max, z * max) / max;
            max /= 2.0;
        }

        return min;
    }

    public double generateNoise3D(double x, double y, double z) {
        double min = 0.0;
        double max = 1.0;

        for (int oct = 0; oct < this.octaves; oct++) {
            min += this.perlinGens[oct].generateNoise(x * max, y * max, z * max) / max;
            max /= 2.0;
        }

        return min;
    }

    @Override
    public final double generateNoise(double x, double z) {
        double min = 0.0;
        double max = 1.0;

        for (int oct = 0; oct < this.octaves; oct++) {
            min += this.perlinGens[oct].generateNoise(x / max, z / max) * max;
            max *= 2.0;
        }

        return min;
    }

    public double[] generateNoiseOctaves(
        double[] noiseArray, int startX, int startY, int startZ, int endX, int endY, int endZ, double scaleX, double scaleY, double scaleZ
    ) {
        if (noiseArray == null) {
            noiseArray = new double[endX * endY * endZ];
        } else {
            for (int index = 0; index < noiseArray.length; index++) {
                noiseArray[index] = 0.0;
            }
        }

        double max = 1.0;

        for (int oct = 0; oct < this.octaves; oct++) {
            this.perlinGens[oct].generateNoise(noiseArray, startX, startY, startZ, endX, endY, endZ, scaleX * max, scaleY * max, scaleZ * max, max);
            max /= 2.0;
        }

        return noiseArray;
    }

    public double[] generateNoiseOctaves(
        double[] noiseArray, double startX, double startY, double startZ, int endX, int endY, int endZ, double scaleX, double scaleY, double scaleZ
    ) {
        if (noiseArray == null) {
            noiseArray = new double[endX * endY * endZ];
        } else {
            for (int index = 0; index < noiseArray.length; index++) {
                noiseArray[index] = 0.0;
            }
        }

        double max = 1.0;

        for (int oct = 0; oct < this.octaves; oct++) {
            this.perlinGens[oct].generateNoise(noiseArray, startX, startY, startZ, endX, endY, endZ, scaleX * max, scaleY * max, scaleZ * max, max);
            max /= 2.0;
        }

        return noiseArray;
    }

    public double[] generateNoise(double[] noise, int startX, int startY, int startZ, int samples, double scaleX, double scaleY, double scaleZ) {
        return this.generateNoiseOctaves(noise, startX, 10.0, startY, startZ, 1, samples, scaleX, 1.0, scaleY);
    }
}
