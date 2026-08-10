package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.entity.mob.animal.Deer;
import net.minecraft.game.entity.mob.animal.flying.Bird;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.general.Stump;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.LeafPiles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.AspenTree;
import net.minecraft.game.world.generation.features.trees.deciduous.MapleTree;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.features.underground.Stalactites;
import net.minecraft.game.world.generation.features.underground.Stalagmites;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Forest extends Biome {
    public Forest(int id) {
        super(id);
        this.biomeCaves = true;
        this.caveMeta1 = 7;
        this.treeAttempts = 10;
        this.generateLava = false;
        this.tallGrassAttempts = 1;
        this.animals.add(new SpawnListEntry(Bird.class, 12));
        this.animals.add(new SpawnListEntry(Deer.class, 10));
        this.animals.add(new SpawnListEntry(Wolf.class, 8));
        this.animals.add(new SpawnListEntry(Bear.class, 6));
        this.variants.add(BiomeList.birchForest);
        this.variants.add(BiomeList.flowerForest);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);
        if (this.id == BiomeList.birchForest.id) {
            Stump.birch.place(world, rand, x, y, z);
        } else {
            Stump.log.place(world, rand, x, y, z);
        }

        if (this.id == BiomeList.birchForest.id) {
            if (rand.nextInt(5) == 0) {
                x = x0 + rand.nextInt(16) + 8;
                z = rand.nextInt(world.getWorldHeight());
                y = z0 + rand.nextInt(16) + 8;
                Boulder.rock.place(world, rand, x, z, y);
            }

            z = x0 + rand.nextInt(16) + 8;
            x = rand.nextInt(world.getWorldHeight());
            y = z0 + rand.nextInt(16) + 8;
            LeafPiles.BIRCH.place(world, rand, z, x, y);
            z = x0 + rand.nextInt(16) + 8;
            x = rand.nextInt(world.getWorldHeight());
            y = z0 + rand.nextInt(16) + 8;
            GrassCluster.flowersWhite.place(world, rand, z, x, y);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(2) == 0) {
            int x = x0 + 9;
            int z = z0 + 9;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            new Puddles(BlockList.waterStill.id, this.yOffset).place(world, rand, x, y, z);
        }

        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = z0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(24) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                z = rand.nextInt(world.getWorldHeight());
            }

            SpikeFeature.STALACTITE.place(world, rand, x, z, y);
        }

        for (int i = 0; i < 250; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y - 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y - 1, z) == 7) {
                Stalagmites.dripstone.place(world, rand, x, y, z);
            }

            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y + 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y + 1, z) == 7) {
                Stalactites.dripstone.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        if (rand.nextInt(10) == 0 || this.id == BiomeList.birchForest.id) {
            return new AspenTree();
        } else {
            return rand.nextInt(10) == 0 ? new MapleTree() : super.defaultTreeType(rand);
        }
    }
}
