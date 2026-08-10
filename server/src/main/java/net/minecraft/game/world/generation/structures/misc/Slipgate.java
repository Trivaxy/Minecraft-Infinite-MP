package net.minecraft.game.world.generation.structures.misc;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.VoidBiome;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Slipgate extends Structure {
    public static final int radiusSquared = 144;
    public static final int radiusInnerSquared = 36;

    public Slipgate(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        int rx = (chunkX << 4) - this.centerX;

        for (int x = 0; x < 16; x++) {
            int rz = (chunkZ << 4) - this.centerZ;

            for (int z = 0; z < 16; z++) {
                int index = z << 4 | x;
                int d = rx * rx + rz * rz;
                if (d < 144) {
                    int height = 16;
                    if (biomesForGeneration[index] instanceof HellBiome) {
                        height = this.world.getWorldHeight() - 8;

                        for (int y = this.world.getWorldHeight() - 16; y <= height; y++) {
                            blockArray[index | y << 8] = 0;
                        }
                    } else {
                        for (int y = 8; y <= height; y++) {
                            blockArray[index | y << 8] = 0;
                        }

                        for (int y = 0; y < 8; y++) {
                            if (this.world.rand.nextBoolean()) {
                                blockArray[index | y << 8] = (short)BlockList.netherrack.id;
                            }
                        }
                    }
                }

                if (d < 36) {
                    int height = 16;
                    if (biomesForGeneration[index] instanceof HellBiome) {
                        height = this.world.getWorldHeight() - 8;

                        for (int y = this.world.getWorldHeight() - 1; y >= height; y--) {
                            blockArray[index | y << 8] = 0;
                        }
                    } else {
                        for (int y = 0; y <= height; y++) {
                            blockArray[index | y << 8] = 0;
                        }
                    }
                }

                rz++;
            }

            rx++;
        }
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
    }

    @Override
    public int getRadius() {
        return 2;
    }

    @Override
    public int getSpawnChance() {
        return 10;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return !(biome instanceof SkyBiome) && !(biome instanceof VoidBiome);
    }
}
