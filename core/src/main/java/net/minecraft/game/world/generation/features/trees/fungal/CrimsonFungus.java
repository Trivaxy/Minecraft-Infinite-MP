package net.minecraft.game.world.generation.features.trees.fungal;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class CrimsonFungus extends Tree {
    public int stemID = BlockList.wood.id;
    public int capID = BlockList.mushroomCap.id;
    public int shroomlightID = BlockList.shroomLight.id;
    public int validBaseID = BlockList.grass.id;
    public int logMeta;
    public int capMeta;
    public boolean planted;

    public CrimsonFungus(int stem, int cap, boolean planted) {
        this.logMeta = stem;
        this.capMeta = cap;
        this.planted = planted;
    }

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        int below = world.getBlockId(x, y - 1, z);
        if (below != this.validBaseID) {
            return false;
        }

        int height = rand.nextInt(6) + 4;
        if (rand.nextInt(12) == 0) {
            height *= 2;
        }

        boolean thickStem = !this.planted && rand.nextFloat() < 0.06F;
        if (y >= 1 && y + height + 1 < world.getWorldHeight()) {
            boolean canPlace = true;

            for (int yy = y; yy <= y + height + 1 && canPlace; yy++) {
                int radius = 0;
                byte var15;
                if (yy == y) {
                    var15 = 0;
                } else if (yy >= y + height - 2) {
                    var15 = 3;
                } else {
                    var15 = (byte)(thickStem ? 1 : 0);
                }

                for (int xx = x - var15; xx <= x + var15 && canPlace; xx++) {
                    for (int zz = z - var15; zz <= z + var15 && canPlace; zz++) {
                        if (yy < 0 || yy >= world.getWorldHeight()) {
                            canPlace = false;
                            break;
                        }

                        int block = world.getBlockId(xx, yy, zz);
                        if (block != 0 && block != BlockList.mushroomCrimson.id) {
                            canPlace = false;
                        }
                    }
                }
            }

            if (!canPlace) {
                return false;
            }

            world.setBlock(x, y, z, 0);
            this.buildStem(world, rand, x, y, z, height, thickStem);
            this.buildCap(world, rand, x, y, z, height, thickStem);
            return true;
        } else {
            return false;
        }
    }

    private boolean canReplace(World world, int x, int y, int z, boolean allowPlants) {
        if (y >= 0 && y < world.getWorldHeight()) {
            int id = world.getBlockId(x, y, z);
            return id == 0 ? true : allowPlants && (id == BlockList.mushroomCrimson.id || id == BlockList.mushroomNylium.id);
        } else {
            return false;
        }
    }

    private void buildStem(World world, Random rand, int x, int y, int z, int height, boolean thick) {
        int radius = thick ? 1 : 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                boolean corner = thick && Math.abs(dx) == radius && Math.abs(dz) == radius;

                for (int dy = 0; dy < height; dy++) {
                    int px = x + dx;
                    int py = y + dy;
                    int pz = z + dz;
                    if (this.canReplace(world, px, py, pz, true)) {
                        if (this.planted) {
                            if (world.getBlockId(px, py - 1, pz) != 0) {
                                world.setBlock(px, py, pz, 0);
                            }

                            world.setBlockAndMetadata(px, py, pz, this.stemID, this.logMeta);
                        } else if (corner) {
                            if (rand.nextFloat() < 0.1F) {
                                world.setBlockAndMetadata(px, py, pz, this.stemID, this.logMeta);
                            }
                        } else {
                            world.setBlockAndMetadata(px, py, pz, this.stemID, this.logMeta);
                        }
                    }
                }
            }
        }
    }

    private void buildCap(World world, Random rand, int x, int y, int z, int height, boolean thick) {
        int capHeight = Math.min(rand.nextInt(1 + height / 3) + 5, height);
        int start = height - capHeight;

        for (int dy = start; dy <= height; dy++) {
            int radius = dy < height - rand.nextInt(3) ? 2 : 1;
            if (capHeight > 8 && dy < start + 4) {
                radius = 3;
            }

            if (thick) {
                radius++;
            }

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int px = x + dx;
                    int py = y + dy;
                    int pz = z + dz;
                    if (this.canReplace(world, px, py, pz, false)) {
                        boolean edgeX = dx == -radius || dx == radius;
                        boolean edgeZ = dz == -radius || dz == radius;
                        boolean isCorner = edgeX && edgeZ;
                        boolean isMiddle = !edgeX && !edgeZ && dy != height;
                        boolean nearBase = dy < start + 3;
                        if (nearBase) {
                            if (!isMiddle) {
                                this.placeCapBlock(world, rand, px, py, pz);
                            }
                        } else if (isMiddle) {
                            this.pickDecoration(world, rand, px, py, pz, 0.1F, 0.2F, 0.0F);
                        } else if (isCorner) {
                            this.pickDecoration(world, rand, px, py, pz, 0.01F, 0.7F, 0.0F);
                        } else {
                            this.pickDecoration(world, rand, px, py, pz, 5.0E-4F, 0.98F, 0.0F);
                        }
                    }
                }
            }
        }
    }

    private void pickDecoration(World world, Random rand, int x, int y, int z, float lightChance, float capChance, float vineChance) {
        float f = rand.nextFloat();
        if (f < lightChance) {
            world.setBlock(x, y, z, this.shroomlightID);
        } else if (f < capChance) {
            world.setBlockAndMetadata(x, y, z, this.capID, this.capMeta);
            if (rand.nextFloat() < vineChance) {
                this.growWeepingVines(world, rand, x, y, z);
            }
        }
    }

    private void placeCapBlock(World world, Random rand, int x, int y, int z) {
        if (world.getBlockId(x, y - 1, z) == this.capID) {
            world.setBlockAndMetadata(x, y, z, this.capID, this.capMeta);
        } else if (rand.nextFloat() < 0.15F) {
            world.setBlockAndMetadata(x, y, z, this.capID, this.capMeta);
            if (rand.nextInt(5) == 0) {
                this.growWeepingVines(world, rand, x, y, z);
            }
        }
    }

    private void growWeepingVines(World world, Random rand, int x, int y, int z) {
        int py = y - 1;
        if (py >= 0) {
            if (world.getBlockId(x, py, z) == 0) {
                int length = rand.nextInt(5) + 1;
                if (rand.nextInt(7) == 0) {
                    length *= 2;
                }

                for (int i = 0; i < length && py - i >= 0 && world.getBlockId(x, py - i, z) == 0; i++) {
                    world.setBlockAndMetadataWithNotify(x, py - i, z, BlockList.roots.id, 15);
                }
            }
        }
    }
}
