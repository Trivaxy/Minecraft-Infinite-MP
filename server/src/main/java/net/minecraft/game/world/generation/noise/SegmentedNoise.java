package net.minecraft.game.world.generation.noise;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.random.SkippableRandom;

public class SegmentedNoise {
    private final SimplexNoise[] noiseLevels;
    private final double coef2;
    private final double coef1;

    public SegmentedNoise(SkippableRandom rand, IntList octaves) {
        this(rand, new IntRBTreeSet(octaves));
    }

    private SegmentedNoise(SkippableRandom rand, IntSortedSet octaves) {
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }

        int first = -octaves.firstInt();
        int last = octaves.lastInt();
        int total = first + last + 1;
        if (total < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }

        SimplexNoise simplexNoise = new SimplexNoise(rand);
        this.noiseLevels = new SimplexNoise[total];
        if (last >= 0 && last < total && octaves.contains(0)) {
            this.noiseLevels[last] = simplexNoise;
        }

        for (int level = last + 1; level < total; level++) {
            if (level >= 0 && octaves.contains(last - level)) {
                this.noiseLevels[level] = new SimplexNoise(rand);
            } else {
                rand.skip(262);
            }
        }

        if (last > 0) {
            long seed = (long)(simplexNoise.generateNoise(simplexNoise.xCoord, simplexNoise.yCoord, simplexNoise.zCoord) * 9.223372E18F);
            SkippableRandom sharedseedrandom = new SkippableRandom(seed);

            for (int level = last - 1; level >= 0; level--) {
                if (level < total && octaves.contains(last - level)) {
                    this.noiseLevels[level] = new SimplexNoise(sharedseedrandom);
                } else {
                    sharedseedrandom.skip(262);
                }
            }
        }

        this.coef1 = Math.pow(2.0, last);
        this.coef2 = 1.0 / (Math.pow(2.0, total) - 1.0);
    }

    public double noiseAt(double x, double y, boolean useNoiseOffsets) {
        double noiseValue = 0.0;
        double c1 = this.coef1;
        double c2 = this.coef2;

        for (SimplexNoise NoiseGeneratorSimplexOcean : this.noiseLevels) {
            if (NoiseGeneratorSimplexOcean != null) {
                noiseValue += NoiseGeneratorSimplexOcean.getValue(
                        x * c1 + (useNoiseOffsets ? NoiseGeneratorSimplexOcean.xCoord : 0.0),
                        y * c1 + (useNoiseOffsets ? NoiseGeneratorSimplexOcean.yCoord : 0.0)
                    )
                    * c2;
            }

            c1 /= 2.0;
            c2 *= 2.0;
        }

        return noiseValue;
    }
}
