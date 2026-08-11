package net.minecraft.game.world.generation.features.trees.coniferous;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.MathHelper;

public class MegaPine extends Tree {
    Random rng = new Random();
    int baseHeight = 13;
    int heightOffset = 15;
    int woodMetadata = 2;
    int leavesMetadata = 3;
    boolean maxHeight = false;

    public MegaPine() {
    }

    public MegaPine(int baseH, int hOffs, int woodmeta, int leafmeta, boolean thing) {
        this.baseHeight = baseH;
        this.heightOffset = hOffs;
        this.woodMetadata = woodmeta;
        this.leavesMetadata = leafmeta;
        this.maxHeight = thing;
    }

    protected boolean checkMaterialValid(Block cock) {
        return cock == null
            || cock.material == MaterialList.air
            || cock == BlockList.snowLayer
            || cock.material == MaterialList.leaves
            || cock == BlockList.grass
            || cock == BlockList.dirt
            || cock == BlockList.wood
            || cock == BlockList.sapling
            || cock == BlockList.vine;
    }

    protected void setBlock(World world, int x, int y, int z, Block cock, int metad) {
        world.setBlockAndMetadata(x, y, z, cock.id, metad);
    }

    protected int randomHeight(Random random, int baseHeight, int heightOffset) {
        int hght = random.nextInt(3) + baseHeight;
        if (heightOffset > 1) {
            hght += random.nextInt(heightOffset);
        }

        return hght;
    }

    protected boolean checkBounds(World world, Random random, int x, int y, int z, int yo) {
        boolean canGenerate = true;
        if (y >= 1 && y + yo + 1 <= 256) {
            for (int yy = y; yy <= y + 1 + yo; yy++) {
                byte size = 2;
                if (yy == y) {
                    size = 1;
                }

                if (yy >= y + 1 + yo - 2) {
                    size = 2;
                }

                for (int xx = x - size; xx <= x + size && canGenerate; xx++) {
                    for (int zz = z - size; zz <= z + size && canGenerate; zz++) {
                        if (yy >= 0 && yy < 256) {
                            Block block = world.getBlock(xx, yy, zz);
                            if (!this.checkMaterialValid(block)) {
                                canGenerate = false;
                            }
                        } else {
                            canGenerate = false;
                        }
                    }
                }
            }

            return canGenerate;
        } else {
            return false;
        }
    }

    protected boolean grassDirtCheck(World world, Random random, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        if ((block == BlockList.grass || block == BlockList.dirt) && y >= 2) {
            world.setBlockAndMetadata(x, y - 1, z, BlockList.dirt.id, 1);
            world.setBlockAndMetadata(x + 1, y - 1, z, BlockList.dirt.id, 1);
            world.setBlockAndMetadata(x, y - 1, z + 1, BlockList.dirt.id, 1);
            world.setBlockAndMetadata(x + 1, y - 1, z + 1, BlockList.dirt.id, 1);
            return true;
        } else {
            return false;
        }
    }

    protected boolean canGenerate(World world, Random random, int x, int y, int z, int rad) {
        return this.checkBounds(world, random, x, y, z, rad) && this.grassDirtCheck(world, random, x, y, z);
    }

