package net.minecraft.util.math;

import java.util.Random;

public class MathHelper {
    private static final int[] SINE_TABLE_INT = new int[16385];
    private static final float SINE_TABLE_MIDPOINT;

    public static float sin(float f0) {
        return lookup((int)(f0 * 10430.378F) & 65535);
    }

    public static float cos(float f0) {
        return lookup((int)(f0 * 10430.378F + 16384.0F) & 65535);
    }

    public static float sqrt_float(float f0) {
        return (float)Math.sqrt(f0);
    }

    public static float sqrt_double(double d0) {
        return (float)Math.sqrt(d0);
    }

    public static int floor_float(float f0) {
        int i1 = (int)f0;
        return f0 < i1 ? i1 - 1 : i1;
    }

    public static int floor_double(double d0) {
        int i2 = (int)d0;
        return d0 < i2 ? i2 - 1 : i2;
    }

    public static float abs(float f0) {
        return f0 >= 0.0F ? f0 : -f0;
    }

    public static int abs(int i0) {
        return i0 >= 0 ? i0 : -i0;
    }

    public static double abs_max(double d0, double d2) {
        if (d0 < 0.0) {
            d0 = -d0;
        }

        if (d2 < 0.0) {
            d2 = -d2;
        }

        return d0 > d2 ? d0 : d2;
    }

    public static int bucketInt(int i0, int i1) {
        return i0 < 0 ? -((-i0 - 1) / i1) - 1 : i0 / i1;
    }

    public static boolean stringNullOrLengthZero(String var0) {
        return var0 == null || var0.isEmpty();
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    private static float lookup(int index) {
        if (index == 32768) {
            return SINE_TABLE_MIDPOINT;
        }

        int neg = (index & 32768) << 16;
        int mask = index << 17 >> 31;
        int pos = (32769 & mask) + (index ^ mask);
        pos &= 32767;
        return Float.intBitsToFloat(SINE_TABLE_INT[pos] ^ neg);
    }

    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    public static int ceiling_float_int(float f) {
        int res = (int)f;
        return f > res ? res + 1 : res;
    }

    public static int ceiling_double_int(double f) {
        int res = (int)f;
        return f > res ? res + 1 : res;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < i ? i - 1 : i;
    }

    public static int offsetBy(double d0) {
        return (int)(d0 + 1024.0) - 1024;
    }

    public static int getRandomIntegerInRange(Random random0, int i1, int i2) {
        return i1 >= i2 ? i1 : random0.nextInt(i2 - i1 + 1) + i1;
    }

    public static double clamp_double(double num, double min, double max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static float clamp_float(float num, float min, float max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static float randomFloatClamp(Random rand, float f1, float f2) {
        return f1 >= f2 ? f1 : rand.nextFloat() * (f2 - f1) + f1;
    }

    public static float lerp(float alpha, float from, float to) {
        return from + alpha * (to - from);
    }

    public static float computeAngle(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }

    static {
        for (int i = 0; i < SINE_TABLE_INT.length; i++) {
            SINE_TABLE_INT[i] = Float.floatToRawIntBits((float)Math.sin(i * Math.PI * 2.0 / 65536.0));
        }

        SINE_TABLE_MIDPOINT = 1.2246469E-16F;
    }
}
