package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.mounts.horse.SkeletonHorse;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Boulder;
import net.minecraft.game.world.generation.features.hell.FossilSpine;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class HellDesert extends HellBiome {
    public HellDesert(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.soulSoil.id;
        this.fillerBlock = (short)BlockList.soulSoil.id;
        this.biomeCaves = true;
        this.caveBlock2 = (short)BlockList.slowSand.id;
        this.animals.add(new SpawnListEntry(SkeletonHorse.class, 8));
        this.monsters.add(new SpawnListEntry(Skeleton.class, 8));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        for (int i = 0; i < 5; i++) {
            int x = x0 + rand.nextInt(16) + 8;
            int y = rand.nextInt(world.getWorldHeight());
            int z = z0 + rand.nextInt(16) + 8;
            FossilSpine.INSTANCE.place(world, rand, x, y, z);
        }

        int x = x0 + rand.nextInt(16) + 8;
        int y = rand.nextInt(world.getWorldHeight());
        int z = z0 + rand.nextInt(16) + 8;
        Boulder.hell.place(world, rand, x, y, z);
    }

    @Override
    public int getSkyColor() {
        return 13107;
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.ASH, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
