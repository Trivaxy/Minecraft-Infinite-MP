package net.minecraft.client.network.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.chunk.EmptyChunk;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public class ChunkLoaderMP implements ChunkLoader {
    private final Chunk blankChunk;
    private final Long2ObjectOpenHashMap<Chunk> chunkMapping = new Long2ObjectOpenHashMap<>();
    private final World world;

    public ChunkLoaderMP(World world) {
        this.blankChunk = new EmptyChunk(world, 0, 0);
        this.world = world;
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return this.chunkMapping.containsKey(ChunkUtils.getPackedCoords(chunkX, chunkZ));
    }

    public void loadChunk(int chunkX, int chunkZ) {
        Chunk chunk = this.generateChunk(chunkX, chunkZ);
        if (!chunk.isLoaded()) {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkUtils.getPackedCoords(chunkX, chunkZ));
    }

    public Chunk prepareChunk(int chunkX, int chunkZ) {
        long chunkCoords = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        Chunk chunk = new Chunk(this.world, chunkX, chunkZ);
        this.chunkMapping.put(chunkCoords, chunk);
        chunk.isLoaded = true;
        return chunk;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        long chunkCoords = ChunkUtils.getPackedCoords(chunkX, chunkZ);
        Chunk chunk = this.chunkMapping.get(chunkCoords);
        return chunk == null ? this.blankChunk : chunk;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        return this.generateChunk(chunkX, chunkZ);
    }

    @Override
    public boolean saveChunks(boolean save, ProgressBar progressListener) {
        return true;
    }

    @Override
    public boolean unloadOldChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public void populate(ChunkLoader chunkProvider, int chunkX, int chunkZ) {
    }

    @Override
    public String loaderName() {
        return "MultiplayerChunkCache: " + this.chunkMapping.size();
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return null;
    }
}
