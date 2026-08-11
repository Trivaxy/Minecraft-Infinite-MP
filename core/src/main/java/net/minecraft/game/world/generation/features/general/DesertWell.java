package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class DesertWell extends Feature {
    public static final DesertWell instance = new DesertWell();

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        while (world.isAir(x, y, z) && y > 2) {
            y--;
        }

        int block = world.getBlockId(x, y, z);
        if (block != BlockList.sand.id) {
            return false;
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (world.isAir(x + dx, y - 1, z + dz) && world.isAir(x + dx, y - 2, z + dz)) {
                    return false;
                }
            }
        }

        for (int var10 = -1; var10 <= 0; var10++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int i9 = -2; i9 <= 2; i9++) {
                    world.setBlock(x + dz, y + var10, z + i9, BlockList.sandStone.id);
                }
            }
        }

        world.setBlock(x, y, z, BlockList.waterMoving.id);
        world.setBlock(x - 1, y, z, BlockList.waterMoving.id);
        world.setBlock(x + 1, y, z, BlockList.waterMoving.id);
        world.setBlock(x, y, z - 1, BlockList.waterMoving.id);
        world.setBlock(x, y, z + 1, BlockList.waterMoving.id);

        for (int i = 1; i < 6; i++) {
            world.setBlock(x, y - i, z, BlockList.susSand.id);
            world.setBlock(x - 1, y - i, z, BlockList.susSand.id);
            world.setBlock(x + 1, y - i, z, BlockList.susSand.id);
            world.setBlock(x, y - i, z - 1, BlockList.susSand.id);
            world.setBlock(x, y - i, z + 1, BlockList.susSand.id);
        }

        for (int var11 = -2; var11 <= 2; var11++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (var11 == -2 || var11 == 2 || dz == -2 || dz == 2) {
                    world.setBlock(x + var11, y + 1, z + dz, BlockList.sandStone.id);
                }
            }
        }

        world.setBlockAndMetadata(x + 2, y + 1, z, BlockList.slabSandstone.id, 0);
        world.setBlockAndMetadata(x - 2, y + 1, z, BlockList.slabSandstone.id, 0);
        world.setBlockAndMetadata(x, y + 1, z + 2, BlockList.slabSandstone.id, 0);
        world.setBlockAndMetadata(x, y + 1, z - 2, BlockList.slabSandstone.id, 0);

        for (int var12 = -1; var12 <= 1; var12++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (var12 == 0 && dz == 0) {
                    world.setBlock(x + var12, y + 4, z + dz, BlockList.sandStone.id);
                } else {
                    world.setBlockAndMetadata(x + var12, y + 4, z + dz, BlockList.slabSandstone.id, 0);
                }
            }
        }

        for (int var13 = 1; var13 <= 3; var13++) {
            world.setBlock(x - 1, y + var13, z - 1, BlockList.sandStone.id);
            world.setBlock(x - 1, y + var13, z + 1, BlockList.sandStone.id);
            world.setBlock(x + 1, y + var13, z - 1, BlockList.sandStone.id);
            world.setBlock(x + 1, y + var13, z + 1, BlockList.sandStone.id);
        }

        return true;
    }
}
