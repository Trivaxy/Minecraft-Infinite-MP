package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class ContainerClosePacket extends Packet {
    public int windowId;

    public ContainerClosePacket() {
    }

    public ContainerClosePacket(int i1) {
        this.windowId = i1;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleContainerClose(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
    }

    @Override
    public int getPacketSize() {
        return 1;
    }
}
