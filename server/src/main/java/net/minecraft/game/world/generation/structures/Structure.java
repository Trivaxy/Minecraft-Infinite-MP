package net.minecraft.game.world.generation.structures;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.ChunkLoader;

public abstract class Structure {
    public static final int MAXCHANCE = 1024;
    public World world;
    public StructureBoundingBox featureAABB;
    public int originChunkX;
    public int originChunkZ;
    public int centerX;
    public int centerZ;
    public StructureList featureProvider;

    public Structure(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        this.world = world;
        this.originChunkX = originChunkX;
        this.originChunkZ = originChunkZ;
        this.centerX = this.originChunkX * 16 + 8;
        this.centerZ = this.originChunkZ * 16 + 8;
        this.featureAABB = new StructureBoundingBox(
            originChunkX - this.getRadius() << 4,
            0,
            originChunkZ - this.getRadius() << 4,
            15 + (originChunkX + this.getRadius() << 4),
            255,
            15 + (originChunkZ + this.getRadius() << 4)
        );
    }

    public abstract int getRadius();

    public abstract int getSpawnChance();

    public void setup(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
    }

    public boolean shouldSpawn(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return rand.nextInt(1024) < this.getSpawnChance() && this.canSpawnHere(chunkProvider, world, rand, biome, chunkX, chunkZ);
    }

    public abstract boolean canSpawnHere(ChunkLoader var1, World var2, Random var3, Biome var4, int var5, int var6);

    public int minimumSeparation() {
        return 2 * this.getRadius();
    }

    public abstract void generate(int var1, int var2, short[] var3, byte[] var4, Biome[] var5);

    public abstract void populate(World var1, Random var2, int var3, int var4);
}
