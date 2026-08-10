package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.elemental.Blaze;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.hell.Glowstone;
import net.minecraft.game.world.generation.features.plants.TallPlants;
import net.minecraft.game.world.generation.features.underground.SpikeFeature;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class GoldCaverns extends HellBiome {
    public GoldCaverns(int biomeID) {
        super(biomeID);
        this.biomeCaves = true;
        this.caveBlock2 = (short)BlockList.ember.id;
        this.ironLumpAttempts = 20;
        this.monsters.add(new SpawnListEntry(Blaze.class, 10));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 20; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            Glowstone.INSTANCE.place(world, rand, x, y, z);
            x = x0 + 16;
            y = rand.nextInt(world.getWorldHeight());
            z = z0 + 16;
            SpikeFeature.GLOWSTONE.place(world, rand, x, y, z);
        }

        for (int var18 = 0; var18 < 10; var18++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            TallPlants.GLOWROOT.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        Boulder.glowing.place(world, rand, x, y, z);
    }

    @Override
    public int getSkyColor() {
        return 6697984;
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.SPORESGOLD, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
