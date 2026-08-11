package net.minecraft.game.world.generation.structures.palace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.boss.Witch;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.HellGenerator;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureBoundingBox;
import net.minecraft.game.world.generation.structures.StructureList;

public class Palace extends SchematicStructure {
    public static final int palaceHeight = 64;
    public static final boolean carveSpheres = true;
    public static final int towerSeparation = 6;
    private List<StructureBoundingBox> pieceAABBs = new ArrayList<>();
    private List<Palace.PalacePiecePosition> corridorPositions = new ArrayList<>();
    private List<Palace.PalacePiecePosition> topPositions = new ArrayList<>();
    private Random pieceRand;
    Random rand = new Random();
    private int y0 = 128;
    private int highestRoomY = 0;
    private static final Class<?>[] selectablePieces = new Class[]{
        PalaceRoom1.class,
        PalaceRoom2.class,
        PalaceRoom3.class,
        PalaceRoom4.class,
        PalaceRoom5.class,
        PalaceRoom6.class,
        PalaceGarden.class,
        PalaceLibrary.class,
        PalaceJail.class
    };

    public Palace(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
    }

    @Override
    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        if (this.featureProvider != null && this.featureProvider.chunkProvider instanceof HellGenerator) {
            int height = rand.nextInt(world.getWorldHeight());
            if (height < 1 || height > world.getSeaLevel() - 16) {
                return;
            }

            this.y0 = height;
        }

