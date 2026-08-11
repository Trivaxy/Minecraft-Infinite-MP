package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class PaintingPacket extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte direction;
    public String title;
    public byte rotation;
    public int type;

    public PaintingPacket() {
    }

    public PaintingPacket(Painting pain) {
        this.entityId = pain.entityId;
        this.xPosition = pain.xPosition;
        this.yPosition = pain.yPosition;
        this.zPosition = pain.zPosition;
        this.direction = (byte)pain.direction;
        this.title = pain.art.title;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.entityId = dis.readInt();
        this.title = IOUtil.readUtf16String(dis);
        this.xPosition = dis.readInt();
        this.yPosition = dis.readInt();
        this.zPosition = dis.readInt();
        this.direction = dis.readByte();
        this.type = dis.readInt();
        this.rotation = dis.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        dos.writeInt(this.entityId);
        IOUtil.writeUtf16String(this.title, dos);
        dos.writeInt(this.xPosition);
        dos.writeInt(this.yPosition);
        dos.writeInt(this.zPosition);
        dos.writeByte(this.direction);
        dos.writeByte(this.rotation);
        dos.writeInt(this.type);
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handlePaintingPlacement(this);
    }

    @Override
    public int getPacketSize() {
        return 4 + this.title.length() + 4 + 4 + 4 + 4 + 4 + 1 + 1;
    }
}
