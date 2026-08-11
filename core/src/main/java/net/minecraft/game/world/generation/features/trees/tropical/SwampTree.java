package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class SwampTree extends Tree {
    private void generateVines(World world, int x, int y, int z, int meta) {
        world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);

        for (int height = 4; world.getBlockId(x, --y, z) == 0 && height > 0; height--) {
            world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);
        }
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(4) + 5;

        while (world.getBlockMaterial(x, y - 1, z) == MaterialList.water) {
            y--;
        }

        boolean canPlace = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte size = 1;
                if (y0 == y) {
                    size = 0;
                }

                if (y0 >= y + 1 + height - 2) {
                    size = 3;
                }

                for (int x0 = x - size; x0 <= x + size && canPlace; x0++) {
                    for (int z0 = z - size; z0 <= z + size && canPlace; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                                if (id != BlockList.waterStill.id && id != BlockList.waterMoving.id) {
                                    canPlace = false;
                                } else if (y0 > y) {
                                    canPlace = false;
                                }
                            }
                        } else {
                            canPlace = false;
                        }
                    }
                }
            }

            if (!canPlace) {
                return false;
            }

            int var16 = world.getBlockId(x, y - 1, z);
            if ((var16 == BlockList.grass.id || var16 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, 2);

                for (int yy = y - 3 + height; yy <= y + height; yy++) {
                    int x0 = yy - (y + height);
                    int z0 = 2 - x0 / 2;

                    for (int id = x - z0; id <= x + z0; id++) {
                        int width = id - x;

                        for (int zz = z - z0; zz <= z + z0; zz++) {
                            int w = zz - z;
                            if ((Math.abs(width) != z0 || Math.abs(w) != z0 || random.nextInt(2) != 0 && x0 != 0)
                                && !Block.isOpaque.get(world.getBlockId(id, yy, zz))) {
                                world.setBlockAndMetadata(id, yy, zz, BlockList.leaves.id, 7);
                            }
                        }
                    }
                }

                for (int var26 = 0; var26 < height; var26++) {
                    int x0 = world.getBlockId(x, y + var26, z);
                    if (x0 == 0 || x0 == BlockList.leaves.id || x0 == BlockList.waterMoving.id || x0 == BlockList.waterStill.id) {
                        world.setBlockAndMetadata(x, y + var26, z, BlockList.wood.id, 6);
                    }
                }

                for (int var27 = y - 3 + height; var27 <= y + height; var27++) {
                    int x0 = var27 - (y + height);
                    int z0 = 2 - x0 / 2;

                    for (int id = x - z0; id <= x + z0; id++) {
                        for (int width = z - z0; width <= z + z0; width++) {
                            if (world.getBlockId(id, var27, width) == BlockList.leaves.id) {
                                if (random.nextInt(4) == 0 && world.getBlockId(id - 1, var27, width) == 0) {
                                    this.generateVines(world, id - 1, var27, width, 4);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(id + 1, var27, width) == 0) {
                                    this.generateVines(world, id + 1, var27, width, 5);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(id, var27, width - 1) == 0) {
                                    this.generateVines(world, id, var27, width - 1, 2);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(id, var27, width + 1) == 0) {
                                    this.generateVines(world, id, var27, width + 1, 3);
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
