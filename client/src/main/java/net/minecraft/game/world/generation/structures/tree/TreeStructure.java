package net.minecraft.game.world.generation.structures.tree;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.temperate.Forest;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.StructureList;

public class TreeStructure extends Structure {
    public TreeStructure(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
    }

    @Override
    public int getRadius() {
        return 1;
    }

    @Override
    public int getSpawnChance() {
        return 5;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof Forest;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        if (chunkX == this.originChunkX && chunkZ == this.originChunkZ) {
            int x = chunkX << 4;
            int z = chunkZ << 4;
            DungeonTree.INSTANCE.place(world, rand, x + 8, world.getSurfaceHeightmap(x, z), z + 8);
        }
    }
}
