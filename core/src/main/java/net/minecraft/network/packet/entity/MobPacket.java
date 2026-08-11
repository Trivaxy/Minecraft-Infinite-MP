package net.minecraft.network.packet.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.game.entity.DataSyncer;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;

public class MobPacket extends Packet {
    public int entityId;
    public int type;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte yaw;
    public byte pitch;
    private DataSyncer metaData;
    private List<DataSyncer.DataItem> receivedMetadata;

    public MobPacket() {
    }

    public MobPacket(Mob entityLiving1) {
        this.entityId = entityLiving1.entityId;
        this.type = EntityManager.getEntityID(entityLiving1);
        this.xPosition = MathHelper.floor_double(entityLiving1.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entityLiving1.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entityLiving1.posZ * 32.0);
        this.yaw = (byte)(entityLiving1.yaw * 256.0F / 360.0F);
        this.pitch = (byte)(entityLiving1.pitch * 256.0F / 360.0F);
        this.metaData = entityLiving1.getDataSync();
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.type = dataInputStream1.readInt();
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.yaw = dataInputStream1.readByte();
        this.pitch = dataInputStream1.readByte();
        this.receivedMetadata = DataSyncer.readObjects(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeInt(this.type);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeByte(this.yaw);
        dataOutputStream1.writeByte(this.pitch);
        this.metaData.writeWatchableObjects(dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleMobSpawn(this);
    }

    @Override
    public int getPacketSize() {
        return 20;
    }

    public List<DataSyncer.DataItem> getMetadata() {
        return this.receivedMetadata;
    }
}
