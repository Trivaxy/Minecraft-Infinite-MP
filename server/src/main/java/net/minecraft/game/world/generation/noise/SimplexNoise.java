package net.minecraft.game.world.generation.noise;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoise {
    private static final int[][] grad3 = new int[][]{
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };
    private final int[] permutations = new int[512];
    public double xCoord;
    public double yCoord;
    public double zCoord;
    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    public SimplexNoise() {
        this(new Random());
    }

    public SimplexNoise(Random random) {
        this.xCoord = random.nextDouble() * 256.0;
        this.yCoord = random.nextDouble() * 256.0;
        this.zCoord = random.nextDouble() * 256.0;
        int height = 0;

        while (height < 256) {
            this.permutations[height] = height++;
        }

        for (int var5 = 0; var5 < 256; var5++) {
            int level = random.nextInt(256 - var5) + var5;
            int finalLvl = this.permutations[var5];
            this.permutations[var5] = this.permutations[level];
            this.permutations[level] = finalLvl;
            this.permutations[var5 + 256] = this.permutations[var5];
        }
    }

    private static int wrap(double value) {
        return value > 0.0 ? (int)value : (int)value - 1;
    }

    private static double dot(int[] values, double x, double z) {
        return values[0] * x + values[1] * z;
    }

    public void generateNoise(double[] noise, double startX, double startZ, int samplesX, int samplesZ, double endX, double endZ, double scale) {
        int index = 0;

        for (int x = 0; x < samplesX; x++) {
            double scaleX = (startX + x) * endX + this.xCoord;

            for (int z = 0; z < samplesZ; z++) {
                double scaleZ = (startZ + z) * endZ + this.yCoord;
                double distort = (scaleX + scaleZ) * F2;
                int posX = wrap(scaleX + distort);
                int posZ = wrap(scaleZ + distort);
                double offset = (posX + posZ) * G2;
                double offX = posX - offset;
                double offZ = posZ - offset;
                double finalX = scaleX - offX;
                double finalZ = scaleZ - offZ;
                byte shiftX;
                byte shiftZ;
                if (finalX > finalZ) {
                    shiftX = 1;
                    shiftZ = 0;
                } else {
                    shiftX = 0;
                    shiftZ = 1;
                }

                double upperX = finalX - shiftX + G2;
                double upperZ = finalZ - shiftZ + G2;
                double lowerX = finalX - 1.0 + 2.0 * G2;
                double lowerZ = finalZ - 1.0 + 2.0 * G2;
                int permX = posX & 0xFF;
                int permZ = posZ & 0xFF;
                int valX = this.permutations[permX + this.permutations[permZ]] % 12;
                int valY = this.permutations[permX + shiftX + this.permutations[permZ + shiftZ]] % 12;
                int valZ = this.permutations[permX + 1 + this.permutations[permZ + 1]] % 12;
                double mestX = 0.5 - finalX * finalX - finalZ * finalZ;
                double x0;
                if (mestX < 0.0) {
                    x0 = 0.0;
                } else {
                    mestX *= mestX;
                    x0 = mestX * mestX * dot(grad3[valX], finalX, finalZ);
                }

                double mestZ = 0.5 - upperX * upperX - upperZ * upperZ;
                double z0;
                if (mestZ < 0.0) {
                    z0 = 0.0;
                } else {
                    mestZ *= mestZ;
                    z0 = mestZ * mestZ * dot(grad3[valY], upperX, upperZ);
                }

                double mestY = 0.5 - lowerX * lowerX - lowerZ * lowerZ;
                double y0;
                if (mestY < 0.0) {
                    y0 = 0.0;
                } else {
                    mestY *= mestY;
                    y0 = mestY * mestY * dot(grad3[valZ], lowerX, lowerZ);
                }

                noise[index++] += 70.0 * (x0 + z0 + y0) * scale;
            }
        }
    }

    public double getValue(double x, double y) {
        double skewFactor = (x + y) * F2;
        int cellX = MathHelper.floor(x + skewFactor);
        int cellY = MathHelper.floor(y + skewFactor);
        double unskewFactor = (cellX + cellY) * G2;
        double cellOriginX = cellX - unskewFactor;
        double cellOriginY = cellY - unskewFactor;
        double localX0 = x - cellOriginX;
        double localY0 = y - cellOriginY;
        int offsetX1;
        int offsetY1;
        if (localX0 > localY0) {
            offsetX1 = 1;
            offsetY1 = 0;
        } else {
            offsetX1 = 0;
            offsetY1 = 1;
        }

        double localX1 = localX0 - offsetX1 + G2;
        double localY1 = localY0 - offsetY1 + G2;
        double localX2 = localX0 - 1.0 + 2.0 * G2;
        double localY2 = localY0 - 1.0 + 2.0 * G2;
        int permX = cellX & 0xFF;
        int permY = cellY & 0xFF;
        int gradIndex0 = this.getPermutValue(permX + this.getPermutValue(permY)) % 12;
        int gradIndex1 = this.getPermutValue(permX + offsetX1 + this.getPermutValue(permY + offsetY1)) % 12;
        int gradIndex2 = this.getPermutValue(permX + 1 + this.getPermutValue(permY + 1)) % 12;
        double contribution0 = this.getContrib(gradIndex0, localX0, localY0, 0.0, 0.5);
        double contribution1 = this.getContrib(gradIndex1, localX1, localY1, 0.0, 0.5);
        double contribution2 = this.getContrib(gradIndex2, localX2, localY2, 0.0, 0.5);
        return 70.0 * (contribution0 + contribution1 + contribution2);
    }

    private int getPermutValue(int permutIndex) {
        return this.permutations[permutIndex & 0xFF];
    }

    protected static double processGrad(int[] gradElement, double xFactor, double yFactor, double zFactor) {
        return gradElement[0] * xFactor + gradElement[1] * yFactor + gradElement[2] * zFactor;
    }

    private double getContrib(int gradIndex, double x, double y, double z, double offset) {
        double total = offset - x * x - y * y - z * z;
        double value;
        if (total < 0.0) {
            value = 0.0;
        } else {
            total *= total;
            value = total * total * processGrad(grad3[gradIndex], x, y, z);
        }

        return value;
    }

    public double generateNoise(double x, double y, double z) {
        double skewFactor = (x + y + z) * 0.3333333333333333;
        int cellX = MathHelper.floor(x + skewFactor);
        int cellY = MathHelper.floor(y + skewFactor);
        int cellZ = MathHelper.floor(z + skewFactor);
        double unskewFactor = (cellX + cellY + cellZ) * 0.16666666666666666;
        double cellOriginX = cellX - unskewFactor;
        double cellOriginY = cellY - unskewFactor;
        double cellOriginZ = cellZ - unskewFactor;
        double localX0 = x - cellOriginX;
        double localY0 = y - cellOriginY;
        double localZ0 = z - cellOriginZ;
        int offsetX1;
        int offsetY1;
        int offsetZ1;
        int offsetX2;
        int offsetY2;
        int offsetZ2;
        if (localX0 >= localY0) {
            if (localY0 >= localZ0) {
                offsetX1 = 1;
                offsetY1 = 0;
                offsetZ1 = 0;
                offsetX2 = 1;
                offsetY2 = 1;
                offsetZ2 = 0;
            } else if (localX0 >= localZ0) {
                offsetX1 = 1;
                offsetY1 = 0;
                offsetZ1 = 0;
                offsetX2 = 1;
                offsetY2 = 0;
                offsetZ2 = 1;
            } else {
                offsetX1 = 0;
                offsetY1 = 0;
                offsetZ1 = 1;
                offsetX2 = 1;
                offsetY2 = 0;
                offsetZ2 = 1;
            }
        } else if (localY0 < localZ0) {
            offsetX1 = 0;
            offsetY1 = 0;
            offsetZ1 = 1;
            offsetX2 = 0;
            offsetY2 = 1;
            offsetZ2 = 1;
        } else if (localX0 < localZ0) {
            offsetX1 = 0;
            offsetY1 = 1;
            offsetZ1 = 0;
            offsetX2 = 0;
            offsetY2 = 1;
            offsetZ2 = 1;
        } else {
            offsetX1 = 0;
            offsetY1 = 1;
            offsetZ1 = 0;
            offsetX2 = 1;
            offsetY2 = 1;
            offsetZ2 = 0;
        }

        double localX1 = localX0 - offsetX1 + 0.16666666666666666;
        double localY1 = localY0 - offsetY1 + 0.16666666666666666;
        double localZ1 = localZ0 - offsetZ1 + 0.16666666666666666;
        double localX2 = localX0 - offsetX2 + 0.3333333333333333;
        double localY2 = localY0 - offsetY2 + 0.3333333333333333;
        double localZ2 = localZ0 - offsetZ2 + 0.3333333333333333;
        double localX3 = localX0 - 1.0 + 0.5;
        double localY3 = localY0 - 1.0 + 0.5;
        double localZ3 = localZ0 - 1.0 + 0.5;
        int permX = cellX & 0xFF;
        int permY = cellY & 0xFF;
        int permZ = cellZ & 0xFF;
        int gradIndex0 = this.getPermutValue(permX + this.getPermutValue(permY + this.getPermutValue(permZ))) % 12;
        int gradIndex1 = this.getPermutValue(permX + offsetX1 + this.getPermutValue(permY + offsetY1 + this.getPermutValue(permZ + offsetZ1))) % 12;
        int gradIndex2 = this.getPermutValue(permX + offsetX2 + this.getPermutValue(permY + offsetY2 + this.getPermutValue(permZ + offsetZ2))) % 12;
        int gradIndex3 = this.getPermutValue(permX + 1 + this.getPermutValue(permY + 1 + this.getPermutValue(permZ + 1))) % 12;
        double contribution0 = this.getContrib(gradIndex0, localX0, localY0, localZ0, 0.6);
        double contribution1 = this.getContrib(gradIndex1, localX1, localY1, localZ1, 0.6);
        double contribution2 = this.getContrib(gradIndex2, localX2, localY2, localZ2, 0.6);
        double contribution3 = this.getContrib(gradIndex3, localX3, localY3, localZ3, 0.6);
        return 32.0 * (contribution0 + contribution1 + contribution2 + contribution3);
    }
}
