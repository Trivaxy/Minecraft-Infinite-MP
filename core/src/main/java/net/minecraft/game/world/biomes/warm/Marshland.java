package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.game.entity.mob.animal.Frog;
import net.minecraft.game.entity.mob.animal.Turtle;
import net.minecraft.game.entity.mob.monster.skeleton.Bogged;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.SwampTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Marshland extends WarmBiome {
    public Marshland(int id) {
        super(id);
        this.treeAttempts = -999;
        this.reedAttempts = 300;
        this.genBeaches = false;
        this.waterAttempts = 1;
        this.tallGrassAttempts = 3;
        this.hasBeaches = false;
        this.animals.add(new SpawnListEntry(Frog.class, 12));
        this.animals.add(new SpawnListEntry(Turtle.class, 10));
        this.animals.add(new SpawnListEntry(Alligator.class, 8));
        this.monsters.add(new SpawnListEntry(Bogged.class, 10));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        Shrub.mangroveShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        x = x0 + rand.nextInt(16) + 8;
        z = z0 + rand.nextInt(16) + 8;
        if (rand.nextInt(5) == 0) {
            this.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
        }

        x = x0 + 9;
        z = z0 + 9;
        new Puddles(BlockList.waterStill.id, this.yOffset).place(world, rand, x, rand.nextInt(world.getWorldHeight()) + 8, z);

        for (int i = 0; i < 20; i++) {
            x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.lilyPad.place(world, rand, x, y, z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new SwampTree();
    }
}
