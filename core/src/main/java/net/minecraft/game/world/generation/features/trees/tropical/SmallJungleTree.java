package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class SmallJungleTree extends JungleTree {
    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(4) + 7;
        boolean place = true;
        if (y >= 1 && y + height + 1 <= world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte size = 1;
                if (y0 == y) {
                    size = 0;
                }

                if (y0 >= y + 1 + height - 2) {
                    size = 2;
                }

                for (int x0 = x - size; x0 <= x + size && place; x0++) {
                    for (int z0 = z - size; z0 <= z + size && place; z0++) {
                        if (y0 >= 0 && y0 < world.getWorldHeight()) {
                            int id = world.getBlockId(x0, y0, z0);
                            if (id != 0 && id != BlockList.leaves.id && id != BlockList.snowLayer.id) {
                                place = false;
                            }
                        } else {
                            place = false;
                        }
                    }
                }
            }

            if (!place) {
                return false;
            }

            int var16 = world.getBlockId(x, y - 1, z);
            if ((var16 == BlockList.grass.id || var16 == BlockList.dirt.id) && y < world.getWorldHeight() - height - 1) {
                world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, 2);

                for (int i = y - 3 + height; i <= y + height; i++) {
                    int x0 = i - (y + height);
                    int z0 = 1 - x0 / 2;

                    for (int id = x - z0; id <= x + z0; id++) {
                        int i13 = id - x;

                        for (int i14 = z - z0; i14 <= z + z0; i14++) {
                            int i15 = i14 - z;
                            if ((Math.abs(i13) != z0 || Math.abs(i15) != z0 || random.nextInt(2) != 0 && x0 != 0)
                                && !Block.isOpaque.get(world.getBlockId(id, i, i14))) {
                                world.setBlockAndMetadata(id, i, i14, BlockList.leaves.id, 6);
                            }
                        }
                    }
                }

                for (int var24 = 0; var24 < height; var24++) {
                    int x0 = world.getBlockId(x, y + var24, z);
                    if (x0 == 0 || x0 == BlockList.leaves.id) {
                        world.setBlockAndMetadata(x, y + var24, z, BlockList.wood.id, 5);
                        if (var24 > 0) {
                            if (random.nextInt(3) > 0 && world.isAir(x - 1, y + var24, z)) {
                                world.setBlockAndMetadata(x - 1, y + var24, z, BlockList.vine.id, 4);
                            }

                            if (random.nextInt(3) > 0 && world.isAir(x + 1, y + var24, z)) {
                                world.setBlockAndMetadata(x + 1, y + var24, z, BlockList.vine.id, 5);
                            }

                            if (random.nextInt(3) > 0 && world.isAir(x, y + var24, z - 1)) {
                                world.setBlockAndMetadata(x, y + var24, z - 1, BlockList.vine.id, 2);
                            }

                            if (random.nextInt(3) > 0 && world.isAir(x, y + var24, z + 1)) {
                                world.setBlockAndMetadata(x, y + var24, z + 1, BlockList.vine.id, 3);
                            }
                        }
                    }
                }

                for (int z0 = y - 3 + height; z0 <= y + height; z0++) {
                    int id = z0 - (y + height);
                    int xx = 2 - id / 2;

                    for (int yy = x - xx; yy <= x + xx; yy++) {
                        for (int zz = z - xx; zz <= z + xx; zz++) {
                            if (world.getBlockId(yy, z0, zz) == BlockList.leaves.id) {
                                if (random.nextInt(4) == 0 && world.getBlockId(yy - 1, z0, zz) == 0) {
                                    this.setVines(world, yy - 1, z0, zz, 4);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(yy + 1, z0, zz) == 0) {
                                    this.setVines(world, yy + 1, z0, zz, 5);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(yy, z0, zz - 1) == 0) {
                                    this.setVines(world, yy, z0, zz - 1, 2);
                                }

                                if (random.nextInt(4) == 0 && world.getBlockId(yy, z0, zz + 1) == 0) {
                                    this.setVines(world, yy, z0, zz + 1, 3);
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
