package net.minecraft.game.world.biomes.hot;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.Meerkat;
import net.minecraft.game.entity.mob.animal.flying.Ostrich;
import net.minecraft.game.entity.mob.animal.mounts.horse.Horse;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.AcaciaTree;
import net.minecraft.game.world.generation.features.trees.tropical.BaobabTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Savanna extends HotBiome {
    public Savanna(int id) {
        super(id);
        this.defaultStoneType = 3;
        this.tallGrassAttempts = 1;
        this.treeAttempts = -1;
        this.biomeCaves = false;
        this.animals.add(new SpawnListEntry(Meerkat.class, 12));
        this.animals.add(new SpawnListEntry(Horse.class, 10));
        this.animals.add(new SpawnListEntry(Ostrich.class, 8));
        this.animals.add(new SpawnListEntry(Wolf.class, 6));
        this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
        this.variants.add(BiomeList.outback);
        this.variants.add(BiomeList.plateau);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        PumpkinPatch.pumpkinPatch.place(world, rand, x, y, z);
        x = x0 + rand.nextInt(16) + 8;
        z = z0 + rand.nextInt(16) + 8;
        Shrub.baobabShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        if (rand.nextInt(5) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            new BaobabTree().place(world, rand, x, world.getHeightmap(x, z), z);
        }

        if (rand.nextInt(5) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            new AcaciaTree().place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public int getSkyColor() {
        return 14341027;
    }
}
