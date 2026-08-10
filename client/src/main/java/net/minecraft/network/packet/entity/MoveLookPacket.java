package net.minecraft.network.packet.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MoveLookPacket extends EntityMovementPacket {
    public MoveLookPacket() {
        this.rotating = true;
    }

    public MoveLookPacket(int i1, byte b2, byte b3, byte b4, byte b5, byte b6) {
        super(i1);
        this.xPosition = b2;
        this.yPosition = b3;
        this.zPosition = b4;
        this.yaw = b5;
        this.pitch = b6;
        this.rotating = true;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        super.readPacketData(dataInputStream1);
        this.xPosition = dataInputStream1.readByte();
        this.yPosition = dataInputStream1.readByte();
        this.zPosition = dataInputStream1.readByte();
        this.yaw = dataInputStream1.readByte();
        this.pitch = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        super.writePacketData(dataOutputStream1);
        dataOutputStream1.writeByte(this.xPosition);
        dataOutputStream1.writeByte(this.yPosition);
        dataOutputStream1.writeByte(this.zPosition);
        dataOutputStream1.writeByte(this.yaw);
        dataOutputStream1.writeByte(this.pitch);
    }

    @Override
    public int getPacketSize() {
        return 9;
    }
}
