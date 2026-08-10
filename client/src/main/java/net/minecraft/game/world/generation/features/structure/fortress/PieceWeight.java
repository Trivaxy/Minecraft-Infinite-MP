package net.minecraft.game.world.generation.features.structure.fortress;

public class PieceWeight {
    public Class<?> piece;
    public final int x;
    public int maxLimit;
    public int z;
    public boolean isSubmerged;

    public PieceWeight(Class<?> class1, int x, int z, boolean underwater) {
        this.piece = class1;
        this.x = x;
        this.z = z;
        this.isSubmerged = underwater;
    }

    public PieceWeight(Class<?> class1, int x, int z) {
        this(class1, x, z, false);
    }

    public boolean getMaxLimit() {
        return this.z == 0 || this.maxLimit < this.z;
    }
}
