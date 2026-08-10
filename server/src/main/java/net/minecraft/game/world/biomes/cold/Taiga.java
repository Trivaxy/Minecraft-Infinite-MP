package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.Stump;
import net.minecraft.game.world.generation.features.plants.FlowerPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.MegaPine;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Taiga extends ColdBiome {
    public Taiga(int id) {
        super(id);
        this.animals.add(new SpawnListEntry(Wolf.class, 8));
        this.animals.add(new SpawnListEntry(Fox.class, 10));
        this.variants.add(BiomeList.dryTaiga);
        this.variants.add(BiomeList.megaTaiga);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 8; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            FlowerPatch.berryBush.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);
        Stump.spruce.place(world, rand, x, y, z);
        if (this.id == BiomeList.megaTaiga.id && rand.nextInt(2) == 0) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            Boulder.moss.place(world, rand, x, y, z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return this.id == BiomeList.megaTaiga.id && rand.nextInt(3) == 0 ? new MegaPine() : super.defaultTreeType(rand);
    }
}
