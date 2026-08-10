package net.minecraft.game.world.chunk;

public class NibbleArray {
    public final byte[] data;
    private final int depthBits;
    private final int quadBits;

    public NibbleArray(int length, int in) {
        this.data = new byte[length >> 1];
        this.depthBits = in;
        this.quadBits = in + 4;
    }

    public NibbleArray(byte[] array, int in) {
        this.data = array;
        this.depthBits = in;
        this.quadBits = in + 4;
    }

    public int get(int x, int y, int z) {
        int index = x << this.quadBits | z << this.depthBits | y;
        byte value = this.data[index >>> 1];
        return value >>> ((index & 1) << 2) & 15;
    }

    public void set(int x, int y, int z, int value) {
        int index = x << this.quadBits | z << this.depthBits | y;
        int shift = (index & 1) << 2;
        int i = index >>> 1;
        this.data[i] = (byte)(this.data[i] & 240 >>> shift | value << shift);
    }

    public boolean isValid() {
        return this.data != null;
    }
}
