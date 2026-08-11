package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class PickupPacket extends Packet {
    public int collectedEntityId;
    public int collectorEntityId;

    public PickupPacket() {
    }

    public PickupPacket(int i1, int i2) {
        this.collectedEntityId = i1;
        this.collectorEntityId = i2;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.collectedEntityId = dataInputStream1.readInt();
        this.collectorEntityId = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.collectedEntityId);
        dataOutputStream1.writeInt(this.collectorEntityId);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleCollect(this);
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
