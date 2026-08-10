package net.minecraft.game.world.generation.features.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class FireCluster extends Feature {
    public static final FireCluster INSTANCE = new FireCluster();

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        for (int attempts = 0; attempts < 64; attempts++) {
            int xOff = x + rand.nextInt(8) - rand.nextInt(8);
            int yOff = y + rand.nextInt(4) - rand.nextInt(4);
            int zOff = z + rand.nextInt(8) - rand.nextInt(8);
            int belowBlock = world.getBlockId(xOff, yOff - 1, zOff);
            if (world.isAir(xOff, yOff, zOff)) {
                if (belowBlock == BlockList.netherrack.id) {
                    world.setBlockWithNotify(xOff, yOff, zOff, BlockList.fire.id);
                } else if (belowBlock == BlockList.slowSand.id || belowBlock == BlockList.soulSoil.id) {
                    world.setBlockWithNotify(xOff, yOff, zOff, BlockList.hellfire.id);
                } else if (belowBlock == BlockList.clusterBlock.id) {
                    world.setBlockAndMetadataWithNotify(xOff, yOff, zOff, BlockList.crystalBlock.id, 3);
                }
            }
        }

        return true;
    }
}
