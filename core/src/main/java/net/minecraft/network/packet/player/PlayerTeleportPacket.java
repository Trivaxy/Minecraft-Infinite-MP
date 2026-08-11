package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerTeleportPacket extends PlayerMovementPacket {
    public PlayerTeleportPacket() {
        this.rotating = true;
        this.moving = true;
    }

    public PlayerTeleportPacket(double d1, double d3, double d5, double d7, float f9, float f10, boolean z11) {
        this.xPosition = d1;
        this.yPosition = d3;
        this.stance = d5;
        this.zPosition = d7;
        this.yaw = f9;
        this.pitch = f10;
        this.onGround = z11;
        this.rotating = true;
        this.moving = true;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.xPosition = dataInputStream1.readDouble();
        this.yPosition = dataInputStream1.readDouble();
        this.stance = dataInputStream1.readDouble();
        this.zPosition = dataInputStream1.readDouble();
        this.yaw = dataInputStream1.readFloat();
        this.pitch = dataInputStream1.readFloat();
        super.readPacketData(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeDouble(this.xPosition);
        dataOutputStream1.writeDouble(this.yPosition);
        dataOutputStream1.writeDouble(this.stance);
        dataOutputStream1.writeDouble(this.zPosition);
        dataOutputStream1.writeFloat(this.yaw);
        dataOutputStream1.writeFloat(this.pitch);
        super.writePacketData(dataOutputStream1);
    }

    @Override
    public int getPacketSize() {
        return 41;
    }
}
