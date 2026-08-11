package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.entity.mob.animal.Seal;
import net.minecraft.game.entity.mob.animal.flying.Penguin;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnListEntry;

public class Glacier extends Fjord {
    private OctaveNoise snowNoise;
    private OctaveNoise iceNoise;
    private long noiseSeed;

    public Glacier(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.snowBlock.id;
        this.fillerBlock = (short)BlockList.snowBlock.id;
        this.treeAttempts = -100;
        this.waterFallAttempts = 0;
        this.stoneLayer = -1;
        this.generateLava = false;
        this.topMeta = this.fillerMeta = 0;
        this.hasBeaches = false;
        this.animals.clear();
        this.animals.add(new SpawnListEntry(Bear.class, 8));
        this.animals.add(new SpawnListEntry(Penguin.class, 10));
        this.animals.add(new SpawnListEntry(Seal.class, 8));
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.noiseSeed == 0L) {
            this.noiseSeed = world.getSeed();
            Random rand = new Random(this.noiseSeed);
            this.snowNoise = new OctaveNoise(rand, 8);
            this.iceNoise = new OctaveNoise(rand, 6);
        }

        boolean generateSand = this.snowNoise.generateNoise2D(x, z) > 72.0;
        boolean generateGrass = Math.abs(this.iceNoise.generateNoise2D(x * 0.6, z * 0.6)) < 3.0;
        if (generateSand) {
            this.topBlock = (short)BlockList.powderSnow.id;
            this.fillerBlock = (short)BlockList.powderSnow.id;
        } else if (generateGrass) {
            this.topBlock = (short)BlockList.packedIce.id;
            this.fillerBlock = (short)BlockList.packedIce.id;
        } else {
            this.topBlock = (short)BlockList.snowBlock.id;
            this.fillerBlock = (short)BlockList.snowBlock.id;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, -900.0, -900.0);
    }

    @Override
    public int getSkyColor() {
        return 13030383;
    }
}
