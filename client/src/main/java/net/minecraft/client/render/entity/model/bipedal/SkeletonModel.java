package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.ModelPart;

public class SkeletonModel extends ZombieModel {
    public SkeletonModel() {
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F, false);
        this.rightArm.setPivots(-5.0F, 2.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F, true);
        this.leftArm.setPivots(5.0F, 2.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F, false);
        this.rightLeg.setPivots(-2.0F, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F, true);
        this.leftLeg.setPivots(2.0F, 12.0F, 0.0F);
    }

    public SkeletonModel(float scale) {
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
}
