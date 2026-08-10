package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class MapPacket extends Packet {
    public short itemID;
    public short uniqueID;
    public byte[] itemData;

    public MapPacket() {
        this.isDelayed = true;
    }

    public MapPacket(short var1, short var2, byte[] var3) {
        this.isDelayed = true;
        this.itemID = var1;
        this.uniqueID = var2;
        this.itemData = var3;
    }

    @Override
    public void readPacketData(DataInputStream var1) throws IOException {
        this.itemID = var1.readShort();
        this.uniqueID = var1.readShort();
        this.itemData = new byte[var1.readUnsignedShort()];
        var1.readFully(this.itemData);
    }

    @Override
    public void writePacketData(DataOutputStream var1) throws IOException {
        var1.writeShort(this.itemID);
        var1.writeShort(this.uniqueID);
        var1.writeShort(this.itemData.length);
        var1.write(this.itemData);
    }

    @Override
    public void processPacket(NetworkHandler var1) {
        var1.handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return 4 + this.itemData.length;
    }
}
