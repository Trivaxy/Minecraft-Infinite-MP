package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class KeepAlivePacket extends Packet {
    public int randomId;

    public KeepAlivePacket() {
    }

    public KeepAlivePacket(int par1) {
        this.randomId = par1;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleKeepAlive(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.randomId = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.randomId);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
