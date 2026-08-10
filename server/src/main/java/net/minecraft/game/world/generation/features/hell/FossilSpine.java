package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class FossilSpine extends Feature {
    public static final FossilSpine INSTANCE = new FossilSpine();

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        while (world.isAir(x0, y0, z0) && y0 > 2) {
            y0--;
        }

        int id = world.getBlockId(x0, y0, z0);
        if (id != BlockList.slowSand.id && id != BlockList.soulSoil.id) {
            return false;
        }

        for (int xSize = -2; xSize <= 2; xSize++) {
            for (int zSize = -2; zSize <= 2; zSize++) {
                if (!world.isAir(x0 + xSize, y0 + 1, z0 + zSize)) {
                    return false;
                }
            }
        }

        for (int y = 1; y < 9; y++) {
            world.setBlockAndMetadata(x0, y0 + y, z0, BlockList.bone.id, 0);
            if (y % 2 == 1) {
                for (int x = -2; x < 3; x++) {
                    if (x != 0) {
                        world.setBlockAndMetadata(x0 + x, y0 + y, z0, BlockList.bone.id, 64);
                    }
                }

                for (int z = -2; z < 3; z++) {
                    if (z != 0) {
                        world.setBlockAndMetadata(x0, y0 + y, z0 + z, BlockList.bone.id, 128);
                    }
                }
            }
        }

        return true;
    }
}
