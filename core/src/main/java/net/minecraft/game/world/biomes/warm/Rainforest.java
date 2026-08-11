package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.game.entity.mob.animal.Frog;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.CanopyTree;
import net.minecraft.game.world.generation.features.trees.tropical.RainforestTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Rainforest extends WarmBiome {
    public Rainforest(int biomeID) {
        super(biomeID);
        this.treeAttempts = 10;
        this.waterAttempts = 1;
        this.waterFallAttempts = 30;
        this.hasBeaches = false;
        this.animals.add(new SpawnListEntry(Frog.class, 10));
        this.animals.add(new SpawnListEntry(Alligator.class, 8));
        this.variants.add(BiomeList.prairie);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(10) == 0 ? new CanopyTree() : new RainforestTree(rand.nextInt(3));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.lilyPad.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y - 1, z) == BlockList.dirt.id) {
                TallPlants.SEAWEED.place(world, rand, x, y, z);
            }
        }
    }
}
