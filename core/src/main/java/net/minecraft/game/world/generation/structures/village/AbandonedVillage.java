package net.minecraft.game.world.generation.structures.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.cold.Muskeg;
import net.minecraft.game.world.biomes.cold.Tundra;
import net.minecraft.game.world.biomes.temperate.Plains;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureBoundingBox;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.game.world.generation.structures.StructurePiece;

public class AbandonedVillage extends Structure {
    public Random randPieces = new Random();
    public StructureBoundingBox featureAABB;
    protected List<StructurePiece> componentsList;
    public int y0;
    private int count = 0;
    private int numberOfBuildings;
    public long randomSeed = 0L;

    public AbandonedVillage(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.componentsList = new ArrayList<>();
        this.featureAABB = new StructureBoundingBox(
            originChunkX - this.getRadius() << 4,
            originChunkZ - this.getRadius() << 4,
            15 + (originChunkX + this.getRadius() << 4),
            15 + (originChunkZ + this.getRadius() << 4)
        );
        this.randomSeed = world.getSeed() + originChunkX * 25117L + originChunkZ * 151121L;
        this.numberOfBuildings = world.rand.nextInt(5) + 5;
    }

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        this.randPieces.setSeed(this.randomSeed);
        if (this.y0 <= world.getSeaLevel() + 24) {
            this.generateVillage(this.randPieces, this.centerX, this.y0, this.centerZ, true);
        }
    }

    @Override
    public boolean shouldSpawn(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return (biome instanceof Plains || biome instanceof Tundra || biome instanceof Muskeg)
            && super.shouldSpawn(chunkProvider, world, rand, biome, chunkX, chunkZ);
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public int getSpawnChance() {
        return 5;
    }

    @Override
    public int minimumSeparation() {
        return 10;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        Chunk chunk = world.justGenerateForHeight(chunkX, chunkZ);
        this.y0 = chunk.getSurfaceHeightmap(8, 8);
        if (this.y0 <= 0) {
            return false;
        }

        Block block = BlockList.blocks[chunk.getBlockID(8, this.y0, 8)];
        return block != null && block.fullBlock();
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
    }

    public void generateVillage(Random rand, int x, int y, int z, boolean firstTime) {
        if (this.count < this.numberOfBuildings) {
            House room;
            switch (rand.nextInt(2)) {
                case 1:
                    room = new BigHouse(this.world, rand.nextBoolean());
                    break;
                default:
                    room = new SmallHouse(this.world, rand.nextBoolean());
            }

            room.place(x, y, z);
            this.componentsList.add(room);
            int directionX = (rand.nextInt(2) << 1) - 1;
            int directionZ = (rand.nextInt(2) << 1) - 1;
            int newX = (rand.nextInt(15) + 12) * directionX;
            int newZ = (rand.nextInt(15) + 12) * directionZ;
            y = this.world.getSurfaceHeightmap(x + newX + room.getWidth() / 2, z + newZ + room.getLength() / 2);
            StructureBoundingBox aabb = new StructureBoundingBox(x + newX, z + newZ, x + newX + 12, z + newZ + 12);
            if (!this.checkForCollisions(aabb) && this.featureAABB.containsFully(aabb) && y <= this.world.getSeaLevel() + 12) {
                this.count++;
                this.generateVillage(rand, x + newX, y, z + newZ, false);
            }
        }
    }

    boolean checkForCollisions(StructureBoundingBox aabb) {
        for (StructurePiece building : this.componentsList) {
            if (aabb.collidesWith(building.aabb)) {
                return true;
            }
        }

        return false;
    }
}
