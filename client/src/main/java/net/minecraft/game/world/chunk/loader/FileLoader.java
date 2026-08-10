package net.minecraft.game.world.chunk.loader;

import java.io.IOException;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;

public interface FileLoader {
    Chunk loadChunk(World var1, int var2, int var3) throws IOException;

    void saveChunk(World var1, Chunk var2) throws IOException;
}
