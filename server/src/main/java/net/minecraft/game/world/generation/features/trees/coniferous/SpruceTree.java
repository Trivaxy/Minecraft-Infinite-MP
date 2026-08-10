package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class SpruceTree extends Tree {
    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(5) + 7;
        int size1 = height - random.nextInt(2) - 3;
        int heightOffset = height - size1;
        int size2 = 1 + random.nextInt(heightOffset + 1);
        boolean canGenerate = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height && canGenerate; y0++) {
                int width;
                if (y0 - y < size1) {
                    width = 0;
                } else {
                    width = size2;
                }

                for (int x0 = x - width; x0 <= x + width && canGenerate; x0++) {
                    for (int z0 = z - width; z0 <= z + width && canGenerate; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                                canGenerate = false;
                            }
                        } else {
                            canGenerate = false;
                        }
                    }
                }
            }

            if (!canGenerate) {
                return false;
            }

            int var18 = world.getBlockId(x, y - 1, z);
            if ((var18 == BlockList.grass.id || var18 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, world.getBiomeAt(x, z).fillerMeta);
                int width = 0;

                for (int x0 = y + height; x0 >= y + size1; x0--) {
                    for (int z0 = x - width; z0 <= x + width; z0++) {
                        int id = z0 - x;

                        for (int zz = z - width; zz <= z + width; zz++) {
                            int w = zz - z;
                            if ((Math.abs(id) != width || Math.abs(w) != width || width <= 0) && !Block.isOpaque.get(world.getBlockId(z0, x0, zz))) {
                                world.setBlockAndMetadata(z0, x0, zz, BlockList.leaves.id, 3);
                            }
                        }
                    }

                    if (width >= 1 && x0 == y + size1 + 1) {
                        width--;
                    } else if (width < size2) {
                        width++;
                    }
                }

                for (int var20 = 0; var20 < height - 1; var20++) {
                    int z0 = world.getBlockId(x, y + var20, z);
                    if (z0 == 0 || z0 == BlockList.leaves.id || z0 == BlockList.snowLayer.id) {
                        world.setBlockAndMetadata(x, y + var20, z, BlockList.wood.id, 2);
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
