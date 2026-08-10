package net.minecraft.game.world.generation.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.Chunk;

public abstract class SchematicStructure extends Structure {
    public int[][][] schematic;
    public List<SchematicStructure.StructureBlockData> specialBlocks = new ArrayList<>();
    public StructureBoundingBox aabb;

    public SchematicStructure(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.featureAABB.y1 = this.getY0();
        this.featureAABB.y2 = this.getY0() + this.getHeight();
    }

    public abstract int getHeight();

    public abstract void generateSchematic(World var1, Random var2, Biome var3, int var4, int var5);

    public abstract int getY0();

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        this.schematic = new int[(1 + this.getRadius() * 2) * 16][(1 + this.getRadius() * 2) * 16][this.getHeight()];
        this.aabb = new StructureBoundingBox(0, 0, 0, this.schematic.length - 1, this.schematic[0][0].length - 1, this.schematic[0].length - 1);
        this.generateSchematic(world, rand, biome, chunkX, chunkZ);
    }

    protected int getPieceX(int chunkX) {
        return chunkX - this.originChunkX + this.getRadius();
    }

    protected int getPieceZ(int chunkZ) {
        return chunkZ - this.originChunkZ + this.getRadius();
    }

    public void addSpecialBlock(int x, int y, int z, int blockID, int meta) {
        this.specialBlocks.add(new SchematicStructure.StructureBlockData(x, y, z, blockID, meta));
    }

    public void drawPieceOnGeneration(int chunkX, int chunkZ, short[] blockArray, byte[] metadata) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        int y0 = this.getY0();
        if (y0 >= 1) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int y = y0;
                    int idx = y << 8 | z << 4 | x;

                    for (int i = 0; i < this.getHeight(); i++) {
                        int blockAndMeta = this.schematic[pieceXoffs + x][pieceZoffs + z][i];
                        if (blockAndMeta != -1) {
                            blockArray[idx] = (short)(blockAndMeta & 4095);
                            metadata[idx] = (byte)(blockAndMeta >> 12 & 0xFF);
                        }

                        idx += 256;
                        y++;
                    }
                }
            }
        }
    }

    public void drawPieceOnPopulation(World world, Random rand, int chunkX, int chunkZ) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        Chunk chunk = world.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = this.getY0();

                for (int i = 0; i < this.getHeight(); i++) {
                    int blockAndMeta = this.schematic[pieceXoffs + x][pieceZoffs + z][i];
                    if (blockAndMeta != -1) {
                        short blockID = (short)(blockAndMeta & 4095);
                        byte metadata = (byte)(blockAndMeta >> 12 & 0xFF);
                        chunk.setBlockAndState(x, y, z, blockID, metadata);
                    }

                    y++;
                }
            }
        }
    }

    public void drawSpecialBlocksForChunk(World world, Random rand, int chunkX, int chunkZ) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        int y0 = this.getY0();

        for (SchematicStructure.StructureBlockData s : this.specialBlocks) {
            if (s.x >= pieceXoffs && s.x < pieceXoffs + 16 && s.z >= pieceZoffs && s.z < pieceZoffs + 16) {
                int xAbs = this.featureAABB.x1 + s.x;
                int zAbs = this.featureAABB.z1 + s.z;
                int yAbs = y0 + s.y;
                world.setBlockAndMetadata(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
                this.onSpecialBlockSet(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
            }
        }
    }

    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
    }

    public void fillSchematic(int v) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, v);
            }
        }
    }

    public boolean withinSphere(int x, int y, int z, int radius) {
        return x * x + y * y + z * z <= radius * radius;
    }

    public void setBlockWithClipping(int x, int y, int z, int block) {
        if (x >= 0 && x < this.schematic.length) {
            if (z >= 0 && z < this.schematic[0].length) {
                if (y >= 0 && y < this.schematic[0][0].length) {
                    this.schematic[x][z][y] = block;
                }
            }
        }
    }

    public void replaceBlockWithClipping(int x, int y, int z, int block, int replaceWith) {
        if (x >= 0 && x < this.schematic.length) {
            if (z >= 0 && z < this.schematic[0].length) {
                if (y >= 0 && y < this.schematic[0][0].length) {
                    if (this.schematic[x][z][y] == block) {
                        this.schematic[x][z][y] = replaceWith;
                    }
                }
            }
        }
    }

    public void sphere(int x0, int y0, int z0, int radius, int block, int replaceWith) {
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                for (int z = 0; z < radius; z++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        this.replaceBlockWithClipping(x0 + x, y0 + y, z0 + z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 - x, y0 + y, z0 + z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 + x, y0 + y, z0 - z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 - x, y0 + y, z0 - z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 + x, y0 - y, z0 + z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 - x, y0 - y, z0 + z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 + x, y0 - y, z0 - z, block, replaceWith);
                        this.replaceBlockWithClipping(x0 - x, y0 - y, z0 - z, block, replaceWith);
                    }
                }
            }
        }
    }

    public void halfSphere(int x0, int y0, int z0, int radius, int replaceWith) {
        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                for (int z = 0; z < radius; z++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        this.setBlockWithClipping(x0 + x, y0 + y, z0 + z, replaceWith);
                        this.setBlockWithClipping(x0 - x, y0 + y, z0 + z, replaceWith);
                        this.setBlockWithClipping(x0 + x, y0 + y, z0 - z, replaceWith);
                        this.setBlockWithClipping(x0 - x, y0 + y, z0 - z, replaceWith);
                    }
                }
            }
        }
    }

    public class StructureBlockData {
        public int x;
        public int y;
        public int z;
        public int blockID;
        public int blockMetadata;

        public StructureBlockData(int x, int y, int z, int blockID, int blockMetada) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockID = blockID;
            this.blockMetadata = blockMetada;
        }
    }
}
