package net.minecraft.game.world.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.animal.Sheep;
import net.minecraft.game.entity.mob.animal.cow.Cow;
import net.minecraft.game.entity.mob.animal.flying.Bat;
import net.minecraft.game.entity.mob.animal.flying.Chicken;
import net.minecraft.game.entity.mob.animal.mounts.Pig;
import net.minecraft.game.entity.mob.monster.Ogre;
import net.minecraft.game.entity.mob.monster.Phantom;
import net.minecraft.game.entity.mob.monster.Stalker;
import net.minecraft.game.entity.mob.monster.arachnid.Spider;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.slime.Slime;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.hot.VolcanicWasteland;
import net.minecraft.game.world.biomes.hot.Wasteland;
import net.minecraft.game.world.biomes.ocean.ColdOcean;
import net.minecraft.game.world.biomes.ocean.Crag;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.biomes.temperate.Mountains;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.deciduous.AppleTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BasicTree;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import net.minecraft.game.world.mobspawner.SpawnCategory;
import net.minecraft.game.world.mobspawner.SpawnListEntry;
import net.minecraft.localization.Translator;

public class Biome {
    public String name;
    public int id = 0;
    public int mapColor = 5169201;
    public float depth = 1.0F;
    public float scale = 1.0F;
    public byte climate = 5;
    private long noiseSeed;
    public short topBlock = (short)BlockList.grass.id;
    public byte topMeta = 0;
    public short fillerBlock = (short)BlockList.dirt.id;
    public byte fillerMeta = 0;
    public int defaultLiquid = BlockList.waterStill.id;
    public short beachBlock1 = (short)BlockList.sand.id;
    public byte beachMeta1 = 0;
    public short beachBlock2 = (short)BlockList.gravel.id;
    public byte beachMeta2 = 0;
    public short caveBlock1 = (short)BlockList.stone.id;
    public byte caveMeta1 = 0;
    public short caveBlock2 = (short)BlockList.stone.id;
    public byte caveMeta2 = 0;
    public byte grassMeta = 0;
    protected List<SpawnListEntry> monsters = new ArrayList<>();
    protected List<SpawnListEntry> animals = new ArrayList<>();
    protected List<SpawnListEntry> seaCreatures = new ArrayList<>();
    public OctaveNoise caveNoise;
    public OctaveNoise stoneNoise;
    protected Biome.Temperature temperature;
    protected Biome.Humidity humidity;
    public List<Biome> variants = new ArrayList<>();
    public int clayAttempts = 20;
    public int dirtLumpAttempts = 20;
    public int gravelLumpAttempts = 10;
    public int marbleLumpAttempts = 5;
    public int coalLumpAttempts = 20;
    public int glowLumpAttempts = 10;
    public int copperLumpAttempts = 20;
    public int copperLumpMaxHeight = 96;
    public int ironLumpAttempts = 15;
    public int ironLumpMaxHeight = 64;
    public int goldLumpAttempts = 5;
    public int goldLumpMaxHeight = 32;
    public int diamondLumpAttempts = 1;
    public int diamondLumpMaxHeight = 8;
    public int rubyLumpAttempts = 3;
    public int rubyLumpMaxHeight = 20;
    public int sapphireLumpAttempts = 3;
    public int sapphireLumpMaxHeight = 20;
    public int emeraldLumpAttempts = 3;
    public int emeraldLumpMaxHeight = 20;
    public int treeAttempts = 0;
    public int flowersAttempts = 2;
    public int mushroomChance = 4;
    public int reedAttempts = 20;
    public int tallGrassAttempts = 0;
    public int pebbleAttempts = 1;
    public int waterFallAttempts = 20;
    public int waterFallMaxHeight = 192;
    public int waterFallMinHeight = 64;
    public int lavaFallAttempts = 5;
    public int waterAttempts = 8;
    public int lavaAttempts = 8;
    public int defaultStoneType = 1;
    public boolean genBeaches = true;
    public boolean generateLava = true;
    public boolean generateWater = true;
    public boolean biomeCaves = false;
    public boolean climateBiome = false;
    protected boolean enableSnow = false;
    protected boolean enableRain = true;
    protected boolean unlockBeachHeight = false;
    public boolean hasBeaches = true;
    protected int stoneLayer = 2;
    protected int stoneHeight = 0;
    protected int caveCeiling = 1;
    public int caveFloor = 21;
    protected int minBeachHeight = 4;
    protected int maxBeachHeight = 1;
    public int yOffset = 64;

