package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.zombie.PigmanZombie;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.NyliumTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class HellWoods extends HellBiome {
    public HellWoods(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.grass.id;
        this.topMeta = 6;
        this.fillerBlock = (short)BlockList.dirt.id;
        this.fillerMeta = 5;
        this.grassMeta = 5;
        this.tallGrassAttempts = 5;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 5;
        this.monsters.clear();
        this.monsters.add(new SpawnListEntry(PigmanZombie.class, 10));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.nylium.place(world, rand, x, y, z);
        }

        for (int var12 = 0; var12 < 5; var12++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            TallPlants.ROOT.place(world, rand, x, y, z);
        }
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 10; i++) {
            int x = x0 + rand.nextInt(8) + 8;
            int z = z0 + rand.nextInt(8) + 8;
            int y = world.getRandomHeight(x, z);
            Tree tre = this.getRandomTreeGenerator(rand, y, world.getWorldHeight());
            if (tre != null) {
                tre.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new NyliumTree();
    }

    @Override
    public int getSkyColor() {
        return 13107;
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.SPORESCYAN, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
