package net.minecraft.game.world.pathfinder;

import net.minecraft.util.math.MathHelper;

public class PathNode {
    protected PathNode previous;
    public boolean isFirst = false;
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    public final int hash;
    public int index = -1;
    float totalPathDistance;
    float distanceToNext;
    float distanceToTarget;

    public PathNode(int x, int y, int z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.hash = getChunkLocation(x, y, z);
    }

    public static int getChunkLocation(int x, int y, int z) {
        return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public float distanceTo(PathNode target) {
        float x = target.xCoord - this.xCoord;
        float y = target.yCoord - this.yCoord;
        float z = target.zCoord - this.zCoord;
        return MathHelper.sqrt_float(x * x + y * y + z * z);
    }

    public boolean isAssigned() {
        return this.index >= 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PathNode)) {
            return false;
        }

        PathNode target = (PathNode)object;
        return this.hash == target.hash && this.xCoord == target.xCoord && this.yCoord == target.yCoord && this.zCoord == target.zCoord;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return this.xCoord + "x, " + this.yCoord + "y, " + this.zCoord + "z";
    }
}
