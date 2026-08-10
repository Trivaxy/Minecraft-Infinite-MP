package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class FirTree extends Tree {
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 14;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 15;
    public int height = 6;
    public boolean large = false;

    public FirTree(int height, boolean large) {
        this.height = height;
        this.large = large;
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        if (this.large) {
            if (y0 + this.height > world.getWorldHeight() - 2) {
                return false;
            }

            if (!this.validGround(world, x0, y0 - 1, z0)) {
                return false;
            }

            if (!this.validGround(world, x0, y0 - 1, z0 + 1)) {
                return false;
            }

            if (!this.validGround(world, x0 + 1, y0 - 1, z0)) {
                return false;
            }

            if (!this.validGround(world, x0 + 1, y0 - 1, z0 + 1)) {
                return false;
            }

            for (int y = 0; y < this.height + 18; y++) {
                for (int x = 0; x <= 1; x++) {
                    for (int z = 0; z <= 1; z++) {
                        if (world.isSolid(x0 + x, y0 + y, z0 + z)) {
                            return false;
                        }
                    }
                }
            }

            for (int y = 0; y < 18; y++) {
                for (int i = 0; i <= 5; i++) {
                    for (int j = 0; j <= i; j++) {
                        if (world.isSolid(x0 - 5 + i, y0 + y, z0 - i)) {
                            return false;
                        }

                        if (world.isSolid(x0 - 5 + i, y0 + y, z0 + 1 + i)) {
                            return false;
                        }

                        if (world.isSolid(x0 + 6 - i, y0 + y, z0 - i)) {
                            return false;
                        }

                        if (world.isSolid(x0 + 6 - i, y0 + y, z0 + 1 + i)) {
                            return false;
                        }
                    }
                }
            }

            int trunkTop = y0 + this.height;

            for (int y = y0; y < trunkTop + 18; y++) {
                this.setBlockIfEmpty(world, x0, y, z0, trunkID, 15);
                this.setBlockIfEmpty(world, x0, y, z0 + 1, trunkID, 15);
                this.setBlockIfEmpty(world, x0 + 1, y, z0, trunkID, 15);
                this.setBlockIfEmpty(world, x0 + 1, y, z0 + 1, trunkID, 15);
            }

            for (int y = 0; y < 3; y++) {
                this.squareTreeLayer(world, rand, x0, trunkTop + 18 + y, z0, 2, leavesID, 14);
                this.diamondTreeLayer(world, rand, x0, trunkTop + 15 + y, z0, 4, leavesID, 14);
                this.diamondTreeLayer(world, rand, x0, trunkTop + 12 + y, z0, 6, leavesID, 14);
            }

            this.diamondTreeLayer(world, rand, x0, trunkTop + 9, z0, 6, leavesID, 14);
            this.diamondTreeLayer(world, rand, x0, trunkTop + 8, z0, 8, leavesID, 14);
            this.diamondTreeLayer(world, rand, x0, trunkTop + 5, z0, 8, leavesID, 14);
            this.diamondTreeLayer(world, rand, x0, trunkTop + 4, z0, 10, leavesID, 14);
            this.diamondTreeLayer(world, rand, x0, trunkTop + 1, z0, 10, leavesID, 14);
            this.diamondTreeLayer(world, rand, x0, trunkTop + 0, z0, 12, leavesID, 14);
            return true;
        } else {
            this.height -= 3;
            if (this.height < 3) {
                this.height = 3;
            }

            if (y0 + this.height > world.getWorldHeight() - 2) {
                return false;
            }

            if (!this.validGround(world, x0, y0 - 1, z0)) {
                return false;
            }

            for (int y = y0; y < y0 + this.height + 5; y++) {
                if (world.isSolid(x0, y, z0)) {
                    return false;
                }
            }

            for (int y = y0 + this.height; y < y0 + this.height + 5; y++) {
                for (int i = 0; i < 5; i++) {
                    if (world.isSolid(x0 + i - 2, y, z0)) {
                        return false;
                    }

                    if (world.isSolid(x0, y, z0 + i - 2)) {
                        return false;
                    }
                }

                for (int x = x0 - 1; x <= x0 + 1; x++) {
                    for (int z = z0 - 1; z <= z0 + 1; z++) {
                        if (world.isSolid(x, y, z)) {
                            return false;
                        }
                    }
                }
            }

            int treeTop = y0 + this.height + 5;

            for (int y = y0; y < y0 + this.height + 5; y++) {
                this.setBlockIfEmpty(world, x0, y, z0, trunkID, 15);
            }

            for (int y = 0; y < 2; y++) {
                this.setBlockIfEmpty(world, x0, treeTop + y, z0, leavesID, 14);
                this.crossTreeLayer(world, rand, x0, treeTop - 1 - y, z0, 3, leavesID, 14);
            }

            this.crossTreeLayer(world, rand, x0, treeTop - 4, z0, 3, leavesID, 14);
            this.crossTreeLayer(world, rand, x0, y0 + this.height, z0, 5, leavesID, 14);
            this.squareTreeLayer(world, rand, x0, y0 + this.height, z0, 3, leavesID, 14);
            return true;
        }
    }
}
