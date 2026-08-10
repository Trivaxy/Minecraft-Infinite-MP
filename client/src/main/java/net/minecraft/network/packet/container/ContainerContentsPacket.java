package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class ContainerContentsPacket extends Packet {
    public int windowId;
    public ItemStack[] itemStack;

    public ContainerContentsPacket() {
    }

    public ContainerContentsPacket(int wid, List<ItemStack> items) {
        this.windowId = wid;
        this.itemStack = new ItemStack[items.size()];

        for (int i = 0; i < this.itemStack.length; i++) {
            ItemStack item = items.get(i);
            this.itemStack[i] = item == null ? null : item.copy();
        }
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
        short length = dataInputStream1.readShort();
        this.itemStack = new ItemStack[length];

        for (int i = 0; i < length; i++) {
            this.itemStack[i] = IOUtil.readItemStack(dataInputStream1);
        }
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
        dataOutputStream1.writeShort(this.itemStack.length);

        for (int i = 0; i < this.itemStack.length; i++) {
            IOUtil.writeItemStack(this.itemStack[i], dataOutputStream1);
        }
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleContainerContents(this);
    }

    @Override
    public int getPacketSize() {
        return 3 + this.itemStack.length * 5;
    }
}
