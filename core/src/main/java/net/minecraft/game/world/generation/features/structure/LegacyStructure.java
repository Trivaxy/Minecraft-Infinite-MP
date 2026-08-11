package net.minecraft.game.world.generation.features.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.generation.features.LargeFeature;

public abstract class LegacyStructure extends LargeFeature {
    protected Long2ObjectOpenHashMap<LegacyStructureStart> coordMap = new Long2ObjectOpenHashMap<>();

    @Override
    public void generate(ChunkLoader loader, World world, int x, int z, short[] shart, byte[] gayta) {
        super.generate(loader, world, x, z, shart, gayta);
    }

    @Override
    protected void recursiveGenerate(World world, int x, int z, int x0, int z0, short[] shart, byte[] gayta) {
        if (!this.coordMap.containsKey(ChunkUtils.getPackedCoords(x, z)) && this.canSpawnStructureAtCoords(x, z)) {
            LegacyStructureStart start = this.getStructureStart(x, z);
            this.coordMap.put(ChunkUtils.getPackedCoords(x, z), start);
        }
    }

    public boolean generateStructuresInChunk(World world, Random random, int cx, int cz) {
        int x = (cx << 4) + 8;
        int z = (cz << 4) + 8;
        boolean canGenerateHere = false;

        for (LegacyStructureStart start : this.coordMap.values()) {
            if (start.getBoundingBox().intersectsWith(x, z, x + 15, z + 15)) {
                start.generateStructure(world, random, new LegacyStructureBoundingBox(x, z, x + 15, z + 15));
                canGenerateHere = true;
            }
        }

        return canGenerateHere;
    }

    protected abstract boolean canSpawnStructureAtCoords(int var1, int var2);

    protected abstract LegacyStructureStart getStructureStart(int var1, int var2);
}
