package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.network.NetworkHandler;

public class RemoveEffectPacket extends Packet {
    public int entityId;
    public byte effectId;

    public RemoveEffectPacket() {
    }

    public RemoveEffectPacket(int i1, EffectData potionEffect2) {
        this.entityId = i1;
        this.effectId = (byte)(potionEffect2.getPotionID() & 0xFF);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.effectId = dataInputStream1.readByte();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        dataOutputStream1.writeByte(this.effectId);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleRemoveEffect(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }
}
