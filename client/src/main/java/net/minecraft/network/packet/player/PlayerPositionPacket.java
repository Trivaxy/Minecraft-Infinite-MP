package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerPositionPacket extends PlayerMovementPacket {
    public PlayerPositionPacket() {
        this.moving = true;
    }

    public PlayerPositionPacket(double d1, double d3, double d5, double d7, boolean z9) {
        this.xPosition = d1;
        this.yPosition = d3;
        this.stance = d5;
        this.zPosition = d7;
        this.onGround = z9;
        this.moving = true;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readDouble();
        this.yPosition = dataInputStream1.readDouble();
        this.zPosition = dataInputStream1.readDouble();
        this.stance = dataInputStream1.readDouble();
        super.readPacketData(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeDouble(this.xPosition);
        dataOutputStream1.writeDouble(this.yPosition);
        dataOutputStream1.writeDouble(this.zPosition);
        dataOutputStream1.writeDouble(this.stance);
        super.writePacketData(dataOutputStream1);
    }

    @Override
    public int getPacketSize() {
        return 33;
    }
}
