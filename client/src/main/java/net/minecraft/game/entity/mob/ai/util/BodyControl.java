package net.minecraft.game.entity.mob.ai.util;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class BodyControl {
    private Mob mob;
    private int rotations = 0;
    private float facing = 0.0F;

    public BodyControl(Mob mob) {
        this.mob = mob;
    }

    public void tick() {
        double distX = this.mob.posX - this.mob.prevX;
        double distZ = this.mob.posZ - this.mob.prevZ;
        if (distX * distX + distZ * distZ > 2.5000003E-7F) {
            this.mob.yawOffset = this.mob.yaw;
            this.mob.headFacing = this.updateRotation(this.mob.yawOffset, this.mob.headFacing, 75.0F);
            this.facing = this.mob.headFacing;
            this.rotations = 0;
        } else {
            float maxRot = 75.0F;
            if (Math.abs(this.mob.headFacing - this.facing) > 15.0F) {
                this.rotations = 0;
                this.facing = this.mob.headFacing;
            } else {
                this.rotations++;
                if (this.rotations > 10) {
                    maxRot = Math.max(1.0F - (this.rotations - 10) / 10.0F, 0.0F) * 75.0F;
                }
            }

            this.mob.yawOffset = this.updateRotation(this.mob.headFacing, this.mob.yawOffset, maxRot);
        }
    }

    private float updateRotation(float facing, float angle, float bound) {
        float offset = MathHelper.computeAngle(facing - angle);
        offset = MathHelper.clamp(offset, -bound, bound);
        return facing - offset;
    }
}
