package net.minecraft.game.world.generation;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.noise.OctaveNoise;

public class ClassicGenerator extends OverworldGenerator implements ChunkLoader {
    private final OctaveNoise classicNoise = new OctaveNoise(this.rand, 5);

    public ClassicGenerator(World world1, long seed) {
        super(world1, seed);
    }

    @Override
    public void generateTerrain(int chunkX, int chunkZ, short[] blocks) {
        long blockX = (long)chunkX << 4;
        long blockZ = (long)chunkZ << 4;
        float multiplier1 = 512.0F;
        float multiplier2 = 4.0F;
        float multiplier3 = 8.0F;

        for (long x = blockX; x < blockX + 16L; x++) {
            for (long z = blockZ; z < blockZ + 16L; z++) {
                byte localX = (byte)(x & 15L);
                byte localZ = (byte)(z & 15L);
                int index = localZ << 4 | localX;
                double avgTerrainHeight = (
                        this.minLimitNoise.generateNoise3D(x / 0.03125, 0.0, z / 0.03125) - this.maxLimitNoise.generateNoise3D(x / 0.015625, 0.0, z / 0.015625)
                    )
                    / multiplier1
                    / multiplier2;
                double surfaceHeight = this.surfaceHeightNoise.generateNoise((float)x / multiplier2, (float)z / multiplier2);
                double variationAmount = this.classicNoise.generateNoise((float)x / multiplier3, (float)z / multiplier3) / multiplier3;
                surfaceHeight = surfaceHeight > 0.0
                    ? this.mainNoise.generateNoise(x * 0.25714284F * 2.0, z * 0.25714284F * 2.0) * variationAmount / 4.0
                    : this.surfaceHeightNoise.generateNoise2D((float)x * 0.25714284F, z * 0.25714284F) * variationAmount;
                long yValue = (int)(avgTerrainHeight + this.world.getSeaLevel() + this.world.getSeaLevel() / 8 + surfaceHeight);
                if (this.surfaceHeightNoise.generateNoise(x, z) < 0.0) {
                    yValue = yValue / 2L << 1;
                    if (this.surfaceHeightNoise.generateNoise(x / 5.0, z / 5.0) < 0.0) {
                        yValue++;
                    }
                }

                for (int y = 0; y < this.world.getWorldHeight(); y++) {
                    int blockID = 0;
                    if (y < this.world.getSeaLevel()) {
                        blockID = BlockList.waterStill.id;
                    }

                    if (y <= yValue - 2L || y == yValue && yValue >= this.world.getSeaLevel() || y <= yValue) {
                        blockID = BlockList.stone.id;
                    }

                    if (blockID < 0) {
                        blockID = 0;
                    }

                    blocks[index] = (byte)blockID;
                    index += 256;
                }
            }
        }
    }

    @Override
    public String loaderName() {
        return "ClassicGenerator";
    }
}
