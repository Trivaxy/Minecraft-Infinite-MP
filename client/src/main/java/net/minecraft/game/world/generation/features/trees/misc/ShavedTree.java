package net.minecraft.game.world.generation.features.trees.misc;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class ShavedTree extends Tree {
    public ShavedTree(int dmg, int leafDamage, int fillerBlockM) {
        this.logMeta = dmg;
        this.leafMeta = leafDamage;
        this.fillerBlockMetadata = fillerBlockM;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(8) + 7;
        boolean canGenerate = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte rng = 1;
                if (y0 == y) {
                    rng = 0;
                }

                if (y0 >= y + 1 + height - 2) {
                    rng = 2;
                }

                for (int x0 = x - rng; x0 <= x + rng && canGenerate; x0++) {
                    for (int z0 = z - rng; z0 <= z + rng && canGenerate; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.waterStill.id && id != BlockList.snowLayer.id) {
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

            int var14 = world.getBlockId(x, y - 1, z);
            if ((var14 == BlockList.grass.id || var14 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, this.fillerBlockMetadata);

                for (int realHeight = y - 8 + height; realHeight <= y + height; realHeight++) {
                    for (int id = x - 1; id <= x + 1; id++) {
                        for (int zz = z - 1; zz <= z + 1; zz++) {
                            if (random.nextInt(3) == 0 && !Block.isOpaque.get(world.getBlockId(id, realHeight, zz))) {
                                world.setBlockAndMetadata(id, realHeight, zz, BlockList.leaves.id, this.leafMeta);
                            }
                        }
                    }
                }

                for (int var18 = 0; var18 < height; var18++) {
                    int x0 = world.getBlockId(x, y + var18, z);
                    if (x0 == 0 || x0 == BlockList.leaves.id) {
                        world.setBlockAndMetadata(x, y + var18, z, BlockList.wood.id, this.logMeta);
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
