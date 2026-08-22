package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class ScorePacket extends Packet {
    public int score;

    public ScorePacket() {
    }

    public ScorePacket(int score) {
        this.score = score;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.score = dataInputStream1.readInt();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.score);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleScore(this);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
