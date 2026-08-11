package net.minecraft.game.physics;

import net.minecraft.util.math.MathHelper;

public class VectorBasic {
    public static final VectorBasic NULL_VECTOR = new VectorBasic(0, 0, 0);
    public int x;
    public int y;
    public int z;

    public VectorBasic(int xIn, int yIn, int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    public VectorBasic(double xIn, double yIn, double zIn) {
        this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof VectorBasic)) {
            return false;
        }

        VectorBasic vec3i = (VectorBasic)other;
        return this.getX() != vec3i.getX() ? false : (this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ());
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(VectorBasic other) {
        return this.getY() == other.getY()
            ? (this.getZ() == other.getZ() ? this.getX() - other.getX() : this.getZ() - other.getZ())
            : this.getY() - other.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public VectorBasic crossProduct(VectorBasic vec) {
        return new VectorBasic(
            this.getY() * vec.getZ() - this.getZ() * vec.getY(),
            this.getZ() * vec.getX() - this.getX() * vec.getZ(),
            this.getX() * vec.getY() - this.getY() * vec.getX()
        );
    }

    public double distanceSq(double toX, double toY, double toZ) {
        double distX = this.getX() - toX;
        double distY = this.getY() - toY;
        double distZ = this.getZ() - toZ;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public double distanceSqToCenter(double xIn, double yIn, double zIn) {
        double distX = this.getX() + 0.5 - xIn;
        double distY = this.getY() + 0.5 - yIn;
        double distZ = this.getZ() + 0.5 - zIn;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public double distanceSq(VectorBasic to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ());
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public VectorBasic copy() {
        return new VectorBasic(this.x, this.y, this.z);
    }
}
