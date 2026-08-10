package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.animal.mounts.Camel;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.monster.arachnid.Antlion;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFire;
import net.minecraft.game.entity.mob.monster.zombie.Mummy;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.DesertWell;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.SmallTree;
import net.minecraft.game.world.generation.features.trees.tropical.PalmTree;
import net.minecraft.game.world.generation.features.underground.OreLump;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Desert extends HotBiome {
    private OctaveNoise sandNoise;
    private long noiseSeed;

    public Desert(int id) {
        super(id);
        this.topBlock = (short)BlockList.sand.id;
        this.fillerBlock = (short)BlockList.sand.id;
        this.topMeta = this.fillerMeta = this.grassMeta = 0;
        this.treeAttempts = -100;
        this.waterFallAttempts = 0;
        this.stoneLayer = -1;
        this.coalLumpAttempts = 30;
        this.flowersAttempts = 16;
        this.tallGrassAttempts = 1;
        this.animals.clear();
        this.animals.add(new SpawnListEntry(Vulture.class, 6));
        this.animals.add(new SpawnListEntry(Fox.class, 6));
        this.animals.add(new SpawnListEntry(Camel.class, 8));
        this.monsters.add(new SpawnListEntry(Antlion.class, 8));
        this.monsters.add(new SpawnListEntry(Mummy.class, 10));
        this.monsters.add(new SpawnListEntry(CreeperFire.class, 4));
        this.variants.add(BiomeList.pampas);
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.sandNoise == null) {
            this.noiseSeed = world.getSeed();
            this.sandNoise = new OctaveNoise(new Random(this.noiseSeed), 8);
        }

        boolean generateSand = this.sandNoise.generateNoise2D(x, z) > 72.0;
        if (generateSand) {
            this.topBlock = (short)BlockList.quickSand.id;
            this.fillerBlock = (short)BlockList.quickSand.id;
        } else {
            this.topBlock = (short)BlockList.sand.id;
            this.fillerBlock = (short)BlockList.sand.id;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, -900.0, -900.0);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        if (rand.nextInt(500) == 0) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            DesertWell.instance.place(world, rand, x, world.getHeightmap(x, z) + 1, z);
        }

        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            TallPlants.CACTUS.place(world, rand, x, y, z);
        }

        for (int var25 = 0; var25 < 4; var25++) {
            int z = x0 + rand.nextInt(16) + 8;
            int x = rand.nextInt(world.getWorldHeight());
            int y = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, z, x, y);
        }

        for (int var26 = 0; var26 < 5; var26++) {
            int z = x0 + rand.nextInt(16) + 8;
            int x = z0 + rand.nextInt(16) + 8;
            SmallTree.miniPalm.place(world, rand, z, world.getHeightmap(z, x), x);
            x = x0 + rand.nextInt(16);
            int y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16);
            OreLump.susSand.place(world, rand, x, y, z);
        }

        int y = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int x = world.getHeightmap(y, z);
        this.defaultTreeType(rand).place(world, rand, y, x, z);
        y = x0 + rand.nextInt(16) + 8;
        x = z0 + rand.nextInt(16) + 8;
        z = world.getHeightmap(y, x);
        if (world.getBlockId(y, z - 1, x) == BlockList.sand.id && rand.nextInt(10) == 0) {
            Boulder.granite.place(world, rand, y, z, x);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new PalmTree();
    }

    @Override
    public int getSkyColor() {
        return 13949649;
    }
}
