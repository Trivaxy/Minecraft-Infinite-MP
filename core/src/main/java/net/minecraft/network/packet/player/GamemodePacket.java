package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class GamemodePacket extends Packet {
    public int gamemode;

    public GamemodePacket() {
    }

    public GamemodePacket(int gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        this.gamemode = in.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream out) throws IOException {
        out.writeByte(this.gamemode);
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handleGamemode(this);
    }

    @Override
    public int getPacketSize() {
        return 1;
    }
}
