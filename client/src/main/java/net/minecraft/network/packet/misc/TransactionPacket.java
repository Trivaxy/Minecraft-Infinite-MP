package net.minecraft.network.packet.misc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class TransactionPacket extends Packet {
    public int windowId;
    public short transactionId;
    public boolean transactionOngoing;

    public TransactionPacket() {
    }

    public TransactionPacket(int i1, short s2, boolean z3) {
        this.windowId = i1;
        this.transactionId = s2;
        this.transactionOngoing = z3;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleTransaction(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
        this.transactionId = dataInputStream1.readShort();
        this.transactionOngoing = dataInputStream1.readByte() != 0;
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
        dataOutputStream1.writeShort(this.transactionId);
        dataOutputStream1.writeByte(this.transactionOngoing ? 1 : 0);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
