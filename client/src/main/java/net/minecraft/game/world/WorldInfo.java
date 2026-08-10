package net.minecraft.game.world;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.Arrays;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;

public class WorldInfo {
    private CompoundTag playerTag;
    private WorldType worldType;
    private Biome singleBiome = BiomeList.biomeDefault;
    private String worldName;
    public String saveFolder;
    private String saveVersion;
    public boolean[] biomes = new boolean[256];
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int dimension;
    private int convertedStatus;
    private int maxSections;
    private int randomEventID;
    private int rainTime;
    private int thunderTime;
    public int border = 0;
    private long randomSeed;
    private long worldTime;
    private long lastTimePlayed;
    private long sizeOnDisk;
    private boolean cheats;
    public boolean bonusHouse;
    private boolean amplified;
    private boolean generateStructures;
    private boolean aiDisabled;
    private boolean physicsDisabled;
    private boolean permaDay;
    private boolean raining;
    private boolean thundering;
    private boolean weather = true;
    public boolean explosions = true;
    public boolean spawning = true;
    public boolean mobDrops = true;
    public boolean blockDrops = true;
    public boolean difficultyLock = false;
    private byte gamemode;
    private byte difficulty;
    public float dayLength = 24000.0F;
    public int firstLayer = 0;
    public int secondLayer = 3;
    public int thirdLayer = 5;
    public int topLayer = 6;
    public int bottomBlock = BlockList.bedrock.id;
    public int firstBlock = BlockList.stone.id;
    public int secondBlock = BlockList.dirt.id;
    public int thirdBlock = BlockList.grass.id;

    public WorldInfo() {
        Arrays.fill(this.biomes, true);
    }

    public WorldInfo(CompoundTag tag) {
        this();
        this.randomSeed = tag.getLong("RandomSeed");
        this.spawnX = tag.getInteger("SpawnX");
        this.spawnY = tag.getInteger("SpawnY");
        this.spawnZ = tag.getInteger("SpawnZ");
        this.worldTime = tag.getLong("Time");
        this.lastTimePlayed = tag.getLong("LastPlayed");
        this.sizeOnDisk = tag.getLong("SizeOnDisk");
        this.worldName = tag.getString("LevelName");
        if (tag.hasKey("maxSections")) {
            this.maxSections = tag.getInteger("maxSections");
        } else {
            this.maxSections = 16;
        }

        this.randomEventID = tag.getInteger("ongoingEvent");
        this.convertedStatus = tag.getInteger("version");
        this.cheats = tag.getBoolean("cheats");
        this.gamemode = tag.getByte("gamemode");
        this.difficulty = tag.getByte("difficulty");
        this.amplified = tag.getBoolean("amplified");
        this.generateStructures = tag.getBoolean("structureGen");
        this.worldType = WorldType.fromNumber(tag.getByte("worldType"));
        this.aiDisabled = tag.getBoolean("aiDisabled");
        this.physicsDisabled = tag.getBoolean("physicsDisabled");
        this.permaDay = tag.getBoolean("permaDay");
        this.rainTime = tag.getInteger("rainTime");
        this.raining = tag.getBoolean("raining");
        this.thunderTime = tag.getInteger("thunderTime");
        this.thundering = tag.getBoolean("thundering");
        this.weather = tag.getBoolean("weatherEnabled");
        this.bonusHouse = tag.getBoolean("bonusHouse");
        this.border = tag.getInteger("worldBorder");
        this.explosions = tag.getBoolean("explosions");
        this.mobDrops = tag.getBoolean("mobDrops");
        this.blockDrops = tag.getBoolean("blockDrops");
        this.spawning = tag.getBoolean("mobSpawning");
        this.difficultyLock = tag.getBoolean("difficultyLock");
        this.saveFolder = tag.getString("saveFolder");
        if (tag.hasKey("dayLength")) {
            this.dayLength = tag.getFloat("dayLength");
        } else {
            this.dayLength = 24000.0F;
        }

        if (tag.hasKey("versionString")) {
            this.saveVersion = tag.getString("versionString");
        } else {
            this.saveVersion = "1.0";
        }

        if (this.worldType == WorldType.BIOME) {
            this.singleBiome = Biome.findById(tag.getInteger("SingleBiomeId"));
        }

        if (this.worldType == WorldType.FLAT) {
            this.topLayer = tag.getShort("topLayer");
            this.firstLayer = tag.getShort("firstLayer");
            this.secondLayer = tag.getShort("secondLayer");
            this.thirdLayer = tag.getShort("thirdLayer");
            this.bottomBlock = tag.getShort("bottomBlock");
            this.firstBlock = tag.getShort("firstBlock");
            this.secondBlock = tag.getShort("secondBlock");
            this.thirdBlock = tag.getShort("thirdBlock");
        }

        if (tag.hasKey("Biomes")) {
            CompoundTag biomeTag = tag.getCompoundTag("Biomes");

            for (int i = 0; i < this.biomes.length; i++) {
                if (BiomeList.biomes[i] != null) {
                    this.biomes[i] = biomeTag.getBoolean(BiomeList.biomes[i].getName());
                }
            }
        }

        if (tag.hasKey("Player")) {
            this.playerTag = tag.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }
    }

