package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.Imp;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.HangingPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.CrimsonFungus;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class HellSwamp extends HellBiome {
    public HellSwamp(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.grass.id;
        this.topMeta = 5;
        this.fillerBlock = (short)BlockList.dirt.id;
        this.fillerMeta = 5;
        this.tallGrassAttempts = 5;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 5;
        this.monsters.add(new SpawnListEntry(Imp.class, 10));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + 9;
        int z = z0 + 9;
        int y = rand.nextInt(world.getWorldHeight());
        new Puddles(BlockList.lavaStill.id, this.yOffset).place(world, rand, x, y, z);

        for (int i = 0; i < 20; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.crimson.place(world, rand, x, y, z);
        }

        for (int var15 = 0; var15 < 100; var15++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            HangingPlants.ROOTS.place(world, rand, x, y, z);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 25; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = world.getRandomHeight(x, z);
            Tree tre = this.getRandomTreeGenerator(rand, y, world.getWorldHeight());
            if (tre != null) {
                tre.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new CrimsonFungus(13, 4, false);
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.SPORES, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
