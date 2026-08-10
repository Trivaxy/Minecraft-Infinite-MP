package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.MathHelper;

public class JungleTree extends Tree {
    final int leavesId = BlockList.leaves.id;
    final int woodId = BlockList.wood.id;
    int woodMeta = 5;
    int leavesMeta = 6;
    int height = 0;
    boolean vines = true;

    public JungleTree() {
    }

    public JungleTree(int height, int wood, int leaves) {
        this.height = height;
        this.woodMeta = wood;
        this.leavesMeta = leaves;
        this.vines = false;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (this.height == 0) {
            this.height = random.nextInt(8) + 8;
        }

        int i = random.nextInt(3) + this.height;
        boolean flag = true;
        if (y >= 1 && y + i + 1 <= world.getWorldHeight()) {
            for (int j = y; j <= y + 1 + i; j++) {
                byte radius = 2;
                if (j == y) {
                    radius = 1;
                }

                if (j >= y + 1 + i - 2) {
                    radius = 2;
                }

                for (int xx = x - radius; xx <= x + radius && flag; xx++) {
                    for (int zz = z - radius; zz <= z + radius && flag; zz++) {
                        if (j >= 0 && j < world.getWorldHeight()) {
                            int k2 = world.getBlockId(xx, j, zz);
                            if (k2 != 0
                                && k2 != BlockList.snowLayer.id
                                && k2 != BlockList.leaves.id
                                && k2 != BlockList.grass.id
                                && k2 != BlockList.dirt.id
                                && k2 != this.woodId
                                && k2 != BlockList.sapling.id) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            }

            int diveFor = 4;

            while (
                y > 32
                    && (
                        world.getBlockId(x, y - 1, z) == BlockList.waterMoving.id
                            || world.getBlockId(x + 1, y - 1, z + 1) == BlockList.waterMoving.id
                            || world.getBlockId(x + 1, y - 1, z) == BlockList.waterMoving.id
                            || world.getBlockId(x, y - 1, z + 1) == BlockList.waterMoving.id
                    )
            ) {
                y--;
                if (--diveFor == 0) {
                    return false;
                }
            }

            int blockID = world.getBlockId(x, y - 1, z);
            if ((blockID == BlockList.grass.id || blockID == BlockList.dirt.id) && y < 256 - i - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x + 1, y - 1, z, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x, y - 1, z + 1, BlockList.dirt.id, 2);
                world.setBlockAndMetadata(x + 1, y - 1, z + 1, BlockList.dirt.id, 2);
                this.drawLeaves(world, x, z, y + i, 2, random, this.leavesId, this.leavesMeta);

                for (int l = y + i - 2 - random.nextInt(4); l > y + i / 2; l -= 2 + random.nextInt(4)) {
                    float f = random.nextFloat() * (float) Math.PI * 2.0F;
                    int l1 = x + (int)(0.5F + MathHelper.cos(f) * 4.0F);
                    int l2 = z + (int)(0.5F + MathHelper.sin(f) * 4.0F);
                    this.drawLeaves(world, l1, l2, l, 0, random, this.leavesId, this.leavesMeta);

                    for (int j3 = 0; j3 < 5; j3++) {
                        int i2 = x + (int)(1.5F + MathHelper.cos(f) * j3);
                        int i3 = z + (int)(1.5F + MathHelper.sin(f) * j3);
                        world.setBlockAndMetadata(i2, l - 3 + j3 / 2, i3, this.woodId, this.woodMeta);
                    }
                }

                for (int j1 = 0; j1 < i; j1++) {
                    blockID = world.getBlockId(x, y + j1, z);
                    if (blockID == 0 || blockID == this.leavesId) {
                        world.setBlockAndMetadata(x, y + j1, z, this.woodId, this.woodMeta);
                        if (j1 > 0 && this.vines) {
                            if (random.nextInt(3) > 0 && 0 == world.getBlockId(x - 1, y + j1, z)) {
                                world.setBlockAndMetadata(x - 1, y + j1, z, BlockList.vine.id, 4);
                            }

                            if (random.nextInt(3) > 0 && 0 == world.getBlockId(x, y + j1, z - 1)) {
                                world.setBlockAndMetadata(x, y + j1, z - 1, BlockList.vine.id, 2);
                            }
                        }
                    }

                    if (j1 < i - 1) {
                        blockID = world.getBlockId(x + 1, y + j1, z);
                        if (blockID == 0 || blockID == this.leavesId) {
                            world.setBlockAndMetadata(x + 1, y + j1, z, this.woodId, this.woodMeta);
                            if (j1 > 0 && this.vines) {
                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x + 2, y + j1, z)) {
                                    world.setBlockAndMetadata(x + 2, y + j1, z, BlockList.vine.id, 5);
                                }

                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x + 1, y + j1, z - 1)) {
                                    world.setBlockAndMetadata(x + 1, y + j1, z - 1, BlockList.vine.id, 2);
                                }
                            }
                        }

                        blockID = world.getBlockId(x + 1, y + j1, z + 1);
                        if (blockID == 0 || blockID == this.leavesId) {
                            world.setBlockAndMetadata(x + 1, y + j1, z + 1, this.woodId, this.woodMeta);
                            if (j1 > 0 && this.vines) {
                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x + 2, y + j1, z + 1)) {
                                    world.setBlockAndMetadata(x + 2, y + j1, z + 1, BlockList.vine.id, 5);
                                }

                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x + 1, y + j1, z + 2)) {
                                    world.setBlockAndMetadata(x + 1, y + j1, z + 2, BlockList.vine.id, 3);
                                }
                            }
                        }

                        blockID = world.getBlockId(x, y + j1, z + 1);
                        if (blockID == 0 || blockID == this.leavesId) {
                            world.setBlockAndMetadata(x, y + j1, z + 1, this.woodId, this.woodMeta);
                            if (j1 > 0) {
                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x - 1, y + j1, z + 1) && this.vines) {
                                    world.setBlockAndMetadata(x - 1, y + j1, z + 1, BlockList.vine.id, 4);
                                }

                                if (random.nextInt(3) > 0 && 0 == world.getBlockId(x, y + j1, z + 2) && this.vines) {
                                    world.setBlockAndMetadata(x, y + j1, z + 2, BlockList.vine.id, 3);
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

    private void drawLeaves(World world, int x, int z, int y, int frnd, Random random, int leavesId, int meta) {
        byte radius = 2;

        for (int i = y - radius; i <= y; i++) {
            int j = i - y;
            int k = frnd + 1 - j;

            for (int l = x - k; l <= x + k + 1; l++) {
                int i1 = l - x;

                for (int j1 = z - k; j1 <= z + k + 1; j1++) {
                    int k1 = j1 - z;
                    int i1sq = i1 * i1;
                    int k1sq = k1 * k1;
                    if ((i1 >= 0 || k1 >= 0 || i1sq + k1sq <= k * k)
                        && (i1 <= 0 && k1 <= 0 || i1sq + k1sq <= (k + 1) * (k + 1))
                        && (random.nextInt(4) != 0 || i1sq + k1sq <= (k - 1) * (k - 1))
                        && !Block.isOpaque.get(world.getBlockId(l, i, j1))) {
                        world.setBlockAndMetadata(l, i, j1, leavesId, meta);
                    }
                }
            }
        }
    }

    protected void setVines(World world, int x, int y, int z, int meta) {
        world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);

        for (int height = 4; world.getBlockId(x, --y, z) == 0 && height > 0; height--) {
            world.setBlockAndMetadata(x, y, z, BlockList.vine.id, meta);
        }
    }
}
