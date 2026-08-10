package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class OpPacket extends Packet {
    public int entityId;
    public boolean state;

    public OpPacket() {
    }

    public OpPacket(int eid, boolean state) {
        this.entityId = eid;
        this.state = state;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.entityId = dis.readInt();
        this.state = dis.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        dos.writeInt(this.entityId);
        dos.writeBoolean(this.state);
    }

    @Override
    public void processPacket(NetworkHandler h) {
        h.handleOp(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
