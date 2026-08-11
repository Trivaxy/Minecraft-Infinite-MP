package net.minecraft.game.world.generation.structures;

public class StructureBoundingBox {
    public int x1;
    public int y1;
    public int z1;
    public int x2;
    public int y2;
    public int z2;

    public StructureBoundingBox(int x1, int z1, int x2, int z2) {
        this(x1, 0, z1, x2, 255, z2);
    }

    public StructureBoundingBox(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public boolean collidesWith(StructureBoundingBox other) {
        return this.x1 <= other.x2 && this.x2 >= other.x1 && this.y1 <= other.y2 && this.y2 >= other.y1 && this.z1 <= other.z2 && this.z2 >= other.z1;
    }

    public boolean containsFully(StructureBoundingBox other) {
        return this.x1 <= other.x1 && this.x2 >= other.x2 && this.y1 <= other.y1 && this.y2 >= other.y2 && this.z1 <= other.z1 && this.z2 >= other.z2;
    }

    @Override
    public String toString() {
        return "(" + this.x1 + ", " + this.y1 + ", " + this.z1 + ") -> (" + this.x2 + ", " + this.y2 + ", " + this.z2 + ")";
    }
}
