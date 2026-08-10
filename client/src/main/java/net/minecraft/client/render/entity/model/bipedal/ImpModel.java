package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.ModelPart;

public class ImpModel extends BipedalModel {
    public ModelPart rightHorn;
    public ModelPart leftHorn;

    public ImpModel() {
        this(0.0F);
    }

    public ImpModel(float scale) {
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, 0.0F, -4.0F, 8, 8, 8, scale, false);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.hair = new ModelPart(this, 32, 0);
        this.hair.addBox(-4.0F, 0.0F, -4.0F, 8, 8, 8, scale + 0.5F, false);
        this.hair.setPivots(0.0F, 0.0F, 0.0F);
        this.rightHorn = new ModelPart(this, 32, 0);
        this.rightHorn.addBox(-3.5F, -2.0F, -0.5F, 1, 2, 1, scale, false);
        this.rightHorn.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.rightHorn);
        this.leftHorn = new ModelPart(this, 32, 0);
        this.leftHorn.addBox(2.5F, -2.0F, -0.5F, 1, 2, 1, scale, false);
        this.leftHorn.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.leftHorn);
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0F, 8.0F, -2.0F, 8, 8, 4, scale, false);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 8, 4, scale, false);
        this.rightArm.setPivots(-5.0F, 10.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 8, 4, scale, true);
        this.leftArm.setPivots(5.0F, 10.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, scale, false);
        this.rightLeg.setPivots(-2.0F, 16.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 8, 4, scale, true);
        this.leftLeg.setPivots(2.0F, 16.0F, 0.0F);
    }

    @Override
    protected void uprightPose() {
        this.body.rotationX = 0.0F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
        this.rightLeg.pivotY = 16.0F;
        this.leftLeg.pivotY = 16.0F;
        this.head.pivotY = 0.0F;
        this.hair.pivotY = 0.0F;
        this.body.pivotY = 0.0F;
        this.leftArm.pivotY = 10.0F;
        this.rightArm.pivotY = 10.0F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
    }
}
