package net.minecraft.game.world.chunk;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class ChunkCache implements WorldSource {
    private Chunk[][] chunks;
    private World world;
    private int chunkX;
    private int chunkZ;
    private boolean chunksEmpty;

    public ChunkCache(World world, int x, int y, int z, int x2, int y2, int z2) {
        this.world = world;
        this.chunkX = x >> 4;
        this.chunkZ = z >> 4;
        int destX = x2 >> 4;
        int destZ = z2 >> 4;
        this.chunks = new Chunk[destX - this.chunkX + 1][destZ - this.chunkZ + 1];
        this.chunksEmpty = true;

        for (int chunkX = this.chunkX; chunkX <= destX; chunkX++) {
            for (int chunkZ = this.chunkZ; chunkZ <= destZ; chunkZ++) {
                Chunk chunk = world.getChunk(chunkX, chunkZ);
                if (chunk != null) {
                    this.chunks[chunkX - this.chunkX][chunkZ - this.chunkZ] = chunk;
                    if (!chunk.layerEmpty(y, y2)) {
                        this.chunksEmpty = false;
                    }
                }
            }
        }
    }

    @Override
    public boolean areChunksEmpty() {
        return this.chunksEmpty;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        if (y < 0) {
            return 0;
        } else if (y >= this.world.getWorldHeight()) {
            return 0;
        } else {
            int chunkX = (x >> 4) - this.chunkX;
            int chunkZ = (z >> 4) - this.chunkZ;
            if (chunkX >= 0 && chunkX < this.chunks.length && chunkZ >= 0 && chunkZ < this.chunks[chunkX].length) {
                Chunk chunk6 = this.chunks[chunkX][chunkZ];
                return chunk6 == null ? 0 : chunk6.getBlockID(x & 15, y, z & 15);
            } else {
                return 0;
            }
        }
    }

    @Override
    public TileEntity getTileEntity(int xCoord, int yCoord, int zCoord) {
        int x = (xCoord >> 4) - this.chunkX;
        int z = (zCoord >> 4) - this.chunkZ;
        return this.chunks[x][z].getTileEntity(xCoord & 15, yCoord, zCoord & 15);
    }

    @Override
    public float getBrightness(int x, int y, int z) {
        return this.world.getBrightness(x, y, z);
    }

    @Override
    public int getBlockMetadata(int xCoord, int yCoord, int zCoord) {
        if (yCoord < 0) {
            return 0;
        }

        if (yCoord >= this.world.getWorldHeight()) {
            return 0;
        }

        int x = (xCoord >> 4) - this.chunkX;
        int z = (zCoord >> 4) - this.chunkZ;
        return this.chunks[x][z].getBlockMetadata(xCoord & 15, yCoord, zCoord & 15);
    }

    @Override
    public Material getBlockMaterial(int x, int y, int z) {
        int id = this.getBlockId(x, y, z);
        return id == 0 ? MaterialList.air : BlockList.blocks[id].material;
    }

    @Override
    public boolean isBlockFull(int xCoord, int yCoord, int zCoord) {
        Block block = BlockList.blocks[this.getBlockId(xCoord, yCoord, zCoord)];
        return block == null ? false : block.fullBlock();
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        return this.world.isSolid(x, y, z);
    }

    @Override
    public int getBrightness(int x, int y, int z, int light) {
        return this.world.getBrightness(x, y, z, light);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return BlockList.blocks[this.getBlockId(x, y, z)];
    }
}
