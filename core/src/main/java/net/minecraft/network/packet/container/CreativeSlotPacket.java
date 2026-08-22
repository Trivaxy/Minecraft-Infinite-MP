package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class CreativeSlotPacket extends Packet {
    public int slot;
    public ItemStack itemStack;

    public CreativeSlotPacket() {
    }

    public CreativeSlotPacket(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        this.slot = in.readShort();
        this.itemStack = IOUtil.readItemStack(in);
    }

    @Override
    public void writePacketData(DataOutputStream out) throws IOException {
        out.writeShort(this.slot);
        IOUtil.writeItemStack(this.itemStack, out);
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handleCreativeSlot(this);
    }

    @Override
    public int getPacketSize() {
        return 7;
    }
}
