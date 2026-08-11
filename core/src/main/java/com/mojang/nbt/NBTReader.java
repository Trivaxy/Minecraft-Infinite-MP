package com.mojang.nbt;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTReader {
    public static CompoundTag readCompressed(InputStream io) throws IOException {
        DataInputStream dataio = new DataInputStream(new GZIPInputStream(io));

        CompoundTag tag;
        try {
            tag = read(dataio);
        } finally {
            dataio.close();
        }

        return tag;
    }

    public static void writeCompressed(CompoundTag tag, OutputStream io) throws IOException {
        DataOutputStream dataio = new DataOutputStream(new GZIPOutputStream(io));

        try {
            write(tag, dataio);
        } finally {
            dataio.close();
        }
    }

    public static CompoundTag read(DataInput dataio) throws IOException {
        Tag baseTag = Tag.readTag(dataio);
        if (baseTag instanceof CompoundTag) {
            return (CompoundTag)baseTag;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(CompoundTag tag, DataOutput dataio) throws IOException {
        Tag.writeTag(tag, dataio);
    }

    public static CompoundTag decompress(byte[] data) throws IOException {
        DataInputStream dataio = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));

        CompoundTag tag;
        try {
            tag = read(dataio);
        } finally {
            dataio.close();
        }

        return tag;
    }

    public static byte[] compress(CompoundTag tag) throws IOException {
        ByteArrayOutputStream byteio = new ByteArrayOutputStream();
        DataOutputStream dataio = new DataOutputStream(new GZIPOutputStream(byteio));

        try {
            write(tag, dataio);
        } finally {
            dataio.close();
        }

        return byteio.toByteArray();
    }

    public static void safeWrite(CompoundTag tag, File file) throws IOException {
        File tmp = new File(file.getAbsolutePath() + "_tmp");
        if (tmp.exists()) {
            tmp.delete();
        }

        write(tag, tmp);
        if (file.exists()) {
            file.delete();
        }

        if (file.exists()) {
            throw new IOException("Failed to delete " + file);
        }

        tmp.renameTo(file);
    }

    public static void write(CompoundTag tag, File file) throws IOException {
        DataOutputStream dataio = new DataOutputStream(new FileOutputStream(file));

        try {
            write(tag, dataio);
        } finally {
            dataio.close();
        }
    }

    public static CompoundTag read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }

        DataInputStream dataio = new DataInputStream(new FileInputStream(file));

        CompoundTag tag;
        try {
            tag = read(dataio);
        } finally {
            dataio.close();
        }

        return tag;
    }
}
