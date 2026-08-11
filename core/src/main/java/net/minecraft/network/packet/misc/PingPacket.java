package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class PingPacket extends Packet {
    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handlePing(this);
    }

    @Override
    public int getPacketSize() {
        return 0;
    }
}
