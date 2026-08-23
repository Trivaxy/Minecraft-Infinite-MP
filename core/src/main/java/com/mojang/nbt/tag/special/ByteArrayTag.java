package com.mojang.nbt.tag.special;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayTag extends Tag {
    public byte[] byteArray;

    public ByteArrayTag() {
    }

    public ByteArrayTag(byte[] array) {
        this.byteArray = array;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeInt(this.byteArray.length);
        dataio.write(this.byteArray);
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        int maxSize = 16777216;
        int arraySize = dataio.readInt();
        if (arraySize >= 0 && arraySize <= 16777216) {
            this.byteArray = new byte[arraySize];
            dataio.readFully(this.byteArray);
        } else {
            throw new IOException("Invalid byte array size: " + arraySize);
        }
    }

    @Override
    public byte getId() {
        return 7;
    }

    @Override
    public String toString() {
        return "[" + this.byteArray.length + " bytes]";
    }

    @Override
    public Tag copy() {
        byte[] arr = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, arr, 0, this.byteArray.length);
        return new ByteArrayTag(arr).setKey(this.getKey());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && java.util.Arrays.equals(this.byteArray, ((ByteArrayTag)other).byteArray);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ java.util.Arrays.hashCode(this.byteArray);
    }
}
