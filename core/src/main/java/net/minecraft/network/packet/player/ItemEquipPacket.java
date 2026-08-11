package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class ItemEquipPacket extends Packet {
    public int entityID;
    public int slot;
    private ItemStack itemSlot;

    public ItemEquipPacket() {
    }

    public ItemEquipPacket(int i1, int i2, ItemStack itemStack3) {
        this.entityID = i1;
        this.slot = i2;
        this.itemSlot = itemStack3 == null ? null : itemStack3.copy();
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityID = dataInputStream1.readInt();
        this.slot = dataInputStream1.readShort();
        this.itemSlot = IOUtil.readItemStack(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityID);
        dataOutputStream1.writeShort(this.slot);
        IOUtil.writeItemStack(this.itemSlot, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleItemEquip(this);
    }

    public ItemStack getItemSlot() {
        return this.itemSlot;
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
