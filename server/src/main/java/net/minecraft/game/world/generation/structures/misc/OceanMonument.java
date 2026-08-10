package net.minecraft.game.world.generation.structures.misc;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.elemental.Flood;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.ocean.FloatingIsland;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class OceanMonument extends SchematicStructure {
    private int y0;
    private boolean spawnedBoss = false;
    private Random rand = new Random();
    public static final int[][][] array = readData();

    public OceanMonument(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.y0 = this.world.getSurfaceHeightmap((originChunkX << 4) + 8, (originChunkZ << 4) + 8);
    }

    @Override
    public int getHeight() {
        return 17;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        int[][][] piece = array;
        boolean rotated = rand.nextBoolean();
        int pieceWidth = piece.length;
        int pieceLength = piece[2].length;
        int xO = rand.nextInt(pieceWidth);
        int zO = rand.nextInt(pieceLength);

        for (int x = 0; x < piece.length; x++) {
            for (int z = 0; z < piece[x].length; z++) {
                for (int y = 0; y < piece[x][z].length; y++) {
                    int s = piece[x][z][y];
                    if (s <= 0) {
                        s = 9;
                    }

                    int blockID = s & 0xFF;
                    int meta = s >> 12;
                    this.schematic[xO + this.rotateX(x, z, rotated)][zO + this.rotateZ(x, z, rotated)][y] = s;
                    if (blockID == 54 || blockID == 179 || blockID == 52) {
                        this.addSpecialBlock(xO + this.rotateX(x, z, rotated), y, zO + this.rotateZ(x, z, rotated), blockID, meta);
                    }
                }
            }
        }
    }

    @Override
    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
        switch (id) {
            case 52:
                if (this.rand.nextBoolean()) {
                    this.world.setBlock(x, y, z, BlockList.waterStill.id);
                } else {
                    SpawnerEntity spawner = (SpawnerEntity)this.world.getTileEntity(x, y, z);
                    if (spawner != null) {
                        spawner.setMob("Flood");
                    }
                }
                break;
            case 54: {
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);
                    int level = this.rand.nextInt(10);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = this.getTreasure(level, this.rand);
                        chest.setSlotContents(this.rand.nextInt(chest.getSize()), itemStack);
                    }
                }
                break;
            }
            case 179: {
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = this.getSpecialTreasure(this.rand);
                        chest.setSlotContents(this.rand.nextInt(chest.getSize()), itemStack);
                    }
                }

                this.world.setBlockMetadata(x, y, z, 1);
                if (!this.spawnedBoss) {
                    Flood dragon = new Flood(this.world);
                    dragon.boss = true;
                    dragon.tier = 2;
                    dragon.health = dragon.maxHealth = 250;
                    dragon.setPosition(x + 0.5, y + 1, z + 0.5);
                    this.world.spawnEntity(dragon);
                    this.spawnedBoss = true;
                }
            }
        }
    }

    protected int rotateX(int x, int z, boolean rotated) {
        return x;
    }

    protected int rotateZ(int x, int z, boolean rotated) {
        return z;
    }

    protected ItemStack getTreasure(int level, Random rand) {
        switch (rand.nextInt(10)) {
            case 0:
                int type = rand.nextInt(11);
                switch (type) {
                    case 0:
                        return new ItemStack(ItemList.recordGold);
                    case 1:
                        return new ItemStack(ItemList.recordGreen);
                    case 2:
                        return new ItemStack(ItemList.recordRed);
                    case 3:
                        return new ItemStack(ItemList.recordPurple);
                    case 4:
                        return new ItemStack(ItemList.recordTeal);
                    case 5:
                        return new ItemStack(ItemList.recordOrange);
                    case 6:
                        return new ItemStack(ItemList.recordBlue);
                    case 7:
                        return new ItemStack(ItemList.recordWhite);
                    case 8:
                        return new ItemStack(ItemList.recordBlack);
                    case 9:
                        return new ItemStack(ItemList.recordBroken);
                    case 10:
                        return new ItemStack(ItemList.recordCactus);
                }
            case 1:
                return new ItemStack(ItemList.ingotGold, rand.nextInt(4) + 1);
            case 2:
                return new ItemStack(ItemList.sapphire, rand.nextInt(3) + 1);
            case 3:
                if (rand.nextInt(10) == 0) {
                    return new ItemStack(ItemList.appleGold);
                }
            case 4:
                return new ItemStack(ItemList.ruby, rand.nextInt(3) + 1);
            case 5:
                return new ItemStack(ItemList.emerald, rand.nextInt(3) + 1);
            case 6:
                return new ItemStack(ItemList.pouch, rand.nextInt(3) + 1);
            case 7:
                return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
            case 8:
                return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
            default:
                return new ItemStack(BlockList.megaSponge, rand.nextInt(4) + 2);
        }
    }

    protected ItemStack getSpecialTreasure(Random rand) {
        switch (rand.nextInt(9)) {
            case 0:
                return new ItemStack(ItemList.trident);
            case 1:
                return new ItemStack(ItemList.bootsPoseidon);
            case 2:
                return new ItemStack(ItemList.helmetPoseidon);
            case 3:
                return new ItemStack(ItemList.platePoseidon);
            case 4:
                return new ItemStack(ItemList.legsPoseidon);
            case 5:
                return new ItemStack(ItemList.shieldRepulsion);
            case 6:
                return new ItemStack(ItemList.necklace);
            case 7:
                return new ItemStack(ItemList.capeInvisibility);
            default:
                return new ItemStack(ItemList.lifeShard);
        }
    }

    @Override
    public void drawPieceOnGeneration(int chunkX, int chunkZ, short[] blockArray, byte[] metadata) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = this.y0;
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

    @Override
    public void drawPieceOnPopulation(World world, Random rand, int chunkX, int chunkZ) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        Chunk chunk = world.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = this.y0;

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

    @Override
    public void drawSpecialBlocksForChunk(World world, Random rand, int chunkX, int chunkZ) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        int y0 = this.y0;

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

    @Override
    public int getY0() {
        return this.world.getSeaLevel() + 1;
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
        return biome == BiomeList.ocean || biome == BiomeList.frozenOcean || biome == BiomeList.warmOcean || biome instanceof FloatingIsland;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        this.drawSpecialBlocksForChunk(world, rand, chunkX, chunkZ);
    }

    public static int[][][] readData() {
        try {
            DataInputStream dis = new DataInputStream(OceanMonument.class.getResourceAsStream("/struct/monument.bin"));
            int x = dis.readInt();
            int y = dis.readInt();
            int z = dis.readInt();
            int[][][] shit = new int[x][z][y];

            for (int xx = 0; xx < x; xx++) {
                for (int zz = 0; zz < z; zz++) {
                    for (int yy = 0; yy < y; yy++) {
                        shit[xx][zz][yy] = dis.readInt();
                    }
                }
            }

            dis.close();
            return shit;
        } catch (Exception e) {
            return (int[][][])null;
        }
    }
}
