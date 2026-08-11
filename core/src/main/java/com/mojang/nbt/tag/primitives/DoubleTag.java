package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends Tag {
    public double doubleValue;

    public DoubleTag() {
    }

    public DoubleTag(double d) {
        this.doubleValue = d;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeDouble(this.doubleValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.doubleValue = dataio.readDouble();
    }

    @Override
    public byte getId() {
        return 6;
    }

    @Override
    public String toString() {
        return "" + this.doubleValue;
    }

    @Override
    public Tag copy() {
        return new DoubleTag(this.doubleValue).setKey(this.getKey());
    }
}
