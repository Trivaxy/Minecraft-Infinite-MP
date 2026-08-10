package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CarbuncleModel extends Model {
    private final ModelPart leftWing;
    private final ModelPart leftOuterWing;
    private final ModelPart rightWing;
    private final ModelPart rightOuterWing;
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart torso;
    private final ModelPart mane;
    private final ModelPart tail;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart hindLeftLeg;
    private final ModelPart hindRightLeg;

    public CarbuncleModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.5F, 22.0F, 0.5F);
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(-1.0F, 18.8F, -0.5F);
        this.setRotation(this.leftWing, 0.0F, 3.1416F, 0.0F);
        this.leftOuterWing = new ModelPart(this);
        this.leftOuterWing.setPivots(0.0F, 1.0F, -1.0F);
        this.leftWing.addChild(this.leftOuterWing);
        this.setRotation(this.leftOuterWing, 0.4363F, 0.0F, 0.6109F);
        this.leftOuterWing.cuboids.add(new Cuboid(this.leftOuterWing, 0, 20, 0.0F, -8.0F, -2.0F, 1, 8, 4, 0.0F, true));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(0.0F, 18.8F, -0.5F);
        this.rightOuterWing = new ModelPart(this);
        this.rightOuterWing.setPivots(0.0F, 1.0F, 1.0F);
        this.rightWing.addChild(this.rightOuterWing);
        this.setRotation(this.rightOuterWing, -0.4363F, 0.0F, 0.6109F);
        this.rightOuterWing.cuboids.add(new Cuboid(this.rightOuterWing, 0, 20, 0.0F, -8.0F, -2.0F, 1, 8, 4, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 22.0F, 0.5F);
        this.head.cuboids.add(new Cuboid(this.head, 17, 8, -2.0F, -3.75F, -6.75F, 3, 3, 3, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 4, 0, -1.0F, -2.25F, -7.25F, 1, 1, 1, 0.0F, false));
        this.leftEar = new ModelPart(this);
        this.leftEar.setPivots(-0.75F, -2.75F, -5.25F);
        this.head.addChild(this.leftEar);
        this.setRotation(this.leftEar, -0.3491F, 0.0F, -0.2618F);
        this.leftEar.cuboids.add(new Cuboid(this.leftEar, 0, 11, -1.0F, -4.0F, -1.0F, 1, 4, 2, 0.0F, false));
        this.rightEar = new ModelPart(this);
        this.rightEar.setPivots(0.75F, -2.5F, -5.25F);
        this.head.addChild(this.rightEar);
        this.setRotation(this.rightEar, -0.3927F, 0.0F, 0.2618F);
        this.rightEar.cuboids.add(new Cuboid(this.rightEar, 0, 11, -1.0F, -4.0F, -1.0F, 1, 4, 2, 0.0F, false));
        this.mane = new ModelPart(this);
        this.mane.setPivots(-0.5F, -2.2F, 0.0F);
        this.torso.addChild(this.mane);
        this.setRotation(this.mane, -0.0873F, 0.0F, 0.0F);
        this.mane.cuboids.add(new Cuboid(this.mane, 12, 0, -1.0F, -1.0F, -4.0F, 1, 1, 7, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(-1.0F, -2.6F, 4.0F);
        this.torso.addChild(this.tail);
        this.setRotation(this.tail, 0.2618F, 0.0F, 0.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 0, 11, -1.0F, -1.0F, -1.0F, 2, 2, 7, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.torso.addChild(this.body);
        this.setRotation(this.body, -0.0436F, 0.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 0, 0, -2.0F, -3.0F, -4.0F, 2, 3, 8, 0.0F, false));
        this.rightFrontLeg = new ModelPart(this);
        this.rightFrontLeg.setPivots(0.5F, 20.0F, -1.5F);
        this.rightFrontLeg.cuboids.add(new Cuboid(this.rightFrontLeg, 12, 0, -0.5F, 0.0F, -1.5F, 1, 4, 2, 0.0F, true));
        this.leftFrontLeg = new ModelPart(this);
        this.leftFrontLeg.setPivots(-1.5F, 20.0F, -1.5F);
        this.leftFrontLeg.cuboids.add(new Cuboid(this.leftFrontLeg, 20, 14, -0.5F, 0.0F, -1.5F, 1, 4, 2, 0.0F, false));
        this.hindLeftLeg = new ModelPart(this);
        this.hindLeftLeg.setPivots(-1.5F, 20.0F, 2.5F);
        this.hindLeftLeg.cuboids.add(new Cuboid(this.hindLeftLeg, 12, 0, -0.5F, 0.0F, -0.5F, 1, 4, 2, 0.0F, false));
        this.hindLeftLeg.cuboids.add(new Cuboid(this.hindLeftLeg, 0, 20, -0.5F, 3.0F, -1.5F, 1, 1, 1, 0.0F, false));
        this.hindRightLeg = new ModelPart(this);
        this.hindRightLeg.setPivots(0.5F, 20.0F, 2.5F);
        this.hindRightLeg.cuboids.add(new Cuboid(this.hindRightLeg, 0, 11, -0.5F, 0.0F, -0.5F, 1, 4, 2, 0.0F, true));
        this.hindRightLeg.cuboids.add(new Cuboid(this.hindRightLeg, 0, 20, -0.5F, 3.0F, -1.5F, 1, 1, 1, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.leftWing.render(tick);
        this.rightWing.render(tick);
        this.head.render(tick);
        this.torso.render(tick);
        this.rightFrontLeg.render(tick);
        this.leftFrontLeg.render(tick);
        this.hindLeftLeg.render(tick);
        this.hindRightLeg.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.leftFrontLeg.rotationX = this.rightFrontLeg.rotationX = MathHelper.cos(swing * 0.3662F) * 1.0F * anim;
        this.hindLeftLeg.rotationX = this.hindRightLeg.rotationX = MathHelper.cos(swing * 0.3662F + 3.141593F) * 0.8F * anim;
        this.leftWing.rotationZ = -angle;
        this.rightWing.rotationZ = angle;
    }
}
