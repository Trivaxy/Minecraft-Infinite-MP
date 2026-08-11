package com.mojang.nbt.tag.special;

import com.mojang.nbt.tag.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class IntegerArrayTag extends Tag {
    public int[] intArray;

    public IntegerArrayTag() {
    }

    public IntegerArrayTag(int[] array) {
        this.intArray = array;
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
        dataio.writeInt(this.intArray.length);

        for (int index = 0; index < this.intArray.length; index++) {
            dataio.writeInt(this.intArray[index]);
        }
    }

    @Override
    public void readContents(DataInput dataio) throws IOException {
        int arraySize = dataio.readInt();
        this.intArray = new int[arraySize];

        for (int index = 0; index < arraySize; index++) {
            this.intArray[index] = dataio.readInt();
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    @Override
    public String toString() {
        return "[" + this.intArray.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        IntegerArrayTag tag = (IntegerArrayTag)obj;
        return this.intArray == null && tag.intArray == null || this.intArray != null && this.intArray.equals(tag.intArray);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }

    @Override
    public Tag copy() {
        int[] arr = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, arr, 0, this.intArray.length);
        return new IntegerArrayTag(arr).setKey(this.getKey());
    }
}
