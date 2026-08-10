package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkSection;
import net.minecraft.game.world.chunk.NibbleArray;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class UpdateChunkPacket extends Packet {
    public int xCh;
    public int zCh;
    public int hasLowerBlocks;
    public int hasUpperBlocks;
    public byte[] chunkData;
    public boolean includeInitialize;
    private int tempLength;
    private int biome;
    private static byte[] temp = new byte[0];

    public UpdateChunkPacket() {
        this.isDelayed = true;
    }

    public UpdateChunkPacket(Chunk par1Chunk, boolean par2, int par3) {
        this.isDelayed = true;
        this.xCh = par1Chunk.xPosition;
        this.zCh = par1Chunk.zPosition;
        this.includeInitialize = par2;
        if (par2) {
            par3 = -1;
        }

        ChunkSection[] sections = par1Chunk.getSections();
        int i = 0;
        int j = 0;

        for (int k = 0; k < sections.length; k++) {
            if (sections[k] != null && (!par2 || !sections[k].isEmpty()) && (par3 & 1 << k) != 0) {
                this.hasLowerBlocks |= 1 << k;
                i++;
                if (sections[k].getUpperBlockArray() != null) {
                    this.hasUpperBlocks |= 1 << k;
                    j++;
                }
            }
        }

        int l = 2048 * (6 * i + j);
        if (par2) {
            l += 256;
        }

        if (temp.length < l) {
            temp = new byte[l];
        }

        byte[] abyte0 = temp;
        int i1 = 0;

        for (int j1 = 0; j1 < sections.length; j1++) {
            if (sections[j1] != null && (!par2 || !sections[j1].isEmpty()) && (par3 & 1 << j1) != 0) {
                byte[] abyte2 = sections[j1].getLowerBlockArray();
                System.arraycopy(abyte2, 0, abyte0, i1, abyte2.length);
                i1 += abyte2.length;
            }
        }

        for (int k1 = 0; k1 < sections.length; k1++) {
            if (sections[k1] != null && (!par2 || !sections[k1].isEmpty()) && (par3 & 1 << k1) != 0) {
                byte[] metadataArray = sections[k1].getBlockMetadataArray();
                System.arraycopy(metadataArray, 0, abyte0, i1, metadataArray.length);
                i1 += metadataArray.length;
            }
        }

        for (int l1 = 0; l1 < sections.length; l1++) {
            if (sections[l1] != null && (!par2 || !sections[l1].isEmpty()) && (par3 & 1 << l1) != 0) {
                NibbleArray nibblearray1 = sections[l1].getBlocklightArray();
                System.arraycopy(nibblearray1.data, 0, abyte0, i1, nibblearray1.data.length);
                i1 += nibblearray1.data.length;
            }
        }

        for (int i2 = 0; i2 < sections.length; i2++) {
            if (sections[i2] != null && (!par2 || !sections[i2].isEmpty()) && (par3 & 1 << i2) != 0) {
                NibbleArray nibblearray2 = sections[i2].getSkylightArray();
                System.arraycopy(nibblearray2.data, 0, abyte0, i1, nibblearray2.data.length);
                i1 += nibblearray2.data.length;
            }
        }

        if (j > 0) {
            for (int j2 = 0; j2 < sections.length; j2++) {
                if (sections[j2] != null && (!par2 || !sections[j2].isEmpty()) && sections[j2].getUpperBlockArray() != null && (par3 & 1 << j2) != 0) {
                    NibbleArray nibblearray3 = sections[j2].getUpperBlockArray();
                    System.arraycopy(nibblearray3.data, 0, abyte0, i1, nibblearray3.data.length);
                    i1 += nibblearray3.data.length;
                }
            }
        }

        if (par2) {
            byte[] abyte1 = par1Chunk.getBiomes();
            System.arraycopy(abyte1, 0, abyte0, i1, abyte1.length);
            i1 += abyte1.length;
        }

        Deflater deflater = new Deflater(-1);

        try {
            deflater.setInput(abyte0, 0, i1);
            deflater.finish();
            this.chunkData = new byte[i1];
            this.tempLength = deflater.deflate(this.chunkData);
        } finally {
            deflater.end();
        }
    }

    @Override
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException {
        this.xCh = par1DataInputStream.readInt();
        this.zCh = par1DataInputStream.readInt();
        this.includeInitialize = par1DataInputStream.readBoolean();
        this.hasLowerBlocks = par1DataInputStream.readInt();
        this.hasUpperBlocks = par1DataInputStream.readInt();
        this.tempLength = par1DataInputStream.readInt();
        this.biome = par1DataInputStream.readInt();
        if (temp.length < this.tempLength) {
            temp = new byte[this.tempLength];
        }

        par1DataInputStream.readFully(temp, 0, this.tempLength);

        // The payload is 12288 bytes per section (lower ids, metadata, blocklight,
        // skylight), plus 2048 bytes for each section whose ids spill into the upper
        // nibble array, plus 256 biome bytes when initializing.
        int k = 12288 * Integer.bitCount(this.hasLowerBlocks) + 2048 * Integer.bitCount(this.hasUpperBlocks);
        if (this.includeInitialize) {
            k += 256;
        }

        this.chunkData = new byte[k];
        Inflater inflater = new Inflater();
        inflater.setInput(temp, 0, this.tempLength);

        try {
            int uncompressedLength = inflater.inflate(this.chunkData);
            if (uncompressedLength != k) {
                throw new IOException(
                    "Chunk data size mismatch: expected " + k + " bytes but decompressed " + uncompressedLength + " (mask=0x" + Integer.toHexString(this.hasLowerBlocks) + ")"
                );
            }
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }
    }

    @Override
    public void writePacketData(DataOutputStream var1) throws IOException {
        var1.writeInt(this.xCh);
        var1.writeInt(this.zCh);
        var1.writeBoolean(this.includeInitialize);
        var1.writeInt(this.hasLowerBlocks);
        var1.writeInt(this.hasUpperBlocks);
        var1.writeInt(this.tempLength);
        var1.writeInt(this.biome);
        var1.write(this.chunkData, 0, this.tempLength);
    }

    @Override
    public void processPacket(NetworkHandler var1) {
        var1.handleChunkUpdate(this);
    }

    @Override
    public int getPacketSize() {
        return 25 + this.tempLength;
    }
}
