package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class WeatherPacket extends Packet {
    public boolean raining;
    public boolean thundering;

    public WeatherPacket() {
    }

    public WeatherPacket(boolean raining, boolean thundering) {
        this.raining = raining;
        this.thundering = thundering;
    }

    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        int flags = in.readByte();
        this.raining = (flags & 1) != 0;
        this.thundering = (flags & 2) != 0;
    }

    @Override
    public void writePacketData(DataOutputStream out) throws IOException {
        out.writeByte((this.raining ? 1 : 0) | (this.thundering ? 2 : 0));
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handleWeather(this);
    }

    @Override
    public int getPacketSize() {
        return 1;
    }
}
