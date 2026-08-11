package net.minecraft.game.world.generation.structures;

public abstract class SchematicStructurePiece {
    public int posX;
    public int posY;
    public int posZ;
    public StructureBoundingBox aabb;

    public void setPiece(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.aabb = new StructureBoundingBox(x, y, z, x + this.getWidth() - 1, y + this.getHeight() - 1, z + this.getLength() - 1);
    }

    public boolean intersectsWith(SchematicStructurePiece other) {
        return this.aabb.collidesWith(other.aabb);
    }

    public abstract int getWidth();

    public abstract int getLength();

    public abstract int getHeight();

    public abstract void drawPiece(int[][][] var1);
}