    protected Biome(int biomeID) {
        this.monsters.add(new SpawnListEntry(Spider.class, 10));
        this.monsters.add(new SpawnListEntry(Zombie.class, 10));
        this.monsters.add(new SpawnListEntry(Skeleton.class, 10));
        this.monsters.add(new SpawnListEntry(Creeper.class, 10));
        this.monsters.add(new SpawnListEntry(Slime.class, 8));
        this.monsters.add(new SpawnListEntry(Ogre.class, 6));
        this.monsters.add(new SpawnListEntry(Phantom.class, 4));
        this.monsters.add(new SpawnListEntry(Stalker.class, 1));
        this.animals.add(new SpawnListEntry(Sheep.class, 12));
        this.animals.add(new SpawnListEntry(Pig.class, 10));
        this.animals.add(new SpawnListEntry(Chicken.class, 10));
        this.animals.add(new SpawnListEntry(Cow.class, 8));
        this.animals.add(new SpawnListEntry(Bat.class, 10));
        this.id = biomeID;
        BiomeList.biomes[this.id] = this;
    }

    protected Biome setClimate(Biome.Temperature temperature, Biome.Humidity rainfall) {
        this.climate = (byte)(temperature.value << 2 | rainfall.value);
        this.temperature = temperature;
        this.humidity = rainfall;
        return this;
    }

    protected Biome addAsMainClimateBiome() {
        BiomeList.climateBiomes[this.climate].add(this);
        BiomeList.climateBiomes[this.climate].remove(BiomeList.biomeDefault);
        this.climateBiome = true;
        return this;
    }

    public Tree defaultTreeType(Random rand) {
        return rand.nextInt(20) == 0 ? new AppleTree() : new BasicTree(this.fillerMeta);
    }

    public Tree getRandomTreeGenerator(Random rand, int y, int worldHeight) {
        return this.defaultTreeType(rand);
    }

    protected Biome setDepthAndScale(float depth, float scale) {
        this.depth = depth;
        this.scale = scale;
        return this;
    }

    protected Biome setCaveFloor(int floor) {
        this.caveFloor = floor;
        return this;
    }

    public Biome disableRain() {
        this.enableRain = false;
        return this;
    }

    public boolean hasNoRain() {
        return !this.enableRain;
    }

    protected Biome enableSnow() {
        this.enableSnow = true;
        return this;
    }

    public boolean hasSnow() {
        return this.enableSnow;
    }

    public boolean canThunder() {
        return !this.enableSnow && this.enableRain;
    }

