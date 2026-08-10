package net.minecraft.game.world.generation.features.trees.fungal;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.plants.Mushroom;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.SkyDimension;
import net.minecraft.game.world.generation.features.trees.Tree;

public class BigMushroom extends Tree {
    private int type;
    public static final Map<Integer, Tree> MUSHROOMS = new HashMap<>();

    public BigMushroom(int type) {
        this.type = type;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int height = random.nextInt(3) + 4;
        boolean canPlace = true;
        if (y >= 1 && y + height + 1 < world.getWorldHeight()) {
            for (int y0 = y; y0 <= y + 1 + height; y0++) {
                byte size = 3;
                if (y0 <= y + 3) {
                    size = 0;
                }

                for (int x0 = x - size; x0 <= x + size && canPlace; x0++) {
                    for (int z0 = z - size; z0 <= z + size && canPlace; z0++) {
                        if (y0 > 0 && y0 < world.getWorldHeight()) {
                            Block block = world.getBlock(x0, y0, z0);
                            if (block != null && !(block instanceof Mushroom)) {
                                canPlace = false;
                            }

                            if (world.currDim instanceof SkyDimension && world.getBlockBrightness(x0, y0, z0) >= 12) {
                                return false;
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
            if (var16 != BlockList.moss.id && var16 != BlockList.grass.id && var16 != BlockList.dirt.id) {
                return false;
            }

            int shroomHeight = y + height;
            if (this.type == 2) {
                shroomHeight = y + height - 3;
            }

            if (this.type == 3) {
                shroomHeight = y + height - 1;
            }

            for (int x0 = shroomHeight; x0 <= y + height; x0++) {
                int z0 = 1;
                if (x0 < y + height) {
                    z0++;
                }

                if (this.type == 1 || this.type == 3) {
                    z0 = 3;
                }

                for (int shroomWidth = x - z0; shroomWidth <= x + z0; shroomWidth++) {
                    for (int capWidth = z - z0; capWidth <= z + z0; capWidth++) {
                        int w = 5;
                        if (shroomWidth == x - z0) {
                            w--;
                        }

                        if (shroomWidth == x + z0) {
                            w++;
                        }

                        if (capWidth == z - z0) {
                            w -= 3;
                        }

                        if (capWidth == z + z0) {
                            w += 3;
                        }

                        if (this.type != 1 && this.type != 3 && x0 >= y + height
                            || shroomWidth != x - z0 && shroomWidth != x + z0
                            || capWidth != z - z0 && capWidth != z + z0) {
                            if (this.type == 3 && x0 == y + height && (x == shroomWidth + 3 || x == shroomWidth - 3 || z == capWidth + 3 || z == capWidth - 3)) {
                                w = 0;
                            }

                            if (w == 5 && x0 < y + height) {
                                w = 0;
                            }

                            if ((w != 0 || y >= y + height - 1) && !Block.isOpaque.get(world.getBlockId(shroomWidth, x0, capWidth))) {
                                world.setBlockAndMetadata(
                                    shroomWidth, x0, capWidth, this.type == 3 ? BlockList.shroomLight.id : BlockList.mushroomCap.id, this.type
                                );
                            }
                        }
                    }
                }
            }

            for (int var18 = 0; var18 < height; var18++) {
                int z0 = world.getBlockId(x, y + var18, z);
                if (!Block.isOpaque.get(z0)) {
                    world.setBlockAndMetadata(x, y + var18, z, BlockList.wood.id, 18);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    static {
        MUSHROOMS.put(BlockList.mushroomBrown.id, new BigMushroom(1));
        MUSHROOMS.put(BlockList.mushroomRed.id, new BigMushroom(2));
        MUSHROOMS.put(BlockList.mushroomGlowing.id, new BigMushroom(3));
        MUSHROOMS.put(BlockList.mushroomCrimson.id, new CrimsonFungus(13, 4, true));
        MUSHROOMS.put(BlockList.mushroomNylium.id, new NyliumTree());
    }
}
