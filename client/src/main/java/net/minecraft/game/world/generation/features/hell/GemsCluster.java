package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class GemsCluster extends Feature {
    public static final GemsCluster INSTANCE = new GemsCluster();

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        for (int attempts = 0; attempts < 64; attempts++) {
            int offX = x + rand.nextInt(8) - rand.nextInt(8);
            int offY = y + rand.nextInt(4) - rand.nextInt(4);
            int offZ = z + rand.nextInt(8) - rand.nextInt(8);
            if (world.isAir(offX, offY, offZ) && world.getBlockId(offX, offY + 1, offZ) == BlockList.clusterBlock.id) {
                world.setBlockAndMetadataWithNotify(offX, offY, offZ, BlockList.crystalBlock.id, world.getBlockMetadata(offX, offY + 1, offZ));
            }
        }

        return true;
    }
}
