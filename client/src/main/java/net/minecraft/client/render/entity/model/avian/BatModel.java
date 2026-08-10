package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BatModel extends Model {
    private ModelPart head;
    private ModelPart body;
    private ModelPart rightWing;
    private ModelPart leftWing;
    private ModelPart outerRightWing;
    private ModelPart outerLeftWing;
    public boolean isHanging = false;

    public BatModel() {
        this.head = new ModelPart(this, 0, 0);
        this.head.setTextureSize(64, 64);
        this.head.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false);
        ModelPart leftEar = new ModelPart(this, 24, 0);
        leftEar.setTextureSize(64, 64);
        leftEar.addBox(-4.0F, -6.0F, -2.0F, 3, 4, 1, 0.0F, false);
        this.head.addChild(leftEar);
        ModelPart rightEar = new ModelPart(this, 24, 0);
        rightEar.setTextureSize(64, 64);
        rightEar.addBox(1.0F, -6.0F, -2.0F, 3, 4, 1, 0.0F, true);
        this.head.addChild(rightEar);
        this.body = new ModelPart(this, 0, 16);
        this.body.setTextureSize(64, 64);
        this.body.addBox(-3.0F, 4.0F, -3.0F, 6, 12, 6, 0.0F, false);
        this.rightWing = new ModelPart(this, 42, 0);
        this.rightWing.setTextureSize(64, 64);
        this.rightWing.addBox(-12.0F, 1.0F, 1.5F, 10, 16, 1, 0.0F, false);
        this.outerRightWing = new ModelPart(this, 24, 16);
        this.outerRightWing.setTextureSize(64, 64);
        this.outerRightWing.setPivots(-12.0F, 1.0F, 1.5F);
        this.outerRightWing.addBox(-8.0F, 1.0F, 0.0F, 8, 12, 1, 0.0F, false);
        this.leftWing = new ModelPart(this, 42, 24);
        this.leftWing.setTextureSize(64, 64);
        this.leftWing.addBox(2.0F, 1.0F, 1.5F, 10, 16, 1, 0.0F, false);
        this.outerLeftWing = new ModelPart(this, 24, 48);
        this.outerLeftWing.setTextureSize(64, 64);
        this.outerLeftWing.setPivots(12.0F, 1.0F, 1.5F);
        this.outerLeftWing.addBox(0.0F, 1.0F, 0.0F, 8, 12, 1, 0.0F, false);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.outerRightWing);
        this.leftWing.addChild(this.outerLeftWing);
    }

    @Override
    public void render(Entity entity, float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        this.isHanging = entity.sitting;
        this.animate(limbSwing, animSpeed, age, headYaw, headPitch, ticks);
        this.head.render(ticks);
        this.body.render(ticks);
    }

    @Override
    public void animate(float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        if (this.isHanging) {
            this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
            this.head.rotationY = (float) Math.PI - headYaw / (180.0F / (float)Math.PI);
            this.head.rotationZ = (float) Math.PI;
            this.head.setPivots(0.0F, -2.0F, 0.0F);
            this.rightWing.setPivots(-3.0F, 0.0F, 3.0F);
            this.leftWing.setPivots(3.0F, 0.0F, 3.0F);
            this.body.rotationX = (float) Math.PI;
            this.rightWing.rotationX = (float) (-Math.PI / 20);
            this.rightWing.rotationY = (float) (-Math.PI * 2.0 / 5.0);
            this.outerRightWing.rotationY = -1.7278761F;
            this.leftWing.rotationX = this.rightWing.rotationX;
            this.leftWing.rotationY = -this.rightWing.rotationY;
            this.outerLeftWing.rotationY = -this.outerRightWing.rotationY;
        } else {
            this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
            this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
            this.head.rotationZ = 0.0F;
            this.head.setPivots(0.0F, 0.0F, 0.0F);
            this.rightWing.setPivots(0.0F, 0.0F, 0.0F);
            this.leftWing.setPivots(0.0F, 0.0F, 0.0F);
            this.body.rotationX = (float) (Math.PI / 4) + MathHelper.cos(age * 0.1F) * 0.15F;
            this.body.rotationY = 0.0F;
            this.rightWing.rotationY = MathHelper.cos(age * 1.3F) * (float) Math.PI * 0.25F;
            this.leftWing.rotationY = -this.rightWing.rotationY;
            this.outerRightWing.rotationY = this.rightWing.rotationY * 0.5F;
            this.outerLeftWing.rotationY = -this.rightWing.rotationY * 0.5F;
        }
    }
}
