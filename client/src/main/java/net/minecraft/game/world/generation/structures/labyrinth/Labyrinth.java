package net.minecraft.game.world.generation.structures.labyrinth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.VoidBiome;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureBoundingBox;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.game.world.generation.structures.StructurePiece;

public class Labyrinth extends Structure {
    public static Random randPieces = new Random();
    public StructureBoundingBox featureAABB;
    protected List<StructurePiece> componentsList;
    protected List<StructurePiece> corridorPiecesList;
    public int y0;
    private int count = 0;
    public long randomSeed = 0L;

    public Labyrinth(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.componentsList = new ArrayList<>();
        this.corridorPiecesList = new ArrayList<>();
        this.featureAABB = new StructureBoundingBox(
            originChunkX - this.getRadius() << 4,
            originChunkZ - this.getRadius() << 4,
            15 + (originChunkX + this.getRadius() << 4),
            15 + (originChunkZ + this.getRadius() << 4)
        );
        this.randomSeed = world.getSeed() + originChunkX * 25117L + originChunkZ * 151121L;
    }

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        randPieces.setSeed(this.randomSeed);
        this.generateDungeon(randPieces, this.centerX - 4, this.y0, this.centerZ - 4, true);
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
        this.y0 = rand.nextInt(world.getSeaLevel()) + 24;
        Chunk chunk = world.justGenerateForHeight(chunkX, chunkZ);
        Block block = BlockList.blocks[chunk.getBlockID(8, this.y0, 8)];
        return block != null && block.fullBlock() && !(biome instanceof HellBiome) && !(biome instanceof SkyBiome) && !(biome instanceof VoidBiome);
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        StructureBoundingBox chunkAABB = new StructureBoundingBox(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15);

        for (StructurePiece building : this.componentsList) {
            if (chunkAABB.collidesWith(building.aabb)) {
                building.populate(chunkX, chunkZ);
            }
        }

        for (StructurePiece building : this.corridorPiecesList) {
            if (chunkAABB.collidesWith(building.aabb)) {
                building.populate(chunkX, chunkZ);
            }
        }
    }

    public void generateDungeon(Random rand, int x, int y, int z, boolean firstTime) {
        LabyrinthPiece room;
        if (firstTime) {
            room = new LabyrinthRoom(this.world, false);
        } else if (rand.nextInt(4) != 0) {
            room = new LabyrinthRoom(this.world, false);
        } else {
            room = new LabyrinthStaircase(this.world, false);
            y -= 7;
        }

        if (this.count < 3) {
            room.place(x, y, z);
            this.componentsList.add(room);
            if (y > 12) {
                boolean spawnedCorridor = false;

                do {
                    if (rand.nextBoolean()) {
                        int corridorLength = rand.nextInt(3) + 2;
                        StructureBoundingBox aabb = new StructureBoundingBox(x + 1, z - 3 * corridorLength - 9 + 2, x + 7, z - 1);
                        if (!this.checkForCollisions(y, aabb) && this.featureAABB.containsFully(aabb)) {
                            for (int i = 0; i < corridorLength; i++) {
                                LabyrinthPiece corridorPiece = new LabyrinthCorridorNS(this.world, false);
                                corridorPiece.place(x + 2, y, z - 3 * i - 2);
                                this.corridorPiecesList.add(corridorPiece);
                            }

                            this.generateDungeon(rand, x, y, z - 3 * corridorLength - 9 + 2, false);
                            spawnedCorridor = true;
                        }
                    }

                    if (rand.nextBoolean()) {
                        int corridorLength = rand.nextInt(3) + 2;
                        StructureBoundingBox aabb = new StructureBoundingBox(x + 1, z + 9, x + 7, z + 8 + 3 * corridorLength + 9 - 1);
                        if (!this.checkForCollisions(y, aabb) && this.featureAABB.containsFully(aabb)) {
                            for (int i = 0; i < corridorLength; i++) {
                                LabyrinthPiece corridorPiece = new LabyrinthCorridorNS(this.world, false);
                                corridorPiece.place(x + 2, y, z + 8 + 3 * i);
                                this.corridorPiecesList.add(corridorPiece);
                            }

                            this.generateDungeon(rand, x, y, z + 8 + 3 * corridorLength - 1, false);
                            spawnedCorridor = true;
                        }
                    }

                    if (rand.nextBoolean()) {
                        int corridorLength = rand.nextInt(4) + 1;
                        StructureBoundingBox aabb = new StructureBoundingBox(x - 3 * corridorLength - 9 + 2, z + 1, x - 1, z + 7);
                        if (!this.checkForCollisions(y, aabb) && this.featureAABB.containsFully(aabb)) {
                            for (int i = 0; i < corridorLength; i++) {
                                LabyrinthPiece corridorPiece = new LabyrinthCorridorWE(this.world, false);
                                corridorPiece.place(x - 3 * i - 2, y, z + 2);
                                this.corridorPiecesList.add(corridorPiece);
                            }

                            this.generateDungeon(rand, x - 3 * corridorLength - 9 + 2, y, z, false);
                            spawnedCorridor = true;
                        }
                    }

                    if (rand.nextBoolean()) {
                        int corridorLength = rand.nextInt(4) + 1;
                        StructureBoundingBox aabb = new StructureBoundingBox(x + 9, z + 1, x + 8 + 3 * corridorLength + 9 - 1, z + 7);
                        if (!this.checkForCollisions(y, aabb) && this.featureAABB.containsFully(aabb)) {
                            for (int i = 0; i < corridorLength; i++) {
                                LabyrinthPiece corridorPiece = new LabyrinthCorridorWE(this.world, false);
                                corridorPiece.place(x + 8 + 3 * i, y, z + 2);
                                this.corridorPiecesList.add(corridorPiece);
                            }

                            this.generateDungeon(rand, x + 8 + 3 * corridorLength - 1, y, z, false);
                            spawnedCorridor = true;
                        }
                    }
                } while (firstTime && !spawnedCorridor);
            }

            this.count++;
        }
    }

    boolean checkForCollisions(int y, StructureBoundingBox aabb) {
        for (StructurePiece building : this.componentsList) {
            if (aabb.collidesWith(building.aabb) && building.y0 == y) {
                return true;
            }
        }

        return false;
    }
}
