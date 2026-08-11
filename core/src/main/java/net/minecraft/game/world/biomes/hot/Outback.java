package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.flying.Ostrich;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFire;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.misc.SmallTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Outback extends HotBiome {
    private OctaveNoise grassNoise;
    private long noiseSeed;

    public Outback(int biomeID) {
        super(biomeID);
        this.defaultStoneType = 3;
        this.treeAttempts = -100;
        this.flowersAttempts = 0;
        this.tallGrassAttempts = 3;
        this.beachBlock1 = (short)BlockList.redSand.id;
        this.genBeaches = false;
        this.caveMeta2 = 2;
        this.animals.add(new SpawnListEntry(Ostrich.class, 6));
        this.animals.add(new SpawnListEntry(Vulture.class, 8));
        this.animals.add(new SpawnListEntry(Wolf.class, 6));
        this.monsters.add(new SpawnListEntry(CreeperFire.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 10; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            Shrub.acaciaShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        }

        for (int var16 = 0; var16 < 5; var16++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            SmallTree.miniAcacia.place(world, rand, x, world.getHeightmap(x, z), z);
        }

        for (int var17 = 0; var17 < 50; var17++) {
            int z = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int x = z0 + rand.nextInt(16) + 8;
            TallPlants.CACTUS.place(world, rand, z, y, x);
        }

        for (int var18 = 0; var18 < 4; var18++) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getWorldHeight());
            int x = z0 + rand.nextInt(16) + 8;
            GrassCluster.deadBush.place(world, rand, y, z, x);
        }
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.grassNoise == null) {
            this.noiseSeed = world.getSeed();
            this.grassNoise = new OctaveNoise(new Random(this.noiseSeed), 6);
        }

        boolean generateGrass = Math.abs(this.grassNoise.generateNoise2D(x * 0.6, z * 0.6)) < 5.0;
        if (generateGrass) {
            this.topBlock = (short)BlockList.redSand.id;
            this.fillerBlock = (short)BlockList.redSand.id;
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
