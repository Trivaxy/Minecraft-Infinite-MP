package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class AnimationPacket extends Packet {
    public int entityId;
    public int animate;

    public AnimationPacket() {
    }

    public AnimationPacket(Entity entity1, int i2) {
        this.entityId = entity1.entityId;
        this.animate = i2;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.animate = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.animate);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleAnimation(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
