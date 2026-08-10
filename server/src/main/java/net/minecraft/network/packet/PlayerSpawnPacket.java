package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.player.Player;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;
import net.minecraft.util.math.MathHelper;

public class PlayerSpawnPacket extends Packet {
    public int entityId;
    public String name;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte yaw;
    public byte pitch;
    public boolean serverOp;

    public PlayerSpawnPacket() {
    }

    public PlayerSpawnPacket(Player entityPlayer1) {
        this.entityId = entityPlayer1.entityId;
        this.name = entityPlayer1.username;
        this.xPosition = MathHelper.floor_double(entityPlayer1.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entityPlayer1.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entityPlayer1.posZ * 32.0);
        this.yaw = (byte)(entityPlayer1.yaw * 256.0F / 360.0F);
        this.pitch = (byte)(entityPlayer1.pitch * 256.0F / 360.0F);
        this.serverOp = entityPlayer1.op;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.name = IOUtil.readUtf16String(dataInputStream1);
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.yaw = dataInputStream1.readByte();
        this.pitch = dataInputStream1.readByte();
        this.serverOp = dataInputStream1.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        IOUtil.writeUtf16String(this.name, dataOutputStream1);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeByte(this.yaw);
        dataOutputStream1.writeByte(this.pitch);
        dataOutputStream1.writeBoolean(this.serverOp);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handlePlayerSpawn(this);
    }

    @Override
    public int getPacketSize() {
        return 29;
    }
}
