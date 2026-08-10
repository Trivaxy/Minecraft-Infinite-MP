package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.util.math.MathHelper;

public class Iceberg extends Feature {
    public static final Iceberg INSTANCE = new Iceberg();

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        BlockPosition pos = new BlockPosition(x, world.getSeaLevel(), z);
        boolean flag = rand.nextDouble() > 0.7;
        int id = BlockList.packedIce.id;
        double randomizer = rand.nextDouble() * 2.0 * Math.PI;
        int maxHeight = 11 - rand.nextInt(5);
        int minHeight = 3 + rand.nextInt(3);
        boolean nextDouble = rand.nextDouble() > 0.7;
        int height = nextDouble ? rand.nextInt(6) + 6 : rand.nextInt(15) + 3;
        if (!nextDouble && rand.nextDouble() > 0.9) {
            height += rand.nextInt(19) + 7;
        }

        int heightVariation = Math.min(height + rand.nextInt(11), 18);
        int length = Math.min(height + rand.nextInt(7) - rand.nextInt(5), 11);
        int width = nextDouble ? maxHeight : 11;

        for (int x0 = -width; x0 < width; x0++) {
            for (int z0 = -width; z0 < width; z0++) {
                for (int y0 = 0; y0 < height; y0++) {
                    int max = nextDouble ? this.positionAbsolute(y0, height, length) : this.calculateHeight(rand, y0, height, length);
                    if (nextDouble || x0 < max) {
                        this.checkBlocks(world, rand, pos, height, x0, y0, z0, max, width, nextDouble, minHeight, randomizer, flag, id);
                    }
                }
            }
        }

        this.hollow(world, pos, length, height, nextDouble, maxHeight);

        for (int x0 = -width; x0 < width; x0++) {
            for (int z0 = -width; z0 < width; z0++) {
                for (int y0 = -1; y0 > -heightVariation; y0--) {
                    int fuck = nextDouble ? MathHelper.ceiling_float_int(width * (1.0F - (float)Math.pow(y0, 2.0) / (heightVariation * 8.0F))) : width;
                    int radius = this.radiusAbsolute(rand, -y0, heightVariation, length);
                    if (x0 < radius) {
                        this.checkBlocks(world, rand, pos, heightVariation, x0, y0, z0, radius, fuck, nextDouble, minHeight, randomizer, flag, id);
                    }
                }
            }
        }

        boolean spikey = nextDouble ? rand.nextDouble() > 0.1 : rand.nextDouble() > 0.7;
        if (spikey) {
            this.raiseSpikes(rand, world, length, height, pos, nextDouble, maxHeight, randomizer, minHeight);
        }

