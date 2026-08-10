package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class ChatPacket extends Packet {
    public String message;

    public ChatPacket() {
    }

    public ChatPacket(String string1) {
        if (string1.length() > 119) {
            string1 = string1.substring(0, 119);
        }

        this.message = string1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.message = IOUtil.readUtf16String(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        IOUtil.writeUtf16String(this.message, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleChat(this);
    }

    @Override
    public int getPacketSize() {
        return this.message.length();
    }
}
