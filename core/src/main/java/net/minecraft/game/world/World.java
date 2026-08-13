package net.minecraft.game.world;

import ca.spottedleaf.starlight.StarlightEngine;
import com.mojang.nbt.tag.special.CompoundTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.Profiler;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.cold.Fjord;
import net.minecraft.game.world.biomes.providers.BiomeProvider;
import net.minecraft.game.world.biomes.temperate.Mountains;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkCache;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkProvider;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.dimension.Dimension;
import net.minecraft.game.world.dimension.Overworld;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.game.world.mapdata.MapStorage;
import net.minecraft.game.world.mobspawner.MobSpawner;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.game.world.pathfinder.PathFinder;
import net.minecraft.game.world.util.Explosion;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.game.world.util.NextTickListEntry;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.util.ProgressBar;
import net.minecraft.util.io.ThreadedFileIOBase;
import net.minecraft.util.math.MathHelper;

public class World implements WorldSource {
    public List<Entity> entities = new ArrayList<>();
    private List<Entity> entitiesToRemove = new ArrayList<>();
    public List<Entity> weatherEffects;
    public List<TileEntity> tileEntities = new ArrayList<>();
    private final List<TileEntity> pendingTileEntities = new ArrayList<>();
    public List<Player> players = new ArrayList<>();
    private final List<Entity> entitiesInBB = new ArrayList<>();
    private ArrayList<BoundingBox> boundingBoxes = new ArrayList<>();
    protected List<WorldListener> worldListeners = new ArrayList<>();
    public TreeSet<NextTickListEntry> tickList = new TreeSet<>();
    public Set<NextTickListEntry> tickSet = new HashSet<>();
    public Random rand = new Random();
    protected ChunkLoader chunkProvider;
    protected final SaveHandler saveHandler;
    public WorldInfo settings;
    public MapStorage mapStorage;
    public Dimension currDim;
    private LongOpenHashSet pollingPositions = new LongOpenHashSet();
    public final StarlightEngine blockLight = new StarlightEngine(false, this);
    public final StarlightEngine skyLight = new StarlightEngine(true, this);
    private long skyColor = 8961023L;
    private long fogColor = 12638463L;
    private long cloudColor = 16777215L;
    public long saveInterval = 256L;
    public int skylightSubtracted = 0;
    private int updateLCG = new Random().nextInt();
    private int soundCounter = this.rand.nextInt(12000);
    private final int MAGIC_NUMBER = 1013904223;
    protected int lastLightningBolt;
    public int lightningStrikeTime;
    public int simDistance = 12;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    public boolean editingBlocks = false;
    public boolean newlyGenerated = false;
    private boolean allPlayersSleeping;
    public boolean isOnline = false;
    protected boolean spawnHostiles;
    protected boolean spawnFriendlies;
    private boolean updateTE;
    public boolean bloodMoon = false;
    public boolean blueMoon = false;
    public boolean badSun = false;

    public World(SaveHandler saveHandler, String name, long seed, Dimension worldProvider) {
        this.saveHandler = saveHandler;
        this.mapStorage = new MapStorage(saveHandler);
        this.weatherEffects = new ArrayList<>();
        boolean[] biomes = new boolean[256];
        Arrays.fill(biomes, true);
        this.settings = new WorldInfo(seed, name, null);
        BiomeList.enabledBiomes = this.settings.biomes;
        if (this.getMaxSections() == 0) {
            this.setMaxSections(20);
        }

        this.chunkProvider = this.getChunkProvider();
        if (worldProvider != null) {
            this.currDim = worldProvider;
        } else {
            this.currDim = Dimension.getDimension(0);
        }

        this.currDim.init(this);
        this.calculateInitialSkylight();
        this.setWeather();
        WorldEdit.init();
    }

    public World(World world, Dimension dim) {
        this.entities = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
        this.tickList = new TreeSet<>();
        this.tickSet = new HashSet<>();
        this.tileEntities = new ArrayList<>();
        this.worldListeners = new ArrayList<>();
        this.players = new ArrayList<>();
        this.weatherEffects = new ArrayList<>();
        this.skylightSubtracted = 0;
        this.editingBlocks = false;
        this.rand = new Random();
        this.newlyGenerated = false;
        this.worldListeners = new ArrayList<>();
        this.boundingBoxes = new ArrayList<>();
        this.spawnHostiles = true;
        this.spawnFriendlies = true;
        this.pollingPositions = new LongOpenHashSet();
        this.soundCounter = this.rand.nextInt(12000);
        this.isOnline = false;
        this.saveHandler = world.saveHandler;
        this.settings = world.settings;
        if (this.getMaxSections() == 0) {
            this.setMaxSections(this.settings.getMaxSections());
        }

        this.mapStorage = new MapStorage(this.saveHandler);
        this.currDim = dim;
        this.currDim.init(this);
        this.chunkProvider = this.getChunkProvider();
        this.settings.setDimension(dim.dimension);
        this.setEvent(this.settings.getEvent());
        this.calculateInitialSkylight();
        this.setWeather();
        WorldEdit.init();
    }

    public World(SaveHandler save, String name, long seed, WorldSettings settings) {
        this(save, name, seed, settings, null);
    }

    public World(SaveHandler save, String name, long seed, WorldSettings settings, Dimension dim) {
        this.saveHandler = save;
        this.settings = save.loadWorldInfo();
        this.newlyGenerated = this.settings == null;
        this.mapStorage = new MapStorage(save);
        this.weatherEffects = new ArrayList<>();
        boolean isNew = false;
        if (this.settings == null) {
            this.settings = new WorldInfo(seed, name, settings);
            isNew = true;
        }

        if (this.getMaxSections() == 0) {
            this.setMaxSections(this.settings.getMaxSections());
        }

        BiomeList.enabledBiomes = this.settings.biomes;
        if (dim != null) {
            this.currDim = dim;
        } else {
            this.currDim = Dimension.getDimension(this.settings.getDimension());
        }

        this.currDim.init(this);
        this.chunkProvider = this.getChunkProvider();
        if (isNew) {
            this.getInitialSpawnPoint();
        }

        this.calculateInitialSkylight();
        this.setWeather();
        this.setEvent(this.settings.getEvent());
        WorldEdit.init();
    }

    public void setEvent(int id) {
        this.getSettings().setEvent(id);
        switch (id) {
            case 0:
                this.badSun = false;
                this.bloodMoon = false;
                this.blueMoon = false;
                break;
            case 1:
                this.badSun = true;
                break;
            case 2:
                this.bloodMoon = true;
                break;
            case 3:
                this.blueMoon = true;
        }
    }

    public ChunkLoader getChunkProvider() {
        FileLoader loader = this.saveHandler.getChunkLoader(this.currDim);
        return new ChunkProvider(this, loader, this.currDim.getChunkProvider());
    }

    protected void getInitialSpawnPoint() {
        Position pos = this.currDim.findSpawnPoint();
        this.settings.setSpawn(pos.x, pos.y, pos.z);
    }

    public void setSpawn() {
        Position pos = this.currDim.findRespawnLocation();
        this.settings.setSpawn(pos.x, pos.y, pos.z);
    }

    public int getTopTile(int x, int z) {
        int y = this.getSeaLevel();

        while (this.isSolid(x, y + 1, z)) {
            y++;
        }

        return this.getBlockId(x, y, z);
    }

    public int getTopTileSafe(int x, int z) {
        int y;
        for (y = this.getWorldHeight() - 6; this.isAir(x, y - 1, z); y--) {
            if (y <= 2) {
                this.setBlock(x, 1, z, BlockList.stone.id);
                return 2;
            }
        }

        return !this.isAir(x, y, z) ? this.getRandomHeight(x, z) : y;
    }

    public int getTopSolidTile(int x, int z) {
        Chunk chunk = this.getChunkAt(x, z);
        int y = this.getWorldHeight() - 1;
        x &= 15;
        z &= 15;

        while (y > 0) {
            int i5 = chunk.getBlockID(x, y, z);
            Material mat = i5 == 0 ? MaterialList.air : BlockList.blocks[i5].material;
            if (mat.isSolid() || mat.getIsLiquid()) {
                return y + 1;
            }

            y--;
        }

        return -1;
    }

    public int getRandomHeight(int x, int z) {
        int y = this.rand.nextInt(this.getWorldHeight());
        if (this.getBlock(x, y, z) == null) {
            for (int i = y; i > 0; i--) {
                if (this.getBlock(x, i - 1, z) != null) {
                    return i;
                }
            }
        } else if (this.getBlock(x, y, z) != null) {
            for (int i = y; i < this.getWorldHeight(); i++) {
                if (this.getBlock(x, i + 1, z) == null) {
                    return i;
                }
            }
        }

        return y;
    }

