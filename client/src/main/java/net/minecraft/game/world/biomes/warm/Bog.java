package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.game.entity.mob.animal.Frog;
import net.minecraft.game.entity.mob.animal.Turtle;
import net.minecraft.game.entity.mob.monster.skeleton.Bogged;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.ShavedTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Bog extends Marshland {
    private OctaveNoise grassNoise;
    private long noiseSeed;

    public Bog(int biomeID) {
        super(biomeID);
        this.mushroomChance = 4;
        this.treeAttempts = -1;
        this.genBeaches = true;
        this.reedAttempts = 40;
        this.animals.add(new SpawnListEntry(Frog.class, 12));
        this.animals.add(new SpawnListEntry(Turtle.class, 10));
        this.animals.add(new SpawnListEntry(Alligator.class, 8));
        this.monsters.add(new SpawnListEntry(Bogged.class, 6));
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new ShavedTree(6, 7, 2);
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.grassNoise == null) {
            this.noiseSeed = world.getSeed();
            this.grassNoise = new OctaveNoise(new Random(this.noiseSeed), 6);
        }

        boolean generateGrass = this.grassNoise.generateNoise2D(x, z) > 1.0;
        if (generateGrass) {
            this.topBlock = (short)BlockList.dirt.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = 127;
            this.fillerMeta = 2;
        } else {
            this.topBlock = (short)BlockList.grass.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = this.fillerMeta = 2;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
        this.topMeta = 2;
    }
}
