package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class HangingPlants extends Feature {
    public static final HangingPlants VINES;
    public static final HangingPlants ICICLES;
    public static final HangingPlants GLOWING;
    public static final HangingPlants ROOTS;
    int id;

    public HangingPlants(int id) {
        this.id = id;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int length = random.nextInt(8) + 3;
        boolean canGenerate = true;

        for (int height = y; height <= y - 1 - length; height--) {
            byte range = 2;
            if (height == y) {
                range = 0;
            }

            if (height >= y + 1 + length - 2) {
                range = 2;
            }

            for (int x0 = x - range; x0 <= x + range && canGenerate; x0++) {
                for (int z0 = z - range; z0 <= z + range && canGenerate; z0++) {
                    if (height < 0 || height >= world.getWorldHeight()) {
                        canGenerate = true;
                    } else if (world.getBlockId(x0, height, z0) != 0) {
                        canGenerate = false;
                    }
                }
            }
        }

        if (!canGenerate) {
            return false;
        }

        if (y >= world.getWorldHeight() - length - 1) {
            return false;
        }

        if (BlockList.blocks[this.id].canStay(world, x, y, z) || this == ICICLES) {
            for (int y0 = 0; y0 < length; y0++) {
                if (world.getBlockId(x, y - y0, z) == 0 && world.getBlockId(x, y - y0 - 1, z) == 0) {
                    world.setBlock(x, y - y0, z, this.id);
                }
            }
        }

        if (this != ICICLES) {
            for (int y0 = 0; y0 < length; y0++) {
                if (world.getBlockId(x, y - y0 + 1, z) == this.id && world.getBlockId(x, y - y0 - 1, z) == this.id && length > 2) {
                    world.setBlockMetadata(x, y - y0, z, 15);
                }

                if (world.isSolid(x, y - y0 + 1, z) && world.getBlockId(x, y - y0 - 1, z) == this.id && world.getBlockId(x, y - y0, z) == this.id) {
                    world.setBlockMetadata(x, y - y0, z, 15);
                }
            }
        }

        return true;
    }

    static {
        VINES = new HangingPlants(BlockList.caveVine.id);
        ICICLES = new HangingPlants(BlockList.ice.id);
        GLOWING = new HangingPlants(BlockList.glowVine.id);
        ROOTS = new HangingPlants(BlockList.roots.id);
    }
}
