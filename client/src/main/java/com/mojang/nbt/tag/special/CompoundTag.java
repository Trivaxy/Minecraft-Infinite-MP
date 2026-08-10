package com.mojang.nbt.tag.special;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.primitives.ByteTag;
import com.mojang.nbt.tag.primitives.FloatTag;
import com.mojang.nbt.tag.primitives.IntegerTag;
import com.mojang.nbt.tag.primitives.LongTag;
import com.mojang.nbt.tag.primitives.ShortTag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CompoundTag extends Tag {
    private final Map<String, Tag> tagMap = new HashMap<>();

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        for (Tag tag : this.tagMap.values()) {
            Tag.writeTag(tag, dataio);
        }

        dataio.writeByte(0);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.tagMap.clear();

        Tag tag;
        while ((tag = Tag.readTag(dataio)).getId() != 0) {
            this.tagMap.put(tag.getKey(), tag);
        }
    }

    @Override
    public byte getId() {
        return 10;
    }

    public void setTag(String type, Tag tag) {
        this.tagMap.put(type, tag.setKey(type));
    }

    public void removeTag(String string) {
        this.tagMap.remove(string);
    }

    public void setByte(String type, byte b) {
        this.tagMap.put(type, new ByteTag(b).setKey(type));
    }

    public void setShort(String type, short s) {
        this.tagMap.put(type, new ShortTag(s).setKey(type));
    }

    public void setInteger(String type, int i) {
        this.tagMap.put(type, new IntegerTag(i).setKey(type));
    }

    public void setLong(String type, long l) {
        this.tagMap.put(type, new LongTag(l).setKey(type));
    }

    public void setFloat(String type, float f) {
        this.tagMap.put(type, new FloatTag(f).setKey(type));
    }

    public void setBoolean(String type, boolean b) {
        this.setByte(type, (byte)(b ? 1 : 0));
    }

    public void setString(String type, String string) {
        this.tagMap.put(type, new StringTag(string).setKey(type));
    }

    public void setByteArray(String type, byte[] array) {
        this.tagMap.put(type, new ByteArrayTag(array).setKey(type));
    }

    public void setIntArray(String type, int[] array) {
        this.tagMap.put(type, new IntegerArrayTag(array).setKey(type));
    }

    public void setCompoundTag(String type, CompoundTag tag) {
        this.tagMap.put(type, tag.setKey(type));
    }

    public byte getByte(String type) {
        return !this.tagMap.containsKey(type) ? 0 : ((ByteTag)this.tagMap.get(type)).byteValue;
    }

    public short getShort(String type) {
        return !this.tagMap.containsKey(type) ? 0 : ((ShortTag)this.tagMap.get(type)).shortValue;
    }

    public int getInteger(String type) {
        return !this.tagMap.containsKey(type) ? 0 : ((IntegerTag)this.tagMap.get(type)).intValue;
    }

    public long getLong(String type) {
        return !this.tagMap.containsKey(type) ? 0L : ((LongTag)this.tagMap.get(type)).longValue;
    }

    public float getFloat(String type) {
        return !this.tagMap.containsKey(type) ? 0.0F : ((FloatTag)this.tagMap.get(type)).floatValue;
    }

    public boolean getBoolean(String type) {
        return this.getByte(type) != 0;
    }

    public String getString(String type) {
        return !this.tagMap.containsKey(type) ? "" : ((StringTag)this.tagMap.get(type)).string;
    }

    public byte[] getByteArray(String type) {
        return !this.tagMap.containsKey(type) ? new byte[0] : ((ByteArrayTag)this.tagMap.get(type)).byteArray;
    }

    public int[] getIntArray(String type) {
        return !this.tagMap.containsKey(type) ? new int[0] : ((IntegerArrayTag)this.tagMap.get(type)).intArray;
    }

    public <T extends Tag> ListTag<T> getTagList(String type1) {
        return !this.tagMap.containsKey(type1) ? new ListTag<>() : (ListTag)this.tagMap.get(type1);
    }

    public CompoundTag getCompoundTag(String type) {
        return !this.tagMap.containsKey(type) ? new CompoundTag() : (CompoundTag)this.tagMap.get(type);
    }

    public Tag getNBTTag(String type) {
        return this.tagMap.get(type);
    }

    public boolean hasKey(String type) {
        return this.tagMap.containsKey(type);
    }

    @Override
    public String toString() {
        return "" + this.tagMap.size() + " entries";
    }

    public Collection<Tag> getTags() {
        return this.tagMap.values();
    }

    public Tag getTag(String type) {
        return this.tagMap.get(type);
    }

    @Override
    public Tag copy() {
        CompoundTag tag = new CompoundTag().setKey(this.getKey());

        for (String key : this.tagMap.keySet()) {
            tag.setTag(key, this.tagMap.get(key).copy());
        }

        return tag;
    }

    public boolean hasExtraValues(String name, int type) {
        byte id = this.convertTagToByte(name);
        return id == type ? true : (type != 99 ? false : id == 1 || id == 2 || id == 3 || id == 4 || id == 5 || id == 6);
    }

    public byte convertTagToByte(String name) {
        Tag tag = this.tagMap.get(name);
        return tag != null ? tag.getId() : 0;
    }
}
