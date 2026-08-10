package net.minecraft.game.world.generation.structures.oceanruins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureBoundingBox;
import net.minecraft.game.world.generation.structures.StructureList;
import net.minecraft.game.world.generation.structures.StructurePiece;

public class OceanRuins extends Structure {
    public Random randPieces;
    protected List<StructurePiece> buildingList = new ArrayList<>();
    private List<Class<?>> availableBuildingsUnrolled = new ArrayList<>();

    public OceanRuins(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        BuildingType[] availableBuildings = this.getAvailableBuildings();

        for (int i = 0; i < availableBuildings.length; i++) {
            for (int j = 0; j < availableBuildings[i].probability; j++) {
                this.availableBuildingsUnrolled.add(availableBuildings[i].buildingClass);
            }
        }
    }

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        long seed = this.world.getSeed() + this.originChunkX * 25117 + this.originChunkZ * 151121;
        this.randPieces = new Random(seed);
        this.generateBuildingList();
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        Iterator<StructurePiece> iterator = this.buildingList.iterator();
        StructureBoundingBox chunkAABB = new StructureBoundingBox(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 127, (chunkZ << 4) + 15);

        while (iterator.hasNext()) {
            StructurePiece building = iterator.next();
            if (chunkAABB.collidesWith(building.aabb)) {
                building.populate(chunkX, chunkZ);
            }
        }
    }

    public Class<?> getRandomBuildingClass() {
        return this.availableBuildingsUnrolled.get(this.randPieces.nextInt(this.availableBuildingsUnrolled.size()));
    }

    public BuildingType[] getAvailableBuildings() {
        return new BuildingType[]{
            new BuildingType(Building1.class, 3),
            new BuildingType(Building2.class, 3),
            new BuildingType(DerelictBuilding1.class, 2),
            new BuildingType(DerelictBuilding2.class, 2),
            new BuildingType(Dome.class, 3),
            new BuildingType(Hut1.class, 5),
            new BuildingType(Hut2.class, 5),
            new BuildingType(Hut3.class, 5),
            new BuildingType(Walkway.class, 3),
            new BuildingType(Buried.class, 1)
        };
    }

    public void generateBuildingList() {
        int x = this.centerX - 2;
        int z = this.centerZ - 2;
        OceanRuinsPiece centerPiece = new Buried(this.world, this.randPieces.nextBoolean());
        int yAtCenter = centerPiece.getLandSurfaceAtCenter(x, z);
        centerPiece.place(x, yAtCenter, z);
        this.generateBuildingListRecursive(centerPiece);
    }

    public void generateBuildingListRecursive(OceanRuinsPiece piece) {
        this.buildingList.add(piece);
        int spawns = 4;

        while (spawns-- > 0) {
            boolean rotated = this.randPieces.nextBoolean();
            OceanRuinsPiece newPiece = null;

            try {
                newPiece = (OceanRuinsPiece)this.getRandomBuildingClass().getConstructor(World.class, boolean.class).newInstance(this.world, rotated);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int x = 0;
            int z = 0;
            switch (this.randPieces.nextInt(4)) {
                case 0: {
                    int offsetX = this.randPieces.nextInt(2) + 2;
                    int offsetZ = this.randPieces.nextInt(8) - 4;
                    x = piece.xAbsolute - offsetX - newPiece.getWidth();
                    z = piece.zAbsolute + offsetZ;
                    break;
                }
                case 1: {
                    int offsetX = this.randPieces.nextInt(2) + 2;
                    int offsetZ = this.randPieces.nextInt(8) - 4;
                    x = piece.xAbsolute + piece.getWidth() + offsetX;
                    z = piece.zAbsolute + offsetZ;
                    break;
                }
                case 2: {
                    int offsetX = this.randPieces.nextInt(8) - 4;
                    int offsetZ = this.randPieces.nextInt(2) + 2;
                    x = piece.xAbsolute + offsetX;
                    z = piece.zAbsolute - newPiece.getLength() - offsetZ;
                    break;
                }
                case 3: {
                    int offsetX = this.randPieces.nextInt(8) - 4;
                    int offsetZ = this.randPieces.nextInt(2) + 2;
                    x = piece.xAbsolute + offsetX;
                    z = piece.zAbsolute + piece.getLength() + offsetZ;
                }
            }

            int yAtCenter = newPiece.getLandSurfaceAtCenter(x, z);
            newPiece.place(x, yAtCenter, z);
            if (!this.featureAABB.containsFully(newPiece.aabb)) {
                break;
            }

            boolean collided = false;
            Iterator<StructurePiece> iterator = this.buildingList.iterator();

            while (iterator.hasNext() && !collided) {
                collided = newPiece.aabb.collidesWith(iterator.next().aabb);
            }

            boolean badChunk = this.badChunksOnCorners(this.world, newPiece.aabb);
            if (!badChunk && !collided && this.randPieces.nextBoolean()) {
                this.generateBuildingListRecursive(newPiece);
            }
        }
    }

    public boolean badChunkInCorner(World world, int x, int z) {
        return !(world.getBiomeAt(x, z) instanceof Ocean);
    }

    public boolean badChunksOnCorners(World world, StructureBoundingBox aabb) {
        return this.badChunkInCorner(world, aabb.x1 >> 4, aabb.z1 >> 4)
            || this.badChunkInCorner(world, aabb.x1 >> 4, aabb.z2 >> 4)
            || this.badChunkInCorner(world, aabb.x2 >> 4, aabb.z1 >> 4)
            || this.badChunkInCorner(world, aabb.x2 >> 4, aabb.z2 >> 4);
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
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof Ocean;
    }

    @Override
    public int minimumSeparation() {
        return 16;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
    }
}
