package net.minecraft.network.packet.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class EntityActionPacket extends Packet {
    public int entityId;
    public int state;

    public EntityActionPacket() {
    }

    public EntityActionPacket(Entity entity1, int i2) {
        this.entityId = entity1.entityId;
        this.state = i2;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.state = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.state);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityAction(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
