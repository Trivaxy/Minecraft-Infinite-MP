package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class EntityStatePacket extends Packet {
    public int entityId;
    public byte entityStatus;

    public EntityStatePacket() {
    }

    public EntityStatePacket(int i1, byte b2) {
        this.entityId = i1;
        this.entityStatus = b2;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.entityStatus = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.entityStatus);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityState(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