        return true;
    }

    private void raiseSpikes(Random random, World world, int x, int z, BlockPosition pos, boolean condition1, int xOffset, double radius, int zOffset) {
        int offX = random.nextBoolean() ? -1 : 1;
        int offZ = random.nextBoolean() ? -1 : 1;
        int radX = random.nextInt(Math.max(x / 2 - 2, 1));
        if (random.nextBoolean()) {
            radX = x / 2 + 1 - random.nextInt(Math.max(x - x / 2 - 1, 1));
        }

        int radZ = random.nextInt(Math.max(x / 2 - 2, 1));
        if (random.nextBoolean()) {
            radZ = x / 2 + 1 - random.nextInt(Math.max(x - x / 2 - 1, 1));
        }

        if (condition1) {
            radX = radZ = random.nextInt(Math.max(xOffset - 5, 1));
        }

        BlockPosition blockPos = new BlockPosition(offX * radX, 0, offZ * radZ);
        double length = condition1 ? radius + (Math.PI / 2) : random.nextDouble() * 2.0 * Math.PI;

        for (int z0 = 0; z0 < z - 3; z0++) {
            int hght = this.calculateHeight(random, z0, z, x);
            this.customize(hght, z0, pos, world, false, length, blockPos, xOffset, zOffset);
        }

        for (int z0 = -1; z0 > -z + random.nextInt(5); z0--) {
            int rad = this.radiusAbsolute(random, -z0, z, x);
            this.customize(rad, z0, pos, world, true, length, blockPos, xOffset, zOffset);
        }
    }

    private void customize(int x, int yoffs, BlockPosition pos, World world, boolean water, double d12, BlockPosition pos2, int z, int i1) {
        int xMax = x + 1 + z / 3;
        int xMin = Math.min(x - 3, 3) + i1 / 2 - 1;

        for (int x0 = -xMax; x0 < xMax; x0++) {
            for (int z0 = -xMax; z0 < xMax; z0++) {
                double dist = this.collisionDistance(x0, z0, pos2, xMax, xMin, d12);
                if (dist < 0.0) {
                    BlockPosition blockPos = pos.add(x0, yoffs, z0);
                    Block block = world.getBlock(blockPos.x, blockPos.y, blockPos.z);
                    if (this.isIce(block) || block == BlockList.snowBlock) {
                        if (water) {
                            world.setBlock(blockPos.x, blockPos.y, blockPos.z, BlockList.waterMoving.id);
                        } else {
                            world.setBlock(blockPos.x, blockPos.y, blockPos.z, 0);
                            this.removeSnowLayer(world, blockPos);
                        }
                    }
                }
            }
        }
    }

    private void removeSnowLayer(World world, BlockPosition pos) {
        if (world.getBlock(pos.x, pos.y + 1, pos.z) == BlockList.snowLayer) {
            world.setBlock(pos.x, pos.y + 1, pos.z, 0);
        }
    }

    private void checkBlocks(
        World world,
        Random random,
        BlockPosition pos,
        int hght,
        int xp,
        int yp,
        int zp,
        int rad,
        int dist,
        boolean flag,
        int w,
        double e,
        boolean trash,
        int state
    ) {
        double durr = flag
            ? this.collisionDistance(xp, zp, BlockPosition.ZERO, dist, this.heightAbsolute(yp, hght, w), e)
            : this.radius(xp, zp, BlockPosition.ZERO, rad, random);
        if (durr < 0.0) {
            BlockPosition pos2 = pos.add(xp, yp, zp);
            double ass = flag ? -0.5 : -6 - random.nextInt(3);
            if (durr > ass && random.nextDouble() > 0.9) {
                return;
            }

            this.placeBlocks(pos2, world, random, hght - yp, hght, flag, trash, state);
        }
    }

    private void placeBlocks(BlockPosition pos, World world, Random random, int x, int z, boolean b1, boolean isSnowy, int state) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        if (block == null || block == BlockList.snowBlock || block == BlockList.ice || block == BlockList.waterMoving || block == BlockList.waterStill) {
            boolean flag = !b1 || random.nextDouble() > 0.05;
            int i = b1 ? 3 : 2;
            if (isSnowy && block != BlockList.waterMoving && x <= random.nextInt(Math.max(1, z / i)) + z * 0.6 && flag) {
                world.setBlock(pos.x, pos.y, pos.z, BlockList.snowBlock.id);
            } else {
                world.setBlockAndMetadata(pos.x, pos.y, pos.z, state, 0);
            }
        }
    }

    private int heightAbsolute(int x, int y, int z) {
        int hght = z;
        if (x > 0 && y - x <= 3) {
            hght = z - (4 - (y - x));
        }

        return hght;
    }

    private double radius(int xoff, int zoff, BlockPosition pos, int a, Random random) {
        float rad = 10.0F * MathHelper.clamp(random.nextFloat(), 0.2F, 0.8F) / a;
        return rad + Math.pow(xoff - pos.x, 2.0) + Math.pow(zoff - pos.z, 2.0) - Math.pow(a, 2.0);
    }

    private double collisionDistance(int x1, int z1, BlockPosition state, int x2, int z2, double d1) {
        return Math.pow(((x1 - state.x) * Math.cos(d1) - (z1 - state.z) * Math.sin(d1)) / x2, 2.0)
            + Math.pow(((x1 - state.x) * Math.sin(d1) + (z1 - state.z) * Math.cos(d1)) / z2, 2.0)
            - 1.0;
    }

    private int calculateHeight(Random rand, int x, int y, int z) {
        float max = 3.5F - rand.nextFloat();
        float hght = (1.0F - (float)Math.pow(x, 2.0) / (y * max)) * z;
        if (y > 15 + rand.nextInt(5)) {
            int i = x < 3 + rand.nextInt(6) ? x / 2 : x;
            hght = (1.0F - i / (y * max * 0.4F)) * z;
        }

        return MathHelper.ceiling_float_int(hght / 2.0F);
    }

    private int positionAbsolute(int x, int y, int z) {
        float rad = (1.0F - (float)Math.pow(x, 2.0) / y) * z;
        return MathHelper.ceiling_float_int(rad / 2.0F);
    }

    private int radiusAbsolute(Random rand, int x, int y, int z) {
        float max = 1.0F + rand.nextFloat() / 2.0F;
        float rad = (1.0F - x / (y * max)) * z;
        return MathHelper.ceiling_float_int(rad / 2.0F);
    }

    private boolean isIce(Block block) {
        return block == BlockList.packedIce || block == BlockList.snowBlock;
    }

    private boolean isAirBelow(World world, BlockPosition pos) {
        return world.getBlock(pos.x, pos.y - 1, pos.z) == null;
    }

    private void hollow(World world, BlockPosition pos, int x, int z, boolean condition, int notch) {
        int max = condition ? notch : x / 2;

        for (int newX = -max; newX <= max; newX++) {
            for (int newZ = -max; newZ <= max; newZ++) {
                for (int y = 0; y <= z; y++) {
                    BlockPosition pos2 = pos.add(newX, y, newZ);
                    Block block = world.getBlock(pos2.x, pos2.y, pos2.z);
                    if (this.isIce(block) || block == BlockList.snowLayer) {
                        if (this.isAirBelow(world, pos2)) {
                            world.setBlock(pos2.x, pos2.y, pos2.z, 0);
                            world.setBlock(pos2.x, pos2.y + 1, pos2.z, 0);
                        } else if (this.isIce(block)) {
                            Block[] ablock = new Block[]{
                                world.getBlock(pos2.x - 1, pos2.y, pos2.z),
                                world.getBlock(pos2.x + 1, pos2.y, pos2.z),
                                world.getBlock(pos2.x, pos2.y, pos2.z - 1),
                                world.getBlock(pos2.x, pos2.y, pos2.z + 1)
                            };
                            int iterations = 0;

                            for (Block block1 : ablock) {
                                if (!this.isIce(block1)) {
                                    iterations++;
                                }
                            }

                            if (iterations >= 3) {
                                world.setBlock(pos2.x, pos2.y, pos2.z, 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
