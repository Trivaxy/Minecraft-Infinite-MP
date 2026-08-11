package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.plants.LeafPiles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.CherryTree;

public class CherryGrove extends Biome {
    public CherryGrove(int biomeID) {
        super(biomeID);
        this.treeAttempts = 1;
        this.flowersAttempts = 16;
        this.reedAttempts = 60;
        this.tallGrassAttempts = 1;
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        LeafPiles.CHERRY.place(world, rand, x, y, z);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new CherryTree();
    }
}
