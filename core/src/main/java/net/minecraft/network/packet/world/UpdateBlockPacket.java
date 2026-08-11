package net.minecraft.network.packet.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.world.World;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

public class UpdateBlockPacket extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int type;
    public int metadata;

    public UpdateBlockPacket() {
        this.isDelayed = true;
    }

    public UpdateBlockPacket(int i1, int i2, int i3, World world4) {
        this.isDelayed = true;
        this.xPosition = i1;
        this.yPosition = i2;
        this.zPosition = i3;
        this.type = world4.getBlockId(i1, i2, i3);
        this.metadata = world4.getBlockMetadata(i1, i2, i3);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readShort();
        this.zPosition = dataInputStream1.readInt();
        this.type = dataInputStream1.readShort();
        this.metadata = dataInputStream1.read();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeShort(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeShort(this.type);
        dataOutputStream1.write(this.metadata);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleBlockUpdate(this);
    }

    @Override
    public int getPacketSize() {
        return 11;
    }
}
