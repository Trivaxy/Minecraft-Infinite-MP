package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet32EntityLook extends EntityMovementPacket {
    public Packet32EntityLook() {
        this.rotating = true;
    }

    public Packet32EntityLook(int i1, byte b2, byte b3) {
        super(i1);
        this.yaw = b2;
        this.pitch = b3;
        this.rotating = true;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        super.readPacketData(dataInputStream1);
        this.yaw = dataInputStream1.readByte();
        this.pitch = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        super.writePacketData(dataOutputStream1);
        dataOutputStream1.writeByte(this.yaw);
        dataOutputStream1.writeByte(this.pitch);
    }

    @Override
    public int getPacketSize() {
        return 6;
    }
}
