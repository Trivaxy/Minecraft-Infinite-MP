package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;

public class SleepPacket extends Packet {
    public int entityID;
    public int bedX;
    public int bedY;
    public int bedZ;
    public int state;

    public SleepPacket() {
    }

    @Override
    public void readPacketData(DataInputStream var1) throws IOException {
        this.entityID = var1.readInt();
        this.state = var1.readByte();
        this.bedX = var1.readInt();
        this.bedY = var1.readByte();
        this.bedZ = var1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream var1) throws IOException {
        var1.writeInt(this.entityID);
        var1.writeByte(this.state);
        var1.writeInt(this.bedX);
        var1.writeByte(this.bedY);
        var1.writeInt(this.bedZ);
    }

    @Override
    public void processPacket(NetworkHandler var1) {
        var1.handleSleep(this);
    }

    @Override
    public int getPacketSize() {
        return 14;
    }

    public SleepPacket(Entity var1, int var2, int var3, int var4, int var5) {
        this.state = var2;
        this.bedX = var3;
        this.bedY = var4;
        this.bedZ = var5;
        this.entityID = var1.entityId;
    }
}
