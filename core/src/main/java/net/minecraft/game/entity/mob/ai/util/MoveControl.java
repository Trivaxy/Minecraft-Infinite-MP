package net.minecraft.game.entity.mob.ai.util;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class MoveControl {
    public Mob entity;
    public double posX;
    public double posY;
    public double posZ;
    public float speed;
    public boolean isMoving = false;

    public MoveControl(Mob mob) {
        this.entity = mob;
        this.posX = mob.posX;
        this.posY = mob.posY;
        this.posZ = mob.posZ;
    }

    public boolean getMoving() {
        return this.isMoving;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        this.isMoving = true;
    }

    public void changeFacing(float angle, boolean angy) {
    }

    public void moveTo(double x, double y, double z, float spd) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.speed = spd;
        this.isMoving = true;
    }

    public void tick() {
        this.entity.moveForward = this.entity.landSpeed = 0.0F;
        if (this.isMoving) {
            this.isMoving = false;
            double distX = this.posX - this.entity.posX;
            double distZ = this.posZ - this.entity.posZ;
            double distY = this.posY - this.entity.posY;
            double dist = distX * distX + distY * distY + distZ * distZ;
            if (dist >= 2.5000003E-7F) {
                float lookAngle = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
                this.entity.yaw = this.updateRotation(this.entity.yaw, lookAngle, 30.0F);
                this.entity.moveForward = this.speed;
                if (this.speed > 0.0F) {
                    this.entity.landSpeed = this.speed;
                } else {
                    this.entity.landSpeed = 0.1F;
                }

                if (distY > 0.0 && distX * distX + distZ * distZ < 1.0) {
                    this.entity.jumpControl.setJumping();
                }
            }
        }
    }

    protected float updateRotation(float facing, float angle, float bound) {
        float offset = MathHelper.computeAngle(angle - facing);
        offset = MathHelper.clamp(offset, -bound, bound);
        float finalAngle = facing + offset;
        if (finalAngle < 0.0F) {
            finalAngle += 360.0F;
        } else if (finalAngle > 360.0F) {
            finalAngle -= 360.0F;
        }

        return finalAngle;
    }
}
