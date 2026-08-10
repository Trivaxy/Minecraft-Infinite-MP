package net.minecraft.game.world.biomes.hell;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.slime.MagmaCube;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.general.Puddles;
import net.minecraft.game.world.generation.features.hell.AshCluster;
import net.minecraft.game.world.generation.features.hell.BasaltSpire;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class BasaltDeltas extends HellBiome {
    private OctaveNoise grassNoise;
    private long noiseSeed;

    public BasaltDeltas(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.stone.id;
        this.topMeta = 2;
        this.fillerBlock = (short)BlockList.stone.id;
        this.fillerMeta = 2;
        this.beachBlock1 = this.beachBlock2 = (short)BlockList.magma.id;
        this.caveBlock2 = (short)BlockList.stone.id;
        this.caveMeta2 = 2;
        this.diamondLumpAttempts *= 2;
        this.lavaFallAttempts = 16;
        this.monsters.add(new SpawnListEntry(MagmaCube.class, 12));
    }

    @Override
    public void prePopulate(World world, Random rand, int x0, int z0) {
        int x = x0 + 9;
        int z = z0 + 9;
        int y = rand.nextInt(world.getWorldHeight());
        new Puddles(BlockList.lavaStill.id, this.yOffset).place(world, rand, x, y, z);
        x = x0 + 16;
        z = z0 + 16;
        y = rand.nextInt(world.getWorldHeight());
        AshCluster.INSTANCE.place(world, rand, x, y, z);

        for (int i = 0; i < 5; i++) {
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            y = world.getRandomHeight(x, z);
            BasaltSpire.INSTANCE.place(world, rand, x, y, z);
            x = x0 + rand.nextInt(16) + 8;
            z = z0 + rand.nextInt(16) + 8;
            y = world.getRandomHeight(x, z);
            BasaltSpire.INVERSE.place(world, rand, x, y, z);
        }
    }

    @Override
    public void caveBuilder(World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
        if (this.noiseSeed != world.getSeed() || this.grassNoise == null) {
            this.noiseSeed = world.getSeed();
            this.grassNoise = new OctaveNoise(new Random(this.noiseSeed), 6);
        }

        boolean generateGrass = Math.abs(this.grassNoise.generateNoise2D(x, z)) < 3.0;
        if (generateGrass) {
            this.topBlock = (short)BlockList.magma.id;
            this.topMeta = 0;
        } else {
            this.topBlock = (short)BlockList.stone.id;
            this.topMeta = 2;
        }

        super.caveBuilder(world, random, blocks, metadata, x, z, heightValue);
    }

    @Override
    public int getSkyColor() {
        return 3355443;
    }

    @Override
    public void spawnParticles(World world, double posX, double posY, double posZ) {
        world.spawnParticle(ParticleType.ASH, posX, posY, posZ, 0.0, 0.0, 0.0);
    }
}
