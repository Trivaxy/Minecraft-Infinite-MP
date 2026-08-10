package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BirdModel extends Model {
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private boolean isFlying = false;

    public BirdModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 21.5F, 2.0F);
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 1.5F, 2.5F);
        this.body.addChild(this.tail);
        this.setRotation(this.tail, -0.2182F, 0.0F, 0.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 3, 2, -1.0F, -3.0F, -1.0F, 2, 1, 3, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.setRotation(this.torso, -0.2182F, 0.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -2.0F, -3.079F, -2.8444F, 4, 3, 5, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -1.75F, -2.25F);
        this.body.addChild(this.head);
        this.setRotation(this.head, 0.1309F, 0.0F, 0.0F);
        this.head.cuboids.add(new Cuboid(this.head, -1, 0, -0.5F, -1.5653F, -3.4957F, 1, 1, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, -1, 16, -1.5F, -3.0653F, -2.4957F, 3, 3, 3, 0.0F, false));
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(-1.5F, 18.5F, 1.75F);
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 0, 9, -1.0F, -0.25F, -2.25F, 1, 3, 4, 0.0F, false));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(1.5F, 18.25F, 1.75F);
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 0, 9, 0.0F, 0.0F, -2.25F, 1, 3, 4, 0.0F, true));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(-0.25F, 21.25F, 2.0F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 13, 0, -1.0F, -0.25F, -2.0F, 1, 3, 2, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(0.25F, 21.5F, 2.0F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 13, 0, 0.0F, -0.5F, -2.0F, 1, 3, 2, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float animSpd, float rotAngle, float headYaw, float headPitch, float ticks) {
        this.isFlying = !entity.onGround;
        this.animate(swing, animSpd, rotAngle, headYaw, headPitch, ticks);
        this.body.render(ticks);
        this.leftWing.render(ticks);
        this.rightWing.render(ticks);
        this.leftLeg.render(ticks);
        this.rightLeg.render(ticks);
    }

    @Override
    public void animate(float swing, float animSpd, float rotAngle, float headYaw, float headPitch, float ticks) {
        this.head.rotationX = -(headPitch / 2.0F / (float) (180.0 / Math.PI));
        this.head.rotationY = headYaw / 2.0F / (float) (180.0 / Math.PI);
        if (this.isFlying) {
            this.leftLeg.rotationX = 1.0F;
            this.rightLeg.rotationX = 1.0F;
        } else {
            this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * animSpd;
            this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * animSpd;
        }

        this.leftWing.rotationZ = rotAngle;
        this.rightWing.rotationZ = -rotAngle;
    }
}