    public WorldInfo(long seed, String name, WorldSettings settings) {
        this.randomSeed = seed;
        this.worldName = name;
        if (settings != null) {
            this.biomes = settings.biomes;
            this.singleBiome = settings.singleBiomeId;
            this.difficultyLock = settings.difficultyLock;
            this.topLayer = settings.topLayer;
            this.firstLayer = settings.layerOne;
            this.secondLayer = settings.layerTwo;
            this.thirdLayer = settings.bottomLayer;
            this.firstBlock = settings.blockTwo;
            this.secondBlock = settings.bottomBlock;
            this.thirdBlock = settings.topBlock;
            this.bottomBlock = settings.blockOne;
        }

        this.cheats = WorldSettings.cheats;
        this.gamemode = (byte)WorldSettings.gamemode;
        this.difficulty = (byte)WorldSettings.difficulty;
        this.generateStructures = WorldSettings.structures;
        this.amplified = WorldSettings.worldType == WorldType.AMPLIFIED;
        this.maxSections = WorldSettings.heightInt;
        this.worldType = WorldSettings.worldType;
        this.border = WorldSettings.size;
        this.weather = WorldSettings.events;
        this.dayLength = WorldSettings.dayLength;
        this.bonusHouse = WorldSettings.bonus;
        this.permaDay = WorldSettings.permaDay;
    }

    public CompoundTag getTag() {
        CompoundTag tag = new CompoundTag();
        this.saveToNBT(tag, this.playerTag);
        return tag;
    }

    public CompoundTag getPlayerTag(List<Player> playersList) {
        CompoundTag tag = new CompoundTag();
        Player player = null;
        CompoundTag playerTag = null;
        if (!playersList.isEmpty()) {
            player = playersList.get(0);
        }

        if (player != null) {
            playerTag = new CompoundTag();
            player.save(playerTag);
        }

        this.saveToNBT(tag, playerTag);
        return tag;
    }

    private void saveToNBT(CompoundTag base, CompoundTag player) {
        base.setLong("RandomSeed", this.randomSeed);
        base.setInteger("SpawnX", this.spawnX);
        base.setInteger("SpawnY", this.spawnY);
        base.setInteger("SpawnZ", this.spawnZ);
        base.setLong("Time", this.worldTime);
        base.setLong("SizeOnDisk", this.sizeOnDisk);
        base.setLong("LastPlayed", System.currentTimeMillis());
        base.setString("LevelName", this.worldName);
        base.setInteger("maxSections", this.maxSections);
        base.setInteger("version", this.convertedStatus);
        base.setBoolean("cheats", this.cheats);
        base.setByte("gamemode", this.gamemode);
        base.setByte("difficulty", this.difficulty);
        base.setBoolean("amplified", this.amplified);
        base.setBoolean("structureGen", this.generateStructures);
        base.setByte("worldType", (byte)this.worldType.id);
        base.setBoolean("aiDisabled", this.aiDisabled);
        base.setBoolean("physicsDisabled", this.physicsDisabled);
        base.setBoolean("permaDay", this.permaDay);
        base.setInteger("rainTime", this.rainTime);
        base.setBoolean("raining", this.raining);
        base.setInteger("thunderTime", this.thunderTime);
        base.setBoolean("thundering", this.thundering);
        base.setBoolean("weatherEnabled", this.weather);
        base.setBoolean("bonusHouse", this.bonusHouse);
        base.setInteger("worldBorder", this.border);
        base.setBoolean("explosions", this.explosions);
        base.setInteger("ongoingEvent", this.randomEventID);
        base.setFloat("dayLength", this.dayLength);
        base.setBoolean("mobDrops", this.mobDrops);
        base.setBoolean("mobSpawning", this.spawning);
        base.setBoolean("blockDrops", this.blockDrops);
        base.setBoolean("difficultyLock", this.difficultyLock);
        if (this.saveFolder != null) {
            base.setString("saveFolder", this.saveFolder);
        }

        if (this.saveVersion != null) {
            base.setString("versionString", this.saveVersion);
        }

        if (this.worldType == WorldType.BIOME) {
            base.setInteger("SingleBiomeId", this.singleBiome.id);
        }

        if (this.worldType == WorldType.FLAT) {
            base.setShort("topLayer", (short)this.topLayer);
            base.setShort("firstLayer", (short)this.firstLayer);
            base.setShort("secondLayer", (short)this.secondLayer);
            base.setShort("thirdLayer", (short)this.thirdLayer);
            base.setShort("bottomBlock", (short)this.bottomBlock);
            base.setShort("firstBlock", (short)this.firstBlock);
            base.setShort("secondBlock", (short)this.secondBlock);
            base.setShort("thirdBlock", (short)this.thirdBlock);
        }

        CompoundTag biomeTag = new CompoundTag();

        for (int i = 0; i < this.biomes.length; i++) {
            if (BiomeList.biomes[i] != null) {
                biomeTag.setBoolean(BiomeList.biomes[i].getName(), this.biomes[i]);
            }
        }

        base.setCompoundTag("Biomes", biomeTag);
        if (player != null) {
            base.setCompoundTag("Player", player);
        }
    }

