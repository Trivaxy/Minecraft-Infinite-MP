package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class Packet15Place extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int direction;
    public ItemStack itemStack;

    public Packet15Place() {
    }

    public Packet15Place(int i1, int i2, int i3, int i4, ItemStack itemStack5) {
        this.xPosition = i1;
        this.yPosition = i2;
        this.zPosition = i3;
        this.direction = i4;
        this.itemStack = itemStack5;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readShort();
        this.zPosition = dataInputStream1.readInt();
        this.direction = dataInputStream1.read();
        this.itemStack = IOUtil.readItemStack(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeShort(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.write(this.direction);
        IOUtil.writeItemStack(this.itemStack, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handlePlace(this);
    }

    @Override
    public int getPacketSize() {
        return 16;
    }
}
