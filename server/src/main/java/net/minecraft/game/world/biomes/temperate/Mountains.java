package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.entity.mob.animal.Goat;
import net.minecraft.game.entity.mob.animal.mounts.Lama;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.features.trees.deciduous.AppleTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BasicTree;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Mountains extends Biome {
    public Mountains(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveMeta1 = 7;
        this.waterAttempts = 4;
        this.marbleLumpAttempts = 5;
        this.treeAttempts = -1;
        this.unlockBeachHeight = true;
        this.stoneHeight = 1;
        this.beachBlock1 = (short)BlockList.gravel.id;
        this.ironLumpAttempts = 20;
        this.ironLumpMaxHeight = this.copperLumpMaxHeight;
        this.waterFallAttempts = 50;
        this.emeraldLumpAttempts = 8;
        this.caveFloor = 20;
        this.hasBeaches = false;
        this.animals.add(new SpawnListEntry(Goat.class, 10));
        this.animals.add(new SpawnListEntry(Bear.class, 8));
        this.animals.add(new SpawnListEntry(Lama.class, 8));
        this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
        this.variants.add(BiomeList.springs);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);
        if (rand.nextBoolean()) {
            this.getRandomTreeGenerator(rand, y, world.getWorldHeight()).place(world, rand, x, y, z);
        }

        if (rand.nextInt(4) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GrassCluster.flowersBlue.place(world, rand, x, y, z);
        }

        if (this.id != BiomeList.springs.id) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            y = world.getHeightmap(x, z);
            SpikeFeature.INSTANCE.block(1, 0);
            SpikeFeature.INSTANCE.placeAnywhere(false);
            SpikeFeature.INSTANCE.place(world, rand, x, y, z);
        } else {
            x = x0 + 9;
            z = z0 + 9;
            y = world.getWorldHeight();
            new Puddles(BlockList.waterStill.id, world.getWorldHeight() / 2 - 20, 1).place(world, rand, x, y, z);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        BiomeList.forest.postPopulate(world, rand, x0, z0);
    }

    @Override
    public Tree getRandomTreeGenerator(Random rand, int y, int worldHeight) {
        int height = worldHeight / 2;
        if (worldHeight == 320) {
            height += 32;
        }

        if (height < 156) {
            height = 156;
        }

        if (y >= height) {
            return rand.nextInt(5) == 0 ? new SpruceTree() : new PineTree();
        } else if (rand.nextInt(20) == 0) {
            return new AppleTree();
        } else if (rand.nextInt(5) == 0) {
            return rand.nextInt(2) == 0 ? new SpruceTree() : new PineTree();
        } else {
            return new BasicTree(this.fillerMeta);
        }
    }

    @Override
    public int getSkyColor() {
        return 8625919;
    }
}
