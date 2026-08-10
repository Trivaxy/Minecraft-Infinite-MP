package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class UpdateSectionPacket extends Packet {
    public int xPosition;
    public int zPosition;
    public int[] coordinateArray;
    public short[] typeArray;
    public short[] metadataArray;
    public int sizeOfAllArrays;

    public UpdateSectionPacket() {
        this.isDelayed = true;
    }

    public UpdateSectionPacket(int x, int z, int[] positions, int size, World level) {
        this.isDelayed = true;
        this.xPosition = x;
        this.zPosition = z;
        this.sizeOfAllArrays = size;
        this.coordinateArray = new int[size];
        this.typeArray = new short[size];
        this.metadataArray = new short[size];
        Chunk cunk = level.getChunk(x, z);

        for (int i7 = 0; i7 < size; i7++) {
            x = positions[i7] >> 20 & 15;
            int y = positions[i7] >> 16 & 15;
            z = positions[i7] & 0xFFFF;
            this.coordinateArray[i7] = positions[i7];
            this.typeArray[i7] = (short)cunk.getBlockID(x, z, y);
            this.metadataArray[i7] = (short)cunk.getBlockMetadata(x, z, y);
        }
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.coordinateArray = IOUtil.readIntArray(dataInputStream1);
        this.typeArray = IOUtil.readShortArray(dataInputStream1);
        this.metadataArray = IOUtil.readShortArray(dataInputStream1);
        this.sizeOfAllArrays = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.zPosition);
        IOUtil.writeIntArray(dataOutputStream1, this.coordinateArray);
        IOUtil.writeShortArray(dataOutputStream1, this.typeArray);
        IOUtil.writeShortArray(dataOutputStream1, this.metadataArray);
        dataOutputStream1.writeInt(this.sizeOfAllArrays);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleSectionUpdate(this);
    }

    @Override
    public int getPacketSize() {
        return 20 + this.sizeOfAllArrays * 8;
    }
}
