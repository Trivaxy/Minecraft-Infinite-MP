package net.minecraft.network.packet.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class EntityVelocityPacket extends Packet {
    public int entityId;
    public int motionX;
    public int motionY;
    public int motionZ;

    public EntityVelocityPacket() {
    }

    public EntityVelocityPacket(Entity entity1) {
        this(entity1.entityId, entity1.motionX, entity1.motionY, entity1.motionZ);
    }

    public EntityVelocityPacket(int i1, double d2, double d4, double d6) {
        this.entityId = i1;
        double d8 = 3.9;
        if (d2 < -d8) {
            d2 = -d8;
        }

        if (d4 < -d8) {
            d4 = -d8;
        }

        if (d6 < -d8) {
            d6 = -d8;
        }

        if (d2 > d8) {
            d2 = d8;
        }

        if (d4 > d8) {
            d4 = d8;
        }

        if (d6 > d8) {
            d6 = d8;
        }

        this.motionX = (int)(d2 * 8000.0);
        this.motionY = (int)(d4 * 8000.0);
        this.motionZ = (int)(d6 * 8000.0);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.motionX = dataInputStream1.readShort();
        this.motionY = dataInputStream1.readShort();
        this.motionZ = dataInputStream1.readShort();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeShort(this.motionX);
        dataOutputStream1.writeShort(this.motionY);
        dataOutputStream1.writeShort(this.motionZ);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityVelocity(this);
    }

    @Override
    public int getPacketSize() {
        return 10;
    }
}
