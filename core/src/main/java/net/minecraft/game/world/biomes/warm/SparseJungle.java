package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.plants.BambooStalks;
import net.minecraft.game.world.generation.features.plants.LeafPiles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.SmallJungleTree;

public class SparseJungle extends Jungle {
    public SparseJungle(int id) {
        super(id);
        this.treeAttempts = 5;
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new SmallJungleTree();
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        if (rand.nextInt(5) == 0) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = world.getHeightmap(x, z);
            BambooStalks.INSTANCE.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            Boulder.chlorite.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        LeafPiles.JUNGLE.place(world, rand, x, y, z);
    }
}
