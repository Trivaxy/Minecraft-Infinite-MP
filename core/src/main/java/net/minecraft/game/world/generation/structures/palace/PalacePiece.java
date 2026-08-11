package net.minecraft.game.world.generation.structures.palace;

import net.minecraft.game.world.generation.structures.StructureBoundingBox;

public abstract class PalacePiece {
    public Palace featureIcePalace;
    public StructureBoundingBox aabb;
    public int centerX;
    public int centerY;
    public int baseY;
    public int centerZ;
    public int width;
    public int height;
    public int length;
    public boolean rotated;

    public abstract int[][][] getPiece();

    public PalacePiece(boolean rotated, Palace featureIcePalace) {
        this.rotated = rotated;
        this.featureIcePalace = featureIcePalace;
        int[][][] piece = this.getPiece();
        this.width = piece.length;
        this.length = piece[0].length;
        this.height = piece[0][0].length;
    }

    public void place(int x, int y, int z) {
        this.centerX = x;
        this.baseY = y;
        this.centerY = y + (this.height >> 1);
        this.centerZ = z;
        if (this.rotated) {
            this.aabb = new StructureBoundingBox(
                this.centerX - (this.length >> 1),
                this.baseY,
                this.centerZ - (this.width >> 1),
                this.centerX + (this.length >> 1),
                this.baseY + this.height - 1,
                this.centerZ + (this.width >> 1)
            );
        } else {
            this.aabb = new StructureBoundingBox(
                this.centerX - (this.width >> 1),
                this.baseY,
                this.centerZ - (this.length >> 1),
                this.centerX + (this.width >> 1),
                this.baseY + this.height - 1,
                this.centerZ + (this.length >> 1)
            );
        }
    }

    protected int rotateX(int x, int z) {
        return this.rotated ? z : x;
    }

    protected int rotateZ(int x, int z) {
        return this.rotated ? x : z;
    }

    public void drawPiece(int[][][] map) {
        int x1 = this.aabb.x1;
        int z1 = this.aabb.z1;
        int y1 = this.aabb.y1;
        int radius = (int)(Math.sqrt(this.width * this.width + this.height * this.height + this.length * this.length) / 2.0);
        this.featureIcePalace.sphere(this.centerX, this.centerY, this.centerZ, radius + 3, -1, 0);
        int[][][] piece = this.getPiece();

        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.length; z++) {
                for (int y = 0; y < this.height; y++) {
                    int s = piece[x][z][y];
                    if (s >= 0) {
                        int blockID = s & 4095;
                        int meta = s >> 12;
                        if (this.rotated) {
                            switch (blockID) {
                                case 54:
                                case 65:
                                    meta ^= 1;
                                default:
                                    s = blockID | meta << 12;
                            }
                        }

                        if (blockID != 52 && blockID != 54 && blockID != 179 && blockID != 47) {
                            map[x1 + this.rotateX(x, z)][z1 + this.rotateZ(x, z)][y1 + y] = s;
                        } else {
                            this.featureIcePalace.addSpecialBlock(x1 + this.rotateX(x, z), y1 + y, z1 + this.rotateZ(x, z), blockID, meta);
                        }
                    }
                }
            }
        }
    }
}
