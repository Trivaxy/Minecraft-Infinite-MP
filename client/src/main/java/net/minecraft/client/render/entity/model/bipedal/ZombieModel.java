package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class ZombieModel extends BipedalModel {
    public ZombieModel() {
        this(0.0F);
    }

    public ZombieModel(float scale) {
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.hair = new ModelPart(this, 32, 0);
        this.hair.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F, false);
        this.hair.setPivots(0.0F, 0.0F, 0.0F);
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightArm.setPivots(-5.0F, 2.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale, true);
        this.leftArm.setPivots(5.0F, 2.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightLeg.setPivots(-2.0F, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, true);
        this.leftLeg.setPivots(2.0F, 12.0F, 0.0F);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        if (((Mob)this.entity).attackFrames <= 0) {
            float swingMain = MathHelper.sin(this.swingMain * (float) Math.PI);
            float swingOff = MathHelper.sin((1.0F - (1.0F - this.swingMain) * (1.0F - this.swingMain)) * (float) Math.PI);
            this.rightArm.rotationZ = 0.0F;
            this.leftArm.rotationZ = 0.0F;
            this.rightArm.rotationY = -(0.1F - swingMain * 0.6F);
            this.leftArm.rotationY = 0.1F - swingMain * 0.6F;
            this.rightArm.rotationX = (float) (-Math.PI / 2);
            this.leftArm.rotationX = (float) (-Math.PI / 2);
            this.rightArm.rotationX -= swingMain * 1.2F - swingOff * 0.4F;
            this.leftArm.rotationX -= swingMain * 1.2F - swingOff * 0.4F;
            this.rightArm.rotationZ = this.rightArm.rotationZ + (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
            this.leftArm.rotationZ = this.leftArm.rotationZ - (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
            this.rightArm.rotationX = this.rightArm.rotationX + MathHelper.sin(angle * 0.067F) * 0.05F;
            this.leftArm.rotationX = this.leftArm.rotationX - MathHelper.sin(angle * 0.067F) * 0.05F;
        }

        if (this.riding) {
            this.rightLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
            this.leftLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
            this.rightLeg.rotationY = (float) (Math.PI / 10);
            this.leftLeg.rotationY = (float) (-Math.PI / 10);
        } else {
            this.rightLeg.rotationY = 0.0F;
            this.leftLeg.rotationY = 0.0F;
        }

        this.hair.pivotY = this.head.pivotY;
        this.hair.rotationY = this.head.rotationY;
        this.hair.rotationX = this.head.rotationX;
    }

    @Override
    public void animateSpecial(Mob entity, float armSwing, float animSpd, float angle) {
        if (entity.deathTimer <= 0) {
            if (entity.attackFrames > 0) {
                float anim = this.performAttackAnim(entity.attackFrames - angle, 10.0F);
                float mid = (float)Math.toRadians(250.0);
                float amp = (float)Math.toRadians(45.0);
                this.leftArm.rotationX = mid + amp * anim;
                this.rightArm.rotationX = mid + amp * anim;
            }
        } else {
            this.rightArm.rotationZ = 0.0F;
            this.leftArm.rotationZ = 0.0F;
            this.rightArm.rotationY = -(0.1F - this.swingMain * 0.6F);
            this.leftArm.rotationY = 0.1F - this.swingMain * 0.6F;
            this.rightArm.rotationX = (float) (-Math.PI / 2);
            this.leftArm.rotationX = (float) (-Math.PI / 2);
            this.rightArm.rotationX = this.rightArm.rotationX - (this.swingMain * 1.2F - this.swingOff * 0.4F);
            this.leftArm.rotationX = this.leftArm.rotationX - (this.swingMain * 1.2F - this.swingOff * 0.4F);
        }
    }

    private float performAttackAnim(float currTime, float prevTime) {
        return (Math.abs(currTime % prevTime - prevTime * 0.5F) - prevTime * 0.25F) / (prevTime * 0.25F);
    }
}
