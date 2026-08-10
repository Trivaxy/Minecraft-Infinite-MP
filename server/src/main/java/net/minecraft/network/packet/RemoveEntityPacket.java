package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class RemoveEntityPacket extends Packet {
    public int entityId;

    public RemoveEntityPacket() {
    }

    public RemoveEntityPacket(int i1) {
        this.entityId = i1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleRemoveEntity(this);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
