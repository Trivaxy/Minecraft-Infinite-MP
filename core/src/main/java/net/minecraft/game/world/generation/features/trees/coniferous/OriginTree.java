package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class OriginTree extends Tree {
    public static final OriginTree INSTANCE = new OriginTree();

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(2) + 4;
        boolean canGenerate = true;
        if (y > 0 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte b7 = 1;
                if (y0 == height) {
                    b7 = 0;
                }

                for (int x0 = x - b7; x0 <= x + b7 && canGenerate; x0++) {
                    for (int z0 = z - b7; z0 <= z + b7 && canGenerate; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                                canGenerate = false;
                            }
                        } else {
                            canGenerate = true;
                        }
                    }
                }
            }

            if (!canGenerate) {
                return false;
            }

            int x0 = world.getBlockId(x, y - 1, z);
            if ((x0 == BlockList.voidGrass.id || x0 == BlockList.grass.id || x0 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                int leafHeight = 2;

                for (int yy = y - leafHeight + height; yy <= y + height; yy++) {
                    x0 = yy - (y + height);

                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        int canopyWidth = xx - x;

                        for (int var16 = z - 1; var16 <= z + 1; var16++) {
                            int width = var16 - z;
                            if (Math.abs(x0) != 0 || Math.abs(canopyWidth) != 1 || Math.abs(width) != 1) {
                                world.setBlockAndMetadata(xx, yy, var16, BlockList.leaves.id, 17);
                            }
                        }
                    }
                }

                for (int var20 = 0; var20 < height; var20++) {
                    int id = world.getBlockId(x, y + var20, z);
                    if (id == 0 || id == BlockList.leaves.id) {
                        world.setBlockAndMetadata(x, y + var20, z, BlockList.wood.id, 8);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
