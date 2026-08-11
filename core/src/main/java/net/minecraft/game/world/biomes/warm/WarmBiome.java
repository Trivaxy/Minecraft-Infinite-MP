package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.HangingPlants;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;

public abstract class WarmBiome extends Biome {
    protected WarmBiome(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveBlock1 = (short)BlockList.moss.id;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 2;
        this.topMeta = 2;
        this.fillerMeta = 2;
        this.grassMeta = 2;
        this.beachBlock1 = (short)BlockList.dirt.id;
        this.beachBlock2 = (short)BlockList.dirt.id;
        this.beachMeta1 = 127;
        this.beachMeta2 = 127;
        this.emeraldLumpAttempts = 8;
        this.tallGrassAttempts = 1;
        this.defaultStoneType = 5;
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            GrassCluster.lushGrass.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.glowLychen.place(world, rand, x, y, z);
        }

        if (rand.nextInt(4) == 0) {
            int x = x0 + 9;
            int z = z0 + 9;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            new Puddles(BlockList.waterStill.id, this.yOffset).place(world, rand, x, y, z);
        }

        for (int var21 = 0; var21 < 20; var21++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getSeaLevel() - 10);
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            Shrub.jungleShrub.place(world, rand, x, y, z);
        }

        for (int var22 = 0; var22 < 100; var22++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset + 8;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            HangingPlants.VINES.place(world, rand, x, y, z);
        }
    }

    @Override
    public int getSkyColor() {
        return 7714478;
    }
}
