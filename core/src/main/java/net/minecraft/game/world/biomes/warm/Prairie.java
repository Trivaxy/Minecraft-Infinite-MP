package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.JungleTree;

public class Prairie extends Rainforest {
    public Prairie(int biomeID) {
        super(biomeID);
        this.treeAttempts = -1;
        this.tallGrassAttempts = 3;
        this.waterAttempts = 8;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        Shrub.mahoganyShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        if (rand.nextInt(4) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            new JungleTree(10 + rand.nextInt(20), 11, 13).place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(10) == 0 ? new JungleTree(10 + rand.nextInt(20), 11, 13) : super.defaultTreeType(rand);
    }
}
