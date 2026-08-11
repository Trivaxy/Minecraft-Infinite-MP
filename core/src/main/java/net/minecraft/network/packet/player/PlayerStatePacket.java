package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class PlayerStatePacket extends Packet {
    public int entityId;
    public byte type;
    public boolean state;

    public PlayerStatePacket() {
    }

    public PlayerStatePacket(int id, byte type, boolean state) {
        this.entityId = id;
        this.type = type;
        this.state = state;
    }

    @Override
    public void readPacketData(DataInputStream dis) throws IOException {
        this.entityId = dis.readInt();
        this.type = dis.readByte();
        this.state = dis.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dos) throws IOException {
        dos.writeInt(this.entityId);
        dos.writeByte(this.type);
        dos.writeBoolean(this.state);
    }

    @Override
    public void processPacket(NetworkHandler handler) {
        handler.handlePlayerState(this);
    }

    @Override
    public int getPacketSize() {
        return 18;
    }
}
