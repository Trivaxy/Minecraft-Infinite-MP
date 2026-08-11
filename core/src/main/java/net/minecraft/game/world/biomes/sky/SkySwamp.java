package net.minecraft.game.world.biomes.sky;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.slime.AquaCube;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.Shrub;
import net.minecraft.game.world.generation.features.trees.tropical.WillowTree;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class SkySwamp extends SkyBiome {
    public SkySwamp(int biomeID, Biome.Temperature temp) {
        super(biomeID);
        this.topMeta = 9;
        this.grassMeta = 8;
        this.waterAttempts = 1;
        this.tallGrassAttempts = 3;
        this.caveBlock2 = (short)BlockList.dirt.id;
        this.caveMeta2 = 7;
        this.reedAttempts = 0;
        if (temp == Biome.Temperature.COLD) {
            this.tallGrassAttempts = 0;
            this.caveBlock1 = (short)BlockList.snowBlock.id;
            this.caveBlock2 = (short)BlockList.packedIce.id;
            this.caveMeta1 = 0;
            this.caveMeta2 = 0;
        }

        this.monsters.add(new SpawnListEntry(AquaCube.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        super.prePopulate(world, rand, x0, z0);

        for (int i = 0; i < 3; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            Shrub.willowShrub.place(world, rand, x, world.getHeightmap(x, z), z);
        }

        int x = x0 + 9;
        int z = z0 + 9;
        if (this.id == BiomeList.skySwampFrozen.id) {
            new Puddles(BlockList.ice.id, this.yOffset).place(world, rand, x, rand.nextInt(world.getWorldHeight()), z);
        } else {
            new Puddles(BlockList.waterStill.id, this.yOffset).place(world, rand, x, rand.nextInt(world.getWorldHeight()), z);
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new WillowTree(rand.nextInt(4) + 6);
    }

    @Override
    public int getSkyColor() {
        return 12116223;
    }
}
