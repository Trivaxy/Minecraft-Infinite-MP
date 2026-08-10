package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.Golem;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.hell.FireCluster;
import net.minecraft.game.world.generation.features.hell.GemsCluster;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class EchoingAbyss extends HellBiome {
    public EchoingAbyss(int biomeID) {
        super(biomeID);
        this.topBlock = this.caveBlock1 = (short)BlockList.obsidian.id;
        this.fillerBlock = (short)BlockList.obsidian.id;
        this.biomeCaves = true;
        this.caveBlock2 = (short)BlockList.clusterBlock.id;
        this.caveMeta2 = 3;
        this.defaultLiquid = 0;
        this.generateLava = false;
        this.monsters.clear();
        this.monsters.add(new SpawnListEntry(Golem.class, 8));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            FireCluster.INSTANCE.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + rand.nextInt(16) + 8;
            GemsCluster.INSTANCE.place(world, rand, x, y, z);
            x = x0 + 16;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + 16;
            SpikeFeature.GEMS.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        Boulder.obsidian.place(world, rand, x, y, z);
    }

    @Override
    public int getSkyColor() {
        return 3342387;
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.SPORESPURPLE, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
