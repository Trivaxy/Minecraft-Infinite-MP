package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.game.world.generation.features.trees.tropical.BambooTree;

public class BambooStalks extends Feature {
    public static final BambooStalks INSTANCE = new BambooStalks();

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 20; i++) {
            int rX = x + random.nextInt(8) - random.nextInt(8);
            int rZ = z + random.nextInt(8) - random.nextInt(8);
            if (BlockList.bamboo.placeableAt(world, rX, y, rZ)) {
                new BambooTree().place(world, random, rX, y, rZ);
            }
        }

        return true;
    }
}