    protected void setLeaves(World world, int x, int y, int z, int rad, Random rand, int leavesMeta) {
        int width7 = rad * rad;

        for (int x0 = x - rad; x0 <= x + rad + 1; x0++) {
            int xx = x0 - x;

            for (int z0 = z - rad; z0 <= z + rad + 1; z0++) {
                int zz = z0 - z;
                int diffX = xx - 1;
                int diffZ = zz - 1;
                if (xx * xx + zz * zz <= width7
                    || diffX * diffX + diffZ * diffZ <= width7
                    || xx * xx + diffZ * diffZ <= width7
                    || diffX * diffX + zz * zz <= width7) {
                    Block block = world.getBlock(x0, y, z0);
                    if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                        this.setBlock(world, x0, y, z0, BlockList.leaves, leavesMeta);
                    }
                }
            }
        }
    }

    protected void generateCrown(World world, int x, int y, int z, int offset, Random random, int leafMeta) {
        int radius = offset * offset;

        for (int x0 = x - offset; x0 <= x + offset; x0++) {
            int diffX = x0 - x;

            for (int z0 = z - offset; z0 <= z + offset; z0++) {
                int diffZ = z0 - z;
                if (diffX * diffX + diffZ * diffZ <= radius) {
                    Block block = world.getBlock(x0, y, z0);
                    if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                        this.setBlock(world, x0, y, z0, BlockList.leaves, leafMeta);
                    }
                }
            }
        }
    }

    protected void generateLayers(World w, int a, int b, int c, int d, Random random, int leafMeta, boolean height_thing, int baseHeight) {
        int height = random.nextInt(5);
        int off = 0;
        if (height_thing) {
            height += baseHeight;
        } else {
            height += 3;
        }

        for (int y = c - height; y <= c; y++) {
            int x = c - y;
            int z = d + MathHelper.floor_float((float)x / height * 3.5F);
            this.setLeaves(w, a, y, b, z + (x > 0 && z == off && (y & 1) == 0 ? 1 : 0), random, leafMeta);
            off = z;
        }
    }

    protected void replaceRoots(World world, Random random, int x, int y, int z) {
        for (int xOff = -2; xOff <= 2; xOff++) {
            for (int zOff = -2; zOff <= 2; zOff++) {
                if (Math.abs(xOff) != 2 || Math.abs(zOff) != 2) {
                    this.setGroundBelow(world, x + xOff, y, z + zOff);
                }
            }
        }
    }

    protected void setGroundBelow(World world, int x, int y, int z) {
        for (int yy = y + 2; yy >= y - 3; yy--) {
            Block block = world.getBlock(x, yy, z);
            if (block == BlockList.grass || block == BlockList.dirt) {
                this.setBlock(world, x, yy, z, BlockList.dirt, 1);
                break;
            }

            if (block == null || block.material != MaterialList.air && yy < y) {
                break;
            }
        }
    }

    protected void afterGenerate(World world, Random random, int x, int y, int z) {
        this.replaceRoots(world, random, x - 1, y, z - 1);
        this.replaceRoots(world, random, x + 2, y, z - 1);
        this.replaceRoots(world, random, x - 1, y, z + 2);
        this.replaceRoots(world, random, x + 2, y, z + 2);

        for (int attempts = 0; attempts < 5; attempts++) {
            int tryOff = random.nextInt(64);
            int xOff = tryOff % 8;
            int zOff = tryOff / 8;
            if (xOff == 0 || xOff == 7 || zOff == 0 || zOff == 7) {
                this.replaceRoots(world, random, x - 3 + xOff, y, z - 3 + zOff);
            }
        }
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        boolean ret = false;
        this.rng.setSeed(random.nextLong());
        this.maxHeight = this.rng.nextBoolean();
        int height = this.randomHeight(this.rng, this.baseHeight, this.heightOffset);
        if (!this.canGenerate(world, this.rng, x, y, z, height)) {
            ret = false;
        } else {
            this.generateLayers(world, x, z, y + height, 0, random, this.leavesMetadata, this.maxHeight, this.baseHeight);

            for (int yy = 0; yy < height; yy++) {
                Block block = world.getBlock(x, y + yy, z);
                if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                    this.setBlock(world, x, y + yy, z, BlockList.wood, this.woodMetadata);
                }

                if (yy < height - 1) {
                    block = world.getBlock(x + 1, y + yy, z);
                    if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                        this.setBlock(world, x + 1, y + yy, z, BlockList.wood, this.woodMetadata);
                    }

                    block = world.getBlock(x + 1, y + yy, z + 1);
                    if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                        this.setBlock(world, x + 1, y + yy, z + 1, BlockList.wood, this.woodMetadata);
                    }

                    block = world.getBlock(x, y + yy, z + 1);
                    if (block == null || block.material == MaterialList.air || block.material == MaterialList.leaves) {
                        this.setBlock(world, x, y + yy, z + 1, BlockList.wood, this.woodMetadata);
                    }
                }
            }

            ret = true;
        }

        if (ret) {
            this.afterGenerate(world, this.rng, x, y, z);
        }

        return ret;
    }
}
