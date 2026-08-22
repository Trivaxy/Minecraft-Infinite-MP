package net.minecraft.network.packet.container;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.io.IOUtil;

public class ContainerOpenPacket extends Packet {
    public static final int CHEST = 0;
    public static final int WORKBENCH = 1;
    public static final int FURNACE = 2;
    public static final int BARREL = 3;
    public static final int BOOKSHELF = 4;
    public static final int GENERATOR = 5;
    public static final int BLAST_FURNACE = 6;
    public static final int ANVIL = 7;
    public static final int GRINDSTONE = 8;
    public static final int STONECUTTER = 9;
    public static final int SMOKER = 10;
    public static final int HELLFORGE = 11;
    public static final int CRAFTER = 12;
    public static final int BEACON = 13;
    public static final int VACUUM = 14;
    public static final int DISPENSER = 15;
    public static final int ENDER_CHEST = 16;

    public int windowId;
    public int inventoryType;
    public String windowTitle;
    public int slotsCount;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public boolean state;

    public ContainerOpenPacket() {
    }

    public ContainerOpenPacket(int i1, int i2, String string3, int i4) {
        this.windowId = i1;
        this.inventoryType = i2;
        this.windowTitle = string3;
        this.slotsCount = i4;
    }

    public ContainerOpenPacket(int wid, int type, String title, int slots, int x, int y, int z, boolean state) {
        this.windowId = wid;
        this.inventoryType = type;
        this.windowTitle = title;
        this.slotsCount = slots;
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.state = state;
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleContainerOpen(this);
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.windowId = dataInputStream1.readByte();
        this.inventoryType = dataInputStream1.readByte();
        this.windowTitle = IOUtil.readUtf16String(dataInputStream1);
        this.slotsCount = dataInputStream1.readByte();
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.state = dataInputStream1.readBoolean();
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeByte(this.windowId);
        dataOutputStream1.writeByte(this.inventoryType);
        IOUtil.writeUtf16String(this.windowTitle, dataOutputStream1);
        dataOutputStream1.writeByte(this.slotsCount);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeBoolean(this.state);
    }

    @Override
    public int getPacketSize() {
        return 3 + this.windowTitle.length();
    }
}
