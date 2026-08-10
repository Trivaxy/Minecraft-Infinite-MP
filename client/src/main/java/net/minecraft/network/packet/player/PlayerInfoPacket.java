package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class PlayerInfoPacket extends Packet {
    public String playerName;
    public boolean isConnected;
    public int ping;

    public PlayerInfoPacket() {
    }

    public PlayerInfoPacket(String par1Str, boolean par2, int par3) {
        this.playerName = par1Str;
        this.isConnected = par2;
        this.ping = par3;
    }

    @Override
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException {
        this.playerName = IOUtil.readUtf16String(par1DataInputStream);
        this.isConnected = par1DataInputStream.readByte() != 0;
        this.ping = par1DataInputStream.readShort();
    }

    @Override
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException {
        IOUtil.writeUtf16String(this.playerName, par1DataOutputStream);
        par1DataOutputStream.writeByte(this.isConnected ? 1 : 0);
        par1DataOutputStream.writeShort(this.ping);
    }

    @Override
    public void processPacket(NetworkHandler par1NetHandler) {
        par1NetHandler.handlePlayerInfo(this);
    }

    @Override
    public int getPacketSize() {
        return this.playerName.length() + 2 + 1 + 2;
    }
}
