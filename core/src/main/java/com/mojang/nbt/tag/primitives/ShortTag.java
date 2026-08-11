package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends Tag {
    public short shortValue;

    public ShortTag() {
    }

    public ShortTag(short s) {
        this.shortValue = s;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeShort(this.shortValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.shortValue = dataio.readShort();
    }

    @Override
    public byte getId() {
        return 2;
    }

    @Override
    public String toString() {
        return "" + this.shortValue;
    }

    @Override
    public Tag copy() {
        return new ShortTag(this.shortValue).setKey(this.getKey());
    }
}
