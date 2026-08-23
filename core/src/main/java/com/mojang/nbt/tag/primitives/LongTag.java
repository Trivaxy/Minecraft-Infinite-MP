package com.mojang.nbt.tag.primitives;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends Tag {
    public long longValue;

    public LongTag() {
    }

    public LongTag(long l) {
        this.longValue = l;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeLong(this.longValue);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.longValue = dataio.readLong();
    }

    @Override
    public byte getId() {
        return 4;
    }

    @Override
    public String toString() {
        return "" + this.longValue;
    }

    @Override
    public Tag copy() {
        return new LongTag(this.longValue).setKey(this.getKey());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && this.longValue == ((LongTag)other).longValue;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (int)(this.longValue ^ this.longValue >>> 32);
    }
}
