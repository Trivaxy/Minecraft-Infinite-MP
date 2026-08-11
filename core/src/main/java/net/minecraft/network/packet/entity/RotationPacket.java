package net.minecraft.network.packet.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class RotationPacket extends Packet {
    public int entityId;
    public byte headRotationYaw;

    public RotationPacket() {
    }

    public RotationPacket(int i1, byte b2) {
        this.entityId = i1;
        this.headRotationYaw = b2;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.headRotationYaw = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.headRotationYaw);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityRotation(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
