package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.game.entity.mob.animal.Frog;
import net.minecraft.game.entity.mob.animal.Turtle;
import net.minecraft.game.entity.mob.monster.skeleton.Bogged;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.tropical.MangroveTree;
import net.minecraft.game.world.generation.features.trees.tropical.SwampTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Swamp extends WarmBiome {
    public Swamp(int id) {
        super(id);
        this.mushroomChance = 4;
        this.waterAttempts = 1;
        this.treeAttempts = 1;
        this.hasBeaches = false;
        this.animals.add(new SpawnListEntry(Frog.class, 12));
        this.animals.add(new SpawnListEntry(Turtle.class, 10));
        this.animals.add(new SpawnListEntry(Alligator.class, 8));
        this.monsters.add(new SpawnListEntry(Bogged.class, 6));
        this.variants.add(BiomeList.marshland);
        this.variants.add(BiomeList.bog);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new MangroveTree();
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        if (rand.nextBoolean()) {
            new SwampTree().place(world, rand, x, world.getHeightmap(x, z), z);
        }

        for (int i = 0; i < 20; i++) {
            x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
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
