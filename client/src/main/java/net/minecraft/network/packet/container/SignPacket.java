package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class SignPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public String[] signLines;

    public SignPacket() {
        this.isDelayed = true;
    }

    public SignPacket(int i1, int i2, int i3, String[] string4) {
        this.isDelayed = true;
        this.xPosition = i1;
        this.yPosition = i2;
        this.zPosition = i3;
        this.signLines = string4;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readShort();
        this.zPosition = dataInputStream1.readInt();
        this.signLines = new String[4];

        for (int i2 = 0; i2 < 4; i2++) {
            this.signLines[i2] = IOUtil.readUtf16String(dataInputStream1);
        }
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeShort(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);

        for (int i2 = 0; i2 < 4; i2++) {
            IOUtil.writeUtf16String(this.signLines[i2], dataOutputStream1);
        }
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleSignUpdate(this);
    }

    @Override
    public int getPacketSize() {
        int i1 = 0;

        for (int i2 = 0; i2 < 4; i2++) {
            i1 += this.signLines[i2].length();
        }

        return i1;
    }
}
