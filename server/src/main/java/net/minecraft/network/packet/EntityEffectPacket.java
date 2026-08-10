package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.network.NetworkHandler;

public class EntityEffectPacket extends Packet {
    public int entityId;
    public byte effectId;
    public byte effectAmp;
    public short duration;

    public EntityEffectPacket() {
    }

    public EntityEffectPacket(int i1, EffectData potionEffect2) {
        this.entityId = i1;
        this.effectId = (byte)(potionEffect2.getPotionID() & 0xFF);
        this.effectAmp = (byte)(potionEffect2.getPotionAmplifier() & 0xFF);
        this.duration = (short)potionEffect2.getPotionDuration();
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.effectId = dataInputStream1.readByte();
        this.effectAmp = dataInputStream1.readByte();
        this.duration = dataInputStream1.readShort();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.effectId);
        dataOutputStream1.writeByte(this.effectAmp);
        dataOutputStream1.writeShort(this.duration);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityEffect(this);
    }

    @Override
    public int getPacketSize() {
        return 8;
    }
}