        super.setup(world, rand, biome, chunkX, chunkZ);
    }

    @Override
    public int getHeight() {
        return 64;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        this.pieceRand = new Random(world.getSeed() + chunkX * 25117 + chunkZ * 151121);
        this.fillSchematic(-1);
        int cx = this.schematic.length >> 1;
        int cz = this.schematic[0].length >> 1;
        int cy = 0;
        PalacePiece base = new PalacePieceBase(false, this);
        base.place(cx, cy + 7, cz);
        this.pieceAABBs.add(base.aabb);
        base.drawPiece(this.schematic);
        PalacePiece underPiece = new PalaceTowerBottom(true, this);
        underPiece.place(cx, cy, cz);
        this.pieceAABBs.add(underPiece.aabb);
        underPiece.drawPiece(this.schematic);
        cy += base.height + 7;
        this.generateSchematicRecursive(cx, cy, cz, this.getNewPiece(this.pieceRand));
        int toppestIndex = 0;
        int toppestY = 0;

        for (int i = 0; i < this.topPositions.size(); i++) {
            if (this.topPositions.get(i).y > toppestY) {
                toppestY = this.topPositions.get(i).y;
                toppestIndex = i;
            }
        }

        for (int i = 0; i < this.topPositions.size(); i++) {
            Palace.PalacePiecePosition topPosition = this.topPositions.get(i);
            PalacePiece top = i == toppestIndex ? new PalaceBossChamber(topPosition.rotated, this) : new PalaceTowerTop(topPosition.rotated, this);
            top.place(topPosition.x, topPosition.y, topPosition.z);
            top.drawPiece(this.schematic);
        }

        for (Palace.PalacePiecePosition corridorPosition : this.corridorPositions) {
            PalacePiece corridor = new PalaceCorridor(corridorPosition.rotated, this);
            corridor.place(corridorPosition.x, corridorPosition.y, corridorPosition.z);
            corridor.drawPiece(this.schematic);
        }
    }

    private void generateSchematicRecursive(int x, int y, int z, PalacePiece newPiece) {
        if (y > this.highestRoomY) {
            this.highestRoomY = y;
        }

        PalacePiece nextPiece = this.getNewPiece(this.pieceRand);
        if (y + newPiece.height + nextPiece.height <= this.getHeight() - 7 && this.pieceRand.nextInt(4) != 0) {
            nextPiece.place(x, y + newPiece.height, z);
            if (!this.collideWithAny(nextPiece.aabb)) {
                newPiece.place(x, y, z);
                this.pieceAABBs.add(newPiece.aabb);
                newPiece.drawPiece(this.schematic);
                this.generateSchematicRecursive(x, y + newPiece.height, z, nextPiece);

                for (int i = 0; i < 2; i++) {
                    PalacePiece piece = this.getNewPiece(this.pieceRand);
                    int newPieceChance = this.pieceRand.nextInt(4);
                    switch (newPieceChance) {
                        case 0:
                            if (this.attemptToPlaceAndRecurr(x + 4 + 6 + 4, y, z, piece)) {
                                Palace.PalacePiecePosition PalacePiecePosition = new Palace.PalacePiecePosition(x + 4 + 3, y - 1, z, false);
                                this.corridorPositions.add(PalacePiecePosition);
                            }
                            break;
                        case 1:
                            if (this.attemptToPlaceAndRecurr(x - 4 - 6 - (piece.width >> 1), y, z, piece)) {
                                Palace.PalacePiecePosition PalacePiecePosition = new Palace.PalacePiecePosition(x - 4 - 3, y - 1, z, false);
                                this.corridorPositions.add(PalacePiecePosition);
                            }
                            break;
                        case 2:
                            if (this.attemptToPlaceAndRecurr(x, y, z + 4 + 6 + 4, piece)) {
                                Palace.PalacePiecePosition PalacePiecePosition = new Palace.PalacePiecePosition(x, y - 1, z + 4 + 3, true);
                                this.corridorPositions.add(PalacePiecePosition);
                            }
                            break;
                        case 3:
                            if (this.attemptToPlaceAndRecurr(x, y, z - 4 - 6 - 4, piece)) {
                                Palace.PalacePiecePosition PalacePiecePosition = new Palace.PalacePiecePosition(x, y - 1, z - 4 - 3, true);
                                this.corridorPositions.add(PalacePiecePosition);
                            }
                    }
                }

                return;
            }
        }

        newPiece = new PalaceTowerTop(false, this);
        newPiece.place(x, y, z);
        this.pieceAABBs.add(newPiece.aabb);
        this.topPositions.add(new Palace.PalacePiecePosition(x, y, z, false));
    }

    private boolean attemptToPlaceAndRecurr(int x, int y, int z, PalacePiece piece) {
        piece.place(x, y, z);
        if (!this.collideWithAny(piece.aabb) && this.aabb.containsFully(piece.aabb)) {
            this.generateSchematicRecursive(x, y, z, piece);
            PalacePiece underPiece = new PalaceTowerBottom(true, this);
            underPiece.place(x, y - 7, z);
            this.pieceAABBs.add(underPiece.aabb);
            underPiece.drawPiece(this.schematic);
            return true;
        } else {
            return false;
        }
    }

    private boolean collideWithAny(StructureBoundingBox aabb) {
        Iterator<StructureBoundingBox> iterator = this.pieceAABBs.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().collidesWith(aabb)) {
                return true;
            }
        }

        return false;
    }

    private PalacePiece getNewPiece(Random rand) {
        PalacePiece piece = null;
        boolean rotated = rand.nextBoolean();
        int random = rand.nextInt(selectablePieces.length);
        switch (random) {
            case 0:
                return new PalaceRoom1(rotated, this);
            case 1:
                return new PalaceRoom2(rotated, this);
            case 2:
                return new PalaceRoom3(rotated, this);
            case 3:
                return new PalaceRoom4(rotated, this);
            case 4:
                return new PalaceRoom5(rotated, this);
            case 5:
                return new PalaceRoom6(rotated, this);
            case 6:
                return new PalaceGarden(rotated, this);
            case 7:
                return new PalaceLibrary(rotated, this);
            case 8:
                return new PalaceJail(rotated, this);
            default:
                return piece;
        }
    }

    @Override
    public int getY0() {
        return this.world.getWorldHeight() >= 256 ? this.y0 : this.y0 / 2;
    }

    @Override
    public int getRadius() {
        return 2;
    }

    @Override
    public int getSpawnChance() {
        return 3;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof HellBiome;
    }

    @Override
    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
        switch (id) {
            case 47:
                BookcaseEntity chest11 = (BookcaseEntity)this.world.getTileEntity(x, y, z);
                if (chest11 != null && chest11.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);

                    for (int i = 0; i < ni; i++) {
                        int randomSlot = this.rand.nextInt(9);
                        ItemStack itemStack = new ItemStack(ItemList.book, 4 + this.rand.nextInt(4));
                        chest11.setSlotContents(randomSlot, itemStack);
                    }
                }
                break;
            case 52:
                SpawnerEntity mobSpawner = (SpawnerEntity)this.world.getTileEntity(x, y, z);
                if (mobSpawner != null) {
                    mobSpawner.setMob("Imp");
                }
                break;
            case 54:
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);
                    int level = this.rand.nextInt(10);

                    for (int i = 0; i < ni; i++) {
                        int randomSlot = this.rand.nextInt(27);
                        ItemStack itemStack = this.getTreasure(level, this.rand);
                        chest.setSlotContents(randomSlot, itemStack);
                    }
                }
                break;
            case 179:
                ChestEntity chest1 = (ChestEntity)this.world.getTileEntity(x, y, z);
                this.world.setBlockMetadata(x, y, z, 1);
                if (chest1 != null && chest1.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);

                    for (int i = 0; i < ni; i++) {
                        int randomSlot = this.rand.nextInt(27);
                        ItemStack itemStack = this.getSpecialTreasure(this.rand);
                        chest1.setSlotContents(randomSlot, itemStack);
                    }
                }

                Witch witch = new Witch(this.world);
                witch.setPosition(x, y + 1, z);
                this.world.spawnEntity(witch);
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 5) {
            switch (rand.nextInt(14)) {
                case 0:
                    return new ItemStack(ItemList.ingotDamascus, 1 + rand.nextInt(2));
                case 1:
                    return new ItemStack(ItemList.diamond, rand.nextInt(3) + 1);
                case 2:
                    return new ItemStack(ItemList.saddle);
                case 3:
                    return new ItemStack(ItemList.swordGold);
                case 4:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                case 5:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 6:
                    return new ItemStack(ItemList.recordHell);
                case 7:
                    return new ItemStack(BlockList.lavaSponge, 1 + rand.nextInt(3));
                case 8:
                    return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
                case 9:
                    return rand.nextBoolean() ? new ItemStack(ItemList.crossboner) : new ItemStack(ItemList.musket);
                case 10:
                    return new ItemStack(ItemList.discFragment, 1 + rand.nextInt(2));
                case 11:
                    return new ItemStack(ItemList.pouch);
                case 12:
                    if (rand.nextInt(5) == 0) {
                        return new ItemStack(ItemList.pearl);
                    }
                default:
                    return new ItemStack(ItemList.arrowExplosive, 1 + rand.nextInt(4));
            }
        } else {
            switch (rand.nextInt(12)) {
                case 0:
                    return new ItemStack(ItemList.beetroot, rand.nextInt(4) + 1);
                case 1:
                    return new ItemStack(BlockList.torch, 4 + rand.nextInt(4));
                case 2:
                    return new ItemStack(ItemList.musketRound, 4 + rand.nextInt(8));
                case 3:
                default:
                    return new ItemStack(ItemList.coal, 4 + rand.nextInt(4));
                case 4:
                    return new ItemStack(ItemList.bone, 4 + rand.nextInt(4));
                case 5:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                case 6:
                    return new ItemStack(ItemList.ingotGold, 2 + rand.nextInt(4));
                case 7:
                    return new ItemStack(ItemList.fireball, 2 + rand.nextInt(4));
                case 8:
                    return new ItemStack(ItemList.gunpowder, 4 + rand.nextInt(4));
                case 9:
                    return new ItemStack(ItemList.bowlSussy, 1);
                case 10:
                    return new ItemStack(ItemList.ash, 4 + rand.nextInt(4));
            }
        }
    }

    protected ItemStack getSpecialTreasure(Random rand) {
        switch (rand.nextInt(14)) {
            case 0:
                return new ItemStack(ItemList.swordMichael, 1);
            case 1:
                return new ItemStack(ItemList.lifeShard, 1);
            case 2:
                return new ItemStack(ItemList.belialScythe, 1);
            case 3:
                return new ItemStack(ItemList.grapplingHook, 1);
            case 4:
                return new ItemStack(ItemList.pickaxePheonix, 1);
            case 5:
                return new ItemStack(ItemList.axePheonix, 1);
            case 6:
                return new ItemStack(ItemList.shovelPheonix, 1);
            case 7:
                return new ItemStack(ItemList.swordPheonix, 1);
            case 8:
                return new ItemStack(ItemList.swordVampire, 1);
            case 9:
                return new ItemStack(ItemList.helmetPheonix, 1);
            case 10:
                return new ItemStack(ItemList.platePheonix, 1);
            case 11:
                return new ItemStack(ItemList.legsPheonix, 1);
            case 12:
                return new ItemStack(ItemList.bootsPheonix, 1);
            case 13:
                return new ItemStack(ItemList.capeAgility, 1);
            default:
                return new ItemStack(ItemList.shieldDamascus, 1);
        }
    }

    @Override
    public int minimumSeparation() {
        return 8;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        this.drawSpecialBlocksForChunk(world, rand, chunkX, chunkZ);
    }

    public class PalacePiecePosition {
        public int x;
        public int y;
        public int z;
        public boolean rotated;

        public PalacePiecePosition(int x, int y, int z, boolean rotated) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotated = rotated;
        }
    }
}
