package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;

public class CustomPayloadPacket extends Packet {
    public String channel;
    public int length;
    public byte[] data;

    public CustomPayloadPacket() {
    }

    public CustomPayloadPacket(String par1Str, byte[] par2ArrayOfByte) {
        this.channel = par1Str;
        this.data = par2ArrayOfByte;
        if (par2ArrayOfByte != null) {
            this.length = par2ArrayOfByte.length;
            if (this.length > 32767) {
                throw new IllegalArgumentException("Payload may not be larger than 32k");
            }
        }
    }

    @Override
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException {
        this.channel = IOUtil.readUtf16String(par1DataInputStream);
        this.length = par1DataInputStream.readShort();
        if (this.length > 0 && this.length < 32767) {
            this.data = new byte[this.length];
            par1DataInputStream.readFully(this.data);
        }
    }

    @Override
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException {
        IOUtil.writeUtf16String(this.channel, par1DataOutputStream);
        par1DataOutputStream.writeShort((short)this.length);
        if (this.data != null) {
            par1DataOutputStream.write(this.data);
        }
    }

    @Override
    public void processPacket(NetworkHandler par1NetHandler) {
        par1NetHandler.handleCustomPayload(this);
    }

    @Override
    public int getPacketSize() {
        return 2 + this.channel.length() * 2 + 2 + this.length;
    }
}
