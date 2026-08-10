package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.math.MathHelper;

public class TeleportPacket extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte yaw;
    public byte pitch;

    public TeleportPacket() {
    }

    public TeleportPacket(Entity entity1) {
        this.entityId = entity1.entityId;
        this.xPosition = MathHelper.floor_double(entity1.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entity1.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entity1.posZ * 32.0);
        this.yaw = (byte)(entity1.yaw * 256.0F / 360.0F);
        this.pitch = (byte)(entity1.pitch * 256.0F / 360.0F);
    }

    public TeleportPacket(int i1, int i2, int i3, int i4, byte b5, byte b6) {
        this.entityId = i1;
        this.xPosition = i2;
        this.yPosition = i3;
        this.zPosition = i4;
        this.yaw = b5;
        this.pitch = b6;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.yaw = (byte)dataInputStream1.read();
        this.pitch = (byte)dataInputStream1.read();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.write(this.yaw);
        dataOutputStream1.write(this.pitch);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityTeleport(this);
    }

    @Override
    public int getPacketSize() {
        return 34;
    }
}
