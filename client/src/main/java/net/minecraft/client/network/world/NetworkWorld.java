package net.minecraft.client.network.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.PlayerProperties;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.dimension.Dimension;
import net.minecraft.game.world.util.Position;
import net.minecraft.network.packet.misc.DisconnectPacket;

public class NetworkWorld extends World {
    private ClientHandler connection;
    private ChunkLoaderMP chunkLoader;
    private LinkedList<NetworkWorld.BlockUpdate> blockUpdates = new LinkedList<>();
    private Int2ObjectOpenHashMap<Entity> entitiesById = new Int2ObjectOpenHashMap<>();
    private Set<Entity> entityList = new HashSet<>();
    private Set<Entity> entityMemory = new HashSet<>();

    public NetworkWorld(ClientHandler conn, long seed, int dim) {
        this(conn, seed, dim, WorldType.DEFAULT, null);
    }

    public NetworkWorld(ClientHandler conn, long seed, int dim, WorldType type, Biome singleBiome) {
        super(new SaveHandlerMP(), "MpServer", seed, Dimension.getDimension(dim));
        this.getSettings().setType(type);
        if (singleBiome != null) {
            this.getSettings().setSingleBiome(singleBiome);
        }

        this.getSettings().setGamemode((byte)PlayerProperties.SURVIVAL);
        this.getSettings().setCheats(false);
        this.currDim.init(this);
        this.connection = conn;
        this.setSpawnPoint(new Position(8, this.getWorldHeight() / 2, 8));
    }

    @Override
    public void tick() {
        this.setTime(this.getWorldTime() + 1L);
        this.updateWeatherStrength();
        int skyLight = this.getSkyBrightness(1.0F);
        if (skyLight != this.skylightSubtracted) {
            this.skylightSubtracted = skyLight;

            for (int index = 0; index < this.worldListeners.size(); index++) {
                this.worldListeners.get(index).updateAllRenderers();
            }
        }

        for (int index = 0; index < 10 && !this.entityMemory.isEmpty(); index++) {
            Entity entity = this.entityMemory.iterator().next();
            this.entityMemory.remove(entity);
            if (!this.entities.contains(entity)) {
                this.spawnEntity(entity);
            }
        }

        this.connection.process();

        for (int var5 = 0; var5 < this.blockUpdates.size(); var5++) {
            NetworkWorld.BlockUpdate update = this.blockUpdates.get(var5);
            if (--update.tickRate == 0) {
                super.setBlockAndMetadata(update.xPos, update.yPos, update.zPos, update.id, update.meta);
                super.markForUpdate(update.xPos, update.yPos, update.zPos);
                this.blockUpdates.remove(var5--);
            }
        }
    }

    public void clearBlockUpdates(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int index = 0; index < this.blockUpdates.size(); index++) {
            NetworkWorld.BlockUpdate block = this.blockUpdates.get(index);
            if (block.xPos >= minX && block.yPos >= minY && block.zPos >= minZ && block.xPos <= maxX && block.yPos <= maxY && block.zPos <= maxZ) {
                this.blockUpdates.remove(index--);
            }
        }
    }

    @Override
    public ChunkLoader getChunkProvider() {
        this.chunkLoader = new ChunkLoaderMP(this);
        return this.chunkLoader;
    }

    @Override
    public void setSpawn() {
        this.setSpawnPoint(new Position(8, this.getWorldHeight() / 2, 8));
    }

    @Override
    public void forceTick(int x, int y, int z, int id) {
    }

    @Override
    public boolean tickPending(boolean state) {
        return false;
    }

    public void doPreChunk(int x, int z, boolean ready) {
        if (ready) {
            this.chunkLoader.prepareChunk(x, z);
        } else {
            this.chunkLoader.loadChunk(x, z);
        }

        if (!ready) {
            this.markBlocksDirty(x * 16, 0, z * 16, x * 16 + 15, this.getWorldHeight(), z * 16 + 15);
        }
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        boolean spawned = super.spawnEntity(entity);
        this.entityList.add(entity);
        if (!spawned) {
            this.entityMemory.add(entity);
        }

        return spawned;
    }

    @Override
    public void kill(Entity entity) {
        super.kill(entity);
        this.entityList.remove(entity);
    }

    @Override
    public void obtainEntitySkin(Entity entity) {
        super.obtainEntitySkin(entity);
        if (this.entityMemory.contains(entity)) {
            this.entityMemory.remove(entity);
        }
    }

    @Override
    protected void releaseEntitySkin(Entity entity) {
        super.releaseEntitySkin(entity);
        if (this.entityList.contains(entity)) {
            if (entity.isAlive()) {
                this.entityMemory.add(entity);
            } else {
                this.entityList.remove(entity);
            }
        }
    }

    public void addEntityToMemory(int id, Entity entity) {
        Entity backup = this.entityInMemory(id);
        if (backup != null) {
            this.kill(backup);
        }

        this.entityList.add(entity);
        entity.entityId = id;
        if (!this.spawnEntity(entity)) {
            this.entityMemory.add(entity);
        }

        this.entitiesById.put(id, entity);
    }

    public Entity entityInMemory(int id) {
        return this.entitiesById.get(id);
    }

    public Entity removeFromWorld(int id) {
        Entity entity = this.entitiesById.remove(id);
        if (entity != null) {
            this.entityList.remove(entity);
            this.kill(entity);
        }

        return entity;
    }

    @Override
    public boolean setBlockMetadata(int x, int y, int z, int meta) {
        int id = this.getBlockId(x, y, z);
        int exitmeta = this.getBlockMetadata(x, y, z);
        if (super.setBlockMetadata(x, y, z, meta)) {
            this.blockUpdates.add(new NetworkWorld.BlockUpdate(this, x, y, z, id, exitmeta));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setBlockAndMetadata(int x, int y, int z, int id, int meta) {
        int existId = this.getBlockId(x, y, z);
        int existMeta = this.getBlockMetadata(x, y, z);
        if (super.setBlockAndMetadata(x, y, z, id, meta)) {
            this.blockUpdates.add(new NetworkWorld.BlockUpdate(this, x, y, z, existId, existMeta));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int id) {
        int existId = this.getBlockId(x, y, z);
        int meta = this.getBlockMetadata(x, y, z);
        if (super.setBlock(x, y, z, id)) {
            this.blockUpdates.add(new NetworkWorld.BlockUpdate(this, x, y, z, existId, meta));
            return true;
        } else {
            return false;
        }
    }

    public boolean updateBlocksAndNeighbors(int x, int y, int z, int id, int meta) {
        this.clearBlockUpdates(x, y, z, x, y, z);
        if (super.setBlockAndMetadata(x, y, z, id, meta)) {
            this.updateBlocks(x, y, z, id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void disconnect() {
        this.entitiesById.clear();
        this.connection.disconnect(new DisconnectPacket("Quitting"));
    }

    class BlockUpdate {
        int xPos;
        int yPos;
        int zPos;
        int tickRate;
        int id;
        int meta;
        final NetworkWorld world;

        public BlockUpdate(NetworkWorld world, int x, int y, int z, int id, int meta) {
            this.world = world;
            this.xPos = x;
            this.yPos = y;
            this.zPos = z;
            this.tickRate = 80;
            this.id = id;
            this.meta = meta;
        }
    }
}
