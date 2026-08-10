package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.Sheep;
import net.minecraft.util.math.MathHelper;

public class SheepModel extends QuadrupedModel {
    private float graze = 1.0F;
    private float prevGrazeValue;
    public boolean grazing = false;
    private float grazeAngle;

    public SheepModel() {
        super(12, 0.0F);
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F, false);
        this.head.setPivots(0.0F, 6.0F, -8.0F);
        this.body = new ModelPart(this, 28, 8);
        this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F, false);
        this.body.setPivots(0.0F, 5.0F, 2.0F);
    }

    @Override
    public void animateSpecial(Mob entity, float swing, float prevSwing, float ticks) {
        if (this.grazing) {
            float angle = (((Sheep)entity).grazingTime + ticks) / 32.0F;
            this.grazeAngle = (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(angle * 28.7F);
        } else {
            this.grazeAngle = entity.pitch / (180.0F / (float)Math.PI);
        }
    }

    @Override
    public void animate(float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        super.animate(limbAngle, animationSpeed, angle, headYaw, headPitch, tick);
        this.head.rotationX = this.grazeAngle;
        this.prevGrazeValue = this.graze;
        if (this.grazing) {
            this.graze += 0.2F;
        } else {
            this.graze -= 0.2F;
        }

        this.graze = MathHelper.clamp(this.graze, 0.5F, 1.5F);
        if (this.grazing) {
            this.head.pivotY = this.head.pivotY + (this.prevGrazeValue + (this.graze - this.prevGrazeValue) * animationSpeed) * 8.0F;
        } else {
            this.head.pivotY = this.head.pivotY - (this.prevGrazeValue + (this.graze - this.prevGrazeValue) * animationSpeed) * 8.0F;
        }

        this.head.pivotY = MathHelper.clamp(this.head.pivotY, 6.0F, 10.0F);
    }

    public static class SheepFur extends QuadrupedModel {
        public SheepFur() {
            super(12, 0.0F);
            this.head = new ModelPart(this, 0, 0);
            this.head.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F, false);
            this.head.setPivots(0.0F, 6.0F, -8.0F);
            this.body = new ModelPart(this, 28, 8);
            this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F, false);
            this.body.setPivots(0.0F, 5.0F, 2.0F);
            float offset = 0.5F;
            this.frontLeftLeg = new ModelPart(this, 0, 16);
            this.frontLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, offset, false);
            this.frontLeftLeg.setPivots(-3.0F, 12.0F, 7.0F);
            this.frontRightLeg = new ModelPart(this, 0, 16);
            this.frontRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, offset, false);
            this.frontRightLeg.setPivots(3.0F, 12.0F, 7.0F);
            this.backLeftLeg = new ModelPart(this, 0, 16);
            this.backLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, offset, false);
            this.backLeftLeg.setPivots(-3.0F, 12.0F, -5.0F);
            this.backRightLeg = new ModelPart(this, 0, 16);
            this.backRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, offset, false);
            this.backRightLeg.setPivots(3.0F, 12.0F, -5.0F);
        }
    }
}
