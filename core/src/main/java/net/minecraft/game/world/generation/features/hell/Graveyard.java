package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Graveyard extends Feature {
    public static final Graveyard INSTANCE = new Graveyard();

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (!this.canPlace(world, x, y, z)) {
            return false;
        }

        for (int i = -3; i < 4; i++) {
            for (int j = -5; j < 5; j++) {
                if (i != -3 && i != 3 && j != -5 && j != 4) {
                    world.setBlock(x + i, y, z + j, BlockList.slowSand.id);
                    world.setBlockAndMetadata(x + i, y + 1, z + j, BlockList.grave.id, 2);
                } else {
                    world.setBlock(x + i, y, z + j, BlockList.obsidianBrick.id);
                    world.setBlock(x + i, y + 1, z + j, BlockList.wallObsidian.id);
                }
            }
        }

        return true;
    }

    private boolean canPlace(World world, int x, int y, int z) {
        int baseY = y - 1;

        for (int i = -3; i < 4; i++) {
            for (int j = -5; j < 5; j++) {
                int groundX = x + i;
                int groundZ = z + j;
                int block = world.getBlockId(groundX, baseY, groundZ);
                if (block != BlockList.soulSoil.id && block != BlockList.slowSand.id) {
                    return false;
                }

                if (world.isAir(groundX, baseY, groundZ)) {
                    return false;
                }
            }
        }

        return true;
    }
}
