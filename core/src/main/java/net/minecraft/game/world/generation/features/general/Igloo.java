package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Igloo extends Feature {
    public static final Igloo INSTANCE = new Igloo();
    private static final int blockSnow = BlockList.snowBlock.id;
    private static final int blockWater = BlockList.waterStill.id;

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        if (!this.canBePlaced(world, x, y, z)) {
            return false;
        }

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if ((dx != -3 && dx != 3 || dz > -2 && dz < 2) && (dz != -3 && dz != 3 || dx > -2 && dx < 2)) {
                    world.setBlock(x + dx, y - 1, z + dz, blockSnow);
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            int yy = y + i;
            world.setBlock(x - 2, yy, z - 2, blockSnow);
            world.setBlock(x + 2, yy, z - 2, blockSnow);
            world.setBlock(x - 2, yy, z + 2, blockSnow);
            world.setBlock(x + 2, yy, z + 2, blockSnow);
            world.setBlock(x - 1 + i, y + 3, z - 2, blockSnow);
            world.setBlock(x - 1 + i, y + 3, z + 2, blockSnow);
            world.setBlock(x - 2, y + 3, z - 1 + i, blockSnow);
            world.setBlock(x + 2, y + 3, z - 1 + i, blockSnow);

            for (int j = 0; j < 3; j++) {
                world.setBlock(x - 1 + j, yy, z - 3, blockSnow);
                world.setBlock(x - 1 + j, yy, z + 3, blockSnow);
                world.setBlock(x - 3, yy, z - 1 + j, blockSnow);
                world.setBlock(x + 3, yy, z - 1 + j, blockSnow);
                world.setBlock(x - 1 + i, y + 4, z - 1 + j, blockSnow);
            }
        }

        world.setBlock(x + 3, y, z, 0);
        world.setBlock(x + 3, y + 1, z, 0);
        world.setBlockAndMetadata(x - 2, y, z, BlockList.stoneOvenIdle.id, 5);
        world.setBlock(x - 2, y, z + 1, BlockList.workbench.id);
        world.setBlock(x - 2, y, z - 1, BlockList.chest.id);
        ChestEntity chest = (ChestEntity)world.getTileEntity(x - 2, y, z - 1);

        for (int itms = 0; itms < 8; itms++) {
            ItemStack stack = StarterHouse.INSTANCE.pickStarterGear(rand);
            if (stack != null && chest != null) {
                chest.setSlotContents(rand.nextInt(chest.getSize()), stack);
            }
        }

        return true;
    }

    private boolean canBePlaced(World world, int x, int y, int z) {
        int attempts = 0;

        for (int xx = x - 3; xx <= x + 3; xx++) {
            for (int zz = z - 3; zz <= z + 3; zz++) {
                if (world.getBlockId(xx, y - 1, zz) == 0) {
                    attempts++;
                }

                if ((xx != x - 3 && xx != x + 3 || zz > z - 2 && zz < z + 2)
                    && (zz != x - 3 && zz != x + 3 || xx > z - 2 && zz < z + 2)
                    && (world.getBlockId(xx, y, zz) != 0 || world.getBlockId(xx, y - 1, zz) == blockWater)) {
                    return false;
                }
            }
        }

        return attempts <= 20;
    }
}
