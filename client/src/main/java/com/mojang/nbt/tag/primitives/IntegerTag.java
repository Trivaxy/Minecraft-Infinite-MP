package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntegerTag extends Tag {
    public int intValue;

    public IntegerTag() {
    }

    public IntegerTag(int i) {
        this.intValue = i;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeInt(this.intValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.intValue = dataio.readInt();
    }

    @Override
    public byte getId() {
        return 3;
    }

    @Override
    public String toString() {
        return "" + this.intValue;
    }

    @Override
    public Tag copy() {
        return new IntegerTag(this.intValue).setKey(this.getKey());
    }
}
