package net.minecraft.game.world.biomes.temperate;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;

public class Springs extends Mountains {
    private OctaveNoise stoneType;
    private long seed;

    public Springs(int biomeID) {
        super(biomeID);
        this.waterAttempts = 2;
        this.waterFallAttempts = 40;
        this.waterFallMaxHeight = 256;
        this.unlockBeachHeight = false;
        this.stoneHeight = -1;
    }

    @Override
    public Tree getRandomTreeGenerator(Random rand, int y, int worldHeight) {
        return rand.nextInt(5) == 0 ? new SpruceTree() : new PineTree();
    }

    @Override
    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        if (this.seed != world.getSeed() || this.stoneType == null) {
            this.seed = world.getSeed();
            this.stoneType = new OctaveNoise(new Random(this.seed), 8);
        }

        boolean generateRed = this.stoneType.generateNoise2D(x, z) > 64.0;
        boolean generateWhite = this.stoneType.generateNoise2D(x, z) > 48.0;
        boolean generateBlack = this.stoneType.generateNoise2D(x, z) > 32.0;
        if (generateRed) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.topMeta = this.fillerMeta = 0;
        } else if (generateWhite) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.topMeta = this.fillerMeta = 1;
        } else if (generateBlack) {
            this.topBlock = (short)BlockList.stone.id;
            this.fillerBlock = (short)BlockList.stone.id;
            this.topMeta = this.fillerMeta = 2;
        } else {
            this.topBlock = (short)BlockList.grass.id;
            this.fillerBlock = (short)BlockList.dirt.id;
            this.topMeta = this.fillerMeta = 0;
        }

        super.surfaceBuilder(world, random, blocks, metadata, x, z, heightValue, sandValue, gravelValue);
        this.topMeta = this.fillerMeta = 0;
    }
}
