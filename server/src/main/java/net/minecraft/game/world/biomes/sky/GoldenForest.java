package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Moose;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.general.Stump;
import net.minecraft.game.world.generation.features.hell.GemsCluster;
import net.minecraft.game.world.generation.features.plants.LeafPiles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.GoldTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class GoldenForest extends SkyBiome {
    public GoldenForest(int biomeID) {
        super(biomeID);
        this.topMeta = 10;
        this.treeAttempts = 3;
        this.grassMeta = 3;
        this.caveBlock1 = (short)BlockList.clusterBlock.id;
        this.caveMeta1 = 4;
        this.goldLumpAttempts *= 20;
        this.reedAttempts = 0;
        this.animals.add(new SpawnListEntry(Moose.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);
        if (rand.nextInt(2) == 0) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            Boulder.angelite.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);
        Stump.oldOak.place(world, rand, x, y, z);

        for (int i = 0; i < 20; i++) {
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GemsCluster.INSTANCE.place(world, rand, x, y, z);
        }

        z = x0 + rand.nextInt(16) + 8;
        x = rand.nextInt(world.getWorldHeight());
        y = z0 + rand.nextInt(16) + 8;
        LeafPiles.GOLD.place(world, rand, z, x, y);
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new GoldTree();
    }

    @Override
    public int getSkyColor() {
        return 13750703;
    }
}
