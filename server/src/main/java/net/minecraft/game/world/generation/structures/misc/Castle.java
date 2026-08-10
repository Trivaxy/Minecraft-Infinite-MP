package net.minecraft.game.world.generation.structures.misc;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.boss.LichKing;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.VectorBasic;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.cold.Muskeg;
import net.minecraft.game.world.biomes.cold.Tundra;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkSection;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Castle extends SchematicStructure {
    public boolean debug = false;
    public boolean generatedTreasure = false;
    private int width;
    private int height;
    private int length;
    private static final int NONE = -1;
    public static final float SLOPEMULTIPLIER = 3.0F;
    private Random castleRand;

    public Castle(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.castleRand = new Random(this.world.getSeed() + originChunkX * 25117 + originChunkZ * 151121);
    }

    @Override
    public int getHeight() {
        return 36;
    }

    @Override
    public int minimumSeparation() {
        return 16;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        this.width = this.schematic.length;
        this.height = this.schematic[0][0].length;
        this.length = this.schematic[0].length;

        for (int cx = this.originChunkX - this.getRadius(); cx <= this.originChunkX + this.getRadius(); cx++) {
            for (int cz = this.originChunkZ - this.getRadius(); cz <= this.originChunkZ + this.getRadius(); cz++) {
                Chunk chunk = world.justGenerateForHeight(cx, cz);

                for (int y = 0; y < this.getHeight(); y++) {
                    int cy = world.getHeightmap(cx >> 4, cz >> 4);
                    ChunkSection chunkSection = chunk.getSections()[cy >> 4];
                    if (chunkSection != null) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                int blockID = chunkSection.getBlockID(x, cy & 15, z);
                                this.schematic[x][z][y] = blockID > 0 ? blockID : -1;
                            }
                        }
                    }
                }
            }
        }

        VectorBasic dimensions = new VectorBasic(this.width - 8, this.height - 4, this.length - 8);
        Castle.DFBlockSolid[][][] dfBlocks = this.setupDFBlocks(dimensions);
        Castle.Fortress fort = new Castle.Fortress(this);
        fort.primeMat = BlockList.cobblestone.id;
        fort.secondaryMat = BlockList.planks.id;
        fort.doorMat = BlockList.wood.id;
        if (this.debug) {
            System.out.println("Placing the fortress");
        }

        int bestx = 0;
        int besty = 0;
        int bestz = 0;

        for (int x = 0; x < this.width; x += 2) {
            for (int z = 0; z < this.length; z += 2) {
                int curHeight = this.searchColumn(x, z, BlockList.stone.id, this.height);
                if (curHeight > besty) {
                    besty = curHeight;
                    bestx = x;
                    bestz = z;
                }
            }
        }

        BlockPosition curCent = new BlockPosition(bestx, besty, bestz);
        if (this.debug) {
            System.out.println("Tentative center @ " + curCent);
        }

        int xPlus = 0;
        int xMinus = 0;
        int zPlus = 0;
        int zMinus = 0;

        for (int i = 0; i < 3; i++) {
            xPlus = this.distToMat(curCent, new VectorBasic(1, 0, 0), 0, false) / 3;
            xMinus = this.distToMat(curCent, new VectorBasic(-1, 0, 0), 0, false) / 3;
            zPlus = this.distToMat(curCent, new VectorBasic(0, 0, 1), 0, false) / 3;
            zMinus = this.distToMat(curCent, new VectorBasic(0, 0, -1), 0, false) / 3;
            curCent.x += (int)(xPlus * 1.618F - xMinus * 1.618F);
            curCent.z += (int)(zPlus * 1.618F - zMinus * 1.618F);
        }

        int distToDirt = this.distToMat(curCent, new VectorBasic(0, -1, 0), BlockList.stone.id, false);
        if (distToDirt <= 4) {
            curCent.move(5, distToDirt);
        } else {
            curCent.move(5, 4);
        }

        fort.center = new BlockPosition((curCent.x + 1) / 3, (curCent.y + 1) / 3, (curCent.z + 1) / 3);
        int xSlope = this.distToMat(curCent, new VectorBasic(3, -1, 0), 0, false);
        xSlope += this.distToMat(curCent, new VectorBasic(-3, -1, 0), 0, false);
        int zSlope = this.distToMat(curCent, new VectorBasic(0, -1, 3), 0, false);
        zSlope += this.distToMat(curCent, new VectorBasic(0, -1, -3), 0, false);
        int xSize = (int)((xPlus + xMinus) * 0.618F);
        int zSize = (int)((zPlus + zMinus) * 0.618F);
        int slopeFactor = (int)(30.0F - (xSlope + zSlope));
        if (slopeFactor < 0) {
            slopeFactor = 0;
        }

        if (slopeFactor > 5) {
            slopeFactor = 5;
        }

        int ySize = (int)((xSize + zSize) / 2.618F + slopeFactor);
        float xRandom = world.rand.nextFloat() + 0.1F;
        float zRandom = world.rand.nextFloat() + 0.1F;
        if (xRandom < 0.5F) {
            xRandom = 0.5F;
        }

        if (zRandom < 0.5F) {
            zRandom = 0.5F;
        }

        fort.size = new VectorBasic(xSize * xRandom, ySize, zSize * zRandom);
        int maxDim = xSize;
        if (ySize > maxDim) {
            maxDim = ySize;
        }

        if (zSize > maxDim) {
            maxDim = zSize;
        }

        int pad = maxDim / 2;
        if (!this.inBounds(curCent, pad)) {
            if (this.debug) {
                System.out.println("Castle too close to the edge, centering");
            }

            int x = this.width / 2;
            int z = this.length / 2;
            int y = this.searchColumn(xSize, zSize, BlockList.stone.id, this.height);
            fort.center = new BlockPosition((x + 1) / 3, (y + 1) / 3, (z + 1) / 3);
        }

        if (this.debug) {
            System.out.println("Fort center " + fort.center + " size " + fort.size);
        }

        fort.create(dfBlocks);
        if (this.debug) {
            System.out.println("Rendering calculated fortress to schematic");
        }

        for (int y = 0; y < this.height; y++) {
            for (int z = 0; z < this.length; z++) {
                for (int x = 0; x < this.length; x++) {
                    if (this.schematic[x][z][y] == BlockList.stone.id) {
                        this.schematic[x][z][y] = -1;
                    }

                    try {
                        Castle.DFBlockSolid thisBlock = dfBlocks[x / 3][y / 3][z / 3];
                        if (thisBlock == null) {
                            this.schematic[x][z][y] = -1;
                        } else {
                            this.schematic[x][z][y] = thisBlock.getMaterial(x % 3, y % 3, z % 3);
                        }
                    } catch (Exception var32) {
                    }
                }
            }
        }

        if (this.debug) {
            System.out.println("Fortress @ " + this.centerX + " " + this.centerZ);
        }
    }

    @Override
    public int getY0() {
        return this.world.getSeaLevel();
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
        return biome instanceof Tundra || biome instanceof Muskeg;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        if (!this.generatedTreasure) {
            world.setBlockAndMetadata(this.centerX, world.getTopTileSafe(this.centerX, this.centerZ), this.centerZ, BlockList.lockedChest.id, 1);
            ChestEntity chest = (ChestEntity)world.getTileEntity(this.centerX, world.getTopTileSafe(this.centerX, this.centerZ) - 1, this.centerZ);
            if (chest != null) {
                int ni = 4 + this.castleRand.nextInt(4);

                for (int k = 0; k < ni; k++) {
                    ItemStack itemStack = this.getSpecialTreasure(this.castleRand);
                    chest.setSlotContents(this.castleRand.nextInt(chest.getSize()), itemStack);
                }
            }

            LichKing slimeKing = new LichKing(world);
            slimeKing.setPosition(this.centerX, world.getTopTileSafe(this.centerX, this.centerZ) - 6, this.centerZ);
            world.spawnEntity(slimeKing);
            this.generatedTreasure = true;
        }

        int x = chunkX << 4;
        int z = chunkZ << 4;

        for (int i = x; i < x + 16; i++) {
            for (int j = z; j < z + 16; j++) {
                for (int y = this.getY0(); y < this.getY0() + this.getHeight(); y++) {
                    if (world.getBlock(i, y + 1, j) == BlockList.doorOldenOak && world.getBlock(i, y, j) == BlockList.doorOldenOak) {
                        world.setBlockMetadata(i, y + 1, j, 8);
                    }

                    TileEntity chest = world.getTileEntity(i, y, j);
                    if (chest instanceof ChestEntity) {
                        if (chest != null && ((ChestEntity)chest).getSize() > 0 && world.getBlock(i, y, j) != BlockList.lockedChest) {
                            int ni = 4 + this.castleRand.nextInt(4);
                            int level = this.castleRand.nextInt(10);

                            for (int k = 0; k < ni; k++) {
                                ItemStack itemStack = this.getTreasure(level, this.castleRand);
                                ((ChestEntity)chest).setSlotContents(this.castleRand.nextInt(((ChestEntity)chest).getSize()), itemStack);
                            }
                        }
                    } else if (chest instanceof SpawnerEntity) {
                        if (rand.nextBoolean()) {
                            ((SpawnerEntity)chest).setMob("Stray");
                        } else {
                            ((SpawnerEntity)chest).setMob("Lich");
                        }
                    }
                }
            }
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        if (level <= 4) {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.ingotIron, rand.nextInt(3) + 1);
                case 1:
                    return new ItemStack(ItemList.nameTag);
                case 2:
                    return new ItemStack(ItemList.bread);
                case 3:
                    return new ItemStack(ItemList.bowlSussy);
                case 4:
                    return new ItemStack(ItemList.wheat, rand.nextInt(3) + 1);
                case 5:
                    return new ItemStack(ItemList.coal, rand.nextInt(5) + 1);
                case 6:
                    return new ItemStack(ItemList.stick, rand.nextInt(16) + 1);
                case 7:
                    return new ItemStack(BlockList.wood, rand.nextInt(4) + 1);
                case 8:
                    return new ItemStack(BlockList.planks, rand.nextInt(8) + 1);
                default:
                    return new ItemStack(BlockList.torch, rand.nextInt(16) + 1);
            }
        } else if (level <= 7) {
            switch (rand.nextInt(11)) {
                case 0:
                    return new ItemStack(ItemList.backpack);
                case 1:
                    return new ItemStack(ItemList.saddle);
                case 2:
                    return new ItemStack(ItemList.arrowExplosive, rand.nextInt(8) + 1);
                case 3:
                default:
                    return new ItemStack(ItemList.crossbow);
                case 4:
                    return new ItemStack(ItemList.helmetChain);
                case 5:
                    return new ItemStack(ItemList.plateChain);
                case 6:
                    return new ItemStack(ItemList.swordSteel);
                case 7:
                    return new ItemStack(ItemList.legsChain);
                case 8:
                    return new ItemStack(ItemList.bootsChain);
                case 9:
                    return new ItemStack(ItemList.pouch);
            }
        } else {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.axeSteel);
                case 1:
                    return new ItemStack(ItemList.pickaxeSteel);
                case 2:
                    return new ItemStack(ItemList.ingotGold, rand.nextInt(3) + 1);
                case 3:
                    return new ItemStack(ItemList.shovelSteel);
                case 4:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 5:
                    return new ItemStack(ItemList.sapphire, rand.nextInt(3) + 1);
                case 6:
                    return new ItemStack(ItemList.ruby, rand.nextInt(3) + 1);
                case 7:
                    return new ItemStack(ItemList.emerald, rand.nextInt(3) + 1);
                case 8:
                    return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
                default:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
            }
        }
    }

    protected ItemStack getSpecialTreasure(Random rand) {
        switch (rand.nextInt(10)) {
            case 0:
                return new ItemStack(ItemList.swordFrost);
            case 1:
                return new ItemStack(ItemList.lichStaff);
            case 2:
                return new ItemStack(ItemList.bootsLich);
            case 3:
            default:
                return new ItemStack(ItemList.lifeShard);
            case 4:
                return new ItemStack(ItemList.helmetLich);
            case 5:
                return new ItemStack(ItemList.legsLich);
            case 6:
                return new ItemStack(ItemList.plateLich);
            case 7:
                return new ItemStack(ItemList.snowmanStaff);
            case 8:
                return new ItemStack(ItemList.ring);
        }
    }

    private boolean inBounds(int x, int y, int z, int border) {
        return x >= border && x < this.width - border && y >= border && y < this.height - border && z >= border && z < this.length - border;
    }

    private boolean inBounds(BlockPosition blockPosition, int border) {
        return this.inBounds(blockPosition.x, blockPosition.y, blockPosition.z, border);
    }

    private int searchColumn(int x, int z, int blockID, int yStart) {
        int y = yStart;
        if (y > this.height - 1 || y < 0) {
            y = this.height - 1;
        }

        while (y > 0) {
            if (this.schematic[x][z][y] == blockID) {
                return y;
            }

            y--;
        }

        return -1;
    }

    private int distToMat(BlockPosition coord, VectorBasic vec, int blockID, boolean invert) {
        BlockPosition curCoord = coord.copy();

        int iterations;
        for (iterations = 0; this.inBounds(curCoord, 0); iterations++) {
            int mapBlock = this.schematic[curCoord.x][curCoord.z][curCoord.y];
            if (mapBlock == blockID && !invert || mapBlock != blockID && invert) {
                break;
            }

            curCoord.move(vec);
        }

        return iterations;
    }

    public Castle.DFBlockSolid[][][] setupDFBlocks(VectorBasic dimensions) {
        return new Castle.DFBlockSolid[dimensions.x / 3 + 1][dimensions.y / 3 + 1][dimensions.z / 3 + 1];
    }

    public boolean assignDFBlocks(int x, int y, int z, Castle.DFBlockSolid dfBlockClass, Castle.DFBlockSolid[][][] dfBlocks) {
        try {
            dfBlocks[x][y][z] = dfBlockClass;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void exteriorWalls(int x, int y, int z, Castle.DFBlockSolid[][][] dfBlocks) {
        try {
            Castle.DFBlockSolid existingBlock = dfBlocks[x][y][z];
            if (existingBlock != null) {
                int wallsX = existingBlock.wallsX;
                int wallsZ = existingBlock.wallsZ;
                if (wallsX != 0 || wallsZ != 0) {
                    IntStream.range(-1, 2).forEachOrdered(xOff -> IntStream.range(-1, 2).forEachOrdered(zOff -> {
                        if (xOff != 0 && zOff == 0 || xOff == 0 && zOff != 0) {
                            int xLoc = xOff + 1;
                            int yLoc = 1;
                            int zLoc = zOff + 1;
                            if (existingBlock.checkWalls(xLoc, yLoc, zLoc) != -1) {
                                try {
                                    Castle.DFBlockSolid exteriorBlock = dfBlocks[x + xOff][y][z + zOff];
                                    if (exteriorBlock == null) {
                                        Castle.DFBlockSolid noneBlock = new Castle.DFBlockSolid(this);
                                        noneBlock.material = -1;
                                        exteriorBlock = noneBlock;
                                        this.assignDFBlocks(x + xOff, y, z + zOff, exteriorBlock, dfBlocks);
                                    }

                                    exteriorBlock.wallMaterial = existingBlock.wallMaterial;
                                    exteriorBlock.windowMaterial = existingBlock.windowMaterial;
                                    if (xOff != 0) {
                                        int wallSide = xOff > 0 ? 1 : 2;
                                        if ((exteriorBlock.wallsX / wallSide & 1) != 1) {
                                            exteriorBlock.wallsX |= wallSide;
                                        }
                                    } else {
                                        int wallSide = zOff > 0 ? 1 : 2;
                                        if ((exteriorBlock.wallsZ / wallSide & 1) != 1) {
                                            exteriorBlock.wallsZ |= wallSide;
                                        }
                                    }
                                } catch (Exception var13) {
                                }
                            }
                        }
                    }));
                    existingBlock.wallsX = 0;
                    existingBlock.wallsZ = 0;
                }
            }
        } catch (Exception var8) {
        }
    }

    public class DFBlockFloor extends Castle.DFBlockSolid {
        public DFBlockFloor(Castle castle) {
            super(castle);
        }

        @Override
        public int getMaterial(int x, int y, int z) {
            int wallNum = this.checkWallsAndPlaceSpawners(x, y, z);
            if (wallNum != -1) {
                return wallNum;
            } else {
                return y == 0 ? this.material : 0;
            }
        }
    }

    public class DFBlockRamp extends Castle.DFBlockSolid {
        public DFBlockRamp(Castle castle) {
            super(castle);
        }

        @Override
        public int getMaterial(int x, int y, int z) {
            return x == y ? this.material : 0;
        }
    }

    private class DFBlockSolid {
        Castle castle;
        public int material;
        public int wallMaterial;
        public int windowMaterial;
        public int wallsX;
        public int wallsZ;
        public int width = 3;
        public int height = 3;
        public int length = 3;

        public DFBlockSolid(Castle castle) {
            this.castle = castle;
            this.material = 0;
            this.wallsX = 0;
            this.wallsZ = 0;
            this.wallMaterial = 0;
            this.windowMaterial = BlockList.ironBars.id;
        }

        public int getWallMat(int xOff, int y, int zOff) {
            return y != 0 && Math.abs(xOff) + Math.abs(zOff) != 2 ? this.windowMaterial : this.wallMaterial;
        }

        public int checkWallsAndPlaceSpawners(int x, int y, int z) {
            if (this.wallsX == 0 && this.wallsZ == 0) {
                return -1;
            }

            int offX = x - 1;
            int offZ = z - 1;
            if ((offX != -(this.wallsX & 1) || offX == 0)
                && (offX != (this.wallsX >> 1 & 1) || offX == 0)
                && (offZ != -(this.wallsZ & 1) || offZ == 0)
                && (offZ != (this.wallsZ >> 1 & 1) || offZ == 0)) {
                int torchy = this.material == 0 ? 0 : 1;
                if (this.wallsX <= 0 || this.wallsZ <= 0 || offX != 0 || offZ != 0 || y != 1 || torchy != 1 || Castle.this.world.rand.nextInt(3) != 0) {
                    return -1;
                } else {
                    return Castle.this.world.rand.nextBoolean() ? BlockList.chest.id : BlockList.mobSpawner.id;
                }
            } else {
                return this.getWallMat(offX, y, offZ);
            }
        }

        public int checkWalls(int x, int y, int z) {
            if (this.wallsX == 0 && this.wallsZ == 0) {
                return -1;
            }

            int offX = x - 1;
            int offZ = z - 1;
            return (offX != -(this.wallsX & 1) || offX == 0)
                    && (offX != (this.wallsX >> 1 & 1) || offX == 0)
                    && (offZ != -(this.wallsZ & 1) || offZ == 0)
                    && (offZ != (this.wallsZ >> 1 & 1) || offZ == 0)
                ? -1
                : this.getWallMat(offX, y, offZ);
        }

        public int getMaterial(int x, int y, int z) {
            int wallNum = this.checkWallsAndPlaceSpawners(x, y, z);
            return wallNum != -1 ? wallNum : this.material;
        }
    }

    public class DFBlockStairDown extends Castle.DFBlockStairUpDown {
        public DFBlockStairDown(Castle castle) {
            super(castle);
        }

        @Override
        public int getMaterial(int x, int y, int z) {
            return y == 0 ? super.getMaterial(x, y, z) : 0;
        }
    }

    public class DFBlockStairUp extends Castle.DFBlockStairUpDown {
        public DFBlockStairUp(Castle castle) {
            super(castle);
        }

        @Override
        public int getMaterial(int x, int y, int z) {
            return y > 0 ? super.getMaterial(x, y, z) : 0;
        }
    }

    public class DFBlockStairUpDown extends Castle.DFBlockSolid {
        public boolean flip = false;
        int[][][] matMap = new int[][][]{{{-1, -1, -1}, {-1, -1, 0}, {-1, 0, 0}}, {{-1, -1, -1}, {0, 0, 0}, {0, 0, 0}}, {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}};

        public DFBlockStairUpDown(Castle castle) {
            super(castle);
        }

        @Override
        public int getMaterial(int x, int y, int z) {
            int newX = x;
            int newY = y;
            int newZ = z;
            if (this.flip) {
                newX = this.width - x - 1;
                newZ = this.length - z - 1;
            }

            int pseudoBlockID = this.matMap[newX][newY][newZ];
            return pseudoBlockID == -1 ? this.material : pseudoBlockID;
        }
    }

    public class Fortress extends Castle.Tower {
        public Fortress(Castle castle) {
            super(castle);
            this.hollow = true;
        }

        @Override
        public void entrance(BlockPosition pos, int side, Castle.DFBlockSolid[][][] dfBlocks) {
            int x = pos.x;
            int y = pos.y - 2;
            int z = pos.z;
            if (this.size.x > 2 && this.size.z > 2) {
                Castle.Tower gateHouse = Castle.this.new Tower(this.castle);
                Castle.this.height = 4;
                if (this.size.y > 2) {
                    Castle.this.height = Castle.this.height + (this.size.y - 2);
                }

                gateHouse.size = new VectorBasic(1, Castle.this.height, 1);
                gateHouse.interior = true;
                gateHouse.primeMat = this.primeMat;
                gateHouse.secondaryMat = this.secondaryMat;
                gateHouse.doorMat = this.doorMat;
                if (side == 0) {
                    gateHouse.size.x = 2;
                    gateHouse.center = new BlockPosition(x, y, z + 1);
                    gateHouse.create(dfBlocks);
                    gateHouse.center = new BlockPosition(x, y, z - 1);
                    gateHouse.create(dfBlocks);
                } else {
                    gateHouse.size.z = 2;
                    gateHouse.center = new BlockPosition(x + 1, y, z);
                    gateHouse.create(dfBlocks);
                    gateHouse.center = new BlockPosition(x - 1, y, z);
                    gateHouse.create(dfBlocks);
                }
            }

            super.entrance(pos, side, dfBlocks);
        }

        @Override
        public void create(Castle.DFBlockSolid[][][] dfBlocks) {
            this.size.x = Math.max(this.size.x, 2);
            this.size.y = Math.max(this.size.y, 2);
            this.size.z = Math.max(this.size.z, 2);
            int centerHeight = this.size.y + 2;
            int outerHeight = (int)(0.382F * this.size.y);
            if (outerHeight > 3) {
                outerHeight = (int)(3.0 + Math.pow(this.size.y - 4.854, 0.383));
            }

            this.size.y = outerHeight;
            super.create(dfBlocks);
            Castle.Tower centerTower = Castle.this.new Tower(this.castle);
            centerTower.interior = true;
            centerTower.center = new BlockPosition(this.center.x, this.center.y - 2, this.center.z);
            int avesize = (int)((this.size.x + this.size.z) * 0.382F);
            if (avesize < 2) {
                if (this.size.x >= 3 && this.size.z >= 3) {
                    avesize = 2;
                } else {
                    avesize = 1;
                }
            } else if (avesize > 5) {
                avesize = 5;
            }

            centerTower.size = new VectorBasic(avesize, centerHeight, avesize);
            centerTower.primeMat = this.primeMat;
            centerTower.secondaryMat = this.secondaryMat;
            centerTower.doorMat = this.doorMat;
            centerTower.mainTower = true;
            centerTower.create(dfBlocks);
            if (this.size.x > 6 && this.size.z > 6) {
                BlockPosition corner = new BlockPosition(
                    this.center.x + this.size.x / 2 - 1 + this.size.x % 2, this.center.y - 2, this.center.z + this.size.z / 2 - 1 + this.size.z % 2
                );
                Castle.Tower cornerTower = Castle.this.new Tower(this.castle);
                int randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.interior = true;
                cornerTower.center = corner;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                cornerTower.primeMat = this.primeMat;
                cornerTower.secondaryMat = this.secondaryMat;
                cornerTower.doorMat = this.doorMat;
                cornerTower.mainTower = true;
                cornerTower.create(dfBlocks);
                BlockPosition otherCenter = corner.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.x = this.center.x - this.size.x / 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                otherCenter = corner.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.z = this.center.z - this.size.z / 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                if (this.size.x < 11 || this.size.z < 11) {
                    return;
                }

                if (this.size.x <= 14 && this.size.z <= 14) {
                    cornerTower.size = new VectorBasic(1, centerHeight - 2, 1);
                } else {
                    cornerTower.size = new VectorBasic(3, centerHeight - 2, 3);
                }

                otherCenter = this.center.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.y = this.center.y - 2;
                otherCenter.x = this.center.x - this.size.x / 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                otherCenter = this.center.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.y = this.center.y - 2;
                otherCenter.z = this.center.z - this.size.z / 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                otherCenter = this.center.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.y = this.center.y - 2;
                if (this.size.x >= this.size.z) {
                    otherCenter.x = this.center.x + this.size.x / 2 - 1 + this.size.x % 2;
                } else {
                    otherCenter.z = this.center.z + this.size.z / 2 - 1 + this.size.z % 2;
                }

                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                otherCenter = this.center.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.y = this.center.y - 2;
                otherCenter.x = this.center.x + this.size.x / 2 - 1 + this.size.x % 2;
                otherCenter.z = this.center.z + this.size.z / 2 - 1 + this.size.z % 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
                otherCenter = this.center.copy();
                randomizer = Castle.this.world.rand.nextInt(3) + 1;
                cornerTower.size = new VectorBasic(randomizer, centerHeight - 2, randomizer);
                otherCenter.y = this.center.y - 2;
                otherCenter.x = this.center.x - this.size.x / 2;
                otherCenter.z = this.center.z - this.size.z / 2;
                cornerTower.center = otherCenter;
                cornerTower.create(dfBlocks);
            }
        }
    }

    public class Tower {
        Castle castle;
        public int primeMat;
        public int secondaryMat;
        public int doorMat;
        public VectorBasic size;
        public BlockPosition center;
        public boolean makeStairs;
        public boolean hollow;
        public boolean interior;
        public boolean mainTower;

        public Tower(Castle castle) {
            this.castle = castle;
            this.primeMat = 0;
            this.secondaryMat = 0;
            this.doorMat = 0;
            this.center = new BlockPosition(0, 0, 0);
            this.size = new VectorBasic(0, 0, 0);
            this.makeStairs = true;
            this.hollow = false;
            this.interior = false;
            this.mainTower = false;
        }

        public void entrance(BlockPosition pos, int side, Castle.DFBlockSolid[][][] dfBlocks) {
            int x = pos.x;
            int y = pos.y;
            int z = pos.z;
            Castle.DFBlockSolid step = Castle.this.new DFBlockFloor(this.castle);
            this.castle.exteriorWalls(x, y, z, dfBlocks);
            this.castle.assignDFBlocks(x, y, z, step, dfBlocks);
            if (side == 0) {
                x--;
            } else {
                z--;
            }

            this.castle.exteriorWalls(x, y, z, dfBlocks);
            step.material = this.primeMat;
            step.wallMaterial = this.primeMat;
            step.windowMaterial = this.doorMat;
        }

        public void placeEntrance(Castle.DFBlockSolid[][][] dfBlocks) {
            Castle.placer placeOnX = y -> {
                int offset = this.size.z > 2 ? this.size.z / 2 : 1;
                int x = this.center.x + (this.size.x + 1) / 2;
                int z = this.center.z - this.size.z / 2 + offset;
                BlockPosition curCoords = new BlockPosition(x * 3 + 1, y * 3 + 1, z * 3 + 1);
                int offsetThruAir = this.castle.distToMat(curCoords, new VectorBasic(0, -1, 0), 0, true);
                if (offsetThruAir > 0) {
                    this.entrance(new BlockPosition(x, y, z), 0, dfBlocks);
                    return true;
                } else {
                    return false;
                }
            };
            Castle.placer placeOnZ = y -> {
                int offset = this.size.x > 2 ? this.size.x / 2 : 0;
                int z = this.center.z + (this.size.z + 1) / 2;
                int x = this.center.x + this.size.x / 2 + offset;
                BlockPosition curCoords = new BlockPosition(x * 3 + 1, y * 3 + 1, z * 3 + 1);
                int offsetThruAir = this.castle.distToMat(curCoords, new VectorBasic(0, -1, 0), 0, true);
                if (offsetThruAir > 0) {
                    this.entrance(new BlockPosition(x, y, z), 1, dfBlocks);
                    return true;
                } else {
                    return false;
                }
            };
            IntStream.range(0, this.size.y + 1).forEachOrdered(yOff -> {
                int y = this.center.y + yOff;
                if (this.size.z <= this.size.x) {
                    if (placeOnZ.place(y)) {
                        return;
                    }
                } else if (this.size.z > 1 && placeOnX.place(y)) {
                    return;
                }
            });
        }

        public void clearSpace(Castle.DFBlockSolid[][][] dfBlocks) {
            IntStream.range(0, this.size.x).forEachOrdered(xOff -> {
                int x = this.center.x + xOff - this.size.x / 2;
                IntStream.range(0, this.size.y * 2).forEachOrdered(yOff -> {
                    int y = this.center.y + yOff;
                    IntStream.range(0, this.size.z).forEachOrdered(zOff -> {
                        int z = this.center.z + zOff - this.size.z / 2;
                        this.castle.assignDFBlocks(x, y, z, Castle.this.new DFBlockSolid(this.castle), dfBlocks);
                    });
                });
            });
        }

        public void stairWell(int yBot, int yTop, int x, int z, Castle.DFBlockSolid[][][] dfBlocks) {
            if (yTop - yBot >= 1) {
                for (int i = yBot; i <= yTop; i++) {
                    Castle.DFBlockStairUpDown stair;
                    if (i == yBot) {
                        stair = Castle.this.new DFBlockStairUp(this.castle);
                    } else if (i == yTop) {
                        stair = Castle.this.new DFBlockStairDown(this.castle);
                    } else {
                        stair = Castle.this.new DFBlockStairUpDown(this.castle);
                    }

                    if ((i & 1) == 1) {
                        stair.flip = true;
                    }

                    stair.material = this.secondaryMat;
                    this.castle.assignDFBlocks(x, i, z, stair, dfBlocks);
                }
            }
        }

        public void foundation(Castle.DFBlockSolid[][][] dfBlocks) {
            IntStream.range(0, this.size.x)
                .forEachOrdered(
                    xOff -> IntStream.range(0, this.size.z)
                        .forEachOrdered(
                            zOff -> {
                                Castle.DFBlockSolid foundationBlock = Castle.this.new DFBlockSolid(this.castle);
                                foundationBlock.material = this.primeMat;
                                this.castle
                                    .assignDFBlocks(
                                        this.center.x + xOff - this.size.x / 2,
                                        this.center.y - 1,
                                        this.center.z + zOff - this.size.z / 2,
                                        foundationBlock,
                                        dfBlocks
                                    );
                            }
                        )
                );
        }

        public void floors(Castle.DFBlockSolid[][][] dfBlocks) {
            IntStream.range(0, this.size.x).forEachOrdered(xOff -> {
                int x = this.center.x + xOff - this.size.x / 2;
                IntStream.range(0, this.size.z).forEachOrdered(zOff -> {
                    int z = this.center.z + zOff - this.size.z / 2;
                    IntStream.range(0, this.size.y + 1).forEachOrdered(yOff -> {
                        int y = this.center.y + yOff;
                        Castle.DFBlockFloor floorBlock = Castle.this.new DFBlockFloor(this.castle);
                        floorBlock.material = (yOff & 1) == 1 ? 0 : this.secondaryMat;
                        if (Castle.this.world.rand.nextBoolean()) {
                            floorBlock.wallMaterial = BlockList.cobblestoneMossy.id;
                        } else {
                            floorBlock.wallMaterial = this.primeMat;
                        }

                        int extFlag = 0;
                        boolean makeIt = !this.hollow;
                        if (yOff == this.size.y) {
                            floorBlock.material = this.secondaryMat;
                            floorBlock.windowMaterial = 0;
                            extFlag = 1;
                        }

                        if (yOff == 0) {
                            makeIt = true;
                        }

                        if (xOff == 0) {
                            floorBlock.wallsX |= 1;
                            makeIt = true;
                        }

                        if (zOff == 0) {
                            floorBlock.wallsZ |= 1;
                            makeIt = true;
                        }

                        if (xOff == this.size.x - 1) {
                            floorBlock.wallsX |= 2;

                            try {
                                Castle.DFBlockSolid otherBlock = dfBlocks[x + 1][y][z];
                                if (this.interior && floorBlock.getClass().equals(otherBlock.getClass()) && floorBlock.material != 0) {
                                    floorBlock.windowMaterial = BlockList.doorOldenOak.id;
                                }
                            } catch (Exception var16) {
                            }

                            makeIt = true;
                        }

                        if (zOff == this.size.z - 1) {
                            floorBlock.wallsZ |= 2;

                            try {
                                Castle.DFBlockSolid otherBlock = dfBlocks[x][y][z + 1];
                                if (this.interior && floorBlock.getClass().equals(otherBlock.getClass()) && floorBlock.material != 0) {
                                    floorBlock.windowMaterial = BlockList.doorOldenOak.id;
                                }
                            } catch (Exception var15) {
                            }

                            makeIt = true;
                        }

                        if (makeIt) {
                            BlockPosition curCoords = new BlockPosition(x * 3 + 1, y * 3 + 1, z * 3 + 1);
                            int offsetThruAir = this.castle.distToMat(curCoords, new VectorBasic(0, -1, 0), 0, true);
                            curCoords.move(5, offsetThruAir);
                            int offsetThruWater = this.castle.distToMat(curCoords, new VectorBasic(0, -1, 0), BlockList.waterStill.id, true);
                            int offsetToGround = offsetThruAir + offsetThruWater;
                            if (!this.mainTower && offsetToGround < 1 && floorBlock.windowMaterial == BlockList.ironBars.id) {
                                floorBlock.windowMaterial = this.primeMat;
                            }

                            if (this.castle.assignDFBlocks(x, y, z, floorBlock, dfBlocks)) {
                                if (this.size.x < 2 || this.size.z < 2) {
                                    extFlag++;
                                }

                                if (extFlag > 0) {
                                    this.castle.exteriorWalls(x, y, z, dfBlocks);
                                    dfBlocks[x][y][z].wallsX = 0;
                                    dfBlocks[x][y][z].wallsZ = 0;
                                }
                            }
                        }
                    });
                });
            });
        }

        public void create(Castle.DFBlockSolid[][][] dfBlocks) {
            int width = Math.max(this.size.x, 1);
            int height = Math.max(this.size.y, 1);
            int length = Math.max(this.size.z, 1);
            int topY = this.size.y + this.center.y;
            int mapTop = dfBlocks[0].length;
            if (topY >= mapTop - 2) {
                height = mapTop - 2 - this.center.y;
            }

            this.size = new VectorBasic(width, height, length);
            this.clearSpace(dfBlocks);
            this.foundation(dfBlocks);
            this.floors(dfBlocks);
            if (this.makeStairs && this.size.x > 2 && this.size.z > 2) {
                int x = this.center.x;
                int yBot = this.center.y;
                int yTop = this.center.y + this.size.y;
                int z = this.center.z;
                this.stairWell(yBot, yTop, x, z, dfBlocks);
            }

            if (this.interior) {
                this.placeEntrance(dfBlocks);
            }
        }

        public Castle.Tower copy() {
            Castle.Tower newTower = Castle.this.new Tower(this.castle);
            newTower.primeMat = this.primeMat;
            newTower.secondaryMat = this.secondaryMat;
            newTower.doorMat = this.doorMat;
            newTower.center = this.center.copy();
            newTower.size = this.size.copy();
            newTower.makeStairs = this.makeStairs;
            newTower.hollow = this.hollow;
            newTower.interior = this.interior;
            return newTower;
        }
    }

    @FunctionalInterface
    interface placer {
        boolean place(int var1);
    }
}
