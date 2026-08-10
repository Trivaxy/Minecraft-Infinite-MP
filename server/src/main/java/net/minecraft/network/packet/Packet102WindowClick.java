package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class Packet102WindowClick extends Packet {
    public int window_Id;
    public int inventorySlot;
    public int mouseClick;
    public short action;
    public ItemStack itemStack;
    public boolean isActive;

    public Packet102WindowClick() {
    }

    public Packet102WindowClick(int i1, int i2, int i3, boolean z4, ItemStack itemStack5, short s6) {
        this.window_Id = i1;
        this.inventorySlot = i2;
        this.mouseClick = i3;
        this.itemStack = itemStack5;
        this.action = s6;
        this.isActive = z4;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleWindowClick(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.window_Id = dataInputStream1.readByte();
        this.inventorySlot = dataInputStream1.readShort();
        this.mouseClick = dataInputStream1.readByte();
        this.action = dataInputStream1.readShort();
        this.isActive = dataInputStream1.readBoolean();
        this.itemStack = IOUtil.readItemStack(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.window_Id);
        dataOutputStream1.writeShort(this.inventorySlot);
        dataOutputStream1.writeByte(this.mouseClick);
        dataOutputStream1.writeShort(this.action);
        dataOutputStream1.writeBoolean(this.isActive);
        IOUtil.writeItemStack(this.itemStack, dataOutputStream1);
    }

    @Override
    public int getPacketSize() {
        return 11;
    }
}
