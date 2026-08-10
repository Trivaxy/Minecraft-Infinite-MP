package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class Packet7UseEntity extends Packet {
    public int playerEntityId;
    public int targetEntity;
    public int isLeftClick;
    public boolean offHand;

    public Packet7UseEntity() {
    }

    public Packet7UseEntity(int i1, int i2, int i3, boolean b) {
        this.playerEntityId = i1;
        this.targetEntity = i2;
        this.isLeftClick = i3;
        this.offHand = b;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.playerEntityId = dataInputStream1.readInt();
        this.targetEntity = dataInputStream1.readInt();
        this.isLeftClick = dataInputStream1.readByte();
        this.offHand = dataInputStream1.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.playerEntityId);
        dataOutputStream1.writeInt(this.targetEntity);
        dataOutputStream1.writeByte(this.isLeftClick);
        dataOutputStream1.writeBoolean(this.offHand);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleUseEntity(this);
    }

    @Override
    public int getPacketSize() {
        return 10;
    }
}
