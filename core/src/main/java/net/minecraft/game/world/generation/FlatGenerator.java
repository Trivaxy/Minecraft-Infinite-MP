package net.minecraft.game.world.generation;

import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.util.ProgressBar;

public class FlatGenerator implements ChunkLoader {
    private World world;

    public FlatGenerator(World w) {
        this.world = w;
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public Chunk generateChunk(int cx, int cz) {
        short[] shart = new short[65536];
        byte[] metadataArray = new byte[256 * this.world.getWorldHeight()];
        int blocklength = shart.length / 256;
        WorldInfo info = this.world.getSettings();

        for (int cx_ = 0; cx_ < 16; cx_++) {
            for (int cz_ = 0; cz_ < 16; cz_++) {
                for (int cy = 0; cy < blocklength; cy++) {
                    int blockid = 0;
                    if (cy == info.topLayer) {
                        blockid = info.thirdBlock;
                    } else if (cy <= info.thirdLayer) {
                        blockid = info.secondBlock;
                    } else if (cy <= info.secondLayer) {
                        blockid = info.firstBlock;
                    } else if (cy <= info.firstLayer) {
                        blockid = info.bottomBlock;
                    }

                    shart[cy << 8 | cz_ << 4 | cx_] = (short)blockid;
                }
            }
        }

        Chunk chunk = new Chunk(this.world, shart, metadataArray, cx, cz);
        chunk.generateHeightMap();
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public Chunk justGenerateForHeight(int chunkX, int chunkZ) {
        return this.generateChunk(chunkX, chunkZ);
    }

    @Override
    public void populate(ChunkLoader src, int cx, int cz) {
    }

    @Override
    public boolean saveChunks(boolean flag, ProgressBar progress) {
        return false;
    }

    @Override
    public boolean unloadOldChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String loaderName() {
        return "FlatGenerator";
    }

    @Override
    public BlockPosition findClosestStructure(World world, Class<? extends Structure> target, int x, int y, int z) {
        return null;
    }
}
