package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFrost;
import net.minecraft.game.entity.mob.monster.skeleton.Stray;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.generation.features.underground.Stalactites;
import net.minecraft.game.world.generation.features.underground.Stalagmites;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public abstract class ColdBiome extends Biome {
    protected ColdBiome(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.grass.id;
        this.topMeta = 1;
        this.fillerBlock = (short)BlockList.dirt.id;
        this.fillerMeta = 1;
        this.beachBlock1 = (short)BlockList.silverSand.id;
        this.tallGrassAttempts = 1;
        this.biomeCaves = true;
        this.caveBlock1 = (short)BlockList.snowBlock.id;
        this.caveMeta2 = 6;
        this.treeAttempts = 10;
        this.defaultStoneType = 6;
        this.sapphireLumpAttempts = 8;
        this.grassMeta = 1;
        this.generateLava = false;
        this.lavaFallAttempts = 0;
        this.monsters.add(new SpawnListEntry(Stray.class, 6));
        this.monsters.add(new SpawnListEntry(CreeperFrost.class, 4));
    }

    @Override
    public void postPopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int z = z0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(24) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            SpikeFeature.ICESTONE.place(world, rand, x, y, z);
        }

        if (rand.nextInt(4) == 0) {
            int x = x0 + 9;
            int z = z0 + 9;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            new Puddles(BlockList.ice.id, this.yOffset).place(world, rand, x, y, z);
        }

        for (int var18 = 0; var18 < 250; var18++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            int z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y - 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y - 1, z) == 6) {
                Stalagmites.icicle.place(world, rand, x, y, z);
            }

            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(48) + this.yOffset;
            if (world.settings.getType() == WorldType.CAVES) {
                y = rand.nextInt(world.getWorldHeight());
            }

            z = z0 + rand.nextInt(16) + 8;
            if (world.getBlockId(x, y + 1, z) == BlockList.stone.id && world.getBlockMetadata(x, y + 1, z) == 6) {
                Stalactites.icicle.place(world, rand, x, y, z);
            }
        }
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(5) == 0 ? new SpruceTree() : new PineTree();
    }

    @Override
    public int getSkyColor() {
        return this.hasSnow() ? 13030383 : 9803519;
    }

    @Override
    protected Biome enableSnow() {
        this.enableSnow = true;
        this.tallGrassAttempts = this.reedAttempts = 0;
        return this;
    }
}
