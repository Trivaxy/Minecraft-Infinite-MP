package net.minecraft.game.world.generation.features.trees.misc;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class SmallTree extends Tree {
    public static final SmallTree miniPalm = new SmallTree(3, 4);
    public static final SmallTree miniAcacia = new SmallTree(4, 5);
    int woodMeta;
    int leavesMeta;

    public SmallTree(int meta1, int meta2) {
        this.woodMeta = meta1;
        this.leavesMeta = meta2;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(2) + 2;
        boolean canPlace = true;
        if (y > 0 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte width = 1;
                if (y0 == height) {
                    width = 0;
                }

                for (int x0 = x - width; x0 <= x + width && canPlace; x0++) {
                    for (int z0 = z - width; z0 <= z + width && canPlace; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                                canPlace = false;
                            }
                        } else {
                            canPlace = true;
                        }
                    }
                }
            }

            if (!canPlace) {
                return false;
            }

            int x0 = world.getBlockId(x, y - 1, z);
            if ((x0 == BlockList.grass.id || x0 == BlockList.dirt.id || x0 == BlockList.moss.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlock(x, y - 1, z, BlockList.dirt.id);
                int leafHeight = 1;

                for (int realHeight = y - leafHeight + height; realHeight <= y + height; realHeight++) {
                    x0 = realHeight - (y + height);

                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        int width = xx - x;

                        for (int var16 = z - 1; var16 <= z + 1; var16++) {
                            int length = var16 - z;
                            if (x0 == -1 || x0 == -2 && height == 4 || Math.abs(width) != 1 || Math.abs(length) != 1) {
                                world.setBlockAndMetadata(xx, realHeight, var16, BlockList.leaves.id, this.leavesMeta);
                            }
                        }
                    }
                }

                for (int var20 = 0; var20 < height; var20++) {
                    int i10 = world.getBlockId(x, y + var20, z);
                    if (i10 == 0 || i10 == BlockList.leaves.id) {
                        world.setBlockAndMetadata(x, y + var20, z, BlockList.wood.id, this.woodMeta);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
