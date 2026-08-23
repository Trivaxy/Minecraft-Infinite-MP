package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class MusicPacket extends Packet {
    public int x;
    public int y;
    public int z;
    public String string1;
    public boolean stop;

    public MusicPacket() {
    }

    public MusicPacket(String string1, int i2, int i3, int i4, boolean stop) {
        String copy = string1;
        if (copy == null) {
            this.string1 = "null";
        } else {
            this.string1 = copy;
        }

        this.x = i2;
        this.y = i3;
        this.z = i4;
        this.stop = stop;
    }

    @Override
    public void readPacketData(DataInputStream i) throws IOException {
        this.x = i.readInt();
        this.y = i.readInt();
        this.z = i.readInt();
        this.string1 = IOUtil.readUtf16String(i);
        this.stop = i.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream o) throws IOException {
        o.writeInt(this.x);
        o.writeInt(this.y);
        o.writeInt(this.z);
        IOUtil.writeUtf16String(this.string1, o);
        o.writeBoolean(this.stop);
    }

    @Override
    public void processPacket(NetworkHandler h) {
        h.handleMusic(this);
    }

    @Override
    public int getPacketSize() {
        return 12 + this.string1.length();
    }
}
