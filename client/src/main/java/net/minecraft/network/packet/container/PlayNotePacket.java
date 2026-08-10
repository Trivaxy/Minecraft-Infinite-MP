package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class PlayNotePacket extends Packet {
    public int xLocation;
    public int yLocation;
    public int zLocation;
    public int instrument;
    public int pitch;

    public PlayNotePacket() {
    }

    public PlayNotePacket(int i1, int i2, int i3, int i4, int i5) {
        this.xLocation = i1;
        this.yLocation = i2;
        this.zLocation = i3;
        this.instrument = i4;
        this.pitch = i5;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xLocation = dataInputStream1.readInt();
        this.yLocation = dataInputStream1.readShort();
        this.zLocation = dataInputStream1.readInt();
        this.instrument = dataInputStream1.read();
        this.pitch = dataInputStream1.read();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xLocation);
        dataOutputStream1.writeShort(this.yLocation);
        dataOutputStream1.writeInt(this.zLocation);
        dataOutputStream1.write(this.instrument);
        dataOutputStream1.write(this.pitch);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleNotePlay(this);
    }

    @Override
    public int getPacketSize() {
        return 12;
    }
}