    public long getRandomSeed() {
        return this.randomSeed;
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getWorldTime() {
        return this.worldTime;
    }

    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    public CompoundTag getPlayerTag() {
        return this.playerTag;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setEvent(int id) {
        this.randomEventID = id;
    }

    public int getEvent() {
        return this.randomEventID;
    }

    public void setSpawnX(int x) {
        this.spawnX = x;
    }

    public void setSpawnY(int y) {
        this.spawnY = y;
    }

    public void setSpawnZ(int z) {
        this.spawnZ = z;
    }

    public void setWorldTime(long time) {
        this.worldTime = time;
    }

    public void setPlayerTag(CompoundTag tag) {
        this.playerTag = tag;
    }

    public void setSpawn(int x, int y, int z) {
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public int getConversionStatus() {
        return this.convertedStatus;
    }

    public int getMaxSections() {
        return this.maxSections;
    }

    public void setConversionStatus(int state) {
        this.convertedStatus = state;
    }

    public void setMaxSections(int sect) {
        this.maxSections = sect;
    }

    public long getLastTimePlayed() {
        return this.lastTimePlayed;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public WorldType getType() {
        return this.worldType;
    }

    public byte getGamemode() {
        return this.gamemode;
    }

    public void setDifficulty(byte value) {
        this.difficulty = value;
    }

    public void setGamemode(byte value) {
        this.gamemode = value;
    }

    public boolean getCheats() {
        return this.cheats;
    }

    public boolean amplified() {
        return this.amplified;
    }

    public boolean generateStructures() {
        return this.generateStructures;
    }

    public boolean isAiDisabled() {
        return this.aiDisabled;
    }

    public void setAiDisabled(boolean aiDisabled) {
        this.aiDisabled = aiDisabled;
    }

    public boolean isPhysicsDisabled() {
        return this.physicsDisabled;
    }

    public void setPhysicsDisabled(boolean physicsDisabled) {
        this.physicsDisabled = physicsDisabled;
    }

    public boolean isPermaDay() {
        return this.permaDay;
    }

    public void setPermaDay(boolean permaDay) {
        this.permaDay = permaDay;
    }

    public boolean[] getBiomes() {
        return this.biomes;
    }

    public Biome getSingleBiome() {
        return this.singleBiome;
    }

    public boolean getIsThundering() {
        return this.thundering;
    }

    public void setIsThundering(boolean state) {
        this.thundering = state;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int value) {
        this.thunderTime = value;
    }

    public boolean getIsRaining() {
        return this.raining;
    }

    public void setIsRaining(boolean state) {
        this.raining = state;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int value) {
        this.rainTime = value;
    }

    public void setWeather(boolean state) {
        this.weather = state;
    }

    public boolean getWeather() {
        return this.weather;
    }

    public void setDimension(int dim) {
        this.dimension = dim;
    }

    public String getVersion() {
        return this.saveVersion;
    }

    public void setVersion(String str) {
        this.saveVersion = str;
    }
}
