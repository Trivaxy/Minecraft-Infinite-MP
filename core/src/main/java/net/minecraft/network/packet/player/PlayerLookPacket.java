package net.minecraft.network.packet.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerLookPacket extends PlayerMovementPacket {
    public PlayerLookPacket() {
        this.rotating = true;
    }

    public PlayerLookPacket(float f1, float f2, boolean z3) {
        this.yaw = f1;
        this.pitch = f2;
        this.onGround = z3;
        this.rotating = true;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.yaw = dataInputStream1.readFloat();
        this.pitch = dataInputStream1.readFloat();
        super.readPacketData(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeFloat(this.yaw);
        dataOutputStream1.writeFloat(this.pitch);
        super.writePacketData(dataOutputStream1);
    }

    @Override
    public int getPacketSize() {
        return 9;
    }
}
