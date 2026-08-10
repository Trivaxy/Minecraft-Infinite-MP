package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class PineTree extends Tree {
    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(4) + 6;
        int size1 = 1 + random.nextInt(2);
        int heightOffset = height - size1;
        int size2 = 2 + random.nextInt(2);
        boolean canGenerate = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height && canGenerate; y0++) {
                int canopyWidth;
                if (y0 - y < size1) {
                    canopyWidth = 0;
                } else {
                    canopyWidth = size2;
                }

                for (int x0 = x - canopyWidth; x0 <= x + canopyWidth && canGenerate; x0++) {
                    for (int z0 = z - canopyWidth; z0 <= z + canopyWidth && canGenerate; z0++) {
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

            int var22 = world.getBlockId(x, y - 1, z);
            if ((var22 == BlockList.grass.id || var22 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, world.getBiomeAt(x, z).fillerMeta);
                int canopyWidth = random.nextInt(2);
                int x0 = 1;
                byte width = 0;

                for (int id = 0; id <= heightOffset; id++) {
                    int yy = y + height - id;

                    for (int xx = x - canopyWidth; xx <= x + canopyWidth; xx++) {
                        int width1 = xx - x;

                        for (int zz = z - canopyWidth; zz <= z + canopyWidth; zz++) {
                            int width2 = zz - z;
                            if ((Math.abs(width1) != canopyWidth || Math.abs(width2) != canopyWidth || canopyWidth <= 0)
                                && !Block.isOpaque.get(world.getBlockId(xx, yy, zz))) {
                                world.setBlockAndMetadata(xx, yy, zz, BlockList.leaves.id, 3);
                            }
                        }
                    }

                    if (canopyWidth >= x0) {
                        canopyWidth = width;
                        width = 1;
                        if (++x0 > size2) {
                            x0 = size2;
                        }
                    } else {
                        canopyWidth++;
                    }
                }

                int var25 = random.nextInt(3);

                for (int yy = 0; yy < height - var25; yy++) {
                    int xx = world.getBlockId(x, y + yy, z);
                    if (xx == 0 || xx == BlockList.leaves.id || xx == BlockList.snowLayer.id) {
                        world.setBlockAndMetadata(x, y + yy, z, BlockList.wood.id, 2);
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
