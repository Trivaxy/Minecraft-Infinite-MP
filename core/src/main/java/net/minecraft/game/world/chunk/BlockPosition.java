package net.minecraft.game.world.chunk;

import net.minecraft.game.physics.VectorBasic;
import net.minecraft.util.math.Direction;

public class BlockPosition {
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    public int x;
    public int y;
    public int z;

    public BlockPosition() {
    }

    public BlockPosition(int xCoord, int yCoord, int zCoord) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockPosition)) {
            return false;
        }

        BlockPosition pos = (BlockPosition)object;
        return pos.x == this.x && pos.y == this.y && pos.z == this.z;
    }

    @Override
    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }

    public BlockPosition set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public BlockPosition set(BlockPosition blockPosition) {
        this.x = blockPosition.x;
        this.y = blockPosition.y;
        this.z = blockPosition.z;
        return this;
    }

    public BlockPosition move(int direction, int distance) {
        this.x = this.x + Direction.offsetX[direction] * distance;
        this.y = this.y + Direction.offsetY[direction] * distance;
        this.z = this.z + Direction.offsetZ[direction] * distance;
        return this;
    }

    public BlockPosition move(int direction) {
        return this.move(direction, 1);
    }

    public BlockPosition move(VectorBasic vector) {
        this.x = this.x + vector.getX();
        this.y = this.y + vector.getY();
        this.z = this.z + vector.getZ();
        return this;
    }

    public BlockPosition copy() {
        return new BlockPosition().set(this.x, this.y, this.z);
    }

    public BlockPosition add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPosition(this.x + x, this.y + y, this.z + z);
    }
}
