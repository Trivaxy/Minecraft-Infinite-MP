package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class CedarTree extends Tree {
    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(5) + 6;
        boolean canPlace = true;

        for (int y0 = y; y0 <= y + 1 + height; y0++) {
            byte size = 5;
            if (y0 == y) {
                size = 0;
            }

            if (y0 >= y + 1 + height - 1) {
                size = 5;
            }

            for (int x0 = x - size; x0 <= x + size && canPlace; x0++) {
                for (int z0 = z - size; z0 <= z + size && canPlace; z0++) {
                    if (y0 >= 0 && y0 < world.getWorldHeight()) {
                        int id = world.getBlockId(x0, y0, z0);
                        if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                            canPlace = false;
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

        int belowBlock = world.getBlockId(x, y - 1, z);
        if ((belowBlock == BlockList.grass.id || belowBlock == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
            for (int hght = y - 5 + height; hght <= y + height; hght++) {
                int x0 = hght - (y + height);
                int z0 = 1 - x0 / 2;

                for (int xx = x - z0; xx <= x + z0; xx++) {
                    int width = xx - x;

                    for (int var16 = z - z0; var16 <= z + z0; var16++) {
                        int lght = var16 - z;
                        if (Math.abs(width) != z0 || Math.abs(lght) != z0 || x0 == 0) {
                            world.setBlockAndMetadata(xx, hght, var16, BlockList.leaves.id, 14);
                        }
                    }
                }
            }

            for (int var21 = 0; var21 < height; var21++) {
                world.setBlockAndMetadata(x, y + var21, z, BlockList.wood.id, 15);
                if (var21 == height - 5 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x + 3, y + var21, z + 3, BlockList.leaves.id, 14);
                }

                if (var21 == height - 5 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x - 3, y + var21, z + 3, BlockList.leaves.id, 14);
                }

                if (var21 == height - 5 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x + 3, y + var21, z - 3, BlockList.leaves.id, 14);
                }

                if (var21 == height - 5 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x - 3, y + var21, z - 3, BlockList.leaves.id, 14);
                }

                if (var21 == height - 3 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x + 2, y + var21, z + 2, BlockList.leaves.id, 14);
                }

                if (var21 == height - 3 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x - 2, y + var21, z + 2, BlockList.leaves.id, 14);
                }

                if (var21 == height - 3 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x + 2, y + var21, z - 2, BlockList.leaves.id, 14);
                }

                if (var21 == height - 3 && random.nextBoolean()) {
                    world.setBlockAndMetadata(x - 2, y + var21, z - 2, BlockList.leaves.id, 14);
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
