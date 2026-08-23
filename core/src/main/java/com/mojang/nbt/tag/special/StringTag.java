package com.mojang.nbt.tag.special;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag extends Tag {
    public String string;

    public StringTag() {
    }

    public StringTag(String string) {
        this.string = string;
        if (string == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeUTF(this.string);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        this.string = dataio.readUTF();
    }

    @Override
    public byte getId() {
        return 8;
    }

    @Override
    public String toString() {
        return "" + this.string;
    }

    @Override
    public Tag copy() {
        return new StringTag(this.string).setKey(this.getKey());
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        } else {
            StringTag tag = (StringTag)other;
            return this.string == null ? tag.string == null : this.string.equals(tag.string);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (this.string == null ? 0 : this.string.hashCode());
    }
}
