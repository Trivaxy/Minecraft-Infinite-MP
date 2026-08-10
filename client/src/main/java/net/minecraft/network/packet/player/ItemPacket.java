package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;
import net.minecraft.util.math.MathHelper;

public class ItemPacket extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte rotation;
    public byte pitch;
    public byte roll;
    public ItemStack itemStack;

    public ItemPacket() {
    }

    public ItemPacket(DroppedItem entityItem1) {
        this.entityId = entityItem1.entityId;
        this.itemStack = entityItem1.item;
        this.xPosition = MathHelper.floor_double(entityItem1.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entityItem1.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entityItem1.posZ * 32.0);
        this.rotation = (byte)(entityItem1.motionX * 128.0);
        this.pitch = (byte)(entityItem1.motionY * 128.0);
        this.roll = (byte)(entityItem1.motionZ * 128.0);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.itemStack = IOUtil.readItemStack(dataInputStream1);
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.rotation = dataInputStream1.readByte();
        this.pitch = dataInputStream1.readByte();
        this.roll = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        IOUtil.writeItemStack(this.itemStack, dataOutputStream1);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeByte(this.rotation);
        dataOutputStream1.writeByte(this.pitch);
        dataOutputStream1.writeByte(this.roll);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleItemSpawn(this);
    }

    @Override
    public int getPacketSize() {
        return 24;
    }
}
