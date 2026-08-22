package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class SoundPacket extends Packet {
    public static final byte TYPE_SOUND = 0;
    public static final byte TYPE_BLOCK = 1;
    public static final byte TYPE_MOB = 2;

    public String soundName;
    public double x;
    public double y;
    public double z;
    public float volume;
    public float pitch;
    public byte soundType;

    public SoundPacket() {
    }

    public SoundPacket(String soundName, double x, double y, double z, float volume, float pitch, byte soundType) {
        this.soundName = soundName != null ? soundName : "";
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
        this.soundType = soundType;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.soundName = IOUtil.readUtf16String(dis);
        this.x = dis.readDouble();
        this.y = dis.readDouble();
        this.z = dis.readDouble();
        this.volume = dis.readFloat();
        this.pitch = dis.readFloat();
        this.soundType = dis.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        IOUtil.writeUtf16String(this.soundName, dos);
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.z);
        dos.writeFloat(this.volume);
        dos.writeFloat(this.pitch);
        dos.writeByte(this.soundType);
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handleSound(this);
    }

    @Override
    public int getPacketSize() {
        return 4 + (this.soundName != null ? this.soundName.length() * 2 : 0) + 8 * 3 + 4 + 4 + 1;
    }
}
