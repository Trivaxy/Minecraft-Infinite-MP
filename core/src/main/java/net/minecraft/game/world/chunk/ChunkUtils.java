package net.minecraft.game.world.chunk;

public class ChunkUtils {
    public static long getPackedCoords(int chunkX, int chunkZ) {
        return chunkX & 4294967295L | (chunkZ & 4294967295L) << 32;
    }

    public static int getChunkX(long packedChunkCoord) {
        return (int)(packedChunkCoord & 4294967295L);
    }

    public static int getChunkZ(long packedChunkCoord) {
        return (int)(packedChunkCoord >>> 32 & 4294967295L);
    }
}
