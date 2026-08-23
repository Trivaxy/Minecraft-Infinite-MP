package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends Tag {
    public byte byteValue;

    public ByteTag() {
    }

    public ByteTag(byte b) {
        this.byteValue = b;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeByte(this.byteValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.byteValue = dataio.readByte();
    }

    @Override
    public byte getId() {
        return 1;
    }

    @Override
    public String toString() {
        return "" + this.byteValue;
    }

    @Override
    public Tag copy() {
        return new ByteTag(this.byteValue).setKey(this.getKey());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && this.byteValue == ((ByteTag)other).byteValue;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.byteValue;
    }
}
