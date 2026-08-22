package net.minecraft.game.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public class ChunkProvider implements ChunkLoader {
    private final LongSet droppedChunks = new LongOpenHashSet();
    private final Chunk emptyChunk;
    private final ChunkLoader chunkProvider;
    private final FileLoader chunkLoader;
    private final Long2ObjectOpenHashMap<Chunk> chunkMap = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<Chunk> terrainMap = new Long2ObjectOpenHashMap<>();
    private final List<Chunk> chunkList = new ArrayList<>();
    private final World world;
    private int chunkCache;

    public ChunkProvider(World world, FileLoader chunkLoader, ChunkLoader chunkProvider) {
        this.emptyChunk = new EmptyChunk(world, 0, 0);
        this.world = world;
        this.chunkLoader = chunkLoader;
        this.chunkProvider = chunkProvider;
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return this.chunkMap.containsKey(ChunkUtils.getPackedCoords(chunkX, chunkZ));
    }

    public void dropChunk(int chunkX, int chunkZ) {
        this.droppedChunks.add(ChunkUtils.getPackedCoords(chunkX, chunkZ));
    }

    public Chunk loadChunk(int chunkX, int chunkZ) {
        Sand.can_fall = false;
        long coords = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        this.droppedChunks.remove(coords);
        Chunk chunk = this.chunkMap.get(coords);
        if (chunk == null) {
            int border = this.world.settings.border / 16 + 2;
            if (chunkX << 4 < -2147483047 || chunkZ << 4 < -2147483047 || chunkX << 4 >= 2147483047 || chunkZ << 4 >= 2147483047) {
                return this.emptyChunk;
            }

            if (this.world.settings.border > 0 && (chunkX < -border || chunkZ < -border || chunkX >= border || chunkZ >= border)) {
                return this.emptyChunk;
            }

            chunk = this.loadChunkFromFile(chunkX, chunkZ);
            if (chunk == null) {
                if (this.chunkProvider == null) {
                    chunk = this.emptyChunk;
                } else {
                    chunk = this.chunkProvider.generateChunk(chunkX, chunkZ);
                }

                int i6 = this.world.settings.border / 16;
                if (i6 > 0 && (chunkX < -i6 || chunkZ < -i6 || chunkX >= i6 || chunkZ >= i6)) {
                    chunk = this.emptyChunk;
                }
            }

            this.chunkMap.put(coords, chunk);
            this.chunkList.add(chunk);
            if (chunk != null) {
                chunk.onChunkLoad();
                chunk.initLighting();
            }

            chunk.populateChunk(this, this, chunkX, chunkZ);
        }

        Sand.can_fall = true;
        return chunk;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Chunk chunk = this.chunkMap.get(ChunkUtils.getPackedCoords(chunkX, chunkZ));
        return chunk == null ? this.loadChunk(chunkX, chunkZ) : chunk;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        long hash = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        Chunk chunk = this.terrainMap.get(hash);
        if (chunk == null) {
            chunk = this.chunkProvider.justGenerateForHeight(chunkX, chunkZ);
            this.terrainMap.put(hash, chunk);
        }

        return chunk;
    }

    private Chunk loadChunkFromFile(int chunkX, int chunkZ) {
        if (this.chunkLoader == null) {
            return null;
        }

        try {
            Chunk chunk = this.chunkLoader.loadChunk(this.world, chunkX, chunkZ);
            if (chunk != null) {
                chunk.lastSaveTime = this.world.getWorldTime();
            }

            return chunk;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private void saveChunkData(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                chunk.lastSaveTime = this.world.getWorldTime();
                this.chunkLoader.saveChunk(this.world, chunk);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    @Override
    public void populate(ChunkLoader chunkProvider, int chunkX, int chunkZ) {
        Chunk chunk = this.generateChunk(chunkX, chunkZ);
        if (!chunk.isPopulated) {
            chunk.isPopulated = true;
            if (this.chunkProvider != null) {
                this.chunkProvider.populate(chunkProvider, chunkX, chunkZ);
                chunk.setModified();
            }
        }
    }

    @Override
    public boolean saveChunks(boolean forceSave, ProgressBar progressListener) {
        int attempts = 0;

        for (Chunk chunk : this.chunkList) {
            if (chunk.save(forceSave)) {
                this.saveChunkData(chunk);
                chunk.isModified = false;
                if (++attempts == 24 && !forceSave) {
                    return false;
                }
            }
        }

        return forceSave && this.chunkLoader == null ? true : true;
    }

    @Override
    public boolean unloadOldChunks() {
        for (int index = 0; index < 100; index++) {
            if (!this.droppedChunks.isEmpty()) {
                long hash = this.droppedChunks.iterator().nextLong();
                Chunk chunk = this.chunkMap.get(hash);
                if (chunk == null) {
                    this.droppedChunks.remove(hash);
                    continue;
                }

                chunk.onChunkUnload();
                this.saveChunkData(chunk);
                this.droppedChunks.remove(hash);
                this.chunkMap.remove(hash);
                this.chunkList.remove(chunk);
            }
        }

        for (int var5 = 0; var5 < 10; var5++) {
            if (this.chunkCache >= this.chunkList.size()) {
                this.chunkCache = 0;
                break;
            }

            Chunk chunk = this.chunkList.get(this.chunkCache++);
            if (chunk == this.emptyChunk) {
                continue;
            }

            Player player = this.world.playerInChunks((chunk.xPosition << 4) + 8.0, (chunk.zPosition << 4) + 8.0, 288.0);
            if (player == null) {
                this.dropChunk(chunk.xPosition, chunk.zPosition);
            }
        }

        return this.chunkProvider.unloadOldChunks();
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String loaderName() {
        return "ChunkCache: " + this.chunkMap.size() + " Drop: " + this.droppedChunks.size();
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return this.chunkProvider.findClosestStructure(world, target, x, y, z);
    }
}
