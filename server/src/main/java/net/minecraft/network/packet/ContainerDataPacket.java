package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class ContainerDataPacket extends Packet {
    public int windowId;
    public int id;
    public int value;

    public ContainerDataPacket() {
    }

    public ContainerDataPacket(int i1, int i2, int i3) {
        this.windowId = i1;
        this.id = i2;
        this.value = i3;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleContainerData(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
        this.id = dataInputStream1.readShort();
        this.value = dataInputStream1.readShort();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
        dataOutputStream1.writeShort(this.id);
        dataOutputStream1.writeShort(this.value);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
