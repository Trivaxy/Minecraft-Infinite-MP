package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class SpawnPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;

    public SpawnPacket() {
    }

    public SpawnPacket(int i1, int i2, int i3) {
        this.xPosition = i1;
        this.yPosition = i2;
        this.zPosition = i3;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleSpawn(this);
    }

    @Override
    public int getPacketSize() {
        return 12;
    }
}
