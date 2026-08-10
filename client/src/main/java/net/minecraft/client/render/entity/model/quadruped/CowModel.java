package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class CowModel extends QuadrupedModel {
    private ModelPart udders;
    private ModelPart leftHorn;
    private ModelPart rightHorn;

    public CowModel() {
        super(12, 0.0F);
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -4.0F, -6.0F, 8, 8, 6, 0.0F, false);
        this.head.setPivots(0.0F, 4.0F, -8.0F);
        this.leftHorn = new ModelPart(this, 22, 0);
        this.leftHorn.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.leftHorn);
        this.leftHorn.addBox(-5.0F, -6.0F, -4.0F, 1, 3, 1, 0.0F, false);
        this.rightHorn = new ModelPart(this, 22, 0);
        this.head.addChild(this.rightHorn);
        this.rightHorn.setPivots(0.0F, 0.0F, 0.0F);
        this.rightHorn.addBox(4.0F, -6.0F, -4.0F, 1, 3, 1, 0.0F, false);
        this.udders = new ModelPart(this, 52, 0);
        this.udders.addBox(-2.0F, -3.0F, 0.0F, 4, 6, 2, 0.0F, false);
        this.udders.setPivots(0.0F, 14.0F, 6.0F);
        this.udders.rotationX = (float) (Math.PI / 2);
        this.body = new ModelPart(this, 18, 4);
        this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F, false);
        this.body.setPivots(0.0F, 5.0F, 2.0F);
        this.frontLeftLeg.pivotX--;
        this.frontRightLeg.pivotX++;
        this.backLeftLeg.pivotX--;
        this.backRightLeg.pivotX++;
        this.backLeftLeg.pivotZ--;
        this.backRightLeg.pivotZ--;
    }

    @Override
    public void render(Entity entity, float swing, float animSpd, float angle, float headYaw, float headPitch, float tick) {
        super.render(entity, swing, animSpd, angle, headYaw, headPitch, tick);
        this.udders.render(tick);
    }
}
