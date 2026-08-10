package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class EntityMovementPacket extends Packet {
    public int entityId;
    public byte xPosition;
    public byte yPosition;
    public byte zPosition;
    public byte yaw;
    public byte pitch;
    public boolean rotating = false;

    public EntityMovementPacket() {
    }

    public EntityMovementPacket(int i1) {
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
        netHandler1.handleEntityMovement(this);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
