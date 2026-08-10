package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class DisconnectPacket extends Packet {
    public String reason;

    public DisconnectPacket() {
    }

    public DisconnectPacket(String string1) {
        this.reason = string1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.reason = IOUtil.readUtf16String(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        IOUtil.writeUtf16String(this.reason, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleDisconnect(this);
    }

    @Override
    public int getPacketSize() {
        return this.reason.length();
    }
}
