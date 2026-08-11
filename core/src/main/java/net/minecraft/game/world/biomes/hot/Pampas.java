package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Ostrich;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.PrairieTree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Pampas extends HotBiome {
    private OctaveNoise grassNoise;
    private long seed;

    public Pampas(int id) {
        super(id);
        this.treeAttempts = -3;
        this.tallGrassAttempts = 3;
        this.beachBlock2 = (short)BlockList.sand.id;
        this.animals.add(new SpawnListEntry(Ostrich.class, 8));
        this.animals.add(new SpawnListEntry(Wolf.class, 6));
        this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new PrairieTree(7, 8);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            Shrub.baobabShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        }

        for (int var25 = 0; var25 < 10; var25++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            TallPlants.CACTUS.place(world, rand, x, y, z);
        }

        for (int var26 = 0; var26 < 2; var26++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        GrassCluster.flowersYellow.place(world, rand, x, y, z);
        y = x0 + rand.nextInt(16) + 8;
        z = rand.nextInt(world.getWorldHeight());
        x = z0 + rand.nextInt(16) + 8;
        GrassCluster.duneGrass.place(world, rand, y, z, x);
        x = x0 + rand.nextInt(16) + 8;
        y = rand.nextInt(world.getWorldHeight());
        z = z0 + rand.nextInt(16) + 8;
        PumpkinPatch.pumpkinPatch.place(world, rand, x, y, z);
        if (rand.nextBoolean()) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            this.defaultTreeType(rand).place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.seed != world.getSeed() || this.grassNoise == null) {
            this.seed = world.getSeed();
            this.grassNoise = new OctaveNoise(new Random(this.seed), 6);
        }

        boolean generateGrass = this.grassNoise.generateNoise2D(x, z) > 1.0;
        if (generateGrass) {
            this.topBlock = (short)BlockList.sand.id;
            this.fillerBlock = (short)BlockList.sand.id;
            this.topMeta = this.fillerMeta = 0;
        } else {
            this.topBlock = (short)BlockList.grass.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = this.fillerMeta = 3;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, -900.0, -900.0);
        this.topMeta = this.fillerMeta = 3;
    }

    @Override
    public int getSkyColor() {
        return 14341027;
    }
}
