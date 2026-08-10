package net.minecraft.game.world.util;

public class Position implements Comparable<Position> {
    public int x;
    public int y;
    public int z;
    public int dim;

    public Position() {
    }

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Position pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.dim = pos.dim;
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }

        Position pos = (Position)obj;
        return this.x == pos.x && this.y == pos.y && this.z == pos.z;
    }

    @Override
    public int hashCode() {
        return this.x + this.z << 8 + this.y << 16;
    }

    public int compareTo(Position o) {
        return this.y == o.y ? (this.z == o.z ? this.x - o.x : this.z - o.z) : this.y - o.y;
    }

    public double getDistanceSq(int x, int y, int z) {
        int xd = this.x - x;
        int yd = this.y - y;
        int zd = this.z - z;
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    @Override
    public String toString() {
        return this.x + ", " + this.y + ", " + this.z;
    }
}
