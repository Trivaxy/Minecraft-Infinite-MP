package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.corals.CoralClaw;
import net.minecraft.game.world.generation.features.corals.CoralHouse;
import net.minecraft.game.world.generation.features.corals.CoralTree;

public class WarmOcean extends Ocean {
    public WarmOcean(int biomeID) {
        super(biomeID);
        this.beachBlock2 = (short)BlockList.sand.id;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 64; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getSeaLevel() - 8);
            int z = z0 + rand.nextInt(16) + 8;
            CoralTree.INSTANCE.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getSeaLevel() - 8);
            z = z0 + rand.nextInt(16) + 8;
            CoralHouse.INSTANCE.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getSeaLevel() - 8);
            z = z0 + rand.nextInt(16) + 8;
            CoralClaw.INSTANCE.place(world, rand, x, y, z);
        }

        super.prePopulate(world, rand, x0, z0);
    }
}
