package net.minecraft.client.render.camera;

import net.minecraft.game.physics.BoundingBox;

public class Camera {
    private Frustum frustum = Frustum.getInstance();
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public void setPosition(double xCoord, double yCoord, double zCoord) {
        this.xPosition = xCoord;
        this.yPosition = yCoord;
        this.zPosition = zCoord;
    }

    public boolean isInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.frustum
            .isInFrustum(
                minX - this.xPosition, minY - this.yPosition, minZ - this.zPosition, maxX - this.xPosition, maxY - this.yPosition, maxZ - this.zPosition
            );
    }

    public boolean isBoundingBoxInFrustum(BoundingBox aabb) {
        return this.isInFrustum(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }
}
