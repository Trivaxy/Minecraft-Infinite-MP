package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class BlockEventPacket extends Packet {
    public int sfx;
    public int data;
    public int x;
    public int y;
    public int z;

    public BlockEventPacket() {
    }

    public BlockEventPacket(int i1, int i2, int i3, int i4, int i5) {
        this.sfx = i1;
        this.x = i2;
        this.y = i3;
        this.z = i4;
        this.data = i5;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.sfx = dataInputStream1.readInt();
        this.x = dataInputStream1.readInt();
        this.y = dataInputStream1.readShort();
        this.z = dataInputStream1.readInt();
        this.data = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.sfx);
        dataOutputStream1.writeInt(this.x);
        dataOutputStream1.writeShort(this.y);
        dataOutputStream1.writeInt(this.z);
        dataOutputStream1.writeInt(this.data);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleBlockEvent(this);
    }

    @Override
    public int getPacketSize() {
        return 21;
    }
}
