package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.game.world.generation.noise.OctaveNoise;

public class Puddles extends Feature {
    private static OctaveNoise noiseGen;
    private static long seed = 0L;
    private int blockId;
    private int maxHeight;
    private int generateOnId = 0;

    public Puddles(int blockId, int hght) {
        this.blockId = blockId;
        this.maxHeight = hght;
    }

    public Puddles(int blockId, int hght, int id) {
        this.blockId = blockId;
        this.maxHeight = hght;
        this.generateOnId = id;
    }

    @Override
    public boolean place(World world, Random rand, int posX, int posY, int posZ) {
        if (noiseGen == null || seed != world.getSeed()) {
            seed = world.getSeed();
            noiseGen = new OctaveNoise(rand, 4);
        }

        for (int x = posX - 8; x < posX + 8; x++) {
            for (int z = posZ - 8; z < posZ + 8; z++) {
                for (int y = this.maxHeight; y < posY; y++) {
                    if (world.isAir(x, y + 1, z)
                        && world.isBlockFull(x, y, z)
                        && world.getBlock(x, y, z) != BlockList.leaves
                        && world.getBlock(x, y, z) != BlockList.mushroomCap
                        && world.isBlockFull(x, y - 1, z)
                        && noiseGen.generateNoise2D(x, z) > 0.0
                        && this.suitableNeighbor(world, x + 1, y, z)
                        && this.suitableNeighbor(world, x - 1, y, z)
                        && this.suitableNeighbor(world, x, y, z + 1)
                        && this.suitableNeighbor(world, x, y, z - 1)) {
                        world.setBlock(x, y, z, this.blockId);
                    }
                }
            }
        }

        return true;
    }

    private boolean suitableNeighbor(World world, int x, int y, int z) {
        return this.generateOnId > 0
            ? world.getBlockId(x, y, z) == this.generateOnId || world.getBlockMaterial(x, y, z) == BlockList.blocks[this.blockId].material
            : world.isBlockFull(x, y, z) || world.getBlockMaterial(x, y, z) == BlockList.blocks[this.blockId].material;
    }
}
