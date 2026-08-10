package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.util.math.MathHelper;

public class LamaModel extends QuadrupedModel {
    public LamaModel(float scale) {
        super(12, scale);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 7.0F, -6.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -2.0F, -14.0F, -10.0F, 4, 4, 9, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 14, -4.0F, -16.0F, -6.0F, 8, 18, 6, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 17, 0, 1.0F, -19.0F, -4.0F, 3, 3, 2, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 17, 0, -4.0F, -19.0F, -4.0F, 3, 3, 2, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 5.0F, 2.0F);
        this.setRotation(this.body, 1.5708F, 0.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 29, 0, -6.0F, -10.0F, -7.0F, 12, 18, 10, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-3.5F, 10.0F, 6.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 29, 29, -2.0F, 0.0F, -2.0F, 4, 14, 4, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(3.5F, 10.0F, 6.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 29, 29, -2.0F, 0.0F, -2.0F, 4, 14, 4, 0.0F, false));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(-3.5F, 10.0F, -5.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 29, 29, -2.0F, 0.0F, -2.0F, 4, 14, 4, 0.0F, false));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(3.5F, 10.0F, -5.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 29, 29, -2.0F, 0.0F, -2.0F, 4, 14, 4, 0.0F, false));
    }

    @Override
    public void animate(float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.frontLeftLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * animSpeed;
        this.frontRightLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * animSpeed;
        this.backLeftLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * animSpeed;
        this.backRightLeg.rotationX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * animSpeed;
    }
}
