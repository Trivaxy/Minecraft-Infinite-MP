package net.minecraft.game.world.generation.structures.misc;

import com.mojang.nbt.tag.special.ListTag;
import com.mojang.nbt.tag.special.StringTag;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.LecternEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.boss.Priest;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.sky.Cloud;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Cathedral extends SchematicStructure {
    private int y0;
    private Random rand = new Random();
    String[] bookContents = new String[]{
        "She came from the water, bold and beautiful. Mighty sorceress from beyond.",
        "When night falls, she cloaks the world in impenetrable darkness. A chill rises from the soil and contaminates the air. Suddenly, life has new meaning.",
        "We have not inherited Thule from our ancestors, we have borrowed it from our children.",
        "From the ashes, he arose. Powerful wings of destruction would soon bring upon our demise.",
        "Cast into the void, a slumbering giant. An age of evolution will dawn upon his awakening.",
        "Today is born the seventh one. Born of woman the seventh son. And he in turn of a seventh son. He has the power to heal, He has the gift of the second sight. He is the chosen one.",
        "Other planes lie beyond the reach of normal sense and common roads.",
        "And when she ascended into the heavens, the pact was sealed. The groundlings would soon forget, her memory laid in stone.",
        "One with the blue sky. A child of the stars. A last hope at the edge of yonder is all we can cling to.",
        "To make the Gods' nature one's own, a man must imitate them in every thought, word and deed.",
        "I wonder how winter will be, with a spring that I shall never see. I wonder how night will be, with a day that I shall never see. I wonder how life will be, with a pain that lasts eternally.",
        "I wonder how life will be, with a death that I shall never see. I wonder why life must be, a life that lasts eternally.",
        "As I lie here, the burden is being lifted once and for all. Beware of the light. It may take you away to a land where no evil dwells. Take you away for all eternity.",
        "There stands an ash called Yggdrasil, a mighty tree, showered in white hail. From there come the dews that fall in the valleys, it stands ever green above Earth's well.",
        "Woe be to him that defies the tree, for he shall be cast out into the world through the veils of pain and fire.",
        "Blessed be the tailors, the masks are cut to fit. Blessed be the woodworkers, the crosses and the gallows. Blessed be the forgers of iron, the spikes and the barbwire. Blessed be the stone cutters, it took a quarry to bury their dreams."
    };
    public static final int[][][] array = readData();

    public Cathedral(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.y0 = this.world.getWorldHeight() - this.getHeight() + this.world.rand.nextInt(8);
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        int[][][] piece = array;

        for (int x = 0; x < piece.length; x++) {
            for (int z = 0; z < piece[x].length; z++) {
                for (int y = 0; y < piece[x][z].length; y++) {
                    int s = piece[x][z][y];
                    if (s >= 0) {
                        int blockID = s & 0xFF;
                        int meta = s >> 12;
                        this.schematic[x][z][y] = s;
                        if (blockID == 54 || blockID == 179 || blockID == 4 || blockID == 52 || blockID == 227) {
                            this.addSpecialBlock(x, y, z, blockID, meta);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
        switch (id) {
            case 52:
                SpawnerEntity spawner = (SpawnerEntity)this.world.getTileEntity(x, y, z);
                if (spawner instanceof SpawnerEntity) {
                    spawner.setMob("PigMan");
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

                if (this.world.isAir(x, y - 1, z)) {
                    this.world.setBlockAndMetadata(x, y - 1, z, BlockList.cobblestone.id, 8);
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

                if (this.world.isAir(x, y - 1, z)) {
                    this.world.setBlockAndMetadata(x, y - 1, z, BlockList.cobblestone.id, 8);
                }

                this.world.setBlockMetadata(x, y, z, 1);
                break;
            }
            case 227:
                LecternEntity lcetern = (LecternEntity)this.world.getTileEntity(x, y, z);
                ItemStack itemStack = new ItemStack(ItemList.writtenBook);
                ListTag<StringTag> bookPages = new ListTag<>();
                bookPages.add(new StringTag(""));
                StringTag read = bookPages.get(0);
                read.string = this.bookContents[this.world.rand.nextInt(this.bookContents.length)];
                itemStack.setTagInfo("pages", bookPages);
                lcetern.setSlotContents(0, itemStack);
                if (this.world.isAir(x, y - 1, z)) {
                    this.world.setBlockAndMetadata(x, y - 1, z, BlockList.cobblestone.id, 8);
                }

                Priest boss = new Priest(this.world);
                boss.setPosition(x, y, z);
                this.world.spawnEntity(boss);
        }
    }

    protected ItemStack getSpecialTreasure(Random rand) {
        switch (rand.nextInt(18)) {
            case 0:
                return new ItemStack(ItemList.pickaxeValkyrie);
            case 1:
                return new ItemStack(ItemList.axeValkyrie);
            case 2:
                return new ItemStack(ItemList.shovelValkyrie);
            case 3:
            default:
                return new ItemStack(ItemList.lifeShard);
            case 4:
                return new ItemStack(ItemList.swordValkyrie);
            case 5:
                return new ItemStack(ItemList.helmetValkyrie);
            case 6:
                return new ItemStack(ItemList.plateValkyrie);
            case 7:
                return new ItemStack(ItemList.legsValkyrie);
            case 8:
                return new ItemStack(ItemList.bootsValkyrie);
            case 9:
                return new ItemStack(ItemList.rosary);
            case 10:
                return new ItemStack(ItemList.elytra);
            case 11:
                return new ItemStack(ItemList.cloudStaff);
            case 12:
                return new ItemStack(ItemList.gabrielStaff);
            case 13:
                return new ItemStack(ItemList.gauntletUriel);
            case 14:
                return new ItemStack(ItemList.harp);
            case 15:
                return new ItemStack(ItemList.mace);
            case 16:
                return new ItemStack(ItemList.windAmulet);
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 5) {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.pouch);
                case 1:
                    return new ItemStack(ItemList.crossbow);
                case 2:
                    if (rand.nextInt(5) == 0) {
                        return new ItemStack(ItemList.pearl);
                    }
                case 3:
                    return new ItemStack(ItemList.helmetSteel);
                case 4:
                    return new ItemStack(ItemList.plateSteel);
                case 5:
                    return new ItemStack(ItemList.saddle);
                case 6:
                    return new ItemStack(ItemList.legsSteel);
                case 7:
                    return new ItemStack(ItemList.bootsSteel);
                case 8:
                    return new ItemStack(ItemList.splashPotion, 1, rand.nextInt(19));
                default:
                    return new ItemStack(ItemList.arrowPiercing, 1 + rand.nextInt(4));
            }
        } else {
            switch (rand.nextInt(9)) {
                case 0:
                    return new ItemStack(ItemList.recordSky);
                case 1:
                    return new ItemStack(ItemList.arrowCalming, rand.nextInt(4) + 1);
                case 2:
                    return new ItemStack(BlockList.blockAdamantine, rand.nextInt(3) + 1);
                case 3:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 4:
                    return new ItemStack(ItemList.topaz, rand.nextInt(3) + 1);
                case 5:
                    return new ItemStack(ItemList.spyGlass);
                case 6:
                    return new ItemStack(ItemList.pouch, rand.nextInt(3) + 1);
                case 7:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                default:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
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
                if (s.blockID == 4 && s.y == 0 && world.getBlock(xAbs, this.y0 - 1, zAbs) == null) {
                    Cloud.SMALL_CLOUD.place(world, rand, xAbs, this.y0 - 3, zAbs);
                }
            }
        }
    }

    @Override
    public int getY0() {
        return this.y0;
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    public int getSpawnChance() {
        return 3;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof SkyBiome;
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
            DataInputStream dis = new DataInputStream(Cathedral.class.getResourceAsStream("/struct/cathedral.bin"));
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
