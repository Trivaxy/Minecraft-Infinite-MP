package net.minecraft.game.physics;

public class BoundingBox {
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public static BoundingBox getBoundingBox(double aabbMinX, double aabbMinY, double aabbMinZ, double aabbMaxX, double aabbMaxY, double aabbMaxZ) {
        return new BoundingBox(aabbMinX, aabbMinY, aabbMinZ, aabbMaxX, aabbMaxY, aabbMaxZ);
    }

    public BoundingBox(double aabbMinX, double aabbMinY, double aabbMinZ, double aabbMaxX, double aabbMaxY, double aabbMaxZ) {
        this.minX = aabbMinX;
        this.minY = aabbMinY;
        this.minZ = aabbMinZ;
        this.maxX = aabbMaxX;
        this.maxY = aabbMaxY;
        this.maxZ = aabbMaxZ;
    }

    public BoundingBox setBounds(double aabbMinX, double aabbMinY, double aabbMinZ, double aabbMaxX, double aabbMaxY, double aabbMaxZ) {
        this.minX = aabbMinX;
        this.minY = aabbMinY;
        this.minZ = aabbMinZ;
        this.maxX = aabbMaxX;
        this.maxY = aabbMaxY;
        this.maxZ = aabbMaxZ;
        return this;
    }

    public BoundingBox addCoord(double xCoord, double yCoord, double zCoord) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;
        if (xCoord < 0.0) {
            minX += xCoord;
        }

        if (xCoord > 0.0) {
            maxX += xCoord;
        }

        if (yCoord < 0.0) {
            minY += yCoord;
        }

        if (yCoord > 0.0) {
            maxY += yCoord;
        }

        if (zCoord < 0.0) {
            minZ += zCoord;
        }

        if (zCoord > 0.0) {
            maxZ += zCoord;
        }

