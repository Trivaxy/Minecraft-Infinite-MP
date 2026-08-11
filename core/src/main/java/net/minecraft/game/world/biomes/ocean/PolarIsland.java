package net.minecraft.game.world.biomes.ocean;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.entity.mob.animal.Seal;
import net.minecraft.game.entity.mob.animal.flying.Penguin;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFrost;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.cold.ColdBiome;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class PolarIsland extends ColdBiome {
    public PolarIsland(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.snowBlock.id;
        this.fillerBlock = (short)BlockList.snowBlock.id;
        this.topMeta = this.fillerMeta = 0;
        this.beachBlock2 = (short)BlockList.silverSand.id;
        this.treeAttempts = -100;
        this.stoneLayer = -1;
        this.animals.add(new SpawnListEntry(Bear.class, 8));
        this.animals.add(new SpawnListEntry(Penguin.class, 10));
        this.animals.add(new SpawnListEntry(Seal.class, 8));
        this.monsters.add(new SpawnListEntry(CreeperFrost.class, 4));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + rand.nextInt(16) + 8;
        int z = z0 + rand.nextInt(16) + 8;
        int y = world.getHeightmap(x, z);

        for (int i = 0; i < 2; i++) {
            x = x0 + 16;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + 16;
            SpikeFeature.ICE.place(world, rand, x, y, z);
        }
    }
}
