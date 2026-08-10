package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.entity.mob.monster.creeper.CreeperMagic;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.plants.BigPumpkin;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.AppleTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Orchard extends Biome {
    public Orchard(int biomeID) {
        super(biomeID);
        this.tallGrassAttempts = 1;
        this.treeAttempts = 1;
        this.reedAttempts = 40;
        this.monsters.add(new SpawnListEntry(CreeperMagic.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = world.getHeightmap(x, z);
            BigPumpkin.PUMPKIN.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            y = world.getHeightmap(x, z);
            BigPumpkin.MELONE.place(world, rand, x, y, z);
        }

        if (rand.nextInt(10) == 0) {
            int z = x0 + rand.nextInt(16) + 8;
            int x = z0 + rand.nextInt(16) + 8;
            int y = world.getHeightmap(z, x);
            PumpkinPatch.pumpkinPatch.place(world, rand, z, y, x);
            z = x0 + rand.nextInt(16) + 8;
            x = z0 + rand.nextInt(16) + 8;
            y = world.getHeightmap(z, x);
            PumpkinPatch.melonPatch.place(world, rand, z, y, x);
        }

        if (rand.nextInt(4) == 0) {
            int y = x0 + rand.nextInt(16) + 8;
            int z = rand.nextInt(world.getWorldHeight());
            int x = z0 + rand.nextInt(16) + 8;
            GrassCluster.flowersOrange.place(world, rand, y, z, x);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextBoolean() ? new AppleTree() : super.defaultTreeType(rand);
    }
}
