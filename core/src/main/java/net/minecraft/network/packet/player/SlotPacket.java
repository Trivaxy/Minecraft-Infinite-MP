package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class SlotPacket extends Packet {
    public int windowId;
    public int itemSlot;
    public ItemStack myItemStack;

    public SlotPacket() {
    }

    public SlotPacket(int i1, int i2, ItemStack itemStack3) {
        this.windowId = i1;
        this.itemSlot = i2;
        this.myItemStack = itemStack3 == null ? itemStack3 : itemStack3.copy();
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleSetSlot(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
        this.itemSlot = dataInputStream1.readShort();
        this.myItemStack = IOUtil.readItemStack(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
        dataOutputStream1.writeShort(this.itemSlot);
        IOUtil.writeItemStack(this.myItemStack, dataOutputStream1);
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
