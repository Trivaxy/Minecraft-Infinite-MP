package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class UpdateTimePacket extends Packet {
    public long time;

    public UpdateTimePacket() {
    }

    public UpdateTimePacket(long j1) {
        this.time = j1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.time = dataInputStream1.readLong();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeLong(this.time);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleUpdateTime(this);
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
