package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.Direction;

public class CanopyTree extends Tree {
    private void setLeaves(World world, int x, int y, int z) {
        Block block = BlockList.blocks[world.getBlockId(x, y, z)];
        if (block == null || block.material == MaterialList.air) {
            world.setBlockAndMetadata(x, y, z, BlockList.leaves.id, 13);
        }
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        int height = rand.nextInt(3) + rand.nextInt(2) + 6;
        boolean generate = true;
        if (y0 >= 1 && y0 + height + 1 <= world.getWorldHeight()) {
            for (int y = y0; y <= y0 + 1 + height; y++) {
                byte radius = 1;
                if (y == y0) {
                    radius = 0;
                }

                if (y >= y0 + 1 + height - 2) {
                    radius = 2;
                }

                for (int x = x0 - radius; x <= x0 + radius && generate; x++) {
                    for (int z = z0 - radius; z <= z0 + radius && generate; z++) {
                        if (y >= 0 && y < world.getWorldHeight()) {
                            Block block = BlockList.blocks[world.getBlockId(x, y, z)];
                            if (block != null && block != BlockList.leaves) {
                                generate = false;
                            }
                        } else {
                            generate = false;
                        }
                    }
                }
            }

            if (!generate) {
                return false;
            }

            Block block = BlockList.blocks[world.getBlockId(x0, y0 - 1, z0)];
            if (block == null) {
                return false;
            }

            if ((block == BlockList.grass || block == BlockList.dirt) && y0 < 256 - height - 1) {
                world.setBlockAndMetadata(x0, y0 - 1, z0, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x0 + 1, y0 - 1, z0, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x0 + 1, y0 - 1, z0 + 1, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x0, y0 - 1, z0 + 1, BlockList.dirt.id, 2);
                int branches = rand.nextInt(4);
                int trunkLength = height - rand.nextInt(4);
                int branchOffset = 2 - rand.nextInt(3);
                int x = x0;
                int z = z0;
                int yOffset = 0;

                for (int xOffset = 0; xOffset < height; xOffset++) {
                    int zOffset = y0 + xOffset;
                    if (xOffset >= trunkLength && branchOffset > 0) {
                        x += Direction.offsetX[branches];
                        z += Direction.offsetZ[branches];
                        branchOffset--;
                    }

                    Block blocc = BlockList.blocks[world.getBlockId(x, zOffset, z)];
                    if (blocc == null || blocc.material == MaterialList.air || blocc.material == MaterialList.leaves) {
                        world.setBlockAndMetadata(x, zOffset, z, BlockList.wood.id, 11);
                        world.setBlockAndMetadata(x + 1, zOffset, z, BlockList.wood.id, 11);
                        world.setBlockAndMetadata(x, zOffset, z + 1, BlockList.wood.id, 11);
                        world.setBlockAndMetadata(x + 1, zOffset, z + 1, BlockList.wood.id, 11);
                        yOffset = zOffset;
                    }
                }

                for (int var25 = -2; var25 <= 0; var25++) {
                    for (int zOffset = -2; zOffset <= 0; zOffset++) {
                        byte offset1 = -1;
                        this.setLeaves(world, x + var25, yOffset + offset1, z + zOffset);
                        this.setLeaves(world, 1 + x - var25, yOffset + offset1, z + zOffset);
                        this.setLeaves(world, x + var25, yOffset + offset1, 1 + z - zOffset);
                        this.setLeaves(world, 1 + x - var25, yOffset + offset1, 1 + z - zOffset);
                        if ((var25 > -2 || zOffset > -1) && (var25 != -1 || zOffset != -2)) {
                            byte offset2 = 1;
                            this.setLeaves(world, x + var25, yOffset + offset2, z + zOffset);
                            this.setLeaves(world, 1 + x - var25, yOffset + offset2, z + zOffset);
                            this.setLeaves(world, x + var25, yOffset + offset2, 1 + z - zOffset);
                            this.setLeaves(world, 1 + x - var25, yOffset + offset2, 1 + z - zOffset);
                        }
                    }
                }

                if (rand.nextBoolean()) {
                    this.setLeaves(world, x, yOffset + 2, z);
                    this.setLeaves(world, x + 1, yOffset + 2, z);
                    this.setLeaves(world, x + 1, yOffset + 2, z + 1);
                    this.setLeaves(world, x, yOffset + 2, z + 1);
                }

                for (int var26 = -3; var26 <= 4; var26++) {
                    for (int zOffset = -3; zOffset <= 4; zOffset++) {
                        if ((var26 != -3 || zOffset != -3)
                            && (var26 != -3 || zOffset != 4)
                            && (var26 != 4 || zOffset != -3)
                            && (var26 != 4 || zOffset != 4)
                            && (Math.abs(var26) < 3 || Math.abs(zOffset) < 3)) {
                            this.setLeaves(world, x + var26, yOffset, z + zOffset);
                        }
                    }
                }

                for (int var27 = -1; var27 <= 2; var27++) {
                    for (int zOffset = -1; zOffset <= 2; zOffset++) {
                        if ((var27 < 0 || var27 > 1 || zOffset < 0 || zOffset > 1) && rand.nextInt(3) <= 0) {
                            int hght = rand.nextInt(2) + 2;

                            for (int yy = 0; yy < hght; yy++) {
                                world.setBlockAndMetadata(x0 + var27, yOffset - yy - 1, z0 + zOffset, BlockList.wood.id, 11);
                            }

                            for (int var34 = -1; var34 <= 1; var34++) {
                                for (int zz = -1; zz <= 1; zz++) {
                                    this.setLeaves(world, x + var27 + var34, yOffset, z + zOffset + zz);
                                }
                            }

                            for (int var35 = -2; var35 <= 2; var35++) {
                                for (int zz = -2; zz <= 2; zz++) {
                                    if (Math.abs(var35) != 2 || Math.abs(zz) != 2) {
                                        this.setLeaves(world, x + var27 + var35, yOffset - 1, z + zOffset + zz);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
