package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;

public class RideEntityPacket extends Packet {
    public int entityId;
    public int vehicleEntityId;

    public RideEntityPacket() {
    }

    public RideEntityPacket(Entity entity1, Entity entity2) {
        this.entityId = entity1.entityId;
        this.vehicleEntityId = entity2 != null ? entity2.entityId : -1;
    }

    @Override
    public int getPacketSize() {
        return 8;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.vehicleEntityId = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeInt(this.vehicleEntityId);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleRideEntity(this);
    }
}
