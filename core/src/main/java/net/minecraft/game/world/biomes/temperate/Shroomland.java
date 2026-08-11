package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.cow.Mooshroom;
import net.minecraft.game.entity.mob.monster.creeper.CreeperMagic;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.HangingPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Shroomland extends Biome {
    public Shroomland(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveBlock1 = (short)BlockList.moss.id;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 4;
        this.topMeta = 4;
        this.fillerMeta = 4;
        this.mushroomChance = 4;
        this.treeAttempts = 1;
        this.genBeaches = false;
        this.animals.clear();
        this.monsters.clear();
        this.animals.add(new SpawnListEntry(Mooshroom.class, 10));
        this.monsters.add(new SpawnListEntry(CreeperMagic.class, 4));
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new BigMushroom(rand.nextInt(3) + 1);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            int flowerType = rand.nextInt(3);
            FlowerPatch instance = FlowerPatch.mushrooms;
            switch (flowerType) {
                case 1:
                    instance = FlowerPatch.redshrooms;
                    break;
                case 2:
                    instance = FlowerPatch.glowshrooms;
            }

            instance.place(world, rand, x, y, z);
        }

        for (int var23 = 0; var23 < 100; var23++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset + 8;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y + 1, z) == BlockList.moss.id) {
                HangingPlants.VINES.place(world, rand, x, y, z);
            }

            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            new BigMushroom(rand.nextInt(3) + 1).place(world, rand, x, y, z);
        }

        for (int var24 = 0; var24 < 3; var24++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.glowLychen.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            z = z0 + rand.nextInt(16) + 8;
            GrassCluster.lushGrass.place(world, rand, x, y, z);
        }
    }

    @Override
    public int getSkyColor() {
        return 10655191;
    }
}
