package net.minecraft.game.world.generation.noise;

public final class DistortNoise extends NoiseGenerator {
    private NoiseGenerator source;
    private NoiseGenerator distort;

    public DistortNoise(NoiseGenerator src, NoiseGenerator distort) {
        this.source = src;
        this.distort = distort;
    }

    @Override
    public final double generateNoise(double x, double z) {
        return this.source.generateNoise(x + this.distort.generateNoise(x, z), z);
    }
}