    protected Biome setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return Translator.getInstance().translateKey(this.name);
    }

    protected Biome setColor(int col) {
        this.mapColor = col;
        return this;
    }

    public void prePopulate(World world, Random rand, int x0, int z0) {
    }

    public void postPopulate(World world, Random rand, int x0, int z0) {
    }

    public int getTopBlock() {
        return this.topBlock;
    }

    public byte getTopMeta() {
        return this.topMeta;
    }

    public int getFillerBlock() {
        return this.fillerBlock;
    }

    public byte getFillerMeta() {
        return this.fillerMeta;
    }

    public void surfaceBuilder(
        World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue, double sandValue, double gravelValue
    ) {
        int top = this.topBlock;
        byte topMetadata = (byte)(this.topMeta & 0xFF);
        int filler = this.fillerBlock;
        byte fillerMetadata = (byte)(this.fillerMeta & 0xFF);
        int stoneHeight = -1;
        boolean beaches = sandValue + random.nextDouble() * 0.2 > 0.0;
        boolean gravelBeaches = gravelValue + random.nextDouble() * 0.2 > 3.0;
        if (this.noiseSeed != world.getSeed()) {
            this.noiseSeed = world.getSeed();
            Random rand = new Random(this.noiseSeed);
            this.caveNoise = new OctaveNoise(rand, 4);
            this.stoneNoise = new OctaveNoise(rand, 6);
        }

        boolean caveFloor = this.caveNoise.generateNoise2D(x, z) > 0.0;
        boolean caveStone = this.stoneNoise.generateNoise2D(x, z) > 8.0;
        int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int chunkX = x & 15;
        int chunkZ = z & 15;
        this.yOffset = 64;
        if (world.getMaxSections() < 20) {
            this.yOffset = 32;
        }

        for (int y = world.getWorldHeight() - 1; y >= 0; y--) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            if (world.settings.getType() == WorldType.FLOATING || y > random.nextInt(6) - 1 && y != 0) {
                if (world.settings.getType() != WorldType.FLOATING && y <= this.yOffset && y > this.yOffset - 6 + random.nextInt(6) - 1) {
                    blocks[index] = (byte)BlockList.stone.id;
                    metadata[index] = 0;
                } else if (world.settings.getType() != WorldType.FLOATING && y <= this.yOffset && blocks[index] == 0) {
                    blocks[index] = (byte)BlockList.stone.id;
                    metadata[index] = 2;
                } else {
                    short block = blocks[index];
                    short meta = metadata[index];
                    if (block > 0) {
                        if (block == BlockList.waterStill.id) {
                            if ((this instanceof Wasteland || this instanceof VolcanicWasteland)
                                && (
                                    world.getBiomeAt(x + 16, z).defaultLiquid != this.defaultLiquid
                                        || world.getBiomeAt(x, z + 16).defaultLiquid != this.defaultLiquid
                                        || world.getBiomeAt(x - 16, z).defaultLiquid != this.defaultLiquid
                                        || world.getBiomeAt(x, z - 16).defaultLiquid != this.defaultLiquid
                                )) {
                                if (this.defaultLiquid == BlockList.lavaStill.id) {
                                    blocks[index] = (short)BlockList.magma.id;
                                } else if (this.defaultLiquid == 0) {
                                    blocks[index] = (short)BlockList.waterMoving.id;
                                } else {
                                    blocks[index] = (short)this.defaultLiquid;
                                }
                            } else {
                                blocks[index] = (short)this.defaultLiquid;
                            }
                        }

                        if (block == BlockList.waterStill.id && y == world.getSeaLevel() - 1 && this instanceof ColdOcean && gravelBeaches) {
                            blocks[index] = (short)BlockList.ice.id;
                        }

                        if (block == BlockList.stone.id && meta == 0) {
                            if (stoneHeight == -1) {
                                if (height <= this.stoneHeight) {
                                    top = 0;
                                    topMetadata = 0;
                                    filler = BlockList.stone.id;
                                    fillerMetadata = 0;
                                    if (this.unlockBeachHeight && (gravelBeaches || beaches)) {
                                        if (gravelBeaches) {
                                            top = this.beachBlock2;
                                            topMetadata = this.beachMeta2;
                                            filler = this.beachBlock2;
                                            fillerMetadata = this.beachMeta2;
                                        } else if (beaches) {
                                            top = this.beachBlock1;
                                            topMetadata = this.beachMeta1;
                                            filler = this.beachBlock1;
                                            fillerMetadata = this.beachMeta1;
                                        }
                                    }

                                    if (this instanceof Mountains && y >= world.getWorldHeight() / 2 + 32 - random.nextInt(4)) {
                                        top = BlockList.snowBlock.id;
                                        filler = BlockList.snowBlock.id;
                                    }
                                } else if (y >= world.getSeaLevel() - this.minBeachHeight && y <= world.getSeaLevel() + this.maxBeachHeight) {
                                    top = this.getTopBlock();
                                    topMetadata = (byte)(this.getTopMeta() & 0xFF);
                                    filler = this.getFillerBlock();
                                    fillerMetadata = (byte)(this.getFillerMeta() & 0xFF);
                                    if (!this.genBeaches) {
                                        top = this.topBlock;
                                        topMetadata = this.topMeta;
                                    } else {
                                        if (gravelBeaches) {
                                            if (!(this instanceof Ocean) && !(this instanceof Crag)) {
                                                top = 0;
                                                topMetadata = 0;
                                            } else {
                                                top = this.beachBlock2;
                                                topMetadata = this.beachMeta2;
                                            }

                                            filler = this.beachBlock2;
                                            fillerMetadata = this.beachMeta2;
                                        }

                                        if (beaches) {
                                            top = this.beachBlock1;
                                            topMetadata = this.beachMeta1;
                                            filler = this.beachBlock1;
                                            fillerMetadata = this.beachMeta1;
                                        }
                                    }
                                }

                                if (y < world.getSeaLevel() && top == 0 && world.settings.getType() != WorldType.FLOATING) {
                                    top = this.defaultLiquid;
                                    topMetadata = 0;
                                }

                                if (y < world.getSeaLevel() - 1 && top == this.topBlock && this instanceof Ocean) {
                                    top = this.fillerBlock;
                                }

                                if (this.id == BiomeList.beach.id || this.id == BiomeList.lushBeach.id) {
                                    if (y >= world.getSeaLevel() + 5) {
                                        top = BlockList.grass.id;
                                        filler = BlockList.dirt.id;
                                    } else {
                                        topMetadata = 0;
                                        fillerMetadata = 0;
                                    }
                                }

                                if (this.id == BiomeList.desert.id && y <= world.getSeaLevel() + 8) {
                                    top = BlockList.grass.id;
                                    filler = BlockList.dirt.id;
                                }

                                if (this.id == BiomeList.fjord.id && y >= world.getWorldHeight() / 2 + 16 - random.nextInt(4)) {
                                    top = BlockList.snowBlock.id;
                                    filler = BlockList.snowBlock.id;
                                    fillerMetadata = 0;
                                    topMetadata = 0;
                                }

                                stoneHeight = height;
                                if (y >= world.getSeaLevel() - this.caveCeiling) {
                                    blocks[index] = (short)top;
                                    metadata[index] = topMetadata;
                                } else if (y < world.getSeaLevel() - this.caveFloor - height) {
                                    if (this.biomeCaves) {
                                        if (caveFloor) {
                                            top = this.caveBlock1;
                                            topMetadata = this.caveMeta1;
                                            filler = this.caveBlock1;
                                            fillerMetadata = this.caveMeta1;
                                        } else {
                                            top = this.caveBlock2;
                                            topMetadata = this.caveMeta2;
                                            filler = this.caveBlock2;
                                            fillerMetadata = this.caveMeta2;
                                        }

                                        if (caveStone) {
                                            top = BlockList.stone.id;
                                            topMetadata = 0;
                                            filler = BlockList.stone.id;
                                            fillerMetadata = 0;
                                        }

                                        blocks[index] = (short)top;
                                        metadata[index] = topMetadata;
                                    } else {
                                        top = 0;
                                        filler = BlockList.stone.id;
                                        fillerMetadata = 0;
                                        blocks[index] = (short)filler;
                                        metadata[index] = fillerMetadata;
                                    }
                                } else if (world.settings.getType() == WorldType.FLOATING) {
                                    blocks[index] = (short)top;
                                    metadata[index] = topMetadata;
                                } else {
                                    blocks[index] = (short)filler;
                                    metadata[index] = fillerMetadata;
                                }
                            } else if (stoneHeight > 0) {
                                stoneHeight--;
                                blocks[index] = (short)filler;
                                metadata[index] = fillerMetadata;
                                if (stoneHeight == 0
                                    && (
                                        BlockList.blocks[filler].material == MaterialList.sand
                                            || BlockList.blocks[filler].material == MaterialList.quicksand
                                            || BlockList.blocks[filler].material == MaterialList.snow
                                    )) {
                                    if (this.stoneLayer == -1) {
                                        stoneHeight = random.nextInt(4) + Math.max(0, y - world.getSeaLevel());
                                    } else {
                                        stoneHeight = random.nextInt(4) + this.stoneLayer;
                                    }

                                    if (filler != BlockList.sand.id
                                        && filler != BlockList.quickSand.id
                                        && filler != BlockList.redSand.id
                                        && filler != BlockList.silverSand.id) {
                                        if (filler == BlockList.snowBlock.id || filler == BlockList.powderSnow.id) {
                                            filler = BlockList.packedIce.id;
                                        } else if (filler == BlockList.gravel.id) {
                                            filler = BlockList.stone.id;
                                        }
                                    } else {
                                        if (filler == BlockList.redSand.id) {
                                            fillerMetadata = 2;
                                        }

                                        if (filler == BlockList.silverSand.id) {
                                            fillerMetadata = 4;
                                        }

                                        filler = BlockList.sandStone.id;
                                    }
                                }
                            }
                        }
                    } else {
                        stoneHeight = -1;
                    }
                }
            } else {
                blocks[index] = (byte)BlockList.bedrock.id;
            }
        }
    }

    public void caveBuilder(World world, Random random, short[] blocks, byte[] metadata, int x, int z, double heightValue) {
        int top = this.topBlock;
        byte topMetadata = (byte)(this.topMeta & 0xFF);
        int filler = this.fillerBlock;
        byte fillerMetadata = (byte)(this.fillerMeta & 0xFF);
        int stoneHeight = -1;
        if (this.noiseSeed != world.getSeed()) {
            this.noiseSeed = world.getSeed();
            Random rand = new Random(this.noiseSeed);
            this.caveNoise = new OctaveNoise(rand, 4);
            this.stoneNoise = new OctaveNoise(rand, 6);
        }

        boolean generateStones = this.caveNoise.generateNoise2D(x, z) > 0.0;
        boolean generateOres = this.stoneNoise.generateNoise2D(x, z) > 8.0;
        int height = (int)(heightValue / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int chunkX = x & 15;
        int chunkZ = z & 15;
        int ymax = world.getSeaLevel();
        int ystart = this.yOffset;

        for (int y = ystart; y < ymax; y++) {
            int index = y << 8 | chunkZ << 4 | chunkX;
            short block = blocks[index];
            short meta = metadata[index];
            if (block <= 0 || block != BlockList.stone.id || meta != 0) {
                stoneHeight = -1;
            } else if (stoneHeight == -1) {
                stoneHeight = height;
                if (y < world.getSeaLevel()) {
                    if (this.biomeCaves) {
                        if (generateStones) {
                            top = this.caveBlock1;
                            topMetadata = this.caveMeta1;
                            filler = this.caveBlock1;
                            fillerMetadata = this.caveMeta1;
                        } else {
                            top = this.caveBlock2;
                            topMetadata = this.caveMeta2;
                            filler = this.caveBlock2;
                            fillerMetadata = this.caveMeta2;
                        }

                        if (generateOres) {
                            top = BlockList.stone.id;
                            topMetadata = 0;
                            filler = BlockList.stone.id;
                            fillerMetadata = 0;
                        }

                        blocks[index] = (short)top;
                        metadata[index] = topMetadata;
                    } else {
                        int var26 = 0;
                        filler = BlockList.stone.id;
                        fillerMetadata = 0;
                        blocks[index] = (short)filler;
                        metadata[index] = fillerMetadata;
                    }
                }
            } else if (stoneHeight > 0) {
                stoneHeight--;
                blocks[index] = (short)filler;
                metadata[index] = fillerMetadata;
            }
        }
    }

    public final byte getRainfall() {
        return (byte)(this.climate & 3);
    }

    public final byte getTemperature() {
        return (byte)(this.climate >> 2);
    }

    public int getSkyColor() {
        return 8961023;
    }

    public void spawnParticles(World world, double posX, double posY, double posZ) {
    }

    public List<SpawnListEntry> getSpawnList(SpawnCategory mobType) {
        switch (mobType) {
            case monster:
                return this.monsters;
            case creature:
                return this.animals;
            case aquatic:
                return this.seaCreatures;
            default:
                return null;
        }
    }

    public Biome.Temperature getTemperatureEnum() {
        return this.temperature == null ? Biome.Temperature.HOT : this.temperature;
    }

    public Biome.Humidity getHumidityEnum() {
        return this.humidity == null ? Biome.Humidity.DRY : this.humidity;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Biome findById(int id) {
        for (Biome biome : BiomeList.biomes) {
            if (biome != null && biome.id == id) {
                return biome;
            }
        }

        throw new NullPointerException("id not found " + id);
    }

    protected static int getLastValidIndex(Object[] array) {
        int lastindex = 0;

        for (int i = 0; i < array.length; i++) {
            Object o = array[i];
            if (o == null) {
                lastindex = i;
                break;
            }
        }

        return lastindex;
    }

    public enum Humidity {
        DRY(0),
        TEMPERATE(1),
        MOIST(2),
        WET(3);

        public final byte value;

        Humidity(int value) {
            this.value = (byte)value;
        }
    }

    public enum Temperature {
        COLD(0),
        TEMPERATE(1),
        WARM(2),
        HOT(3);

        public final byte value;

        Temperature(int value) {
            this.value = (byte)value;
        }
    }
}
