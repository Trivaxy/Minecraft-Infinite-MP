package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class QuadrupedModel extends Model {
    public ModelPart head;
    public ModelPart body;
    public ModelPart frontLeftLeg;
    public ModelPart frontRightLeg;
    public ModelPart backLeftLeg;
    public ModelPart backRightLeg;
    protected boolean rotate = true;

    public QuadrupedModel(int offset, float scaleFactor) {
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, scaleFactor, false);
        this.head.setPivots(0.0F, 18 - offset, -6.0F);
        this.body = new ModelPart(this, 28, 8);
        this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, scaleFactor, false);
        this.body.setPivots(0.0F, 17 - offset, 2.0F);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, offset, 4, scaleFactor, false);
        this.frontLeftLeg.setPivots(-3.0F, 24 - offset, 7.0F);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, offset, 4, scaleFactor, true);
        this.frontRightLeg.setPivots(3.0F, 24 - offset, 7.0F);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, offset, 4, scaleFactor, false);
        this.backLeftLeg.setPivots(-3.0F, 24 - offset, -5.0F);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, offset, 4, scaleFactor, true);
        this.backRightLeg.setPivots(3.0F, 24 - offset, -5.0F);
    }

    @Override
    public void render(Entity animal, float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float deltaTime) {
        this.animate(limbSwing, animSpeed, age, headYaw, headPitch, deltaTime);
        this.head.render(deltaTime);
        this.body.render(deltaTime);
        this.frontLeftLeg.render(deltaTime);
        this.frontRightLeg.render(deltaTime);
        this.backLeftLeg.render(deltaTime);
        this.backRightLeg.render(deltaTime);
    }

    @Override
    public void animate(float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        if (this.rotate) {
            this.body.rotationX = (float) (Math.PI / 2);
        }

        this.frontLeftLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * animSpeed;
        this.frontRightLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * animSpeed;
        this.backLeftLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * animSpeed;
        this.backRightLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * animSpeed;
    }
}
