package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;

public class Packet27Position extends Packet {
    private float strafeMovement;
    private float fowardMovement;
    private boolean state;
    private boolean isInJump;
    private float pitchRotation;
    private float yawRotation;

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.strafeMovement = dataInputStream1.readFloat();
        this.fowardMovement = dataInputStream1.readFloat();
        this.pitchRotation = dataInputStream1.readFloat();
        this.yawRotation = dataInputStream1.readFloat();
        this.state = dataInputStream1.readBoolean();
        this.isInJump = dataInputStream1.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeFloat(this.strafeMovement);
        dataOutputStream1.writeFloat(this.fowardMovement);
        dataOutputStream1.writeFloat(this.pitchRotation);
        dataOutputStream1.writeFloat(this.yawRotation);
        dataOutputStream1.writeBoolean(this.state);
        dataOutputStream1.writeBoolean(this.isInJump);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handlePosition(this);
    }

    @Override
    public int getPacketSize() {
        return 18;
    }

    public float getStrafeMovement() {
        return this.strafeMovement;
    }

    public float getPitchRot() {
        return this.pitchRotation;
    }

    public float getForwardMov() {
        return this.fowardMovement;
    }

    public float getYawRot() {
        return this.yawRotation;
    }

    public boolean getState() {
        return this.state;
    }

    public boolean jumping() {
        return this.isInJump;
    }
}
