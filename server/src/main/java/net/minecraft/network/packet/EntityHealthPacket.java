package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class EntityHealthPacket extends Packet {
    public int healthMP;
    public int food;
    public float foodSaturation;

    public EntityHealthPacket() {
    }

    public EntityHealthPacket(int i1) {
        this.healthMP = i1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.healthMP = dataInputStream1.readShort();
        this.food = dataInputStream1.readShort();
        this.foodSaturation = dataInputStream1.readFloat();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeShort(this.healthMP);
        dataOutputStream1.writeShort(this.food);
        dataOutputStream1.writeFloat(this.foodSaturation);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleHealth(this);
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
