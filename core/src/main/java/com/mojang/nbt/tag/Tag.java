package com.mojang.nbt.tag;

import com.mojang.nbt.tag.primitives.ByteTag;
import com.mojang.nbt.tag.primitives.DoubleTag;
import com.mojang.nbt.tag.primitives.FloatTag;
import com.mojang.nbt.tag.primitives.IntegerTag;
import com.mojang.nbt.tag.primitives.LongTag;
import com.mojang.nbt.tag.primitives.ShortTag;
import com.mojang.nbt.tag.special.ByteArrayTag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.IntegerArrayTag;
import com.mojang.nbt.tag.special.ListTag;
import com.mojang.nbt.tag.special.StringTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag {
    private String key = null;

    public abstract void writeContents(DataOutput var1) throws IOException;

    public abstract void readContents(DataInput var1) throws IOException;

    public abstract byte getId();

    public abstract Tag copy();

    public String getKey() {
        return this.key == null ? "" : this.key;
    }

    public <T extends Tag> T setKey(String key) {
        this.key = key;
        return (T)this;
    }

    public static Tag readTag(DataInput dataio) throws IOException {
        byte b = dataio.readByte();
        if (b == 0) {
            return new EndTag();
        }

        Tag tag = getTagType(b);
        tag.key = dataio.readUTF();
        tag.readContents(dataio);
        return tag;
    }

    public static void writeTag(Tag tag, DataOutput dataio) throws IOException {
        dataio.writeByte(tag.getId());
        if (tag.getId() != 0) {
            dataio.writeUTF(tag.getKey());
            tag.writeContents(dataio);
        }
    }

    public static Tag getTagType(byte id) {
        switch (id) {
            case 0:
                return new EndTag();
            case 1:
                return new ByteTag();
            case 2:
                return new ShortTag();
            case 3:
                return new IntegerTag();
            case 4:
                return new LongTag();
            case 5:
                return new FloatTag();
            case 6:
                return new DoubleTag();
            case 7:
                return new ByteArrayTag();
            case 8:
                return new StringTag();
            case 9:
                return new ListTag();
            case 10:
                return new CompoundTag();
            case 11:
                return new IntegerArrayTag();
            default:
                return null;
        }
    }

    public static String getTagName(byte id) {
        switch (id) {
            case 0:
                return "TAG_End";
            case 1:
                return "TAG_Byte";
            case 2:
                return "TAG_Short";
            case 3:
                return "TAG_Int";
            case 4:
                return "TAG_Long";
            case 5:
                return "TAG_Float";
            case 6:
                return "TAG_Double";
            case 7:
                return "TAG_Byte_Array";
            case 8:
                return "TAG_String";
            case 9:
                return "TAG_List";
            case 10:
                return "TAG_Compound";
            case 11:
                return "TAG_Int_Array";
            default:
                return "UNKNOWN";
        }
    }
}
