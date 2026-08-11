package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class LoginPacket extends Packet {
    public int protocolVersion;
    public String username;
    public long mapSeed;
    public byte dimension;
    public byte maxPlayers;
    public byte maxSections;

    public LoginPacket() {
    }

    public LoginPacket(String string1, int i2) {
        this.username = string1;
        this.protocolVersion = i2;
    }

    public LoginPacket(String string1, int i2, long j3, byte b5, byte par8, byte maxSections) {
        this.username = string1;
        this.protocolVersion = i2;
        this.mapSeed = j3;
        this.dimension = b5;
        this.maxPlayers = par8;
        this.maxSections = maxSections;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.protocolVersion = dataInputStream1.readInt();
        this.username = IOUtil.readUtf16String(dataInputStream1);
        this.mapSeed = dataInputStream1.readLong();
        this.dimension = dataInputStream1.readByte();
        this.maxPlayers = dataInputStream1.readByte();
        this.maxSections = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.protocolVersion);
        IOUtil.writeUtf16String(this.username, dataOutputStream1);
        dataOutputStream1.writeLong(this.mapSeed);
        dataOutputStream1.writeByte(this.dimension);
        dataOutputStream1.writeByte(this.maxPlayers);
        dataOutputStream1.writeByte(this.maxSections);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleLogin(this);
    }

    @Override
    public int getPacketSize() {
        return 4 + this.username.length() + 4 + 7;
    }
}
