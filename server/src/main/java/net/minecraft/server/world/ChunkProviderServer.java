package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.chunk.EmptyChunk;
import net.minecraft.game.world.chunk.loader.FileLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public class ChunkProviderServer implements ChunkLoader {
    private final LongOpenHashSet idList = new LongOpenHashSet();
    private final Chunk dummyChunk;
    private final ChunkLoader serverChunkGenerator;
    private final FileLoader chunkLoaderInstance;
    public boolean chunkLoadOverride = false;
    private final Long2ObjectOpenHashMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap<>();
    private final List<Chunk> chunkMap = new ArrayList<>();
    private final WorldServer world;

    public ChunkProviderServer(WorldServer worldServer1, FileLoader iChunkLoader2, ChunkLoader iChunkProvider3) {
        this.dummyChunk = new EmptyChunk(worldServer1, 0, 0);
        this.world = worldServer1;
        this.chunkLoaderInstance = iChunkLoader2;
        this.serverChunkGenerator = iChunkProvider3;
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return this.id2ChunkMap.containsKey(ChunkUtils.getPackedCoords(chunkX, chunkZ));
    }

    public void addChunk(int chunkX, int chunkZ) {
        int i4 = chunkX * 16 + 8 - this.world.getSettings().getSpawnX();
        int i5 = chunkZ * 16 + 8 - this.world.getSettings().getSpawnZ();
        short s6 = 128;
        if (i4 < -s6 || i4 > s6 || i5 < -s6 || i5 > s6) {
            this.idList.add(ChunkUtils.getPackedCoords(chunkX, chunkZ));
        }
    }

    public Chunk loadChunk(int chunkX, int chunkZ) {
        Sand.can_fall = false;
        long i3 = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        this.idList.remove(i3);
        Chunk chunk4 = this.id2ChunkMap.get(i3);
        if (chunk4 == null) {
            chunk4 = this.load_chunk_xz(chunkX, chunkZ);
            if (chunk4 == null) {
                if (this.serverChunkGenerator == null) {
                    chunk4 = this.dummyChunk;
                } else {
                    chunk4 = this.serverChunkGenerator.generateChunk(chunkX, chunkZ);
                }
            }

            this.id2ChunkMap.put(i3, chunk4);
            this.chunkMap.add(chunk4);
            if (chunk4 != null) {
                chunk4.onChunkLoad();
                chunk4.initLighting();
            }

            if (!chunk4.isPopulated && this.chunkExists(chunkX + 1, chunkZ + 1) && this.chunkExists(chunkX, chunkZ + 1) && this.chunkExists(chunkX + 1, chunkZ)
                )
             {
                this.populate(this, chunkX, chunkZ);
            }

            if (this.chunkExists(chunkX - 1, chunkZ)
                && !this.generateChunk(chunkX - 1, chunkZ).isPopulated
                && this.chunkExists(chunkX - 1, chunkZ + 1)
                && this.chunkExists(chunkX, chunkZ + 1)
                && this.chunkExists(chunkX - 1, chunkZ)) {
                this.populate(this, chunkX - 1, chunkZ);
            }

            if (this.chunkExists(chunkX, chunkZ - 1)
                && !this.generateChunk(chunkX, chunkZ - 1).isPopulated
                && this.chunkExists(chunkX + 1, chunkZ - 1)
                && this.chunkExists(chunkX, chunkZ - 1)
                && this.chunkExists(chunkX + 1, chunkZ)) {
                this.populate(this, chunkX, chunkZ - 1);
            }

            if (this.chunkExists(chunkX - 1, chunkZ - 1)
                && !this.generateChunk(chunkX - 1, chunkZ - 1).isPopulated
                && this.chunkExists(chunkX - 1, chunkZ - 1)
                && this.chunkExists(chunkX, chunkZ - 1)
                && this.chunkExists(chunkX - 1, chunkZ)) {
                this.populate(this, chunkX - 1, chunkZ - 1);
            }
        }

        Sand.can_fall = true;
        return chunk4;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Chunk chunk3 = this.id2ChunkMap.get(ChunkUtils.getPackedCoords(chunkX, chunkZ));
        return chunk3 == null ? (!this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(chunkX, chunkZ)) : chunk3;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        return this.serverChunkGenerator.justGenerateForHeight(chunkX, chunkZ);
    }

    private Chunk load_chunk_xz(int chunkX, int chunkZ) {
        if (this.chunkLoaderInstance == null) {
            return null;
        }

        try {
            Chunk chunk3 = this.chunkLoaderInstance.loadChunk(this.world, chunkX, chunkZ);
            if (chunk3 != null) {
                chunk3.lastSaveTime = this.world.getWorldTime();
            }

            return chunk3;
        } catch (Exception exception4) {
            exception4.printStackTrace();
            return null;
        }
    }

    private void saveChunkData(Chunk chunk1) {
    }

    private void saveChunkToFile(Chunk chunk1) {
        if (this.chunkLoaderInstance != null) {
            try {
                chunk1.lastSaveTime = this.world.getWorldTime();
                this.chunkLoaderInstance.saveChunk(this.world, chunk1);
            } catch (IOException iOException3) {
                iOException3.printStackTrace();
            }
        }
    }

    @Override
    public void populate(ChunkLoader iChunkProvider1, int chunkX, int chunkZ) {
        Sand.can_fall = false;
        Chunk chunk4 = this.generateChunk(chunkX, chunkZ);
        if (!chunk4.isPopulated) {
            chunk4.isPopulated = true;
            if (this.serverChunkGenerator != null) {
                this.serverChunkGenerator.populate(iChunkProvider1, chunkX, chunkZ);
                chunk4.setModified();
            }
        }

        Sand.can_fall = true;
    }

    @Override
    public boolean saveChunks(boolean z1, ProgressBar iProgressUpdate2) {
        int i3 = 0;

        for (Chunk chunk : this.chunkMap) {
            if (z1 && !chunk.neverSave) {
                this.saveChunkData(chunk);
            }

            if (chunk.save(z1)) {
                this.saveChunkToFile(chunk);
                chunk.isModified = false;
                if (++i3 == 24 && !z1) {
                    return false;
                }
            }
        }

        return z1 && this.chunkLoaderInstance == null ? true : true;
    }

    @Override
    public boolean unloadOldChunks() {
        if (!this.world.levelSaving) {
            for (int i1 = 0; i1 < 100; i1++) {
                if (!this.idList.isEmpty()) {
                    long integer2 = this.idList.iterator().nextLong();
                    Chunk chunk3 = this.id2ChunkMap.get(integer2);
                    chunk3.onChunkUnload();
                    this.saveChunkToFile(chunk3);
                    this.saveChunkData(chunk3);
                    this.idList.remove(integer2);
                    this.id2ChunkMap.remove(integer2);
                    this.chunkMap.remove(chunk3);
                }
            }
        }

        return this.serverChunkGenerator.unloadOldChunks();
    }

    @Override
    public boolean canSave() {
        return !this.world.levelSaving;
    }

    @Override
    public String loaderName() {
        return "ServerChunkCache: " + this.chunkMap.size();
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int i2, int i3, int i4) {
        return null;
    }
}
