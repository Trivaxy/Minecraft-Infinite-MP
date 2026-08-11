package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class ServerIconPacket extends Packet {
    public byte[] iconData;
    public String serverAddress;

    public ServerIconPacket() {
    }

    public ServerIconPacket(byte[] iconData, String serverName) {
        this.iconData = iconData;
        this.serverAddress = serverName;
    }

    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        int length = in.readInt();
        this.iconData = new byte[length];
        in.readFully(this.iconData);
        this.serverAddress = IOUtil.readUtf16String(in);
    }

    @Override
    public void writePacketData(DataOutputStream out) throws IOException {
        out.writeInt(this.iconData.length);
        out.write(this.iconData);
        IOUtil.writeUtf16String(this.serverAddress, out);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleServerIcon(this);
    }

    @Override
    public int getPacketSize() {
        return 8197;
    }
}
