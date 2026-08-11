package net.minecraft.game.world.generation.features.trees.deciduous;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class BasicTree extends Tree {
    public BasicTree(int fillerBlockM) {
        this(0, fillerBlockM);
    }

    public BasicTree(int leafDamage, int fillerBlockM) {
        this.leafMeta = leafDamage;
        this.fillerBlockMetadata = fillerBlockM;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(3) + 4;
        boolean canPlace = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte size = 1;
                if (y0 == y) {
                    size = 0;
                }

                if (y0 >= y + 1 + height - 2) {
                    size = 2;
                }

                for (int x0 = x - size; x0 <= x + size && canPlace; x0++) {
                    for (int z0 = z - size; z0 <= z + size && canPlace; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id) {
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

            int var16 = world.getBlockId(x, y - 1, z);
            if ((var16 == BlockList.grass.id || var16 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, this.fillerBlockMetadata);

                for (int i = y - 3 + height; i <= y + height; i++) {
                    int x0 = i - (y + height);
                    int z0 = 1 - x0 / 2;

                    for (int id = x - z0; id <= x + z0; id++) {
                        int xFactor = id - x;

                        for (int zz = z - z0; zz <= z + z0; zz++) {
                            int zFactor = zz - z;
                            if ((Math.abs(xFactor) != z0 || Math.abs(zFactor) != z0 || random.nextInt(2) != 0 && x0 != 0)
                                && !Block.isOpaque.get(world.getBlockId(id, i, zz))) {
                                world.setBlockAndMetadata(id, i, zz, BlockList.leaves.id, this.leafMeta);
                            }
                        }
                    }
                }

                for (int var22 = 0; var22 < height; var22++) {
                    int x0 = world.getBlockId(x, y + var22, z);
                    if (x0 == 0 || x0 == BlockList.leaves.id) {
                        world.setBlock(x, y + var22, z, BlockList.wood.id);
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
