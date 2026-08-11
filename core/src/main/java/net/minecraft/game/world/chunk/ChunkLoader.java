package net.minecraft.game.world.chunk;

import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public interface ChunkLoader {
    boolean chunkExists(int var1, int var2);

    Chunk generateChunk(int var1, int var2);

    void populate(ChunkLoader var1, int var2, int var3);

    boolean saveChunks(boolean var1, ProgressBar var2);

    boolean unloadOldChunks();

    boolean canSave();

    String loaderName();

    Chunk justGenerateForHeight(int var1, int var2);

    BlockPosition findClosestStructure(World var1, Class<? extends Structure> var2, int var3, int var4, int var5);
}
