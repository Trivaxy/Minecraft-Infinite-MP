package net.minecraft.game.world.biomes.cold;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.misc.ShavedTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;

public class Muskeg extends Tundra {
    private OctaveNoise snowNoise;
    private long noiseSeed;

    public Muskeg(int biomeID) {
        super(biomeID);
        this.topBlock = (short)BlockList.dirt.id;
        this.treeAttempts = -1;
        this.flowersAttempts = 0;
        this.generateWater = false;
        this.generateLava = false;
    }

    @Override
    public Tree defaultTreeType(Random rand) {
        return new ShavedTree(2, 3, 1);
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.noiseSeed != world.getSeed() || this.snowNoise == null) {
            this.noiseSeed = world.getSeed();
            this.snowNoise = new OctaveNoise(new Random(this.noiseSeed), 6);
        }

        boolean snowStripes = this.snowNoise.generateNoise2D(x, z) > 1.0;
        if (!snowStripes) {
            this.topBlock = (short)BlockList.silverSand.id;
            this.fillerBlock = (short)BlockList.silverSand.id;
            this.topMeta = 0;
            this.fillerMeta = 0;
        } else {
            this.topBlock = (short)BlockList.dirt.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = 1;
            this.fillerMeta = 1;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
        this.topMeta = 1;
        this.fillerMeta = 1;
    }
}
