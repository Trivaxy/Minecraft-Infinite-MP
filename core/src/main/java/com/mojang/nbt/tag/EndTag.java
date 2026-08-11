package com.mojang.nbt.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EndTag extends Tag {
    @Override
    public void readContents(DataInput dataio) throws IOException {
    }

    @Override
    public void writeContents(DataOutput dataio) throws IOException {
    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public Tag copy() {
        return new EndTag();
    }
}
