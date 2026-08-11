package net.minecraft.game.entity.mob.ai.util;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class LookControl {
    private Mob entity;
    private float yaw;
    private float pitch;
    private double posX;
    private double posY;
    private double posZ;
    private boolean isRotating = false;

    public LookControl(Mob mob) {
        this.entity = mob;
    }

    public void faceEntity(Entity target, float y, float p) {
        this.posX = target.posX;
        if (target instanceof Mob) {
            this.posY = target.posY + ((Mob)target).getEyeHeight();
        } else {
            this.posY = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0;
        }

        this.posZ = target.posZ;
        this.yaw = y;
        this.pitch = p;
        this.isRotating = true;
    }

    public void setLookPosition(double x, double y, double z, float yaw, float pitch) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.isRotating = true;
    }

    public void tick() {
        this.entity.pitch = 0.0F;
        if (this.isRotating) {
            this.isRotating = false;
            double distX = this.posX - this.entity.posX;
            double distY = this.posY - (this.entity.posY + this.entity.getEyeHeight());
            double distZ = this.posZ - this.entity.posZ;
            double dist = MathHelper.sqrt_double(distX * distX + distZ * distZ);
            float yaw = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
            float pitch = (float)(-(Math.atan2(distY, dist) * 180.0 / (float) Math.PI));
            this.entity.pitch = this.updateRotation(this.entity.pitch, pitch, this.pitch);
            this.entity.headFacing = this.updateRotation(this.entity.headFacing, yaw, this.yaw);
        } else {
            this.entity.headFacing = this.updateRotation(this.entity.headFacing, this.entity.yawOffset, 10.0F);
        }

        float lookAngle = MathHelper.computeAngle(this.entity.headFacing - this.entity.yawOffset);

        while (lookAngle >= 180.0F) {
            lookAngle -= 360.0F;
        }

        if (!this.entity.navigator.noPath()) {
            if (lookAngle < -75.0F) {
                this.entity.headFacing = this.entity.yawOffset - 75.0F;
            }

            if (lookAngle > 75.0F) {
                this.entity.headFacing = this.entity.yawOffset + 75.0F;
            }
        }
    }

    private float updateRotation(float facing, float angle, float bound) {
        float offset = MathHelper.computeAngle(angle - facing);
        offset = MathHelper.clamp(offset, -bound, bound);
        return facing + offset;
    }
}
