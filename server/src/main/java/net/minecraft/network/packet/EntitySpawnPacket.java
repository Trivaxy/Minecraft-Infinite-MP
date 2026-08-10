package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.util.io.IOUtil;
import net.minecraft.util.math.MathHelper;

public class EntitySpawnPacket extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int motionX;
    public int motionY;
    public int motionZ;
    public EntitySpawnPacket.EntityType type;
    public int ownerId;
    public short metadata;
    public ItemStack stack;

    public EntitySpawnPacket() {
    }

    public EntitySpawnPacket(Entity entity1, EntitySpawnPacket.EntityType i2) {
        this(entity1, i2, 0);
    }

    public EntitySpawnPacket(Entity entity1, EntitySpawnPacket.EntityType i2, short i) {
        this(entity1, i2, 0);
        this.metadata = i;
    }

    public EntitySpawnPacket(Entity entity1, EntitySpawnPacket.EntityType i2, int i3) {
        this.entityId = entity1.entityId;
        this.xPosition = MathHelper.floor_double(entity1.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entity1.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entity1.posZ * 32.0);
        this.type = i2;
        this.ownerId = i3;
        if (i3 > 0) {
            double d4 = entity1.motionX;
            double d6 = entity1.motionY;
            double d8 = entity1.motionZ;
            double d10 = 3.9;
            if (d4 < -d10) {
                d4 = -d10;
            }

            if (d6 < -d10) {
                d6 = -d10;
            }

            if (d8 < -d10) {
                d8 = -d10;
            }

            if (d4 > d10) {
                d4 = d10;
            }

            if (d6 > d10) {
                d6 = d10;
            }

            if (d8 > d10) {
                d8 = d10;
            }

            this.motionX = (int)(d4 * 8000.0);
            this.motionY = (int)(d6 * 8000.0);
            this.motionZ = (int)(d8 * 8000.0);
        }
    }

    public EntitySpawnPacket(Entity entity1, EntitySpawnPacket.EntityType i2, int i3, byte i4, ItemStack itemStack1) {
        this(entity1, i2, i3);
        this.metadata = i4;
        this.stack = itemStack1;
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.type = EntitySpawnPacket.EntityType.read(dataInputStream1);
        this.xPosition = dataInputStream1.readInt();
        this.yPosition = dataInputStream1.readInt();
        this.zPosition = dataInputStream1.readInt();
        this.ownerId = dataInputStream1.readInt();
        if (this.ownerId > 0) {
            this.motionX = dataInputStream1.readShort();
            this.motionY = dataInputStream1.readShort();
            this.motionZ = dataInputStream1.readShort();
        }

        this.metadata = dataInputStream1.readShort();
        this.stack = IOUtil.readItemStack(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        EntitySpawnPacket.EntityType.write(dataOutputStream1, this.type);
        dataOutputStream1.writeInt(this.xPosition);
        dataOutputStream1.writeInt(this.yPosition);
        dataOutputStream1.writeInt(this.zPosition);
        dataOutputStream1.writeInt(this.ownerId);
        if (this.ownerId > 0) {
            dataOutputStream1.writeShort(this.motionX);
            dataOutputStream1.writeShort(this.motionY);
            dataOutputStream1.writeShort(this.motionZ);
        }

        dataOutputStream1.writeShort(this.metadata);
        IOUtil.writeItemStack(this.stack, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntitySpawn(this);
    }

    @Override
    public int getPacketSize() {
        return 21 + this.ownerId > 0 ? 6 : 0;
    }

    public enum EntityType {
        MINECART(10),
        FRAME(20),
        PROJECTILE(30),
        TNT(50),
        ARROW(60),
        FALLING_BLOCK(70),
        BOBBER(80),
        BOAT(90);

        public final byte id;

        EntityType(int typ) {
            this.id = (byte)typ;
        }

        public static EntitySpawnPacket.EntityType read(DataInputStream dis) throws IOException {
            byte id = dis.readByte();

            for (EntitySpawnPacket.EntityType typ : values()) {
                if (typ.id == id) {
                    return typ;
                }
            }

            throw new IOException("No entity ID found!");
        }

        public static void write(DataOutputStream dos, EntitySpawnPacket.EntityType t) throws IOException {
            dos.writeByte(t.id);
        }
    }
}
