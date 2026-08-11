package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Fruit;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class BigPumpkin extends Feature {
    public static final BigPumpkin PUMPKIN = new BigPumpkin(BlockList.pumpkin.id, 8);
    public static final BigPumpkin MELONE = new BigPumpkin(BlockList.melon.id, 6);
    int blockID;
    int meta;

    public BigPumpkin(int id, int meta) {
        this.blockID = id;
        this.meta = meta;
    }

    @Override
    public boolean place(World world, Random random, int x1, int y1, int z1) {
        for (int x = 0; x < 6; x++) {
            for (int y = -5; y < 6; y++) {
                for (int z = 0; z < 6; z++) {
                    if (world.getBlock(x1 + x, y1 + y, z1 + z) instanceof Fruit) {
                        return false;
                    }
                }
            }
        }

        for (int x = 1; x < 4; x++) {
            for (int z = 1; z < 4; z++) {
                if (world.getBlock(x1 + x, y1 - 1, z1 + z) != BlockList.grass) {
                    return false;
                }
            }
        }

        for (int x = 1; x < 4; x++) {
            for (int y = -1; y < 4; y++) {
                for (int z = 1; z < 4; z++) {
                    world.setBlock(x1 + x, y1 + y, z1 + z, this.blockID);
                }
            }
        }

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 1; z < 4; z++) {
                    world.setBlock(x1 + x, y1 + y, z1 + z, this.blockID);
                }
            }
        }

        for (int x = 1; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 5; z++) {
                    world.setBlock(x1 + x, y1 + y, z1 + z, this.blockID);
                }
            }
        }

        world.setBlock(x1 + 2, y1 + 4, z1 + 2, BlockList.wood.id);
        if (random.nextBoolean()) {
            world.setBlockAndMetadata(x1 + 1, y1 + 4, z1 + 2, BlockList.leaves.id, this.meta);
            world.setBlockAndMetadata(x1 + 2, y1 + 4, z1 + 1, BlockList.leaves.id, this.meta);
        }

        if (random.nextBoolean()) {
            world.setBlockAndMetadata(x1 + 2, y1 + 4, z1 + 3, BlockList.leaves.id, this.meta);
            world.setBlockAndMetadata(x1 + 3, y1 + 4, z1 + 2, BlockList.leaves.id, this.meta);
        }

        world.setBlockAndMetadata(x1 + 2, y1 + 5, z1 + 2, BlockList.leaves.id, this.meta);
        return true;
    }
}
