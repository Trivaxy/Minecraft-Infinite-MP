package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class GeneratorPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public boolean state;

    public GeneratorPacket() {
    }

    public GeneratorPacket(int x, int y, int z, boolean active) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.state = active;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.xPosition = dis.readInt();
        this.yPosition = dis.readInt();
        this.zPosition = dis.readInt();
        this.state = dis.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        dos.writeInt(this.xPosition);
        dos.writeInt(this.yPosition);
        dos.writeInt(this.zPosition);
        dos.writeBoolean(this.state);
    }

    @Override
    public void processPacket(NetworkHandler h) {
        h.handleGenerator(this);
    }

    @Override
    public int getPacketSize() {
        return 13;
    }
}
