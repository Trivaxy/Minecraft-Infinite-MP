package net.minecraft.game.world.chunk.loader.region;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    private RandomAccessFile dataFile;
    private ArrayList<Boolean> sectorFree;
    private static final byte[] emptySector = new byte[4096];
    private final int[] offsets = new int[1024];
    public long lastModified = 0L;

    public RegionFile(File file) {
        try {
            if (file.exists()) {
                this.lastModified = file.lastModified();
            }

            this.dataFile = new RandomAccessFile(file, "rw");
            if (this.dataFile.length() < 4096L) {
                for (int length = 0; length < 1024; length++) {
                    this.dataFile.writeInt(0);
                }

                for (int var7 = 0; var7 < 1024; var7++) {
                    this.dataFile.writeInt(0);
                }
            }

            if ((this.dataFile.length() & 4095L) != 0L) {
                for (int length = 0; length < (this.dataFile.length() & 4095L); length++) {
                    this.dataFile.write(0);
                }
            }

            int length = (int)this.dataFile.length() / 4096;
            this.sectorFree = new ArrayList<>(length);

            for (int index = 0; index < length; index++) {
                this.sectorFree.add(Boolean.TRUE);
            }

            this.sectorFree.set(0, Boolean.FALSE);
            this.sectorFree.set(1, Boolean.FALSE);
            this.dataFile.seek(0L);

            for (int var10 = 0; var10 < 1024; var10++) {
                int fileSize = this.dataFile.readInt();
                this.offsets[var10] = fileSize;
                if (fileSize != 0 && (fileSize >> 8) + (fileSize & 0xFF) <= this.sectorFree.size()) {
                    for (int var5 = 0; var5 < (fileSize & 0xFF); var5++) {
                        this.sectorFree.set((fileSize >> 8) + var5, Boolean.FALSE);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized DataInputStream getDataInputStream(int x, int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        }

        try {
            int offset = this.getOffset(x, z);
            if (offset == 0) {
                return null;
            } else {
                int index = offset >> 8;
                int bit = offset & 0xFF;
                if (index + bit > this.sectorFree.size()) {
                    return null;
                } else {
                    this.dataFile.seek(index * 4096L);
                    int fileSize = this.dataFile.readInt();
                    if (fileSize > 4096 * bit) {
                        return null;
                    } else {
                        byte type = this.dataFile.readByte();
                        if (type == 1) {
                            byte[] bytes = new byte[fileSize - 1];
                            this.dataFile.read(bytes);
                            return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
                        } else if (type == 2) {
                            byte[] bytes = new byte[fileSize - 1];
                            this.dataFile.read(bytes);
                            return new DataInputStream(new FastBufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes))));
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            return null;
        }
    }

    public DataOutputStream getDataOutputStream(int x, int z) {
        return this.outOfBounds(x, z)
            ? null
            : new DataOutputStream(new FastBufferedOutputStream(new DeflaterOutputStream(new RegionFile.RegionFileChunkBuffer(x, z))));
    }

    protected synchronized void write(int x, int z, byte[] data, int index) {
        try {
            int offset = this.getOffset(x, z);
            int shift = offset >> 8;
            int bit = offset & 0xFF;
            int approxSize = (index + 5) / 4096 + 1;
            if (approxSize >= 256) {
                return;
            }

            if (shift != 0 && bit == approxSize) {
                this.write(shift, data, index);
            } else {
                for (int sector = 0; sector < bit; sector++) {
                    this.sectorFree.set(shift + sector, Boolean.TRUE);
                }

                int var15 = this.sectorFree.indexOf(Boolean.TRUE);
                int size = 0;
                if (var15 != -1) {
                    for (int sectorSize = var15; sectorSize < this.sectorFree.size(); sectorSize++) {
                        if (size != 0) {
                            if (this.sectorFree.get(sectorSize)) {
                                size++;
                            } else {
                                size = 0;
                            }
                        } else if (this.sectorFree.get(sectorSize)) {
                            var15 = sectorSize;
                            size = 1;
                        }

                        if (size >= approxSize) {
                            break;
                        }
                    }
                }

                if (size >= approxSize) {
                    shift = var15;
                    this.setOffset(x, z, var15 << 8 | approxSize);

                    for (int sectorSize = 0; sectorSize < approxSize; sectorSize++) {
                        this.sectorFree.set(shift + sectorSize, Boolean.FALSE);
                    }

                    this.write(shift, data, index);
                } else {
                    this.dataFile.seek(this.dataFile.length());
                    shift = this.sectorFree.size();

                    for (int sectorSize = 0; sectorSize < approxSize; sectorSize++) {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(Boolean.FALSE);
                    }

                    this.write(shift, data, index);
                    this.setOffset(x, z, shift << 8 | approxSize);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void write(int region, byte[] data, int size) throws IOException {
        this.dataFile.seek(region * 4096L);
        this.dataFile.writeInt(size + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(data, 0, size);
    }

    private boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    public boolean isChunkSaved(int var1, int var2) {
        return this.getOffset(var1, var2) != 0;
    }

    private void setOffset(int var1, int var2, int var3) throws IOException {
        this.offsets[var1 + var2 * 32] = var3;
        this.dataFile.seek((var1 + var2 * 32L) * 4L);
        this.dataFile.writeInt(var3);
    }

    public void close() throws IOException {
        this.dataFile.close();
    }

    private class RegionFileChunkBuffer extends ByteArrayOutputStream {
        private final int chunkX;
        private final int chunkZ;

        public RegionFileChunkBuffer(int var2, int var3) {
            super(8096);
            this.chunkX = var2;
            this.chunkZ = var3;
        }

        @Override
        public void close() {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}
