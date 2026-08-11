package net.minecraft.game.world.generation.features.structure;

public class LegacyStructureBoundingBox {
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public static LegacyStructureBoundingBox getNewBB() {
        return new LegacyStructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static LegacyStructureBoundingBox addToBB(int x0, int y0, int z0, int xoff, int yoff, int zoff, int xsize, int ysize, int zsize, int dir) {
        switch (dir) {
            case 0:
                return new LegacyStructureBoundingBox(x0 + xoff, y0 + yoff, z0 + zoff, x0 + xsize - 1 + xoff, y0 + ysize - 1 + yoff, z0 + zsize - 1 + zoff);
            case 1:
                return new LegacyStructureBoundingBox(x0 - zsize + 1 + zoff, y0 + yoff, z0 + xoff, x0 + zoff, y0 + ysize - 1 + yoff, z0 + xsize - 1 + xoff);
            case 2:
                return new LegacyStructureBoundingBox(x0 + xoff, y0 + yoff, z0 - zsize + 1 + zoff, x0 + xsize - 1 + xoff, y0 + ysize - 1 + yoff, z0 + zoff);
            case 3:
                return new LegacyStructureBoundingBox(x0 + zoff, y0 + yoff, z0 + xoff, x0 + zsize - 1 + zoff, y0 + ysize - 1 + yoff, z0 + xsize - 1 + xoff);
            default:
                return new LegacyStructureBoundingBox(x0 + xoff, y0 + yoff, z0 + zoff, x0 + xsize - 1 + xoff, y0 + ysize - 1 + yoff, z0 + zsize - 1 + zoff);
        }
    }

    public LegacyStructureBoundingBox(int minx, int miny, int minz, int maxx, int maxy, int maxz) {
        this.minX = minx;
        this.minY = miny;
        this.minZ = minz;
        this.maxX = maxx;
        this.maxY = maxy;
        this.maxZ = maxz;
    }

    public LegacyStructureBoundingBox(int x0, int z0, int x1, int z1) {
        this.minX = x0;
        this.minZ = z0;
        this.maxX = x1;
        this.maxZ = z1;
        this.minY = 1;
        this.maxY = 512;
    }

    public boolean intersectsWith(LegacyStructureBoundingBox bb) {
        return this.maxX >= bb.minX && this.minX <= bb.maxX && this.maxZ >= bb.minZ && this.minZ <= bb.maxZ && this.maxY >= bb.minY && this.minY <= bb.maxY;
    }

    public boolean intersectsWith(int x0, int z0, int x1, int z1) {
        return this.maxX >= x0 && this.minX <= x1 && this.maxZ >= z0 && this.minZ <= z1;
    }

    public void expand(LegacyStructureBoundingBox bb) {
        this.minX = Math.min(this.minX, bb.minX);
        this.minY = Math.min(this.minY, bb.minY);
        this.minZ = Math.min(this.minZ, bb.minZ);
        this.maxX = Math.max(this.maxX, bb.maxX);
        this.maxY = Math.max(this.maxY, bb.maxY);
        this.maxZ = Math.max(this.maxZ, bb.maxZ);
    }

    public void offset(int xo, int yo, int zo) {
        this.minX += xo;
        this.minY += yo;
        this.minZ += zo;
        this.maxX += xo;
        this.maxY += yo;
        this.maxZ += zo;
    }

    public boolean isVecInside(int x, int y, int z) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ && y >= this.minY && y <= this.maxY;
    }

    public boolean isVecInsideHorizontally(int x, int y, int z, int maxHeight) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ && y >= 0 && y <= maxHeight;
    }

    public int getXSize() {
        return this.maxX - this.minX + 1;
    }

    public int getYSize() {
        return this.maxY - this.minY + 1;
    }

    public int getZSize() {
        return this.maxZ - this.minZ + 1;
    }

    public int getCenterX() {
        return this.minX + (this.maxX - this.minX + 1) / 2;
    }

    public int getCenterY() {
        return this.minY + (this.maxY - this.minY + 1) / 2;
    }

    public int getCenterZ() {
        return this.minZ + (this.maxZ - this.minZ + 1) / 2;
    }

    @Override
    public String toString() {
        return "(" + this.minX + ", " + this.minY + ", " + this.minZ + "; " + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")";
    }
}