        return getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public BoundingBox expand(double xCoord, double yCoord, double zCoord) {
        double minX = this.minX - xCoord;
        double minY = this.minY - yCoord;
        double minZ = this.minZ - zCoord;
        double maxX = this.maxX + xCoord;
        double maxY = this.maxY + yCoord;
        double maxZ = this.maxZ + zCoord;
        return getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public BoundingBox getOffsetBoundingBox(double xCoord, double yCoord, double zCoord) {
        return getBoundingBox(this.minX + xCoord, this.minY + yCoord, this.minZ + zCoord, this.maxX + xCoord, this.maxY + yCoord, this.maxZ + zCoord);
    }

    public double calculateXOffset(BoundingBox aabb, double offsetX) {
        if (!(aabb.maxY > this.minY) || !(aabb.minY < this.maxY)) {
            return offsetX;
        }

        if (aabb.maxZ > this.minZ && aabb.minZ < this.maxZ) {
            if (offsetX > 0.0 && aabb.maxX <= this.minX) {
                double dist = this.minX - aabb.maxX;
                if (dist < offsetX) {
                    offsetX = dist;
                }
            }

            if (offsetX < 0.0 && aabb.minX >= this.maxX) {
                double dist = this.maxX - aabb.minX;
                if (dist > offsetX) {
                    offsetX = dist;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    public double calculateYOffset(BoundingBox aabb, double offsetY) {
        if (!(aabb.maxX > this.minX) || !(aabb.minX < this.maxX)) {
            return offsetY;
        }

        if (aabb.maxZ > this.minZ && aabb.minZ < this.maxZ) {
            if (offsetY > 0.0 && aabb.maxY <= this.minY) {
                double dist = this.minY - aabb.maxY;
                if (dist < offsetY) {
                    offsetY = dist;
                }
            }

            if (offsetY < 0.0 && aabb.minY >= this.maxY) {
                double dist = this.maxY - aabb.minY;
                if (dist > offsetY) {
                    offsetY = dist;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    public double calculateZOffset(BoundingBox aabb, double offsetZ) {
        if (!(aabb.maxX > this.minX) || !(aabb.minX < this.maxX)) {
            return offsetZ;
        }

        if (aabb.maxY > this.minY && aabb.minY < this.maxY) {
            if (offsetZ > 0.0 && aabb.maxZ <= this.minZ) {
                double dist = this.minZ - aabb.maxZ;
                if (dist < offsetZ) {
                    offsetZ = dist;
                }
            }

            if (offsetZ < 0.0 && aabb.minZ >= this.maxZ) {
                double dist = this.maxZ - aabb.minZ;
                if (dist > offsetZ) {
                    offsetZ = dist;
                }
            }

            return offsetZ;
        } else {
            return offsetZ;
        }
    }

    public boolean intersects(BoundingBox aabb) {
        return !(aabb.maxX > this.minX) || !(aabb.minX < this.maxX)
            ? false
            : (aabb.maxY > this.minY && aabb.minY < this.maxY ? aabb.maxZ > this.minZ && aabb.minZ < this.maxZ : false);
    }

    public BoundingBox offset(double xCoord, double yCoord, double zCoord) {
        this.minX += xCoord;
        this.minY += yCoord;
        this.minZ += zCoord;
        this.maxX += xCoord;
        this.maxY += yCoord;
        this.maxZ += zCoord;
        return this;
    }

    public double getLength() {
        double x = this.maxX - this.minX;
        double y = this.maxY - this.minY;
        double z = this.maxZ - this.minZ;
        return (x + y + z) / 3.0;
    }

    public BoundingBox shrink(double xCoord, double yCoord, double zCoord) {
        double minX = this.minX + xCoord;
        double minY = this.minY + yCoord;
        double minZ = this.minZ + zCoord;
        double maxX = this.maxX - xCoord;
        double maxY = this.maxY - yCoord;
        double maxZ = this.maxZ - zCoord;
        return getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public BoundingBox copy() {
        return getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public TargetingSystem calculateIntercept(Vector vec1, Vector vec2) {
        Vector minX = vec1.getIntermediateX(vec2, this.minX);
        Vector maxX = vec1.getIntermediateX(vec2, this.maxX);
        Vector minY = vec1.getIntermediateY(vec2, this.minY);
        Vector maxY = vec1.getIntermediateY(vec2, this.maxY);
        Vector minZ = vec1.getIntermediateZ(vec2, this.minZ);
        Vector maxZ = vec1.getIntermediateZ(vec2, this.maxZ);
        if (!this.isVecInYZ(minX)) {
            minX = null;
        }

        if (!this.isVecInYZ(maxX)) {
            maxX = null;
        }

        if (!this.isVecInXZ(minY)) {
            minY = null;
        }

        if (!this.isVecInXZ(maxY)) {
            maxY = null;
        }

        if (!this.isVecInXY(minZ)) {
            minZ = null;
        }

        if (!this.isVecInXY(maxZ)) {
            maxZ = null;
        }

        Vector newVec = null;
        if (minX != null && (newVec == null || vec1.squareDistanceTo(minX) < vec1.squareDistanceTo(newVec))) {
            newVec = minX;
        }

        if (maxX != null && (newVec == null || vec1.squareDistanceTo(maxX) < vec1.squareDistanceTo(newVec))) {
            newVec = maxX;
        }

        if (minY != null && (newVec == null || vec1.squareDistanceTo(minY) < vec1.squareDistanceTo(newVec))) {
            newVec = minY;
        }

        if (maxY != null && (newVec == null || vec1.squareDistanceTo(maxY) < vec1.squareDistanceTo(newVec))) {
            newVec = maxY;
        }

        if (minZ != null && (newVec == null || vec1.squareDistanceTo(minZ) < vec1.squareDistanceTo(newVec))) {
            newVec = minZ;
        }

        if (maxZ != null && (newVec == null || vec1.squareDistanceTo(maxZ) < vec1.squareDistanceTo(newVec))) {
            newVec = maxZ;
        }

        if (newVec == null) {
            return null;
        }

        byte side = -1;
        if (newVec == minX) {
            side = 4;
        }

        if (newVec == maxX) {
            side = 5;
        }

        if (newVec == minY) {
            side = 0;
        }

        if (newVec == maxY) {
            side = 1;
        }

        if (newVec == minZ) {
            side = 2;
        }

        if (newVec == maxZ) {
            side = 3;
        }

        return new TargetingSystem(0, 0, 0, side, newVec);
    }

    private boolean isVecInYZ(Vector vec) {
        return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXZ(Vector vec) {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean isVecInXY(Vector vec) {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public boolean isVecInside(Vector vec) {
        return !(vec.xCoord > this.minX) || !(vec.xCoord < this.maxX)
            ? false
            : (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false);
    }

    public void setBB(BoundingBox aabb) {
        this.minX = aabb.minX;
        this.minY = aabb.minY;
        this.minZ = aabb.minZ;
        this.maxX = aabb.maxX;
        this.maxY = aabb.maxY;
        this.maxZ = aabb.maxZ;
    }
}
