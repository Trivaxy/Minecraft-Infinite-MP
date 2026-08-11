package net.minecraft.util.random;

import java.util.Random;

public class SkippableRandom extends Random {
    private static final long serialVersionUID = -2613795872951364460L;

    public SkippableRandom() {
    }

    public SkippableRandom(long seed) {
        super(seed);
    }

    public void skip(int bits) {
        for (int i = 0; i < bits; i++) {
            this.next(1);
        }
    }

    @Override
    protected int next(int n) {
        return super.next(n);
    }
}
