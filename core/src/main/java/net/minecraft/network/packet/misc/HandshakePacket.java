package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class HandshakePacket extends Packet {
    public String username;

    public HandshakePacket() {
    }

    public HandshakePacket(String string1) {
        this.username = string1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.username = IOUtil.readUtf16String(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        IOUtil.writeUtf16String(this.username, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleHandshake(this);
    }

    @Override
    public int getPacketSize() {
        return 4 + this.username.length() + 4;
    }
}
