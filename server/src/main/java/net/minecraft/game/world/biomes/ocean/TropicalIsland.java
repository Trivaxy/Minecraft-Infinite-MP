package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.SmallTree;
import net.minecraft.game.world.generation.features.trees.tropical.PalmTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class TropicalIsland extends Biome {
    public TropicalIsland(int biomeID) {
        super(biomeID);
        this.flowersAttempts = 8;
        this.reedAttempts = 40;
        this.tallGrassAttempts = 1;
        this.treeAttempts = 5;
        this.waterAttempts = 4;
        this.waterFallAttempts = 10;
        this.monsters.add(new SpawnListEntry(CreeperWind.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 4; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            PumpkinPatch.melonPatch.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            SmallTree.miniPalm.place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new PalmTree();
    }

    @Override
    public int getSkyColor() {
        return 6670334;
    }
}
