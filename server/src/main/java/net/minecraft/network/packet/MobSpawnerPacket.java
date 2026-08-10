package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class MobSpawnerPacket extends Packet {
    public int x;
    public int y;
    public int z;
    public String entityId;

    public MobSpawnerPacket() {
    }

    public MobSpawnerPacket(int x, int y, int z, String entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.entityId = IOUtil.readUtf16String(dis);
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        IOUtil.writeUtf16String(this.entityId, dos);
    }

    @Override
    public void processPacket(NetworkHandler nh) {
        nh.handleMobSpawner(this);
    }

    @Override
    public int getPacketSize() {
        return 12 + this.entityId.length();
    }
}
