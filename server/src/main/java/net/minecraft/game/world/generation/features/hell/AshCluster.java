package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class AshCluster extends Feature {
    public static final AshCluster INSTANCE;
    private int id;

    public AshCluster(int blockID) {
        this.id = blockID;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int idCheck = 0;

        for (int attempts = 0; attempts < 64; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);

            for (int newX = 0; newX <= random.nextInt(8); newX++) {
                for (int newZ = 0; newZ <= random.nextInt(8); newZ++) {
                    if (world.getBlockId(x0 + newX, y0, z0 + newZ) == idCheck && BlockList.blocks[this.id].canStay(world, x0 + newX, y0, z0 + newZ)) {
                        world.setBlock(x0 + newX, y0, z0 + newZ, this.id);
                    }

                    if (world.getBlockId(x0 - newX, y0, z0 - newZ) == idCheck && BlockList.blocks[this.id].canStay(world, x0 - newX, y0, z0 - newZ)) {
                        world.setBlock(x0 - newX, y0, z0 - newZ, this.id);
                    }
                }
            }
        }

        return true;
    }

    static {
        INSTANCE = new AshCluster(BlockList.ash.id);
    }
}
