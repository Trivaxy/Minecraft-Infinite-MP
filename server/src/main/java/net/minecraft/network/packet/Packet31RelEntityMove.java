package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet31RelEntityMove extends EntityMovementPacket {
    public Packet31RelEntityMove() {
    }

    public Packet31RelEntityMove(int i1, byte b2, byte b3, byte b4) {
        super(i1);
        this.xPosition = b2;
        this.yPosition = b3;
        this.zPosition = b4;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        super.readPacketData(dataInputStream1);
        this.xPosition = dataInputStream1.readByte();
        this.yPosition = dataInputStream1.readByte();
        this.zPosition = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        super.writePacketData(dataOutputStream1);
        dataOutputStream1.writeByte(this.xPosition);
        dataOutputStream1.writeByte(this.yPosition);
        dataOutputStream1.writeByte(this.zPosition);
    }

    @Override
    public int getPacketSize() {
        return 7;
    }
}