    public void spawnPlayer(Player playa) {
        try {
            CompoundTag tag = this.settings.getPlayerTag();
            if (tag != null) {
                playa.load(tag);
                this.settings.setPlayerTag(null);
            }

            this.spawnEntity(playa);
            if (tag != null && tag.hasKey("Riding")) {
                Entity mount = EntityManager.createEntityFromNBT(tag.getCompoundTag("Riding"), this);
                if (mount != null) {
                    mount.setPosition(playa.posX, playa.posY, playa.posZ);
                    this.spawnEntity(mount);
                    playa.mountEntity(mount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save(boolean status, ProgressBar progress) {
        if (this.chunkProvider.canSave()) {
            if (progress != null) {
                progress.displayProgressMessage(Translator.getInstance().translateKey("gui.savelvl"));
            }

            this.saveData();
            if (progress != null) {
                progress.displayLoadingString(Translator.getInstance().translateKey("gui.savechunks"));
            }

            this.chunkProvider.saveChunks(status, progress);
        }
    }

    public void forceSave(ProgressBar progress) {
        this.save(true, progress);

        try {
            ThreadedFileIOBase.threadedIOInstance.waitForFinish();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void saveData() {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfo(this.settings, this.players);
        this.mapStorage.saveAllData();
    }

    public void loadData() {
        this.saveHandler.loadWorldInfo();
    }

    public boolean saveAll() {
        if (!this.chunkProvider.canSave()) {
            return true;
        }

        this.saveData();
        return this.chunkProvider.saveChunks(false, null);
    }

    @Override
    public int getBlockId(int xCoord, int yCoord, int zCoord) {
        return xCoord < -2147483647 || zCoord < -2147483647 || xCoord >= Integer.MAX_VALUE || zCoord > Integer.MAX_VALUE
            ? 0
            : (yCoord < 0 ? 0 : (yCoord >= this.getWorldHeight() ? 0 : this.getChunk(xCoord >> 4, zCoord >> 4).getBlockID(xCoord & 15, yCoord, zCoord & 15)));
    }

    public boolean isAir(int x, int y, int z) {
        return this.getBlockId(x, y, z) == 0;
    }

    public boolean blockExists(int xCoord, int yCoord, int zCoord) {
        return xCoord >= -2147483647
            && zCoord >= -2147483647
            && xCoord < Integer.MAX_VALUE
            && zCoord <= Integer.MAX_VALUE
            && yCoord >= 0
            && yCoord < this.getWorldHeight()
            && this.chunkExists(xCoord >> 4, zCoord >> 4);
    }

    public boolean chunksInRadiusExist(int x, int y, int z, int rad) {
        return this.chunksExist(x - rad, y - rad, z - rad, x + rad, y + rad, z + rad);
    }

    public boolean chunksExist(int xCoordNegative, int yCoordNegative, int zCoordNegative, int xCoordPositive, int yCoordPositive, int zCoordPositive) {
        if (yCoordPositive >= 0 && yCoordNegative < this.getWorldHeight()) {
            xCoordNegative >>= 4;
            yCoordNegative >>= 4;
            zCoordNegative >>= 4;
            xCoordPositive >>= 4;
            yCoordPositive >>= 4;
            zCoordPositive >>= 4;

            for (int x = xCoordNegative; x <= xCoordPositive; x++) {
                for (int z = zCoordNegative; z <= zCoordPositive; z++) {
                    if (!this.chunkExists(x, z)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean chunkExists(int xCoord, int zCoord) {
        return this.chunkProvider.chunkExists(xCoord, zCoord);
    }

    public Chunk getChunkAt(int x, int z) {
        return this.getChunk(x >> 4, z >> 4);
    }

    public Chunk getChunk(int x, int z) {
        return this.chunkProvider.generateChunk(x, z);
    }

    public boolean setBlockAndMetadata(int xCoord, int yCoord, int zCoord, int blockid, int metadataValue) {
        if (xCoord < -2147483647 || zCoord < -2147483647 || xCoord >= Integer.MAX_VALUE || zCoord > Integer.MAX_VALUE) {
            return false;
        }

        if (yCoord < 0) {
            return false;
        }

        if (yCoord >= this.getWorldHeight()) {
            return false;
        }

        Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
        return chunk.setBlockAndState(xCoord & 15, yCoord, zCoord & 15, blockid, metadataValue);
    }

    public boolean setBlock(int xCoord, int yCoord, int zCoord, int blockid) {
        if (xCoord < -2147483647 || zCoord < -2147483647 || xCoord >= Integer.MAX_VALUE || zCoord > Integer.MAX_VALUE) {
            return false;
        }

        if (yCoord < 0) {
            return false;
        }

        if (yCoord >= this.getWorldHeight()) {
            return false;
        }

        Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
        return chunk.setBlockID(xCoord & 15, yCoord, zCoord & 15, blockid);
    }

    @Override
    public Material getBlockMaterial(int x, int y, int z) {
        int id = this.getBlockId(x, y, z);
        return id != 0 && BlockList.blocks[id] != null ? BlockList.blocks[id].material : MaterialList.air;
    }

    @Override
    public int getBlockMetadata(int xCoord, int yCoord, int zCoord) {
        if (xCoord < -2147483647 || zCoord < -2147483647 || xCoord >= Integer.MAX_VALUE || zCoord > Integer.MAX_VALUE) {
            return 0;
        }

        if (yCoord < 0) {
            return 0;
        }

        if (yCoord >= this.getWorldHeight()) {
            return 0;
        }

        Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
        return chunk.getBlockMetadata(xCoord & 15, yCoord, zCoord & 15);
    }

    public void setBlockMetadataWithNotify(int xCoord, int yCoord, int zCoord, int metadataValue) {
        this.setBlockMetadata(xCoord, yCoord, zCoord, metadataValue);
        this.updateBlocks(xCoord, yCoord, zCoord, this.getBlockId(xCoord, yCoord, zCoord));
    }

    public boolean setBlockMetadata(int xCoord, int yCoord, int zCoord, int metadataValue) {
        if (xCoord < -2147483647 || zCoord < -2147483647 || xCoord >= Integer.MAX_VALUE || zCoord > Integer.MAX_VALUE) {
            return false;
        }

        if (yCoord < 0) {
            return false;
        }

        if (yCoord >= this.getWorldHeight()) {
            return false;
        }

        Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
        chunk.setBlockMetadata(xCoord & 15, yCoord, zCoord & 15, metadataValue);
        return true;
    }

    public boolean setBlockWithNotify(int xCoord, int yCoord, int zCoord, int blockid) {
        if (this.setBlock(xCoord, yCoord, zCoord, blockid)) {
            this.updateBlocks(xCoord, yCoord, zCoord, blockid);
            return true;
        } else {
            return false;
        }
    }

    public boolean setBlockAndMetadataWithNotify(int xCoord, int yCoord, int zCoord, int blockid, int metadataValue) {
        if (this.setBlockAndMetadata(xCoord, yCoord, zCoord, blockid, metadataValue)) {
            this.updateBlocks(xCoord, yCoord, zCoord, blockid);
            return true;
        } else {
            return false;
        }
    }

    public void markForUpdate(int x, int y, int z) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.changeBlocks(x, y, z);
        }
    }

    protected void updateBlocks(int x, int y, int z, int id) {
        this.markForUpdate(x, y, z);
        this.updateNeighbors(x, y, z, id);
    }

    public void markBlocksDirty(int x1, int y1, int z1, int x2, int y2, int z2) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.markBlocksDirty(x1, y1, z1, x2, y2, z2);
        }
    }

    public void updateNeighbors(int xCoord, int yCoord, int zCoord, int i4) {
        this.neighborChanged(xCoord - 1, yCoord, zCoord, i4);
        this.neighborChanged(xCoord + 1, yCoord, zCoord, i4);
        this.neighborChanged(xCoord, yCoord - 1, zCoord, i4);
        this.neighborChanged(xCoord, yCoord + 1, zCoord, i4);
        this.neighborChanged(xCoord, yCoord, zCoord - 1, i4);
        this.neighborChanged(xCoord, yCoord, zCoord + 1, i4);
    }

    public void neighborChanged(int x, int y, int z, int id) {
        if (!this.editingBlocks && !this.isOnline) {
            Block block = BlockList.blocks[this.getBlockId(x, y, z)];
            if (block != null) {
                block.onNeighborChange(this, x, y, z, id);
            }
        }
    }

    public boolean isBlockExposed(int xCoord, int yCoord, int zCoord) {
        return this.getChunk(xCoord >> 4, zCoord >> 4).isBlockExposed(xCoord & 15, yCoord, zCoord & 15);
    }

    public int getBlockBrightness(int xCoord, int yCoord, int zCoord) {
        return this.getBlockBrightness(xCoord, yCoord, zCoord, true);
    }

    public int getBlockBrightness(int xCoord, int yCoord, int zCoord, boolean updateNeighbors) {
        if (xCoord >= -2147483647 && zCoord >= -2147483647 && xCoord < Integer.MAX_VALUE && zCoord <= Integer.MAX_VALUE) {
            if (updateNeighbors) {
                int id = this.getBlockId(xCoord, yCoord, zCoord);
                if (BlockList.blocks[id] instanceof Slab || id == BlockList.tilledField.id || id == BlockList.pathBlock.id) {
                    int above = this.getBlockBrightness(xCoord, yCoord + 1, zCoord, false);
                    int left = this.getBlockBrightness(xCoord + 1, yCoord, zCoord, false);
                    int right = this.getBlockBrightness(xCoord - 1, yCoord, zCoord, false);
                    int front = this.getBlockBrightness(xCoord, yCoord, zCoord + 1, false);
                    int back = this.getBlockBrightness(xCoord, yCoord, zCoord - 1, false);
                    if (left > above) {
                        above = left;
                    }

                    if (right > above) {
                        above = right;
                    }

                    if (front > above) {
                        above = front;
                    }

                    if (back > above) {
                        above = back;
                    }

                    return above;
                }
            }

            if (yCoord < 0) {
                return 0;
            }

            if (yCoord >= this.getWorldHeight()) {
                int id = 15 - this.skylightSubtracted;
                if (id < 0) {
                    id = 0;
                }

                return id;
            } else {
                Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
                return chunk.getBlockLightValue(xCoord & 15, yCoord, zCoord & 15, this.skylightSubtracted);
            }
        } else {
            return 15;
        }
    }

    public int getHeightmap(int xCoord, int zCoord) {
        if (xCoord >= -2147483647 && zCoord >= -2147483647 && xCoord < Integer.MAX_VALUE && zCoord <= Integer.MAX_VALUE) {
            if (!this.chunkExists(xCoord >> 4, zCoord >> 4)) {
                return 0;
            }

            Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
            return chunk.getHeightmap(xCoord & 15, zCoord & 15);
        } else {
            return 0;
        }
    }

    public int getBrightness(LightLayer light, int xCoord, int yCoord, int zCoord) {
        if (yCoord >= 0 && yCoord < this.getWorldHeight()) {
            int x = xCoord >> 4;
            int z = zCoord >> 4;
            if (!this.chunkExists(x, z)) {
                return 0;
            }

            Chunk chunk = this.getChunk(x, z);
            return chunk.getLightValue(light, xCoord & 15, yCoord, zCoord & 15);
        } else {
            return light.defaultLightValue;
        }
    }

    @Override
    public float getBrightness(int x, int y, int z) {
        return this.currDim.lightValues[this.getBlockBrightness(x, y, z)];
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    public Player getClosestPlayer(Entity entity, double dist) {
        return this.getClosestPlayer(entity.posX, entity.posY, entity.posZ, dist);
    }

    public Player getClosestPlayer(double x, double y, double z, double dist) {
        double maxDist = -1.0;
        Player targetPlayer = null;

        for (Player player : this.players) {
            double distToPlayer = player.getDistanceSq(x, y, z);
            if ((dist < 0.0 || distToPlayer < dist * dist) && (maxDist == -1.0 || distToPlayer < maxDist)) {
                maxDist = distToPlayer;
                targetPlayer = player;
            }
        }

        return targetPlayer;
    }

    public TargetingSystem rayTraceBlocks(Vector src, Vector target) {
        return this.rayTrace(src, target, false);
    }

    public TargetingSystem rayTrace(Vector src, Vector target, boolean countFluids) {
        if (Double.isNaN(src.xCoord) || Double.isNaN(src.yCoord) || Double.isNaN(src.zCoord)) {
            return null;
        }

        if (!Double.isNaN(target.xCoord) && !Double.isNaN(target.yCoord) && !Double.isNaN(target.zCoord)) {
            int targetX = MathHelper.floor_double(target.xCoord);
            int targetY = MathHelper.floor_double(target.yCoord);
            int targetZ = MathHelper.floor_double(target.zCoord);
            int srcX = MathHelper.floor_double(src.xCoord);
            int srcY = MathHelper.floor_double(src.yCoord);
            int srcZ = MathHelper.floor_double(src.zCoord);
            int id = this.getBlockId(srcX, srcY, srcZ);
            int meta = this.getBlockMetadata(srcX, srcY, srcZ);
            Block block = BlockList.blocks[id];
            if (id > 0 && block.canCollideWith(meta, countFluids)) {
                TargetingSystem hit = block.rayTrace(this, srcX, srcY, srcZ, src, target);
                if (hit != null) {
                    return hit;
                }
            }

            id = 200;

            while (id-- >= 0) {
                if (Double.isNaN(src.xCoord) || Double.isNaN(src.yCoord) || Double.isNaN(src.zCoord)) {
                    return null;
                }

                if (srcX == targetX && srcY == targetY && srcZ == targetZ) {
                    return null;
                }

                boolean hitX = true;
                boolean hitY = true;
                boolean hitZ = true;
                double offX = 999.0;
                double offY = 999.0;
                double offZ = 999.0;
                if (targetX > srcX) {
                    offX = srcX + 1.0;
                } else if (targetX < srcX) {
                    offX = srcX + 0.0;
                } else {
                    hitX = false;
                }

                if (targetY > srcY) {
                    offY = srcY + 1.0;
                } else if (targetY < srcY) {
                    offY = srcY + 0.0;
                } else {
                    hitY = false;
                }

                if (targetZ > srcZ) {
                    offZ = srcZ + 1.0;
                } else if (targetZ < srcZ) {
                    offZ = srcZ + 0.0;
                } else {
                    hitZ = false;
                }

                double locX = 999.0;
                double locY = 999.0;
                double locZ = 999.0;
                double distX = target.xCoord - src.xCoord;
                double distY = target.yCoord - src.yCoord;
                double distZ = target.zCoord - src.zCoord;
                if (hitX) {
                    locX = (offX - src.xCoord) / distX;
                }

                if (hitY) {
                    locY = (offY - src.yCoord) / distY;
                }

                if (hitZ) {
                    locZ = (offZ - src.zCoord) / distZ;
                }

                byte face;
                if (locX < locY && locX < locZ) {
                    if (targetX > srcX) {
                        face = 4;
                    } else {
                        face = 5;
                    }

                    src.xCoord = offX;
                    src.yCoord += distY * locX;
                    src.zCoord += distZ * locX;
                } else if (locY < locZ) {
                    if (targetY > srcY) {
                        face = 0;
                    } else {
                        face = 1;
                    }

                    src.xCoord += distX * locY;
                    src.yCoord = offY;
                    src.zCoord += distZ * locY;
                } else {
                    if (targetZ > srcZ) {
                        face = 2;
                    } else {
                        face = 3;
                    }

                    src.xCoord += distX * locZ;
                    src.yCoord += distY * locZ;
                    src.zCoord = offZ;
                }

                Vector hitPos = new Vector(src.xCoord, src.yCoord, src.zCoord);
                srcX = (int)(hitPos.xCoord = MathHelper.floor_double(src.xCoord));
                if (face == 5) {
                    srcX--;
                    hitPos.xCoord++;
                }

                srcY = (int)(hitPos.yCoord = MathHelper.floor_double(src.yCoord));
                if (face == 1) {
                    srcY--;
                    hitPos.yCoord++;
                }

                srcZ = (int)(hitPos.zCoord = MathHelper.floor_double(src.zCoord));
                if (face == 3) {
                    srcZ--;
                    hitPos.zCoord++;
                }

                int eyed = this.getBlockId(srcX, srcY, srcZ);
                int data = this.getBlockMetadata(srcX, srcY, srcZ);
                Block blockHit = BlockList.blocks[eyed];
                if (eyed > 0 && blockHit.canCollideWith(data, countFluids)) {
                    TargetingSystem hit = blockHit.rayTrace(this, srcX, srcY, srcZ, src, target);
                    if (hit != null) {
                        return hit;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void playEntitySound(Entity entity, String soundName, float volume, float pitch) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.playMobSound(soundName, entity.entityId, entity.posX, entity.posY - entity.yOffset, entity.posZ, volume, pitch);
        }
    }

    public void playBlockSound(double x, double y, double z, String snd, float vol, float pitch) {
        try {
            for (WorldListener worldAccess : this.worldListeners) {
                worldAccess.playBlockSound(snd, x, y, z, vol, pitch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playSoundEffect(double xCoord, double yCoord, double zCoord, String soundName, float volume, float pitch) {
        try {
            for (WorldListener worldAccess : this.worldListeners) {
                worldAccess.playSound(soundName, xCoord, yCoord, zCoord, volume, pitch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playRandomAmbience(String soundName, float volume, float pitch) {
        try {
            for (WorldListener worldAccess : this.worldListeners) {
                worldAccess.playAmbienceSound(soundName, volume, pitch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void spawnParticle(
        ParticleType particle, double xCoordBlock, double yCoordBlock, double zCoordBlock, double xPosition, double yPosition, double zPosition
    ) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.spawnParticle(particle, xCoordBlock, yCoordBlock, zCoordBlock, xPosition, yPosition, zPosition);
        }
    }

    public boolean spawnEntity(Entity entity) {
        int x = MathHelper.floor_double(entity.posX / 16.0);
        int z = MathHelper.floor_double(entity.posZ / 16.0);
        boolean isPlayer = entity instanceof Player;
        if (!isPlayer && !this.chunkExists(x, z) && !entity.boss) {
            return false;
        }

        if (isPlayer) {
            Player player = (Player)entity;
            this.players.add(player);
            this.wakePlayers();
        }

        this.getChunk(x, z).addEntity(entity);
        this.entities.add(entity);
        this.obtainEntitySkin(entity);
        return true;
    }

    public void obtainEntitySkin(Entity entity) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.obtainEntitySkin(entity);
        }
    }

    protected void releaseEntitySkin(Entity entity) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.releaseEntitySkin(entity);
        }
    }

    public void addEntityToHashmap(Entity e) {
    }

    public void removeEntityFromHashmap(Entity e) {
    }

    public void kill(Entity entity) {
        if (entity.rider != null) {
            entity.rider.mountEntity(null);
        }

        if (entity.mount != null) {
            entity.mountEntity(null);
        }

        entity.setDead();
        if (entity instanceof Player) {
            this.players.remove(entity);
            this.wakePlayers();
        }
    }

    public void addListener(WorldListener listener) {
        this.worldListeners.add(listener);
    }

    public void removeListener(WorldListener listener) {
        this.worldListeners.remove(listener);
    }

    public WorldListener getListener(int i) {
        return this.worldListeners.get(i);
    }

    public List<BoundingBox> getBoundingBoxes(Entity entity, BoundingBox aabb) {
        this.boundingBoxes.clear();
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY - 1; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = BlockList.blocks[this.getBlockId(x, y, z)];
                    if (block != null) {
                        block.getCollisionBox(this, x, y, z, aabb, this.boundingBoxes);
                    }
                }
            }
        }

        double bbSize = 0.25;

        for (Entity entity1 : this.getEntities(entity, aabb.expand(bbSize, bbSize, bbSize))) {
            BoundingBox boundingBox = entity1.getBoundingBox();
            if (boundingBox != null) {
                this.boundingBoxes.add(boundingBox);
            }

            boundingBox = entity.getCollisionBox(entity1);
            if (boundingBox != null) {
                this.boundingBoxes.add(boundingBox);
            }
        }

        return this.boundingBoxes;
    }

    public boolean containsFire(int x, int y, int z, int side) {
        boolean condition = false;
        if (this.isOnline) {
            return false;
        }

        if (side == 0) {
            y--;
        }

        if (side == 1) {
            y++;
        }

        if (side == 2) {
            z--;
        }

        if (side == 3) {
            z++;
        }

        if (side == 4) {
            x--;
        }

        if (side == 5) {
            x++;
        }

        if (this.getBlockId(x, y, z) == BlockList.fire.id || this.getBlockId(x, y, z) == BlockList.hellfire.id) {
            condition = true;
            this.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            this.setBlockWithNotify(x, y, z, 0);
        }

        return condition;
    }

    public int getSkyBrightness(float partialTicks) {
        return this.currDim.getSkyBrightness(partialTicks);
    }

    public float getSunBrightness(float partialTicks) {
        return this.currDim.getSunBrightness(partialTicks);
    }

    public void setBaseSkyColor(long l) {
        this.skyColor = l;
    }

    public long getBaseSkyColor() {
        return this.skyColor;
    }

    public void setBaseFogColor(long l) {
        this.fogColor = l;
    }

    public long getBaseFogColor() {
        return this.fogColor;
    }

    public Vector getSkyColor(Entity entity, float partialTicks, int sky) {
        return this.currDim.getSkyColor(entity, partialTicks, sky);
    }

    public float getSunAngle(float partialTicks) {
        return this.currDim.getSunAngle(this.settings.getWorldTime() % (long)this.settings.dayLength, this.settings.dayLength, partialTicks);
    }

    public float getSunAngleRadians(float angl) {
        return this.getSunAngle(angl) * (float) Math.PI * 2.0F;
    }

    public Vector getCloudColor(float angl) {
        float skyAngle = this.getSunAngle(angl);
        float realAngle = MathHelper.cos(skyAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        realAngle = MathHelper.clamp(realAngle, 0.0F, 1.0F);
        float red = (float)(this.cloudColor >> 16 & 255L) / 255.0F;
        float green = (float)(this.cloudColor >> 8 & 255L) / 255.0F;
        float blue = (float)(this.cloudColor & 255L) / 255.0F;
        float rainStr = this.getRainStrength(angl);
        if (rainStr > 0.0F) {
            float opacity1 = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.6F;
            float opacity = 1.0F - rainStr * 0.95F;
            red = red * opacity + opacity1 * (1.0F - opacity);
            green = green * opacity + opacity1 * (1.0F - opacity);
            blue = blue * opacity + opacity1 * (1.0F - opacity);
        }

        red *= realAngle * 0.9F + 0.1F;
        green *= realAngle * 0.9F + 0.1F;
        blue *= realAngle * 0.85F + 0.15F;
        float opacity1 = this.getThunderStrength(angl);
        if (opacity1 > 0.0F) {
            float opacity = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.2F;
            float alpha = 1.0F - opacity1 * 0.95F;
            red = red * alpha + opacity * (1.0F - alpha);
            green = green * alpha + opacity * (1.0F - alpha);
            blue = blue * alpha + opacity * (1.0F - alpha);
        }

        return Vector.createVector(red, green, blue);
    }

    public Vector getFogColor(float partialTicks) {
        return this.currDim.getFogColor(partialTicks);
    }

    public float getStarBrightness(float partialTicks) {
        return this.currDim.getStarBrightness(partialTicks);
    }

    public void forceTick(int x, int y, int z, int id) {
        NextTickListEntry tickEntry = new NextTickListEntry(x, y, z, id);
        byte chunkOffset = 8;
        if (this.chunksExist(x - chunkOffset, y - chunkOffset, z - chunkOffset, x + chunkOffset, y + chunkOffset, z + chunkOffset)) {
            if (id > 0) {
                int tickRate = BlockList.blocks[id].tickRate();
                // currDim, not settings.getDimension(): on a dedicated server every dimension
                // loads its own WorldInfo from the shared level.dat, where "Dimension" comes
                // from the player tag and is meaningless. currDim is always right per world.
                if (id == BlockList.lavaMoving.id && this.currDim.dimension == 1) {
                    tickRate = 5;
                }

                tickEntry.setDelay(tickRate + this.getWorldTime());
            }

            if (!this.tickSet.contains(tickEntry)) {
                this.tickSet.add(tickEntry);
                this.tickList.add(tickEntry);
            }
        }
    }

    public void tickEntities() {
        Profiler.startSection("entities");
        Profiler.startSection("global");

        for (int index = 0; index < this.weatherEffects.size(); index++) {
            Entity entity = this.weatherEffects.get(index);
            entity.onTick();
            if (entity.isDead) {
                this.weatherEffects.remove(index--);
                this.releaseEntitySkin(entity);
            }
        }

        Profiler.endStartSection("remove");
        this.entities.removeAll(this.entitiesToRemove);

        for (int var8 = 0; var8 < this.entitiesToRemove.size(); var8++) {
            Entity entity = this.entitiesToRemove.get(var8);
            int x = entity.chunkX;
            int z = entity.chunkZ;
            if (entity.addedToChunk && this.chunkExists(x, z)) {
                this.getChunk(x, z).removeEntity(entity);
            }

            this.releaseEntitySkin(this.entitiesToRemove.get(var8));
        }

        this.entitiesToRemove.clear();
        Profiler.endStartSection("regular");

        for (int var9 = 0; var9 < this.entities.size(); var9++) {
            Entity entity = this.entities.get(var9);
            if (entity.mount != null) {
                if (!entity.mount.isDead && entity.mount.rider == entity) {
                    continue;
                }

                entity.mount.rider = null;
                entity.mount = null;
            }

            Player player = this.getClosestPlayer(entity, 16.0 * this.simDistance);
            if (!entity.isDead && (entity.alwaysTick() || player != null)) {
                this.tickEntity(entity);
            }

            entity.despawnTick();
            Profiler.startSection("remove");
            if (entity.isDead) {
                int x = entity.chunkX;
                int z = entity.chunkZ;
                if (entity.addedToChunk && this.chunkExists(x, z)) {
                    this.getChunk(x, z).removeEntity(entity);
                }

                this.entities.remove(var9--);
                this.releaseEntitySkin(entity);
            }

            Profiler.endSection();
        }

        Profiler.endStartSection("tileEntities");
        this.updateTE = true;

        for (int i = 0; i < this.tileEntities.size(); i++) {
            TileEntity tent = this.tileEntities.get(i);
            if (!tent.isInvalid()) {
                tent.tick();
            }

            if (tent.isInvalid()) {
                this.tileEntities.remove(tent);
                Chunk chunk = this.getChunk(tent.x >> 4, tent.z >> 4);
                if (chunk != null) {
                    chunk.removeTileEntity(tent.x & 15, tent.y, tent.z & 15);
                }

                i--;
            }
        }

        this.updateTE = false;
        Profiler.endStartSection("pendingTileEntities");
        if (!this.pendingTileEntities.isEmpty()) {
            for (TileEntity tileEntity8 : this.pendingTileEntities) {
                if (!tileEntity8.isInvalid()) {
                    Chunk chunk9 = this.getChunk(tileEntity8.x >> 4, tileEntity8.z >> 4);
                    if (chunk9 != null) {
                        chunk9.setTileEntity(tileEntity8.x & 15, tileEntity8.y, tileEntity8.z & 15, tileEntity8);
                    }

                    this.markForUpdate(tileEntity8.x, tileEntity8.y, tileEntity8.z);
                }
            }

            this.pendingTileEntities.clear();
        }

        Profiler.endSection();
        Profiler.endSection();
    }

    public void tickEntity(Entity entity) {
        this.tickEntity(entity, true);
    }

    public void tickEntity(Entity entity, boolean shouldTick) {
        int x = MathHelper.floor_double(entity.posX);
        int z = MathHelper.floor_double(entity.posZ);
        byte range = 32;
        if (!shouldTick || this.chunksExist(x - range, 0, z - range, x + range, this.getWorldHeight(), z + range)) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevYaw = entity.yaw;
            entity.prevPitch = entity.pitch;
            if (shouldTick && entity.addedToChunk) {
                if (entity.mount != null) {
                    entity.updateRidden();
                } else {
                    entity.onTick();
                }
            }

            Profiler.startSection("chunkCheck");
            if (Double.isNaN(entity.posX) || Double.isInfinite(entity.posX)) {
                entity.posX = entity.lastTickPosX;
            }

            if (Double.isNaN(entity.posY) || Double.isInfinite(entity.posY)) {
                entity.posY = entity.lastTickPosY;
            }

            if (Double.isNaN(entity.posZ) || Double.isInfinite(entity.posZ)) {
                entity.posZ = entity.lastTickPosZ;
            }

            if (Double.isNaN(entity.pitch) || Double.isInfinite(entity.pitch)) {
                entity.pitch = entity.prevPitch;
            }

            if (Double.isNaN(entity.yaw) || Double.isInfinite(entity.yaw)) {
                entity.yaw = entity.prevYaw;
            }

            int posX = MathHelper.floor_double(entity.posX / 16.0);
            int posY = MathHelper.floor_double(entity.posY / 16.0);
            int posZ = MathHelper.floor_double(entity.posZ / 16.0);
            if (!entity.addedToChunk || entity.chunkX != posX || entity.chunkY != posY || entity.chunkZ != posZ) {
                if (entity.addedToChunk && this.chunkExists(entity.chunkX, entity.chunkZ)) {
                    this.getChunk(entity.chunkX, entity.chunkZ).removeEntity(entity, entity.chunkY);
                }

                if (this.chunkExists(posX, posZ)) {
                    entity.addedToChunk = true;
                    this.getChunk(posX, posZ).addEntity(entity);
                } else {
                    entity.addedToChunk = false;
                }
            }

            Profiler.endSection();
            if (shouldTick && entity.addedToChunk && entity.rider != null) {
                if (!entity.rider.isDead && entity.rider.mount == entity) {
                    this.tickEntity(entity.rider);
                } else {
                    entity.rider.mount = null;
                    entity.rider = null;
                }
            }
        }
    }

    public boolean isClear(BoundingBox aabb) {
        for (Entity entity : this.getEntities(null, aabb)) {
            if (!entity.isDead && entity.preventSpawning) {
                return false;
            }
        }

        return true;
    }

    public boolean containsLiquid(BoundingBox aabb) {
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);
        if (aabb.minX < 0.0) {
            minX--;
        }

        if (aabb.minY < 0.0) {
            minY--;
        }

        if (aabb.minZ < 0.0) {
            minZ--;
        }

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = BlockList.blocks[this.getBlockId(x, y, z)];
                    if (block != null && block.material.getIsLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean containsFire(BoundingBox aabb) {
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    int id = this.getBlockId(x, y, z);
                    if (id == BlockList.hellfire.id || id == BlockList.fire.id || id == BlockList.lavaMoving.id || id == BlockList.lavaStill.id) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean handleWaterMovement(BoundingBox aabb, Material material, Entity entity) {
        aabb = aabb.shrink(0.001, 0.001, 0.001);
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);
        boolean inWater = false;
        Vector pos = Vector.createVector(0.0, 0.0, 0.0);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = BlockList.blocks[this.getBlockId(x, y, z)];
                    if (block != null && block.material == material) {
                        double height = y + 1 - LiquidBlock.getHeight(this.getBlockMetadata(x, y, z));
                        if (maxY >= height) {
                            inWater = true;
                            block.multiplyEntityVelocity(this, x, y, z, entity, pos);
                        }
                    }
                }
            }
        }

        if (pos.lengthVector() > 0.0) {
            pos = pos.normalize();
            double multiply = 0.004;
            entity.motionX = entity.motionX + pos.xCoord * multiply;
            entity.motionY = entity.motionY + pos.yCoord * multiply;
            entity.motionZ = entity.motionZ + pos.zCoord * multiply;
        }

        return inWater;
    }

    public boolean containsMaterial(BoundingBox aabb, Material material) {
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = BlockList.blocks[this.getBlockId(x, y, z)];
                    if (block != null && block.material == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Explosion createExplosion(Entity entity, double x, double t, double z, float rad) {
        Explosion explosion = new Explosion(this, entity, x, t, z, rad);
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public Explosion createMagicExplosion(Entity entity, double x, double y, double z, float rad, EffectData effect, int damage) {
        Explosion explosion = new Explosion(this, entity, x, y, z, rad);
        explosion.isMagic = true;
        explosion.effect = effect;
        explosion.damage = damage;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public Explosion createFireExplosion(Entity entity, double x, double t, double z, float rad) {
        Explosion explosion = new Explosion(this, entity, x, t, z, rad);
        explosion.isFlaming = true;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public Explosion createFrostExplosion(Entity entity, double x, double y, double z, float rad) {
        Explosion explosion = new Explosion(this, entity, x, y, z, rad);
        explosion.isFrost = true;
        explosion.destroyBlocks = false;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public Explosion createSmallExplosion(Entity entity, double x, double y, double z, float rad) {
        Explosion explosion = new Explosion(this, entity, x, y, z, rad);
        explosion.destroyBlocks = false;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public Explosion createWindExplosion(Entity entity, double x, double y, double z, float rad) {
        Explosion explosion = new Explosion(this, entity, x, y, z, rad);
        explosion.isWind = true;
        explosion.destroyBlocks = false;
        explosion.explode();
        explosion.addParticles(true);
        return explosion;
    }

    public void setAllowedSpawnTypes(boolean hostiles, boolean friendlies) {
        this.spawnHostiles = hostiles;
        this.spawnFriendlies = friendlies;
    }

    public void extinguishFire(Player thePlayer, int x, int y, int z, int side) {
        if (side == 0) {
            y--;
        }

        if (side == 1) {
            y++;
        }

        if (side == 2) {
            z--;
        }

        if (side == 3) {
            z++;
        }

        if (side == 4) {
            x--;
        }

        if (side == 5) {
            x++;
        }

        if (this.getBlockId(x, y, z) == BlockList.fire.id || this.getBlockId(x, y, z) == BlockList.hellfire.id) {
            this.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            this.setBlockWithNotify(x, y, z, 0);
        }
    }

    public String getLoadedEntities() {
        return "All: " + this.entities.size();
    }

    public String getProviderName() {
        return this.chunkProvider.loaderName();
    }

    @Override
    public TileEntity getTileEntity(int xCoord, int yCoord, int zCoord) {
        Chunk chunk = this.getChunk(xCoord >> 4, zCoord >> 4);
        return chunk != null ? chunk.getTileEntity(xCoord & 15, yCoord, zCoord & 15) : null;
    }

    public void setBlockTileEntity(int xCoord, int yCoord, int zCoord, TileEntity tileEntity) {
        if (!tileEntity.isInvalid()) {
            if (this.updateTE) {
                tileEntity.x = xCoord;
                tileEntity.y = yCoord;
                tileEntity.z = zCoord;
                this.pendingTileEntities.add(tileEntity);
            } else {
                this.tileEntities.add(tileEntity);
                Chunk chonk = this.getChunk(xCoord >> 4, zCoord >> 4);
                if (chonk != null) {
                    chonk.setTileEntity(xCoord & 15, yCoord, zCoord & 15, tileEntity);
                }
            }
        }
    }

    public void removeBlockTileEntity(int xCoord, int yCoord, int zCoord) {
        TileEntity te = this.getTileEntity(xCoord, yCoord, zCoord);
        if (te != null && this.updateTE) {
            te.invalidate();
        } else {
            if (te != null) {
                this.tileEntities.remove(te);
            }

            Chunk chonk = this.getChunk(xCoord >> 4, zCoord >> 4);
            if (chonk != null) {
                chonk.removeTileEntity(xCoord & 15, yCoord, zCoord & 15);
            }
        }
    }

    @Override
    public boolean isBlockFull(int xCoord, int yCoord, int zCoord) {
        Block block = BlockList.blocks[this.getBlockId(xCoord, yCoord, zCoord)];
        return block != null && block.fullBlock();
    }

    public void calculateInitialSkylight() {
        int light = this.getSkyBrightness(1.0F);
        if (light != this.skylightSubtracted) {
            this.skylightSubtracted = light;
        }
    }

    private void updateEvents() {
        if (this.currDim instanceof Overworld) {
            int diff = this.settings.getDifficulty();
            if (diff <= 0) {
                diff = 1;
            } else if (diff == 3) {
                diff = 4;
            }

            int chance = 200 / diff;
            if (this.rand.nextInt(chance) == 0
                && (float)this.getWorldTime() % this.settings.dayLength == this.settings.dayLength - this.settings.dayLength / 16.0F
                && this.settings.getEvent() == 0) {
                this.badSun = true;
                this.settings.setEvent(1);
                int i = 0;
                if (i < this.players.size()) {
                    this.players.get(i).addChatMessage(Translator.getInstance().translateKey("event.sun"));
                }
            } else if (this.badSun
                && (float)this.getWorldTime() % this.settings.dayLength >= this.settings.dayLength / 2.0F + this.settings.dayLength / 24.0F
                && (float)this.getWorldTime() % this.settings.dayLength < this.settings.dayLength - this.settings.dayLength / 16.0F) {
                this.badSun = false;
                this.settings.setEvent(0);
            }

            if (this.bloodMoon
                && (float)this.getWorldTime() % this.settings.dayLength >= 0.0F
                && (float)this.getWorldTime() % this.settings.dayLength < this.settings.dayLength / 2.0F) {
                this.bloodMoon = false;
                this.settings.setEvent(0);
            } else if (this.rand.nextInt(chance) == 0
                && this.settings.getEvent() == 0
                && (float)this.getWorldTime() % this.settings.dayLength == this.settings.dayLength / 2.0F) {
                this.bloodMoon = true;
                this.settings.setEvent(2);
                int i = 0;
                if (i < this.players.size()) {
                    this.players.get(i).addChatMessage(Translator.getInstance().translateKey("event.blood"));
                }
            }

            if (this.blueMoon
                && (float)this.getWorldTime() % this.settings.dayLength >= 0.0F
                && (float)this.getWorldTime() % this.settings.dayLength < this.settings.dayLength / 2.0F) {
                this.blueMoon = false;
                this.settings.setEvent(0);
            } else if (this.rand.nextInt(chance) == 0
                && this.settings.getEvent() == 0
                && (float)this.getWorldTime() % this.settings.dayLength == this.settings.dayLength / 2.0F) {
                this.blueMoon = true;
                this.settings.setEvent(3);
                int i = 0;
                if (i < this.players.size()) {
                    this.players.get(i).addChatMessage(Translator.getInstance().translateKey("event.blau"));
                }
            }
        }
    }

    public void tick() {
        this.getBiomeProvider().clearCache();
        if (this.playersSleeping()) {
            this.triggerSleep();
        }

        if (this.settings.spawning) {
            Profiler.startSection("mobSpawner");
            MobSpawner.performSpawning(this, this.spawnHostiles, this.spawnFriendlies);
            Profiler.endSection();
        }

        Profiler.startSection("chunkSource");
        this.chunkProvider.unloadOldChunks();
        int skyBrightness = this.getSkyBrightness(1.0F);
        if (skyBrightness != this.skylightSubtracted) {
            this.skylightSubtracted = skyBrightness;
        }

        long time = this.settings.getWorldTime() + 1L;
        if (time % this.saveInterval == 0L) {
            Profiler.endStartSection("save");
            this.save(false, null);
        }

        this.settings.setWorldTime(time);
        Profiler.endStartSection("tickPending");
        this.tickPending(false);
        Profiler.endStartSection("tickTiles");
        this.tickBlocks();
        if (this.settings.getWeather()) {
            Profiler.endStartSection("weather");
            this.updateWeather();
            Profiler.endStartSection("events");
            this.updateEvents();
        }

        Profiler.endSection();
    }

    protected void tickBlocks() {
        this.pollingPositions.clear();
        Profiler.startSection("buildList");
        byte distance = (byte)this.simDistance;

        for (Player player : this.players) {
            int playerX = MathHelper.floor_double(player.posX / 16.0);
            int playerZ = MathHelper.floor_double(player.posZ / 16.0);

            for (int posX = -distance; posX <= distance; posX++) {
                for (int posZ = -distance; posZ <= distance; posZ++) {
                    if (this.chunkExists(posX + playerX, posZ + playerZ)) {
                        this.pollingPositions.add(ChunkUtils.getPackedCoords(posX + playerX, posZ + playerZ));
                    }
                }
            }
        }

        Profiler.endSection();
        if (this.soundCounter > 0) {
            this.soundCounter--;
        }

        for (long pos : this.pollingPositions) {
            int chunkX = ChunkUtils.getChunkX(pos);
            int chunkZ = ChunkUtils.getChunkZ(pos);
            int playerX = chunkX * 16;
            int playerZ = chunkZ * 16;
            Profiler.startSection("getChunk");
            Chunk chunk = this.getChunk(chunkX, chunkZ);
            Profiler.endStartSection("caveSound");
            if (this.soundCounter == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int posX = this.updateLCG >> 2;
                int posZ = posX & 15;
                int x0 = posX >> 8 & 15;
                int y0 = posX >> 16 & 127;
                int z0 = chunk.getBlockID(posZ, y0, x0);
                posZ += playerX;
                x0 += playerZ;
                if (z0 == 0
                    && this.getBlockBrightness(posZ, y0, x0) <= this.rand.nextInt(8)
                    && this.getBrightness(LightLayer.Sky, posZ, y0, x0) <= 0
                    && !this.players.isEmpty()) {
                    Player player = this.players.get(this.rand.nextInt(this.players.size()));
                    if (player != null && player.getDistanceSq(posZ + 0.5, y0 + 0.5, x0 + 0.5) > 4.0 && player.posY <= this.getSeaLevel()) {
                        this.playRandomAmbience("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        this.soundCounter = this.rand.nextInt(12000) + 6000;
                    }
                }
            }

            Profiler.endStartSection("thunder");
            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering() && this.currDim instanceof Overworld) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int posX = this.updateLCG >> 2;
                int posZ = playerX + (posX & 15);
                int x0 = playerZ + (posX >> 8 & 15);
                int y0 = this.isSpaceObstructed(posZ, x0);
                byte rodRadius = 16;

                for (int x = posZ - rodRadius; x <= posZ + rodRadius; x++) {
                    for (int z = x0 - rodRadius; z <= x0 + rodRadius; z++) {
                        int y = this.getTopSolidTile(x, z);
                        if (y > y0 && this.getBlockId(x, y - 1, z) == BlockList.rod.id) {
                            posZ = x;
                            x0 = z;
                            y0 = y;
                            break;
                        }
                    }
                }

                if (this.canLightningStrikeAt(posZ, y0, x0)) {
                    Player player = this.players.get(this.rand.nextInt(this.players.size()));
                    if (this.rand.nextInt(3) == 0
                        && player != null
                        && this.isBlockExposed(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ))
                        && player.inventory.accessorySlots[3] != null
                        && player.inventory.accessorySlots[3].getItem() == ItemList.necklace
                        && player.inventory.accessorySlots[3].isNotBroken()) {
                        posZ = MathHelper.floor_double(player.posX);
                        y0 = MathHelper.floor_double(player.posY);
                        x0 = MathHelper.floor_double(player.posZ);
                    }

                    this.addWeatherEffect(new LightningBolt(this, posZ, y0, x0));
                    this.lastLightningBolt = 2;
                }
            }

            Profiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int posX = this.updateLCG >> 2;
                int posZ = posX & 15;
                int x0 = posX >> 8 & 15;
                int y0 = this.isSpaceObstructed(posZ + playerX, x0 + playerZ);
                if (this.getBiomeAt(posZ + playerX, x0 + playerZ).hasSnow()
                    && y0 >= 0
                    && y0 < this.getWorldHeight()
                    && chunk.getLightValue(LightLayer.Block, posZ, y0, x0) < 10) {
                    int z0 = chunk.getBlockID(posZ, y0 - 1, x0);
                    int id = chunk.getBlockID(posZ, y0, x0);
                    if (this.isRaining()
                        && id == 0
                        && BlockList.snowLayer.placeableAt(this, posZ + playerX, y0, x0 + playerZ)
                        && z0 != 0
                        && z0 != BlockList.ice.id
                        && z0 != BlockList.snowLayer.id
                        && BlockList.blocks[z0].material.isSolid()) {
                        this.setBlockWithNotify(posZ + playerX, y0, x0 + playerZ, BlockList.snowLayer.id);
                    }

                    if (z0 == BlockList.waterStill.id && this.isRaining() && chunk.getBlockMetadata(posZ, y0 - 1, x0) == 0) {
                        boolean canPlace = true;
                        if (canPlace && this.getBlockMaterial(posZ + playerX - 1, y0 - 1, x0 + playerZ) != MaterialList.water) {
                            canPlace = false;
                        }

                        if (canPlace && this.getBlockMaterial(posZ + playerX + 1, y0 - 1, x0 + playerZ) != MaterialList.water) {
                            canPlace = false;
                        }

                        if (canPlace && this.getBlockMaterial(posZ + playerX, y0 - 1, x0 + playerZ - 1) != MaterialList.water) {
                            canPlace = false;
                        }

                        if (canPlace && this.getBlockMaterial(posZ + playerX, y0 - 1, x0 + playerZ + 1) != MaterialList.water) {
                            canPlace = false;
                        }

                        int size = this.settings.border - 1;
                        if (size > 0 && (posZ + playerX < -size || x0 + playerZ < -size || posZ + playerX >= size || x0 + playerZ >= size)) {
                            canPlace = true;
                        }

                        if (!canPlace) {
                            this.setBlockWithNotify(posZ + playerX, y0 - 1, x0 + playerZ, BlockList.ice.id);
                        }
                    }
                }
            }

            Profiler.endStartSection("tickTiles");

            for (Player player : this.players) {
                int x = MathHelper.floor_double(player.posX);
                int z = MathHelper.floor_double(player.posZ);

                for (int iterations = 0; iterations < 80; iterations++) {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int blockY = this.updateLCG >> 2;
                    int blockX = (blockY & 127) - 64 + x;
                    int blockZ = (blockY >> 8 & 127) - 64 + z;
                    blockY = (blockY >> 16) % (this.settings.getMaxSections() * 16);
                    int block = this.getBlockId(blockX, blockY, blockZ);
                    if (Block.canTick.get(block) && !this.physicsDisabled()) {
                        BlockList.blocks[block].onTick(this, blockX, blockY, blockZ, this.rand);
                    }
                }
            }

            Profiler.endSection();
        }
    }

    public boolean addWeatherEffect(Entity entity) {
        this.weatherEffects.add(entity);
        return true;
    }

    public boolean tickPending(boolean force) {
        int ticksTotal = this.tickList.size();
        if (ticksTotal != this.tickSet.size()) {
            throw new IllegalStateException("NextTick list out of synch");
        }

        if (ticksTotal > 500) {
            ticksTotal = 500;
        }

        for (int i = 0; i < ticksTotal; i++) {
            NextTickListEntry tick = this.tickList.first();
            if (!force && tick.delay > this.getWorldTime()) {
                break;
            }

            this.tickList.remove(tick);
            this.tickSet.remove(tick);
            byte offset = 8;
            if (this.chunksExist(
                tick.xCoord - offset, tick.yCoord - offset, tick.zCoord - offset, tick.xCoord + offset, tick.yCoord + offset, tick.zCoord + offset
            )) {
                int id = this.getBlockId(tick.xCoord, tick.yCoord, tick.zCoord);
                if (id == tick.blockID && id > 0) {
                    BlockList.blocks[id].onTick(this, tick.xCoord, tick.yCoord, tick.zCoord, this.rand);
                }
            }
        }

        return !this.tickList.isEmpty();
    }

    public void tickAnimations(int x, int y, int i3) {
        byte range = 16;
        Random rand = new Random();

        for (int iterations = 0; iterations < 1000; iterations++) {
            int posX = x + this.rand.nextInt(range) - this.rand.nextInt(range);
            int posY = y + this.rand.nextInt(range) - this.rand.nextInt(range);
            int posZ = i3 + this.rand.nextInt(range) - this.rand.nextInt(range);
            int id = this.getBlockId(posX, posY, posZ);
            int random = this.rand.nextInt(10);
            if (posY > 0) {
                random = this.rand.nextInt(10 * posY);
            }

            if (random == 0
                && posY <= this.getSeaLevel() - this.getSeaLevel() / 2
                && id == 0
                && this.settings.getType() != WorldType.FLAT
                && this.currDim instanceof Overworld) {
                this.spawnParticle(
                    ParticleType.TOWNAURA, posX + this.rand.nextFloat(), posY + this.rand.nextFloat(), posZ + this.rand.nextFloat(), 0.0, 0.0, 0.0
                );
            }

            if (this.rand.nextInt(50) == 0) {
                this.getBiomeAt(posX, posZ).spawnParticles(this, posX + this.rand.nextDouble(), posY + this.rand.nextDouble(), posZ + this.rand.nextDouble());
            }

            if (id > 0) {
                BlockList.blocks[id].spawnParticles(this, posX, posY, posZ, rand);
            }
        }
    }

    public List<Entity> getEntities(Entity entity, BoundingBox aabb) {
        this.entitiesInBB.clear();
        int minX = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
        int maxX = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
        int minZ = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
        int maxZ = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (this.chunkExists(x, z)) {
                    this.getChunk(x, z).getEntitiesInBB(entity, aabb, this.entitiesInBB);
                }
            }
        }

        return this.entitiesInBB;
    }

    public List<Entity> getEntitiesOfType(Class<?> targetClass, BoundingBox aabb) {
        int minX = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
        int maxX = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
        int minZ = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
        int maxZ = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);
        ArrayList<Entity> entityList = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (this.chunkExists(x, z)) {
                    this.getChunk(x, z).getSpecificEntitiesInBB(targetClass, aabb, entityList);
                }
            }
        }

        return entityList;
    }

    public List<Entity> getEntityList() {
        return this.entities;
    }

    public Entity getEntityOfType(Class<?> typ, BoundingBox bb, Entity entity) {
        List<Entity> entities = this.getEntitiesOfType(typ, bb);
        Entity target = null;
        double maxDist = Double.MAX_VALUE;

        for (Entity entityToFind : entities) {
            if (entityToFind != entity) {
                double dist = entity.getDistanceSqTo(entityToFind);
                if (dist <= maxDist) {
                    target = entityToFind;
                    maxDist = dist;
                }
            }
        }

        return target;
    }

    public void setChunkModified(int xCoord, int yCoord, int zCoord) {
        if (this.blockExists(xCoord, yCoord, zCoord)) {
            this.getChunkAt(xCoord, zCoord).setModified();
        }
    }

    public void addLoadedEntities(List<Entity> entities) {
        this.entities.addAll(entities);

        for (WorldListener worldAccess : this.worldListeners) {
            for (Entity entity : entities) {
                worldAccess.obtainEntitySkin(entity);
            }
        }
    }

    public void unloadEntities(List<Entity> entities) {
        this.entitiesToRemove.addAll(entities);
    }

    public void dropOldChunks() {
        while (this.chunkProvider.unloadOldChunks()) {
        }
    }

    public boolean canPlace(int id, int x, int y, int z, boolean forcePlace, int side) {
        int currId = this.getBlockId(x, y, z);
        Block currBlock = BlockList.blocks[currId];
        Block blockToPlace = BlockList.blocks[id];
        BoundingBox theBB = blockToPlace.getCollisionBox(this, x, y, z);
        if (forcePlace) {
            theBB = null;
        }

        if (theBB != null && !this.isClear(theBB)) {
            return false;
        }

        if (currBlock != null
            && (
                currBlock == BlockList.waterMoving
                    || currBlock == BlockList.waterStill
                    || currBlock == BlockList.lavaMoving
                    || currBlock == BlockList.lavaStill
                    || currBlock == BlockList.fire
                    || currBlock.material.isGroundCover()
                    || currBlock instanceof LayerBlock
            )) {
            currBlock = null;
        }

        return id > 0 && currBlock == null && blockToPlace.placeableAt(this, x, y, z);
    }

    public Path findPathEntity(Entity src, Entity target, float spd) {
        Profiler.startSection("pathfind");
        int srcX = MathHelper.floor_double(src.posX);
        int srcY = MathHelper.floor_double(src.posY + 1.0);
        int srcZ = MathHelper.floor_double(src.posZ);
        int dist = (int)(spd + 16.0F);
        int startX = srcX - dist;
        int startY = srcY - dist;
        int startZ = srcZ - dist;
        int endX = srcX + dist;
        int endY = srcY + dist;
        int endZ = srcZ + dist;
        ChunkCache cache = new ChunkCache(this, startX, startY, startZ, endX, endY, endZ);
        Profiler.endSection();
        return new PathFinder(cache).pathToEntity(src, target, spd);
    }

    public Path findPathLocation(Entity entity, int xCoord, int yCoord, int zCoord, float spd) {
        Profiler.startSection("pathfind");
        int srcX = MathHelper.floor_double(entity.posX);
        int srcY = MathHelper.floor_double(entity.posY);
        int srcZ = MathHelper.floor_double(entity.posZ);
        int dist = (int)(spd + 8.0F);
        int startX = srcX - dist;
        int startY = srcY - dist;
        int startZ = srcZ - dist;
        int endX = srcX + dist;
        int endY = srcY + dist;
        int endZ = srcZ + dist;
        ChunkCache cache = new ChunkCache(this, startX, startY, startZ, endX, endY, endZ);
        Profiler.endSection();
        return new PathFinder(cache).pathToLocation(entity, xCoord, yCoord, zCoord, spd);
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        int id = this.getBlockId(x, y, z);
        return BlockList.blocks[id] == null ? false : id > 0 && BlockList.blocks[id].fullBlock();
    }

    public Player playerInChunks(double x, double z, double rad) {
        double dist = -1.0;
        Player returnPlayer = null;

        for (Player player : this.players) {
            if (player != null) {
                double maxDist = player.getDistanceSq(x, player.posY, z);
                if ((rad < 0.0 || maxDist < rad * rad) && (dist == -1.0 || maxDist < dist)) {
                    dist = maxDist;
                    returnPlayer = player;
                }
            }
        }

        return returnPlayer;
    }

    @Override
    public int getBrightness(int x, int y, int z, int value) {
        int sky = this.getLayerBrightness(LightLayer.Sky, x, y, z);
        int block = this.getLayerBrightness(LightLayer.Block, x, y, z);
        if (block < value) {
            block = value;
        }

        return sky << 20 | block << 4;
    }

    public int getLayerBrightness(LightLayer layer, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= this.getWorldHeight() && layer == LightLayer.Sky) {
            return 15;
        }

        if (y < this.getWorldHeight()) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if (!this.chunkExists(chunkX, chunkZ)) {
                return 0;
            }

            int id = this.getBlockId(x, y, z);
            Block block = BlockList.blocks[id];
            if (!(block instanceof Slab) && !(block instanceof Stairs) && id != BlockList.tilledField.id && id != BlockList.pathBlock.id) {
                return this.getChunk(chunkX, chunkZ).getLightValue(layer, x & 15, y, z & 15);
            }

            int bright = this.getBrightness(layer, x, y + 1, z);
            if (block instanceof Slab && this.getBlockMetadata(x, y, z) > 127) {
                bright = this.getBrightness(layer, x, y - 1, z);
            }

            int left = this.getBrightness(layer, x + 1, y, z);
            int right = this.getBrightness(layer, x - 1, y, z);
            int front = this.getBrightness(layer, x, y, z + 1);
            int back = this.getBrightness(layer, x, y, z - 1);
            if (left > bright) {
                bright = left;
            }

            if (right > bright) {
                bright = right;
            }

            if (front > bright) {
                bright = front;
            }

            if (back > bright) {
                bright = back;
            }

            return bright;
        } else {
            return layer.defaultLightValue;
        }
    }

    public boolean isWater(int x, int y, int z) {
        Biome biom = this.getBiomeAt(x, z);
        byte temp = biom.getTemperature();
        if (temp <= Biome.Temperature.COLD.value && biom.hasSnow() && y >= 0 && y < this.getWorldHeight() && this.getBrightness(LightLayer.Block, x, y, z) < 10
            )
         {
            int id = this.getBlockId(x, y, z);
            if ((id == BlockList.waterStill.id || id == BlockList.waterMoving.id) && this.getBlockMetadata(x, y, z) == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean canSnowAt(int x, int y, int z, boolean force) {
        Biome thisBiome = this.getBiomeAt(x, z);
        float temp = thisBiome.getTemperature();
        int height = this.getWorldHeight() / 2;
        int heightOffset = 32;
        int offset = this.rand.nextInt(4);
        if (force) {
            offset = 0;
        }

        if (thisBiome instanceof Fjord) {
            heightOffset = 16;
        }

        if (this.getWorldHeight() == 320) {
            height += heightOffset;
        }

        if (height < 156) {
            height = 156;
        }

        if (thisBiome.id == BiomeList.muskeg.id && this.getBlockId(x, y - 1, z) == BlockList.dirt.id) {
            return false;
        }

        if ((
                temp <= Biome.Temperature.COLD.value && thisBiome.hasSnow()
                    || (thisBiome instanceof Mountains || thisBiome instanceof Fjord || thisBiome == BiomeList.winterFloating) && y >= height - offset
                    || thisBiome.id == BiomeList.field.id && y >= this.getSeaLevel() + 24 - offset
            )
            && y >= 0
            && y < this.getWorldHeight()
            && this.getBrightness(LightLayer.Block, x, y, z) < 10) {
            if (force) {
                return true;
            }

            int belowID = this.getBlockId(x, y - 1, z);
            int id = this.getBlockId(x, y, z);
            return id == 0
                && BlockList.snowLayer.placeableAt(this, x, y, z)
                && belowID != 0
                && belowID != BlockList.ice.id
                && belowID != BlockList.packedIce.id
                && belowID != BlockList.powderSnow.id
                && BlockList.blocks[belowID].material.isSolid();
        } else {
            return false;
        }
    }

    public void queueAll(int x, int y, int z) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.changeBlocks(x, y, z);
        }
    }

    public float getSunsetAngle(float angl) {
        return this.getSunAngle(angl) * (float) Math.PI * 2.0F;
    }

    public Position getSpawnPoint() {
        return new Position(this.settings.getSpawnX(), this.settings.getSpawnY(), this.settings.getSpawnZ());
    }

    public Player getExactPlayer(String name) {
        for (Player playerEntity : this.players) {
            if (name.equals(playerEntity.username)) {
                return playerEntity;
            }
        }

        return null;
    }

    public void playEffect(int x, int y, int z, int id, int meta) {
        this.playEffect(null, x, y, z, id, meta);
    }

    public void playEffect(Player player, int x, int y, int z, int id, int meta) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.worldEvent(player, x, y, z, id, meta);
        }
    }

    public void checkSessionLock() {
        this.saveHandler.checkSessionLock();
    }

    public void setTime(long time) {
        this.settings.setWorldTime(time);
    }

    public void setMaxSections(int sections) {
        this.settings.setMaxSections(sections);
    }

    public long getSeed() {
        return this.settings.getRandomSeed();
    }

    public long getWorldTime() {
        return this.settings.getWorldTime();
    }

    public int getMaxSections() {
        return this.settings.getMaxSections();
    }

    public int getWorldHeight() {
        return this.getMaxSections() << 4;
    }

    public int getSeaLevel() {
        return Math.min(this.getWorldHeight() / 2, 128);
    }

    public void setSpawnPoint(Position pos) {
        this.settings.setSpawn(pos.x, pos.y, pos.z);
    }

    public SaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    public WorldInfo getSettings() {
        return this.settings;
    }

    public boolean aiDisabled() {
        return this.settings.isAiDisabled();
    }

    public boolean physicsDisabled() {
        return this.settings.isPhysicsDisabled();
    }

    public void setPermaday(boolean on) {
        this.settings.setPermaDay(on);
    }

    public boolean getPermaday() {
        return this.settings.isPermaDay();
    }

    public void setPhysicsDisabled(boolean physicsDisabled) {
        this.settings.setPhysicsDisabled(physicsDisabled);
    }

    public void wakePlayers() {
        this.allPlayersSleeping = !this.players.isEmpty();

        for (Player playa : this.players) {
            if (!playa.isPlayerSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void triggerSleep() {
        this.allPlayersSleeping = false;

        for (Player playa : this.players) {
            if (playa.isPlayerSleeping()) {
                playa.wakeUpPlayer(false, false, true);
            }
        }

        this.stopAllWeather();
    }

    public boolean playersSleeping() {
        if (this.allPlayersSleeping && !this.isOnline) {
            for (Player playa : this.players) {
                if (!playa.isPlayerFullyAsleep()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void playRecord(String name, int x, int y, int z, boolean stop) {
        for (WorldListener worldAccess : this.worldListeners) {
            worldAccess.playRecord(name, x, y, z, stop);
        }
    }

    public boolean getDirectSignal(int x, int y, int z, int meta) {
        int id = this.getBlockId(x, y, z);
        return id != 0 && BlockList.blocks[id].powersDirectly(this, x, y, z, meta);
    }

    public boolean hasDirectSignal(int x, int y, int z) {
        return this.getDirectSignal(x, y - 1, z, 0)
            || this.getDirectSignal(x, y + 1, z, 1)
            || this.getDirectSignal(x, y, z - 1, 2)
            || this.getDirectSignal(x, y, z + 1, 3)
            || this.getDirectSignal(x - 1, y, z, 4)
            || this.getDirectSignal(x + 1, y, z, 5);
    }

    public boolean getSignal(int x, int y, int z, int metal) {
        int id = this.getBlockId(x, y, z);
        Block adjacentTile = BlockList.blocks[id];
        boolean powered = false;
        boolean block = false;
        if (this.isBlockFull(x, y, z)) {
            powered |= this.hasDirectSignal(x, y, z);
            if (!adjacentTile.providesPower()) {
                block = true;
            }
        }

        if (!block) {
            powered |= id != 0 && BlockList.blocks[id].powersIndirectly(this, x, y, z, metal);
        }

        return powered;
    }

    public boolean hasNeighborSignal(int x, int y, int z) {
        return this.getSignal(x, y - 1, z, 0)
            || this.getSignal(x, y + 1, z, 1)
            || this.getSignal(x, y, z - 1, 2)
            || this.getSignal(x, y, z + 1, 3)
            || this.getSignal(x - 1, y, z, 4)
            || this.getSignal(x + 1, y, z, 5);
    }

    public boolean isPowered(int x, int y, int z, int meta, int id) {
        int target = this.getBlockId(x, y, z);
        return target == id && BlockList.blocks[target].powersDirectly(this, x, y, z, meta);
    }

    public boolean isPowering(int x, int y, int z, int id) {
        int target = this.getBlockId(x, y, z);
        return target == id;
    }

    public boolean isPoweredBy(int x, int y, int z, int id) {
        return this.isPowered(x, y - 1, z, 0, id)
            || this.isPowered(x, y + 1, z, 1, id)
            || this.isPowered(x, y, z - 1, 2, id)
            || this.isPowered(x, y, z + 1, 3, id)
            || this.isPowered(x - 1, y, z, 4, id)
            || this.isPowered(x + 1, y, z, 5, id);
    }

    public void blockEvent(int x, int y, int z, int id, int meta) {
        int block = this.getBlockId(x, y, z);
        if (block > 0) {
            BlockList.blocks[block].blockEvent(this, x, y, z, id, meta);
        }
    }

    public void setMapData(String name, MapData data) {
        this.mapStorage.setData(name, data);
    }

    public MapData loadMapData(Class<? extends MapData> data, String name) {
        return this.mapStorage.loadData(data, name);
    }

    public int getMapId(String name) {
        return this.mapStorage.getUniqueId(name);
    }

    public BiomeProvider getBiomeProvider() {
        return this.currDim.biomeProvider;
    }

    public Biome getBiomeAt(int x, int z) {
        return this.currDim.biomeProvider.getBiomeAt(x, z);
    }

    public int getSurfaceHeightmap(int blockX, int blockZ) {
        Chunk chunk;
        if (this.chunkExists(blockX >> 4, blockZ >> 4)) {
            chunk = this.getChunk(blockX >> 4, blockZ >> 4);
        } else {
            chunk = this.chunkProvider.justGenerateForHeight(blockX >> 4, blockZ >> 4);
        }

        return chunk.getSurfaceHeightmap(blockX & 15, blockZ & 15);
    }

    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        return this.chunkProvider.justGenerateForHeight(chunkX, chunkZ);
    }

    public int getMoonPhase() {
        return this.currDim.getMoonPhase(this.settings.getWorldTime());
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return BlockList.blocks[this.getBlockId(x, y, z)];
    }

    public void setEntityState(Entity e, byte myAss) {
    }

    public int countBlocks(BoundingBox bb, int id) {
        int count = 0;
        int minX = (int)bb.minX;
        int maxX = (int)bb.maxX + 1;
        int minY = (int)bb.minY;
        int maxY = (int)bb.maxY + 1;
        int minZ = (int)bb.minZ;
        int maxZ = (int)bb.maxZ + 1;
        if (bb.minX < 0.0) {
            minX--;
        }

        if (bb.minY < 0.0) {
            minY--;
        }

        if (bb.minZ < 0.0) {
            minZ--;
        }

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (this.getBlockId(x, y, z) == id) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private void setWeather() {
        if (this.settings.getIsRaining()) {
            this.rainingStrength = 1.0F;
            if (this.settings.getIsThundering()) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    protected void updateWeather() {
        if (this.lastLightningBolt > 0) {
            this.lastLightningBolt--;
        }

        int thunderTime = this.settings.getThunderTime();
        if (thunderTime <= 0) {
            if (this.settings.getIsThundering()) {
                this.settings.setThunderTime(this.rand.nextInt(12000) + 3600);
            } else {
                this.settings.setThunderTime(this.rand.nextInt(168000) + 12000);
            }
        } else {
            this.settings.setThunderTime(--thunderTime);
            if (thunderTime <= 0) {
                this.settings.setIsThundering(!this.settings.getIsThundering());
            }
        }

        int rainTime = this.settings.getRainTime();
        if (rainTime <= 0) {
            if (this.settings.getIsRaining()) {
                this.settings.setRainTime(this.rand.nextInt(12000) + 12000);
            } else {
                this.settings.setRainTime(this.rand.nextInt(168000) + 12000);
            }
        } else {
            this.settings.setRainTime(--rainTime);
            if (rainTime <= 0) {
                this.settings.setIsRaining(!this.settings.getIsRaining());
            }
        }

        this.prevRainingStrength = this.rainingStrength;
        if (this.settings.getIsRaining()) {
            this.rainingStrength = (float)(this.rainingStrength + 0.01);
        } else {
            this.rainingStrength = (float)(this.rainingStrength - 0.01);
        }

        this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
        this.prevThunderingStrength = this.thunderingStrength;
        if (this.settings.getIsThundering()) {
            this.thunderingStrength = (float)(this.thunderingStrength + 0.01);
        } else {
            this.thunderingStrength = (float)(this.thunderingStrength - 0.01);
        }

        this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
    }

    public void stopAllWeather() {
        this.settings.setRainTime(0);
        this.settings.setIsRaining(false);
        this.settings.setThunderTime(0);
        this.settings.setIsThundering(false);
    }

    public float getThunderStrength(float str) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * str) * this.getRainStrength(str);
    }

    public float getRainStrength(float str) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * str;
    }

    public void setRainStrength(float str) {
        this.prevRainingStrength = str;
        this.rainingStrength = str;
    }

    public boolean isThundering() {
        return this.getThunderStrength(1.0F) > 0.9;
    }

    public boolean isRaining() {
        return this.getRainStrength(1.0F) > 0.0;
    }

    public boolean canLightningStrikeAt(int x, int y, int z) {
        if (this.isRaining() && this.isBlockExposed(x, y, z) && this.isSpaceObstructed(x, z) <= y) {
            Biome biome = this.getBiomeAt(x, z);
            return biome.hasSnow() ? false : biome.canThunder();
        } else {
            return false;
        }
    }

    public int isSpaceObstructed(int x, int z) {
        return this.getChunkAt(x, z).isSpaceFree(x & 15, z & 15);
    }

    public void setDimension(Dimension dim) {
        this.currDim = dim;
        this.currDim.init(this);
    }

    public void setEmittanceToFull(int x, int y, int z, int light) {
        this.blockLight.checkBlockEmittanceFixed(x, y, z, light);
    }

    public void restoreBlockEmittance(int x, int y, int z) {
        this.blockLight.checkBlockEmittance(x, y, z);
    }

    public BlockPosition findClosestStructure(Class<? extends Structure> target, int x, int y, int z) {
        return this.chunkProvider.findClosestStructure(this, target, x, y, z);
    }

    @Override
    public boolean areChunksEmpty() {
        return false;
    }

    public void setSaving(boolean state) {
    }

    public void postSaveCleanup() {
    }

    public void updateServerEntity(Entity entity, byte tag) {
    }

    public void disconnect() {
    }
}
