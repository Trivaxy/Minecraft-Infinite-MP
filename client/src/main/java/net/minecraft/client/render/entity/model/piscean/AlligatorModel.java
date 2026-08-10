package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.util.math.MathHelper;

public class AlligatorModel extends Model {
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart jawUpper;
    private final ModelPart jawLower;
    private final ModelPart tail;
    private final ModelPart tailSmall;
    private final ModelPart tailEnd;
    private final ModelPart backRightLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backLeftLeg;

    public AlligatorModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 24.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 3, 3, -6.0F, -6.0F, -15.0F, 12, 6, 24, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.1F, -2.0F, -13.0F);
        this.torso.addChild(this.head);
        this.jawUpper = new ModelPart(this);
        this.jawUpper.setPivots(0.1F, 0.0F, -2.0F);
        this.head.addChild(this.jawUpper);
        this.jawUpper.cuboids.add(new Cuboid(this.jawUpper, 86, 20, -2.7F, -2.0F, -13.0F, 5, 2, 6, 0.0F, false));
        this.jawUpper.cuboids.add(new Cuboid(this.jawUpper, 56, 5, -3.7F, -3.0F, -7.0F, 7, 3, 7, 0.0F, false));
        this.jawLower = new ModelPart(this);
        this.jawLower.setPivots(0.1F, 0.0F, -2.0F);
        this.head.addChild(this.jawLower);
        this.jawLower.cuboids.add(new Cuboid(this.jawLower, 19, 73, -2.7F, 0.0F, -13.0F, 5, 2, 6, 0.0F, false));
        this.jawLower.cuboids.add(new Cuboid(this.jawLower, 5, 58, -3.7F, 0.0F, -7.0F, 7, 2, 7, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, -4.0F, 12.0F);
        this.torso.addChild(this.tail);
        this.tail.cuboids.add(new Cuboid(this.tail, 2, 35, -5.0F, -1.0F, -5.0F, 10, 5, 13, 0.0F, false));
        this.tailSmall = new ModelPart(this);
        this.tailSmall.setPivots(0.0F, 4.0F, -12.0F);
        this.tail.addChild(this.tailSmall);
        this.tailEnd = new ModelPart(this);
        this.tailEnd.setPivots(0.0F, 0.0F, 0.0F);
        this.tailSmall.addChild(this.tailEnd);
        this.setRotation(this.tailEnd, -0.0436F, 0.0F, 0.0F);
        this.tailEnd.cuboids.add(new Cuboid(this.tailEnd, 36, 39, -3.0F, -5.0F, 18.0F, 6, 4, 14, 0.0F, false));
        this.tailEnd.cuboids.add(new Cuboid(this.tailEnd, 36, 39, -2.5F, -4.0F, 32.0F, 5, 3, 14, 0.0F, false));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(6.0F, 23.0F, -9.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 0, 18, 2.0F, 0.0F, -4.0F, 5, 1, 5, 0.0F, false));
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 51, 23, 0.0F, 0.0F, -2.0F, 5, 1, 3, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(6.0F, 23.0F, 5.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 0, 18, 2.0F, 0.0F, -4.0F, 5, 1, 5, 0.0F, false));
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 51, 23, 0.0F, 0.0F, -2.0F, 5, 1, 3, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-6.0F, 23.0F, 5.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 0, 12, -7.0F, 0.0F, -4.0F, 5, 1, 5, 0.0F, false));
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 67, 23, -5.0F, 0.0F, -2.0F, 5, 1, 3, 0.0F, false));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(-6.0F, 23.0F, -9.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 0, 12, -7.0F, 0.0F, -4.0F, 5, 1, 5, 0.0F, false));
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 67, 23, -5.0F, 0.0F, -2.0F, 5, 1, 3, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        this.animate(limbSwing, animSpeed, age, headYaw, headPitch, ticks);
        if (((Alligator)entity).attackFrames > 0) {
            this.jawUpper.rotationX = (float)Math.toRadians(-30.0);
        } else {
            this.jawUpper.rotationX = 0.0F;
        }

        this.torso.render(ticks);
        this.backRightLeg.render(ticks);
        this.frontRightLeg.render(ticks);
        this.frontLeftLeg.render(ticks);
        this.backLeftLeg.render(ticks);
    }

    @Override
    public void animate(float limbSwing, float animSpeed, float age, float headYaw, float headPitch, float ticks) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.tail.rotationY = -(headYaw / (180.0F / (float)Math.PI));
        this.backRightLeg.rotationY = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * animSpeed;
        this.frontLeftLeg.rotationY = MathHelper.cos(limbSwing * 0.6662F + 3.141593F) * 1.2F * animSpeed;
        this.backLeftLeg.rotationY = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * animSpeed;
        this.frontRightLeg.rotationY = MathHelper.cos(limbSwing * 0.6662F + 3.141593F) * 1.2F * animSpeed;
    }
}
