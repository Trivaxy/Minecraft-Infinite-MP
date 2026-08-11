package net.minecraft.util.math;

import java.util.Random;

public class BlockArrayUtils {
    private short[] data;
    private byte[] meta;
    int chunkX;
    int chunkZ;
    int cX1;
    int cX2;
    int cZ1;
    int cZ2;

    public BlockArrayUtils(short[] blockArray, byte[] metaArray, int chunkX, int chunkZ) {
        this.data = blockArray;
        this.meta = metaArray;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.cX1 = chunkX << 4;
        this.cX2 = this.cX1 + 15;
        this.cZ1 = chunkZ << 4;
        this.cZ2 = this.cZ1 + 15;
    }

    public void setBlockWithClipping(int x, int y, int z, short b) {
        if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
            this.data[y << 8 | z << 4 | x] = b;
        }
    }

    public void setBlockWithClippingAbsolute(int x, int y, int z, short b, byte m) {
        if (x >= this.cX1 && x <= this.cX2 && y >= 0 && y < 256 && z >= this.cZ1 && z <= this.cZ2) {
            this.data[x - this.cX1 | z - this.cZ1 << 4 | y << 8] = b;
            this.meta[x - this.cX1 | z - this.cZ1 << 4 | y << 8] = m;
        }
    }

    public boolean withinSphere(int x, int y, int z, int radius) {
        return x * x + y * y + z * z <= radius * radius;
    }

    public boolean drawSphere(int x0, int y0, int z0, int radius, int blockID) {
        short b = (short)blockID;

        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        this.setBlockWithClipping(x0 + x, y0 + y, z0 + z, b);
                        this.setBlockWithClipping(x0 - x, y0 + y, z0 + z, b);
                        this.setBlockWithClipping(x0 + x, y0 + y, z0 - z, b);
                        this.setBlockWithClipping(x0 - x, y0 + y, z0 - z, b);
                        this.setBlockWithClipping(x0 + x, y0 - y, z0 + z, b);
                        this.setBlockWithClipping(x0 - x, y0 - y, z0 + z, b);
                        this.setBlockWithClipping(x0 + x, y0 - y, z0 - z, b);
                        this.setBlockWithClipping(x0 - x, y0 - y, z0 - z, b);
                    }
                }
            }
        }

        return true;
    }

    public boolean drawSphereAbsolute(int x0, int y0, int z0, int radius, int blockID, int meta) {
        short b = (short)blockID;
        byte m = (byte)meta;

        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        this.setBlockWithClippingAbsolute(x0 + x, y0 + y, z0 + z, b, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 + y, z0 + z, b, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 + y, z0 - z, b, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 + y, z0 - z, b, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 - y, z0 + z, b, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 - y, z0 + z, b, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 - y, z0 - z, b, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 - y, z0 - z, b, m);
                    }
                }
            }
        }

        return true;
    }

    public boolean drawSphereRandom(Random rnd, int x0, int y0, int z0, int radius, int blockID, int meta, int altId) {
        short b = (short)blockID;
        byte m = (byte)meta;

        for (int x = 0; x <= radius; x++) {
            for (int z = 0; z <= radius; z++) {
                for (int y = 0; y <= radius; y++) {
                    if (this.withinSphere(x, y, z, radius)) {
                        this.setBlockWithClippingAbsolute(x0 + x, y0 + y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 + y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 + y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 + y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 - y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 - y, z0 + z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 + x, y0 - y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                        this.setBlockWithClippingAbsolute(x0 - x, y0 - y, z0 - z, rnd.nextInt(4) == 0 ? b : (byte)altId, m);
                    }
                }
            }
        }

        return true;
    }
}
