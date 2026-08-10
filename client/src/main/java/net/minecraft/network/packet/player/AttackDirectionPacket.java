package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class AttackDirectionPacket extends Packet {
    public float yaw;

    public AttackDirectionPacket() {
    }

    public AttackDirectionPacket(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public void readPacketData(DataInputStream datainputstream) throws IOException {
        this.yaw = datainputstream.readFloat();
    }

    @Override
    public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
        dataoutputstream.writeFloat(this.yaw);
    }

    @Override
    public void processPacket(NetworkHandler nethandler) {
        nethandler.handleAttackDirection(this);
    }

    @Override
    public int getPacketSize() {
        return 4;
    }
}
