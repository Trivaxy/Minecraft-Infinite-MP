package net.minecraft.game.world.generation.features;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.ChunkLoader;

public abstract class LargeFeature {
    protected int range = 8;
    protected static Random rand = new Random();
    protected World world;

    public void generate(ChunkLoader chunkProvider, World world, int chunkX, int chunkZ, short[] blocks, byte[] metadata) {
        int range = this.range;
        this.world = world;
        rand.setSeed(world.getSeed());
        long seed1 = rand.nextLong() / 2L * 2L + 1L;
        long seed2 = rand.nextLong() / 2L * 2L + 1L;

        for (int x = chunkX - range; x <= chunkX + range; x++) {
            for (int z = chunkZ - range; z <= chunkZ + range; z++) {
                long seedX = x * seed1;
                long seedZ = z * seed2;
                rand.setSeed(seedX ^ seedZ ^ world.getSeed());
                this.recursiveGenerate(world, x, z, chunkX, chunkZ, blocks, metadata);
            }
        }
    }

    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originalX, int originalZ, short[] blocks, byte[] metadata) {
    }
}
