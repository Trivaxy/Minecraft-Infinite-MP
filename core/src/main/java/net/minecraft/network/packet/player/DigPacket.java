package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class DigPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int face;
    public int status;

    public DigPacket() {
    }

    public DigPacket(int i1, int i2, int i3, int i4, int i5) {
        this.status = i1;
        this.xPosition = i2;
        this.yPosition = i3;
        this.zPosition = i4;
        this.face = i5;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.status = dataInputStream1.read();
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readShort();
        this.zPosition = dataInputStream1.readInt();
        this.face = dataInputStream1.read();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.write(this.status);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeShort(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.write(this.face);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleBlockDig(this);
    }

    @Override
    public int getPacketSize() {
        return 12;
    }
}
