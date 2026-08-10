package net.minecraft.util.io;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.game.item.ItemStack;

public class IOUtil {
    public static void writeItemStack(ItemStack item, DataOutput dos) throws IOException {
        if (item == null) {
            dos.writeShort(-1);
        } else {
            dos.writeShort(item.itemID);
            dos.writeByte(item.stackSize);
            dos.writeShort(item.getItemDamage());
            if (item.hasTag()) {
                writeNbtCompound(item.customTag, dos);
            } else {
                dos.writeShort(-1);
            }
        }
    }

    public static ItemStack readItemStack(DataInput dis) throws IOException {
        ItemStack item = null;
        short id = dis.readShort();
        if (id >= 0) {
            byte stacksize = dis.readByte();
            short damage = dis.readShort();
            item = new ItemStack(id, stacksize, damage);
            item.customTag = readNbtCompound(dis);
        }

        return item;
    }

    public static CompoundTag readNbtCompound(DataInput dis) throws IOException {
        short size = dis.readShort();
        if (size < 0) {
            return null;
        }

        byte[] buffer = new byte[size];
        dis.readFully(buffer);
        return NBTReader.decompress(buffer);
    }

    public static void writeNbtCompound(CompoundTag tag, DataOutput dos) throws IOException {
        if (tag == null) {
            dos.writeShort(-1);
        } else {
            byte[] buffer = NBTReader.compress(tag);
            dos.writeShort((short)buffer.length);
            dos.write(buffer);
        }
    }

    public static void writeUtf16String(String stringpacket, DataOutput dos) throws IOException {
        byte[] dat = stringpacket.getBytes(StandardCharsets.UTF_16BE);
        dos.writeInt(dat.length);
        dos.write(dat);
    }

    public static String readUtf16String(DataInput dis) throws IOException {
        int length = dis.readInt();
        byte[] buffer = new byte[length];
        dis.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_16BE);
    }

    public static void writeShortArray(DataOutput dos, short[] array) throws IOException {
        int len = array.length;
        dos.writeInt(len);

        for (int i = 0; i < len; i++) {
            dos.writeShort(array[i]);
        }
    }

    public static short[] readShortArray(DataInput dis) throws IOException {
        int length = dis.readInt();
        short[] ret = new short[length];

        for (int i = 0; i < length; i++) {
            ret[i] = dis.readShort();
        }

        return ret;
    }

    public static void writeIntArray(DataOutput dos, int[] array) throws IOException {
        int len = array.length;
        dos.writeInt(len);

        for (int i = 0; i < len; i++) {
            dos.writeInt(array[i]);
        }
    }

    public static int[] readIntArray(DataInput dis) throws IOException {
        int length = dis.readInt();
        int[] ret = new int[length];

        for (int i = 0; i < length; i++) {
            ret[i] = dis.readInt();
        }

        return ret;
    }

    public static void writeLongArray(DataOutput dos, long[] array) throws IOException {
        int len = array.length;
        dos.writeInt(len);

        for (int i = 0; i < len; i++) {
            dos.writeLong(array[i]);
        }
    }

    public static long[] readLongArray(DataInput dis) throws IOException {
        int length = dis.readInt();
        long[] ret = new long[length];

        for (int i = 0; i < length; i++) {
            ret[i] = dis.readLong();
        }

        return ret;
    }
}
