package net.minecraft.game.world.chunk;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;

public class ChunkSection {
    private NibbleArray blockArray;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;
    private byte[] lowerBlockArray;
    private byte[] blockMetadataArray;
    private int yBase;
    private int blocksCount;
    private int ticksCount;

    public ChunkSection(int y) {
        this.yBase = y;
        this.lowerBlockArray = new byte[4096];
        this.blockMetadataArray = new byte[4096];
        this.skylightArray = new NibbleArray(this.lowerBlockArray.length, 4);
        this.blocklightArray = new NibbleArray(this.lowerBlockArray.length, 4);
    }

    public int getBlockID(int x, int y, int z) {
        int id = this.lowerBlockArray[y << 8 | z << 4 | x] & 255;
        return this.blockArray != null ? this.blockArray.get(x, y, z) << 8 | id : id;
    }

    public void setBlockID(int x, int y, int z, int id) {
        int existingID = this.lowerBlockArray[y << 8 | z << 4 | x] & 255;
        if (this.blockArray != null) {
            existingID |= this.blockArray.get(x, y, z) << 8;
        }

        if (id >= 0 && existingID >= 0) {
            if (existingID == 0 && id != 0) {
                this.blocksCount++;
                if (BlockList.blocks[id] != null && Block.canTick.get(id)) {
                    this.ticksCount++;
                }
            } else if (existingID != 0 && id == 0) {
                this.blocksCount--;
                if (BlockList.blocks[existingID] != null && Block.canTick.get(existingID)) {
                    this.ticksCount--;
                }
            } else if (BlockList.blocks[existingID] == null || !Block.canTick.get(existingID) || BlockList.blocks[id] != null && Block.canTick.get(id)) {
                if ((BlockList.blocks[existingID] == null || !Block.canTick.get(existingID)) && BlockList.blocks[id] != null && Block.canTick.get(id)) {
                    this.ticksCount++;
                }
            } else {
                this.ticksCount--;
            }

            this.lowerBlockArray[y << 8 | z << 4 | x] = (byte)(id & 0xFF);
            if (id > 255) {
                if (this.blockArray == null) {
                    this.blockArray = new NibbleArray(this.lowerBlockArray.length, 4);
                }

                this.blockArray.set(x, y, z, (id & 3840) >> 8);
            } else if (this.blockArray != null) {
                this.blockArray.set(x, y, z, 0);
            }
        }
    }

    public int getBlockMetadata(int x, int y, int z) {
        return this.blockMetadataArray[y << 8 | z << 4 | x] & 0xFF;
    }

    public void setBlockMetadata(int x, int y, int z, int meta) {
        this.blockMetadataArray[y << 8 | z << 4 | x] = (byte)meta;
    }

    public boolean isEmpty() {
        return this.blocksCount == 0;
    }

    public boolean needsTick() {
        return this.ticksCount > 0;
    }

    public int getY() {
        return this.yBase;
    }

    public void setSkylightValue(int x, int y, int z, int light) {
        this.skylightArray.set(x, y, z, light);
    }

    public int getExtSkylightValue(int x, int y, int z) {
        return this.skylightArray.get(x, y, z);
    }

    public void setBlocklightValue(int x, int y, int z, int light) {
        this.blocklightArray.set(x, y, z, light);
    }

    public int getBlocklightValue(int x, int y, int z) {
        return this.blocklightArray.get(x, y, z);
    }

    public void calculateBlocks() {
        this.blocksCount = 0;
        this.ticksCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int id = this.getBlockID(x, y, z);
                    if (id > 0) {
                        if (BlockList.blocks[id] == null) {
                            this.lowerBlockArray[y << 8 | z << 4 | x] = 0;
                            if (this.blockArray != null) {
                                this.blockArray.set(x, y, z, 0);
                            }
                        } else {
                            this.blocksCount++;
                            if (Block.canTick.get(id)) {
                                this.ticksCount++;
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeInvalidBlocks() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int id = this.getBlockID(x, y, z);
                    if (BlockList.blocks[id] == null && id != 0) {
                        this.setBlockID(x, y, z, 0);
                    }
                }
            }
        }
    }

    public int getBlocks() {
        return this.blocksCount;
    }

    public byte[] getLowerBlockArray() {
        return this.lowerBlockArray;
    }

    public void clearUpperBlockArray() {
        this.blockArray = null;
    }

    public NibbleArray getUpperBlockArray() {
        return this.blockArray;
    }

    public byte[] getBlockMetadataArray() {
        return this.blockMetadataArray;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public void setLowerBlockArray(byte[] arr) {
        this.lowerBlockArray = arr;
    }

    public void setUpperBlockArray(NibbleArray arr) {
        this.blockArray = arr;
    }

    public void setBlockMetadataArray(byte[] bytes) {
        this.blockMetadataArray = bytes;
    }

    public void setBlocklightArray(NibbleArray arr) {
        this.blocklightArray = arr;
    }

    public void setSkylightArray(NibbleArray arr) {
        this.skylightArray = arr;
    }

    public NibbleArray createUpperBlockArray() {
        this.blockArray = new NibbleArray(this.lowerBlockArray.length, 4);
        return this.blockArray;
    }
}
