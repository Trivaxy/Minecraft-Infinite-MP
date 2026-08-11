package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.mounts.horse.ZombieHorse;
import net.minecraft.game.entity.mob.monster.Imp;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.hell.AshCluster;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class HellPlains extends HellBiome {
    public HellPlains(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.netherrack.id;
        this.topMeta = 0;
        this.fillerBlock = (short)BlockList.dirt.id;
        this.fillerMeta = 5;
        this.biomeCaves = true;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 5;
        this.tallGrassAttempts = 5;
        this.lavaAttempts = 8;
        this.animals.add(new SpawnListEntry(ZombieHorse.class, 8));
        this.monsters.add(new SpawnListEntry(Imp.class, 6));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + 16;
        int z = z0 + 16;
        int y = rand.nextInt(world.getWorldHeight());
        AshCluster.INSTANCE.place(world, rand, x, y, z);

        for (int i = 0; i < 20; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.blackFlower.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.orangeFlower.place(world, rand, x, y, z);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 100; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            Tree tre = this.getRandomTreeGenerator(rand, y, world.getWorldHeight());
            if (tre != null) {
                tre.place(world, rand, x, y, z);
            }
        }
    }
}
