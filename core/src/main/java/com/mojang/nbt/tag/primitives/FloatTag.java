package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends Tag {
    public float floatValue;

    public FloatTag() {
    }

    public FloatTag(float f) {
        this.floatValue = f;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeFloat(this.floatValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.floatValue = dataio.readFloat();
    }

    @Override
    public byte getId() {
        return 5;
    }

    @Override
    public String toString() {
        return "" + this.floatValue;
    }

    @Override
    public Tag copy() {
        return new FloatTag(this.floatValue).setKey(this.getKey());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Float.compare(this.floatValue, ((FloatTag)other).floatValue) == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.floatValue);
    }
}
