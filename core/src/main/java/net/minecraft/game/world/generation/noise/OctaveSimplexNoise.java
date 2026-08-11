package net.minecraft.game.world.generation.noise;

import java.util.Random;

public class OctaveSimplexNoise extends NoiseGenerator {
    private SimplexNoise[] simplexLayers;
    private int iterations;

    public OctaveSimplexNoise(Random rand, int iterations) {
        this.iterations = iterations;
        this.simplexLayers = new SimplexNoise[iterations];

        for (int current = 0; current < iterations; current++) {
            this.simplexLayers[current] = new SimplexNoise(rand);
        }
    }

    public double[] generateNoiseOctaves(double[] noise, double startX, double startY, int startZ, int endX, double endY, double endZ, double scale) {
        return this.generateNoiseOctaves(noise, startX, startY, startZ, endX, endY, endZ, scale, 0.5);
    }

    public double[] generateNoiseOctaves(
        double[] noise, double startX, double startZ, int samplesX, int samplesZ, double endX, double endZ, double depth, double scale
    ) {
        endX /= 1.5;
        endZ /= 1.5;
        if (noise != null && noise.length >= samplesX * samplesZ) {
            for (int index = 0; index < noise.length; index++) {
                noise[index] = 0.0;
            }
        } else {
            noise = new double[samplesX * samplesZ];
        }

        double max = 1.0;
        double min = 1.0;

        for (int layer = 0; layer < this.iterations; layer++) {
            this.simplexLayers[layer].generateNoise(noise, startX, startZ, samplesX, samplesZ, endX * min, endZ * min, 0.55 / max);
            min *= depth;
            max *= scale;
        }

        return noise;
    }

    @Override
    public double generateNoise(double x, double z) {
        return 0.0;
    }
}
