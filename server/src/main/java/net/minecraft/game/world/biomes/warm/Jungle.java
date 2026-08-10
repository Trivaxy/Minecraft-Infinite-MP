package net.minecraft.game.world.biomes.warm;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Monkey;
import net.minecraft.game.entity.mob.animal.flying.Parrot;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.plants.PumpkinPatch;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.JungleTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Jungle extends WarmBiome {
    public Jungle(int id) {
        super(id);
        this.waterAttempts = 6;
        this.tallGrassAttempts = 2;
        this.beachBlock1 = (short)BlockList.sand.id;
        this.beachBlock2 = (short)BlockList.gravel.id;
        this.beachMeta1 = 0;
        this.beachMeta2 = 0;
        this.treeAttempts = 10;
        this.reedAttempts = 40;
        this.waterFallAttempts = 30;
        this.generateLava = false;
        this.animals.add(new SpawnListEntry(Wolf.class, 8));
        this.animals.add(new SpawnListEntry(Ocelot.class, 8));
        this.animals.add(new SpawnListEntry(Monkey.class, 6));
        this.animals.add(new SpawnListEntry(Parrot.class, 10));
        this.variants.add(BiomeList.sparseJungle);
        this.variants.add(BiomeList.bambooJungle);
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int bushLimit = 10;
        if (this.id == BiomeList.sparseJungle.id) {
            bushLimit = 5;
        }

        for (int i = 0; i < 4; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            PumpkinPatch.melonPatch.place(world, rand, x, y, z);
        }

        for (int var12 = 0; var12 < bushLimit; var12++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            Shrub.jungleShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new JungleTree();
    }

    @Override
    public int getSkyColor() {
        return 7002584;
    }
}
