package net.minecraft.game.world.generation;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.carvers.RavineCarver;
import net.minecraft.game.world.generation.features.general.Pillar;
import net.minecraft.game.world.generation.features.general.Pond;
import net.minecraft.game.world.generation.features.general.Shrine;
import net.minecraft.game.world.generation.features.general.Waterfall;
import net.minecraft.game.world.generation.features.hell.BasaltSpire;
import net.minecraft.game.world.generation.features.plants.GrassCluster;
import net.minecraft.game.world.generation.features.trees.coniferous.OriginTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public class VoidGenerator implements ChunkLoader {
    private final World world;
    private final Random rand;
    private RavineCarver ravineGenerator = new RavineCarver();
    private final OctaveNoise noiseGen1;
    private final OctaveNoise noiseGen2;
    private final OctaveNoise noiseGen3;
    private final OctaveNoise noiseGen4;
    private final OctaveNoise noiseGen5;
    private final OctaveNoise noiseGen6;
    private final OctaveNoise treeNoise;

    public VoidGenerator(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
        this.noiseGen1 = new OctaveNoise(this.rand, 16);
        this.noiseGen2 = new OctaveNoise(this.rand, 16);
        this.noiseGen3 = new OctaveNoise(this.rand, 8);
        this.noiseGen4 = new OctaveNoise(this.rand, 4);
        this.noiseGen5 = new OctaveNoise(this.rand, 4);
        this.noiseGen6 = new OctaveNoise(this.rand, 5);
        this.treeNoise = new OctaveNoise(this.rand, 5);
    }

    public void generateTerrain(int chunkX, int chunkZ, short[] blocks, byte[] metadata) {
        long blockX = (long)chunkX << 4;
        long blockZ = (long)chunkZ << 4;
        float multiplier1 = 512.0F;
        float multiplier2 = 4.0F;
        float multiplier3 = 8.0F;

        for (long x = blockX; x < blockX + 16L; x++) {
            for (long z = blockZ; z < blockZ + 16L; z++) {
                byte localX = (byte)(x & 15L);
                byte localZ = (byte)(z & 15L);
                int pyramidX = (int)(x / 1024L);
                int pyramidZ = (int)(z / 1024L);
                int index = localZ << 4 | localX;
                double terrainHeight = (
                        this.noiseGen1.generateNoise3D((float)x / 0.03125F, 0.0, (float)z / 0.03125F)
                            - this.noiseGen2.generateNoise3D(x / 0.015625, 0.0, z / 0.015625)
                    )
                    / multiplier1
                    / multiplier2;
                double surfaceHeight = this.noiseGen5.generateNoise((float)x / multiplier2, (float)z / multiplier2);
                double variation = this.noiseGen6.generateNoise((float)x / multiplier3, (float)z / multiplier3) / multiplier3;
                surfaceHeight = surfaceHeight > 0.0
                    ? this.noiseGen3.generateNoise(x * 0.05714284F, z * 0.05714284F) * variation / 4.0
                    : this.noiseGen4.generateNoise2D((float)x * 0.05714284F, z * 0.05714284F) * variation;
                long height = (int)(terrainHeight + this.world.getSeaLevel() / 2 + surfaceHeight);
                if (this.noiseGen5.generateNoise(x, z) < 0.0) {
                    height = height / 2L << 1;
                    if (this.noiseGen5.generateNoise(x / 5.0, z / 5.0) < 0.0) {
                        height++;
                    }
                }

                for (int y = 0; y < this.world.getWorldHeight(); y++) {
                    int blockID = 0;
                    int meta = 0;
                    if ((x == 0L || z == 0L) && y <= height + 2L) {
                        blockID = BlockList.stoneBrick.id;
                        meta = 4;
                    } else if (y <= height - 2L || y == height && height >= this.world.getSeaLevel() / 2 || y <= height) {
                        blockID = BlockList.voidRock.id;
                        meta = 0;
                    }

                    this.rand.setSeed(pyramidX + pyramidZ * 13871);
                    int posX = (pyramidX << 10) + 256 + this.rand.nextInt(512);
                    int posZ = (pyramidZ << 10) + 256 + this.rand.nextInt(512);
                    posX = (int)(x - posX);
                    posZ = (int)(z - posZ);
                    if (posX < 0) {
                        posX = -posX;
                    }

                    if (posZ < 0) {
                        posZ = -posZ;
                    }

                    if (posZ > posX) {
                        posX = posZ;
                    }

                    posX = 127 - posX;
                    if (posX == 128) {
                        posX = 1;
                    }

                    if (posX < height) {
                        posX = (int)height;
                    }

                    if (y <= posX && blockID == 0) {
                        blockID = BlockList.stoneBrick.id;
                        meta = 4;
                    }

                    if (blockID < 0) {
                        blockID = 0;
                    }

                    blocks[index] = (short)blockID;
                    metadata[index] = (byte)meta;
                    index += 256;
                }
            }
        }
    }

    public void replaceBlocks(int chunkX, int chunkZ, short[] blocks) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double posX = chunkX * 16 + x;
                double posZ = chunkZ * 16 + z;
                double div = 0.015625;
                boolean grass = this.noiseGen4.generateNoise3D(posX * div, posZ * div, 0.0) + this.rand.nextDouble() * 0.2 > 4.0;
                int grassAmount = (int)(this.noiseGen5.generateNoise2D(posX * div * 2.0, posZ * div * 2.0) / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
                int stoneLayer = -1;
                int top = BlockList.voidRock.id;
                int filler = BlockList.voidRock.id;
                int index = this.world.getWorldHeight() << 8 | z << 4 | x;

                for (int y = this.world.getWorldHeight() - 1; y >= 0; y--) {
                    index -= 256;
                    Block block = BlockList.blocks[blocks[index]];
                    if (y <= this.rand.nextInt(6) - 1) {
                        blocks[index] = (byte)BlockList.bedrock.id;
                    } else if (blocks[index] == 0) {
                        stoneLayer = -1;
                    } else if (block.material == MaterialList.rock && block != BlockList.stoneBrick) {
                        if (stoneLayer == -1) {
                            if (grass) {
                                top = BlockList.voidGrass.id;
                            }

                            stoneLayer = grassAmount;
                            blocks[index] = (short)top;
                        } else if (stoneLayer > 0) {
                            stoneLayer--;
                            blocks[index] = (short)filler;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed(x * 341873128712L + z * 132897987541L);
        short[] block = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(x, z, block, metadataArray);
        this.replaceBlocks(x, z, block);
        this.ravineGenerator.generate(this, this.world, x, z, block, metadataArray);
        Chunk chunk = new Chunk(this.world, block, metadataArray, x, z);
        chunk.generateHeightMap();
        chunk.generateSkylightMap();
        return chunk;
    }

    public int[] generateLandSurfaceHeightMap(short[] blockArray) {
        int index = 0;
        int[] landSurfaceHeightMap = new int[256];

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int y = this.world.getWorldHeight() - 1;

                for (int layerIndex = y << 8; blockArray[index | layerIndex] == 0 && y > 0; layerIndex -= 256) {
                    y--;
                }

                landSurfaceHeightMap[index] = y;
                index++;
            }
        }

        return landSurfaceHeightMap;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        short[] blockArray = new short[256 * this.world.getWorldHeight()];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        this.generateTerrain(chunkX, chunkZ, blockArray, metadataArray);
        int[] landSurfaceHeightMap = this.generateLandSurfaceHeightMap(blockArray);
        Chunk chunk = new Chunk(this.world, blockArray, metadataArray, chunkX, chunkZ);
        chunk.landSurfaceHeightMap(landSurfaceHeightMap);
        return chunk;
    }

    @Override
    public void populate(ChunkLoader loader, int chunkX, int chunkZ) {
        int x0 = chunkX << 4;
        int z0 = chunkZ << 4;
        this.rand.setSeed(this.world.getSeed());
        long xOff = this.rand.nextLong() / 2L * 2L + 1L;
        long zOff = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * xOff + chunkZ * zOff ^ this.world.getSeed());

        for (int i = 0; i < 10; i++) {
            int x = x0 + this.rand.nextInt(16) + 8;
            int z = z0 + this.rand.nextInt(16) + 8;
            OriginTree.INSTANCE.place(this.world, this.rand, x, this.world.getHeightmap(x, z), z);
        }

        int x = x0 + this.rand.nextInt(16) + 8;
        int z = z0 + this.rand.nextInt(16) + 8;
        new GrassCluster(BlockList.glowGrass.id, this.rand.nextInt(2)).place(this.world, this.rand, x, this.world.getHeightmap(x, z), z);
        if ((int)this.treeNoise.generateNoise2D(x0 * 0.02125, z0 * 0.02125) + this.rand.nextDouble() * 0.2 > 6.0) {
            x = x0 + this.rand.nextInt(16) + 8;
            z = z0 + this.rand.nextInt(16) + 8;
            BasaltSpire.VOID.place(this.world, this.rand, x, this.world.getHeightmap(x, z), z);
            x = x0 + this.rand.nextInt(16) + 8;
            z = z0 + this.rand.nextInt(16) + 8;
            Pond.WATER.place(this.world, this.rand, x, this.world.getHeightmap(x, z), z);

            for (int var14 = 0; var14 < 64; var14++) {
                x = x0 + this.rand.nextInt(16) + 8;
                z = z0 + this.rand.nextInt(16) + 8;
                Waterfall.VOIDWATER.place(this.world, this.rand, x, this.rand.nextInt(this.world.getSeaLevel()), z);
            }
        }

        if (this.rand.nextBoolean() && x0 >= -64 && z0 >= -64 && x0 <= 64 && z0 <= 64) {
            x = x0 + this.rand.nextInt(16) + 8;
            z = z0 + this.rand.nextInt(16) + 8;
            int y = this.world.getTopSolidTile(x, z);
            Pillar.INSTANCE.place(this.world, this.rand, x, y, z);
        }

        if (x0 == 0 && z0 == 0) {
            int y = this.world.getTopSolidTile(x0, z0);
            Shrine.INSTANCE.place(this.world, this.rand, x0, y, z0);
        }
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public boolean saveChunks(boolean flag, ProgressBar progress) {
        return true;
    }

    @Override
    public boolean unloadOldChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String loaderName() {
        return "VoidGenerator";
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return null;
    }
}
