package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Fae;
import net.minecraft.game.entity.mob.monster.elemental.Breeze;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.CherryTree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.WillowTree;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class SkyCliffs extends SkyBiome {
    private OctaveNoise clayType1;
    long randomSeed;

    public SkyCliffs(int biomeID) {
        super(biomeID);
        this.tallGrassAttempts = 1;
        this.treeAttempts = -1;
        this.caveFloor = 32;
        this.stoneHeight = 1;
        this.waterAttempts = 1;
        this.diamondLumpAttempts *= 2;
        this.goldLumpAttempts *= 2;
        this.ironLumpAttempts *= 2;
        this.monsters.add(new SpawnListEntry(Breeze.class, 10));
        this.animals.add(new SpawnListEntry(Fae.class, 12));
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.randomSeed != world.getSeed() || this.clayType1 == null) {
            this.randomSeed = world.getSeed();
            this.clayType1 = new OctaveNoise(new Random(this.randomSeed), 5);
        }

        boolean generateRed = Math.abs(this.clayType1.generateNoise2D(x * 0.3, z * 0.3)) < 2.0;
        boolean generateWhite = Math.abs(this.clayType1.generateNoise2D(x * 0.3, z * 0.3)) < 3.0;
        if (generateRed) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.topMeta = 8;
            this.fillerMeta = 8;
        } else if (generateWhite) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.topMeta = 6;
            this.fillerMeta = 6;
        } else {
            this.topBlock = (short)BlockList.grass.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = 7;
            this.fillerMeta = 7;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        int x = x0 + 16;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + 16;
        SpikeFeature.SKY.place(world, rand, x, y, z);
        x = x0 + rand.nextInt(16) + 8;
        z = z0 + rand.nextInt(16) + 8;
        y = world.getHeightmap(x, z);
        Shrub.cherryShrub.place(world, rand, x, y, z);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(5) == 0 ? new WillowTree(rand.nextInt(4) + 6) : new CherryTree();
    }

    @Override
    public int getSkyColor() {
        return 11964107;
    }
}
