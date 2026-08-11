package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.monster.arachnid.Antlion;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFire;
import net.minecraft.game.entity.mob.monster.zombie.Mummy;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.DeadTree;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Wasteland extends Biome {
    private OctaveNoise sandNoise;
    private long noiseSeed;

    public Wasteland(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveMeta1 = 2;
        this.lavaFallAttempts = 10;
        this.waterFallAttempts = 0;
        this.stoneHeight = 1;
        this.coalLumpAttempts = 30;
        this.rubyLumpAttempts = 8;
        this.lavaAttempts = 1;
        this.treeAttempts = -1;
        this.flowersAttempts = -1;
        this.generateWater = false;
        this.beachBlock1 = (short)BlockList.gravel.id;
        this.beachBlock2 = (short)BlockList.gravel.id;
        this.topBlock = (short)BlockList.sand.id;
        this.fillerBlock = (short)BlockList.sand.id;
        this.defaultStoneType = 4;
        this.defaultLiquid = 0;
        this.diamondLumpMaxHeight = 16;
        this.unlockBeachHeight = true;
        this.animals.add(new SpawnListEntry(Vulture.class, 6));
        this.animals.add(new SpawnListEntry(Fox.class, 8));
        this.monsters.add(new SpawnListEntry(Antlion.class, 8));
        this.monsters.add(new SpawnListEntry(Mummy.class, 10));
        this.monsters.add(new SpawnListEntry(CreeperFire.class, 4));
        this.variants.add(BiomeList.volcano);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;

        for (int i = 0; i < 5; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, x, y, z);
            y = x0 + rand.nextInt(16);
            z = rand.nextInt(world.getWorldHeight());
            x = z0 + rand.nextInt(16);
            OreLump.susSand.place(world, rand, y, z, x);
        }

        z = x0 + rand.nextInt(16) + 8;
        y = z0 + rand.nextInt(16) + 8;
        x = world.getHeightmap(z, y);
        if (world.getBlockId(z, x - 1, y) == BlockList.sand.id && rand.nextInt(5) == 0) {
            Boulder.granite.place(world, rand, z, x, y);
        }

        if (world.getBlockId(z, x - 1, y) == BlockList.dirt.id && rand.nextInt(5) == 0) {
            Boulder.limestone.place(world, rand, z, x, y);
        }

        if (rand.nextInt(4) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GrassCluster.duneGrass.place(world, rand, x, y, z);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(4) == 0) {
            int x = x0 + 9;
            int y = z0 + 9;
            int z = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                z = rand.nextInt(world.getWorldHeight());
            }

            new Puddles(BlockList.lavaStill.id, this.yOffset).place(world, rand, x, z, y);
        }

        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(24) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            SpikeFeature.VOLCANIC.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(8) + 4;
        int y = rand.nextInt(24) + this.yOffset;
        int z = z0 + rand.nextInt(8) + 4;
        OreLump.magma.place(world, rand, x, y, z);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new DeadTree();
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this == BiomeList.volcano) {
            super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
        } else {
            if (this.noiseSeed != world.getSeed() || this.sandNoise == null) {
                this.noiseSeed = world.getSeed();
                this.sandNoise = new OctaveNoise(new Random(this.noiseSeed), 8);
            }

            boolean generateSand = this.sandNoise.generateNoise2D(x, z) > 36.0;
            if (generateSand) {
                this.topBlock = (short)BlockList.dirt.id;
                this.fillerBlock = (short)BlockList.dirt.id;
                this.topMeta = this.fillerMeta = 6;
            } else {
                this.topBlock = (short)BlockList.sand.id;
                this.fillerBlock = (short)BlockList.sand.id;
                this.topMeta = this.fillerMeta = 0;
            }

            super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
            this.topMeta = this.fillerMeta = 6;
        }
    }

    @Override
    public int getSkyColor() {
        return 13949649;
    }
}
