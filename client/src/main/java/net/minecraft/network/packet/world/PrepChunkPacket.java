package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class PrepChunkPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public boolean mode;

    public PrepChunkPacket() {
        this.isDelayed = false;
    }

    public PrepChunkPacket(int i1, int i2, boolean z3) {
        this.isDelayed = false;
        this.xPosition = i1;
        this.yPosition = i2;
        this.mode = z3;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.mode = dataInputStream1.read() != 0;
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.write(this.mode ? 1 : 0);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleChunkPrep(this);
    }

    @Override
    public int getPacketSize() {
        return 9;
    }
}
