package net.minecraft.game.world.generation.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class PerlinNoise extends NoiseGenerator {
    public double xCoord;
    public double yCoord;
    public double zCoord;
    private final int[] permutations = new int[512];

    public PerlinNoise() {
        this(new Random());
    }

    public PerlinNoise(Random random) {
        this.xCoord = random.nextDouble() * 256.0;
        this.yCoord = random.nextDouble() * 256.0;
        this.zCoord = random.nextDouble() * 256.0;
        int height = 0;

        while (height < 256) {
            this.permutations[height] = height++;
        }

        for (int var5 = 0; var5 < 256; var5++) {
            int level = random.nextInt(256 - var5) + var5;
            int perm = this.permutations[var5];
            this.permutations[var5] = this.permutations[level];
            this.permutations[level] = perm;
            this.permutations[var5 + 256] = this.permutations[var5];
        }
    }

    private static double generateNoise1D(double val) {
        return val * val * val * (val * (val * 6.0 - 15.0) + 10.0);
    }

    public double generateNoise(double x, double y, double z) {
        double offX = x + this.xCoord;
        double offY = y + this.yCoord;
        double offZ = z + this.zCoord;
        long maxX = (long)offX;
        int maxY = (int)offY;
        long maxZ = (long)offZ;
        if (offX < maxX) {
            maxX--;
        }

        if (offY < maxY) {
            maxY--;
        }

        if (offZ < maxZ) {
            maxZ--;
        }

        int indexX = (int)(maxX & 255L);
        int indexY = maxY & 0xFF;
        int indexZ = (int)(maxZ & 255L);
        offX -= maxX;
        offY -= maxY;
        offZ -= maxZ;
        double falloffX = offX * offX * offX * (offX * (offX * 6.0 - 15.0) + 10.0);
        double falloffY = offY * offY * offY * (offY * (offY * 6.0 - 15.0) + 10.0);
        double falloffZ = offZ * offZ * offZ * (offZ * (offZ * 6.0 - 15.0) + 10.0);
        int height = this.permutations[indexX] + indexY;
        int depth = this.permutations[height] + indexZ;
        int depthPlusOne = this.permutations[height + 1] + indexZ;
        int heightPlusOne = this.permutations[indexX + 1] + indexY;
        int depthPlus = this.permutations[heightPlusOne] + indexZ;
        int depthFinal = this.permutations[heightPlusOne + 1] + indexZ;
        return this.lerp(
            falloffZ,
            this.lerp(
                falloffY,
                this.lerp(falloffX, this.grad(this.permutations[depth], offX, offY, offZ), this.grad(this.permutations[depthPlus], offX - 1.0, offY, offZ)),
                this.lerp(
                    falloffX,
                    this.grad(this.permutations[depthPlusOne], offX, offY - 1.0, offZ),
                    this.grad(this.permutations[depthFinal], offX - 1.0, offY - 1.0, offZ)
                )
            ),
            this.lerp(
                falloffY,
                this.lerp(
                    falloffX,
                    this.grad(this.permutations[depth + 1], offX, offY, offZ - 1.0),
                    this.grad(this.permutations[depthPlus + 1], offX - 1.0, offY, offZ - 1.0)
                ),
                this.lerp(
                    falloffX,
                    this.grad(this.permutations[depthPlusOne + 1], offX, offY - 1.0, offZ - 1.0),
                    this.grad(this.permutations[depthFinal + 1], offX - 1.0, offY - 1.0, offZ - 1.0)
                )
            )
        );
    }

    public double getValue(double x, double y, double z) {
        double offX = x + this.xCoord;
        double offY = y + this.yCoord;
        double offZ = z + this.zCoord;
        int permX = MathHelper.floor_double(offX) & 0xFF;
        int permY = MathHelper.floor_double(offY) & 0xFF;
        int permZ = MathHelper.floor_double(offZ) & 0xFF;
        offX -= MathHelper.floor_double(offX);
        offY -= MathHelper.floor_double(offY);
        offZ -= MathHelper.floor_double(offZ);
        double noiseX = generateNoise1D(offX);
        double noiseY = generateNoise1D(offY);
        double noiseZ = generateNoise1D(offZ);
        int height = this.permutations[permX] + permY;
        int depth = this.permutations[height] + permZ;
        height = this.permutations[height + 1] + permZ;
        permX = this.permutations[permX + 1] + permY;
        permY = this.permutations[permX] + permZ;
        permX = this.permutations[permX + 1] + permZ;
        return this.lerp(
            noiseZ,
            this.lerp(
                noiseY,
                this.lerp(noiseX, this.grad(this.permutations[depth], offX, offY, offZ), this.grad(this.permutations[permY], offX - 1.0, offY, offZ)),
                this.lerp(
                    noiseX, this.grad(this.permutations[height], offX, offY - 1.0, offZ), this.grad(this.permutations[permX], offX - 1.0, offY - 1.0, offZ)
                )
            ),
            this.lerp(
                noiseY,
                this.lerp(
                    noiseX,
                    this.grad(this.permutations[depth + 1], offX, offY, offZ - 1.0),
                    this.grad(this.permutations[permY + 1], offX - 1.0, offY, offZ - 1.0)
                ),
                this.lerp(
                    noiseX,
                    this.grad(this.permutations[height + 1], offX, offY - 1.0, offZ - 1.0),
                    this.grad(this.permutations[permX + 1], offX - 1.0, offY - 1.0, offZ - 1.0)
                )
            )
        );
    }

    public double lerp(double x, double y, double z) {
        return y + x * (z - y);
    }

    public double grad(int scale, double x, double y, double z) {
        int off = scale & 15;
        double scaleX = off < 8 ? x : y;
        double scaleZ = off < 4 ? y : (off != 12 && off != 14 ? z : x);
        return ((off & 1) == 0 ? scaleX : -scaleX) + ((off & 2) == 0 ? scaleZ : -scaleZ);
    }

    @Override
    public double generateNoise(double x, double z) {
        return this.generateNoise(x, z, 0.0);
    }

    public void generateNoise(
        double[] array, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double xScale, double yScale, double zScale, double valueReduction
    ) {
        int increment = 0;
        double div = 1.0 / valueReduction;
        int perm = -1;
        double startX = 0.0;
        double startZ = 0.0;
        double endX = 0.0;
        double endZ = 0.0;

        for (int x = 0; x < maxX; x++) {
            double offX = (minX + x) * xScale + this.xCoord;
            long limitX = (long)offX;
            if (offX < limitX) {
                limitX--;
            }

            int permX = (int)(limitX & 255L);
            offX -= limitX;
            double noiseX = offX * offX * offX * (offX * (offX * 6.0 - 15.0) + 10.0);

            for (int z = 0; z < maxZ; z++) {
                double offZ = (minZ + z) * zScale + this.zCoord;
                long limitZ = (long)offZ;
                if (offZ < limitZ) {
                    limitZ--;
                }

                int permZ = (int)(limitZ & 255L);
                offZ -= limitZ;
                double noiseZ = offZ * offZ * offZ * (offZ * (offZ * 6.0 - 15.0) + 10.0);

                for (int y = 0; y < maxY; y++) {
                    double offY = (minY + y) * yScale + this.yCoord;
                    long limitY = (long)offY;
                    if (offY < limitY) {
                        limitY--;
                    }

                    int permY = (int)(limitY & 255L);
                    offY -= limitY;
                    double noiseY = offY * offY * offY * (offY * (offY * 6.0 - 15.0) + 10.0);
                    if (y == 0 || permY != perm) {
                        perm = permY;
                        int height = this.permutations[permX] + permY;
                        int depth = this.permutations[height] + permZ;
                        int heightPlusOne = this.permutations[height + 1] + permZ;
                        int depthplusOne = this.permutations[permX + 1] + permY;
                        int heightPlus = this.permutations[depthplusOne] + permZ;
                        int heightFinal = this.permutations[depthplusOne + 1] + permZ;
                        startX = this.lerp(
                            noiseX, this.grad(this.permutations[depth], offX, offY, offZ), this.grad(this.permutations[heightPlus], offX - 1.0, offY, offZ)
                        );
                        startZ = this.lerp(
                            noiseX,
                            this.grad(this.permutations[heightPlusOne], offX, offY - 1.0, offZ),
                            this.grad(this.permutations[heightFinal], offX - 1.0, offY - 1.0, offZ)
                        );
                        endX = this.lerp(
                            noiseX,
                            this.grad(this.permutations[depth + 1], offX, offY, offZ - 1.0),
                            this.grad(this.permutations[heightPlus + 1], offX - 1.0, offY, offZ - 1.0)
                        );
                        endZ = this.lerp(
                            noiseX,
                            this.grad(this.permutations[heightPlusOne + 1], offX, offY - 1.0, offZ - 1.0),
                            this.grad(this.permutations[heightFinal + 1], offX - 1.0, offY - 1.0, offZ - 1.0)
                        );
                    }

                    double finalX = this.lerp(noiseY, startX, startZ);
                    double finalY = this.lerp(noiseY, endX, endZ);
                    double finalZ = this.lerp(noiseZ, finalX, finalY);
                    int permutations = increment++;
                    if (permutations > array.length) {
                        permutations = array.length;
                        System.err.println("FUCK FUCK FUCK");
                    }

                    array[permutations] += finalZ * div;
                }
            }
        }
    }

    public final double computeValues(int scale, double x, double z) {
        int offset = scale & 15;
        double noiseX = (1 - ((offset & 8) >> 3)) * x;
        double noiseZ = offset < 4 ? 0.0 : (offset != 12 && offset != 14 ? z : x);
        return ((offset & 1) == 0 ? noiseX : -noiseX) + ((offset & 2) == 0 ? noiseZ : -noiseZ);
    }

    public void generateNoise(
        double[] noiseArray, double x, double y, double z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double amplitude
    ) {
        if (ySize == 1) {
            int permTemp = 0;
            double amplitudeInv = 1.0 / amplitude;

            for (int xIdx = 0; xIdx < xSize; xIdx++) {
                double lerpX1 = (x + xIdx) * xScale + this.xCoord;
                long cellX = (long)lerpX1;
                if (lerpX1 < cellX) {
                    cellX--;
                }

                int permXMasked = (int)(cellX & 255L);
                lerpX1 -= cellX;
                double lerpX3 = lerpX1 * lerpX1 * lerpX1 * (lerpX1 * (lerpX1 * 6.0 - 15.0) + 10.0);

                for (int xIndex = 0; xIndex < zSize; xIndex++) {
                    double sampleX = (z + xIndex) * zScale + this.zCoord;
                    long cellZ = (long)sampleX;
                    if (sampleX < cellZ) {
                        cellZ--;
                    }

                    int permX = (int)(cellZ & 255L);
                    sampleX -= cellZ;
                    double fadeX = sampleX * sampleX * sampleX * (sampleX * (sampleX * 6.0 - 15.0) + 10.0);
                    int permBase = this.permutations[permXMasked];
                    int permXZ0 = this.permutations[permBase] + permX;
                    int permBaseNext = this.permutations[permXMasked + 1];
                    int lastPermY = this.permutations[permBaseNext] + permX;
                    double lerpX0 = this.lerp(
                        lerpX3,
                        this.computeValues(this.permutations[permXZ0], lerpX1, sampleX),
                        this.grad(this.permutations[lastPermY], lerpX1 - 1.0, 0.0, sampleX)
                    );
                    double lerpX1b = this.lerp(
                        lerpX3,
                        this.grad(this.permutations[permXZ0 + 1], lerpX1, 0.0, sampleX - 1.0),
                        this.grad(this.permutations[lastPermY + 1], lerpX1 - 1.0, 0.0, sampleX - 1.0)
                    );
                    double lerpZ = this.lerp(fadeX, lerpX0, lerpX1b);
                    int indexOut = permTemp++;
                    if (indexOut >= noiseArray.length) {
                        indexOut = noiseArray.length;
                        System.err.println("BAD BAD BAD FUCK SHHIT");
                    }

                    noiseArray[indexOut] += lerpZ * amplitudeInv;
                }
            }
        } else {
            int permBase = 0;
            double amplitudeInv = 1.0 / amplitude;
            int lastPermY = -1;
            double lerpX00 = 0.0;
            double lerpX1 = 0.0;
            double lerpX01 = 0.0;
            double lerpX3 = 0.0;

            for (int xIndex = 0; xIndex < xSize; xIndex++) {
                double sampleX = (x + xIndex) * xScale + this.xCoord;
                long cellX = (long)sampleX;
                if (sampleX < cellX) {
                    cellX--;
                }

                int permX = (int)(cellX & 255L);
                sampleX -= cellX;
                double fadeX = sampleX * sampleX * sampleX * (sampleX * (sampleX * 6.0 - 15.0) + 10.0);

                for (int zIdx = 0; zIdx < zSize; zIdx++) {
                    double sampleZ = (z + zIdx) * zScale + this.zCoord;
                    long cellZ = (long)sampleZ;
                    if (sampleZ < cellZ) {
                        cellZ--;
                    }

                    int permZ = (int)(cellZ & 255L);
                    sampleZ -= cellZ;
                    double fadeZ = sampleZ * sampleZ * sampleZ * (sampleZ * (sampleZ * 6.0 - 15.0) + 10.0);

                    for (int yIdx = 0; yIdx < ySize; yIdx++) {
                        double sampleY = (y + yIdx) * yScale + this.yCoord;
                        long cellY = (long)sampleY;
                        if (sampleY < cellY) {
                            cellY--;
                        }

                        int permY = (int)(cellY & 255L);
                        sampleY -= cellY;
                        double fadeY = sampleY * sampleY * sampleY * (sampleY * (sampleY * 6.0 - 15.0) + 10.0);
                        if (yIdx == 0 || permY != lastPermY) {
                            lastPermY = permY;
                            int permXY0 = this.permutations[permX] + permY;
                            int permXYZ0 = this.permutations[permXY0] + permZ;
                            int permXYZ1 = this.permutations[permXY0 + 1] + permZ;
                            int permXY1 = this.permutations[permX + 1] + permY;
                            int permXYZ2 = this.permutations[permXY1] + permZ;
                            int permXYZ3 = this.permutations[permXY1 + 1] + permZ;
                            lerpX00 = this.lerp(
                                fadeX,
                                this.grad(this.permutations[permXYZ0], sampleX, sampleY, sampleZ),
                                this.grad(this.permutations[permXYZ2], sampleX - 1.0, sampleY, sampleZ)
                            );
                            lerpX1 = this.lerp(
                                fadeX,
                                this.grad(this.permutations[permXYZ1], sampleX, sampleY - 1.0, sampleZ),
                                this.grad(this.permutations[permXYZ3], sampleX - 1.0, sampleY - 1.0, sampleZ)
                            );
                            lerpX01 = this.lerp(
                                fadeX,
                                this.grad(this.permutations[permXYZ0 + 1], sampleX, sampleY, sampleZ - 1.0),
                                this.grad(this.permutations[permXYZ2 + 1], sampleX - 1.0, sampleY, sampleZ - 1.0)
                            );
                            lerpX3 = this.lerp(
                                fadeX,
                                this.grad(this.permutations[permXYZ1 + 1], sampleX, sampleY - 1.0, sampleZ - 1.0),
                                this.grad(this.permutations[permXYZ3 + 1], sampleX - 1.0, sampleY - 1.0, sampleZ - 1.0)
                            );
                        }

                        double lerpY0 = this.lerp(fadeY, lerpX00, lerpX1);
                        double lerpY1 = this.lerp(fadeY, lerpX01, lerpX3);
                        double lerpZ = this.lerp(fadeZ, lerpY0, lerpY1);
                        int indexOut = permBase++;
                        noiseArray[indexOut] += lerpZ * amplitudeInv;
                    }
                }
            }
        }
    }
}
