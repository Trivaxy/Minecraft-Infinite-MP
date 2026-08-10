package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class WorldEventPacket extends Packet {
    public static final String[] clientMessage = new String[]{"tile.bed.notValid"};
    public int eventType;

    public WorldEventPacket() {
    }

    @Override
    public void readPacketData(DataInputStream var1) throws IOException {
        this.eventType = var1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream var1) throws IOException {
        var1.writeByte(this.eventType);
    }

    @Override
    public void processPacket(NetworkHandler var1) {
        var1.handleGameEvent(this);
    }

    @Override
    public int getPacketSize() {
        return 1;
    }

    public WorldEventPacket(int var1) {
        this.eventType = var1;
    }
}
