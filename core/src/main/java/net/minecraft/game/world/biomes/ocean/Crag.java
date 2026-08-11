package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.aquatic.Dolphin;
import net.minecraft.game.entity.mob.animal.aquatic.Fish;
import net.minecraft.game.entity.mob.animal.aquatic.Jellyfish;
import net.minecraft.game.entity.mob.animal.aquatic.Shark;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Crag extends Biome {
    private OctaveNoise grassNoise;
    private long noiseSeed;

    public Crag(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.stone.id;
        this.fillerBlock = (short)BlockList.stone.id;
        this.beachBlock1 = (short)BlockList.gravel.id;
        this.beachBlock2 = (short)BlockList.gravel.id;
        this.unlockBeachHeight = true;
        this.minBeachHeight = 99999;
        this.maxBeachHeight = -15;
        this.caveCeiling = 99999;
        this.copperLumpAttempts = 30;
        this.treeAttempts = 80;
        this.stoneHeight = -1;
        this.hasBeaches = false;
        this.seaCreatures.add(new SpawnListEntry(Jellyfish.class, 10));
        this.seaCreatures.add(new SpawnListEntry(Dolphin.class, 8));
        this.seaCreatures.add(new SpawnListEntry(Shark.class, 6));
        this.seaCreatures.add(new SpawnListEntry(Fish.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(2) == 0) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getWorldHeight());
            int y = z0 + rand.nextInt(16) + 8;
            Boulder.moss.place(world, rand, x, z, y);
        }

        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            GrassCluster.lushGrass.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            SpikeFeature.INSTANCE.block(BlockList.stone.id, 0);
            SpikeFeature.INSTANCE.place(world, rand, x, y, z);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.grassNoise == null) {
            this.noiseSeed = world.getSeed();
            this.grassNoise = new OctaveNoise(new Random(this.noiseSeed), 4);
        }

        boolean generateGrass = Math.abs(this.grassNoise.generateNoise2D(x * 0.6, z * 0.6)) < 1.5;
        if (generateGrass) {
            this.topBlock = (short)BlockList.moss.id;
            this.fillerBlock = (short)BlockList.moss.id;
        } else {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(5) == 0 ? new SpruceTree() : new PineTree();
    }
}
