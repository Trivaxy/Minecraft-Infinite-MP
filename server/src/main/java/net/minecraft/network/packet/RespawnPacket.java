package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class RespawnPacket extends Packet {
    public byte dimension;

    public RespawnPacket() {
    }

    public RespawnPacket(byte b1) {
        this.dimension = b1;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleRespawn(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.dimension = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.dimension);
    }

    @Override
    public int getPacketSize() {
        return 1;
    }
}
