package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class GolemModel extends Model {
    private final ModelPart leftLeg;
    private final ModelPart leftShin;
    private final ModelPart leftFoot;
    private final ModelPart rightLeg;
    private final ModelPart rightShin;
    private final ModelPart rightFoot;
    private final ModelPart torso;
    private final ModelPart waist;
    private final ModelPart bone1;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart skull;
    private final ModelPart head;

    public GolemModel() {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(-1.0F, -3.0F, -7.0F);
        this.leftShin = new ModelPart(this);
        this.leftShin.setPivots(0.0F, 12.0F, 4.0F);
        this.leftLeg.addChild(this.leftShin);
        this.setRotation(this.leftShin, 0.0F, 0.0F, 0.0F);
        this.leftShin.cuboids.add(new Cuboid(this.leftShin, 115, 113, -12.0F, 0.0F, -3.0F, 10, 15, 10, 0.0F, false));
        this.leftFoot = new ModelPart(this);
        this.leftFoot.setPivots(0.0F, 0.0F, 0.0F);
        this.leftLeg.addChild(this.leftFoot);
        this.setRotation(this.leftFoot, -0.0F, 0.0F, 0.0F);
        this.leftFoot.cuboids.add(new Cuboid(this.leftFoot, 40, 127, -12.0F, 0.0F, -3.0F, 10, 15, 10, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(16.0F, -3.0F, -6.0F);
        this.rightShin = new ModelPart(this);
        this.rightShin.setPivots(0.0F, 12.0F, 4.0F);
        this.rightLeg.addChild(this.rightShin);
        this.setRotation(this.rightShin, 0.0F, 0.0F, 0.0F);
        this.rightShin.cuboids.add(new Cuboid(this.rightShin, 115, 63, -12.0F, 0.0F, -3.0F, 10, 15, 10, 0.0F, false));
        this.rightFoot = new ModelPart(this);
        this.rightFoot.setPivots(0.0F, 0.0F, 0.0F);
        this.rightLeg.addChild(this.rightFoot);
        this.setRotation(this.rightFoot, -0.0F, 0.0F, 0.0F);
        this.rightFoot.cuboids.add(new Cuboid(this.rightFoot, 115, 88, -12.0F, 0.0F, -3.0F, 10, 15, 10, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 24.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 69, 66, -10.0F, -41.0F, -5.0F, 11, 4, 6, 0.0F, false));
        this.torso.cuboids.add(new Cuboid(this.torso, 130, 28, -9.0F, -47.0F, -4.0F, 13, 4, 4, 0.0F, false));
        this.waist = new ModelPart(this);
        this.waist.setPivots(-6.0F, -40.0F, -7.0F);
        this.torso.addChild(this.waist);
        this.setRotation(this.waist, 0.1309F, 0.0F, 0.0F);
        this.waist.cuboids.add(new Cuboid(this.waist, 40, 87, -1.0F, -5.0F, -2.0F, 5, 5, 9, 0.0F, false));
        this.bone1 = new ModelPart(this);
        this.bone1.setPivots(0.0F, -28.0F, 0.0F);
        this.torso.addChild(this.bone1);
        this.setRotation(this.bone1, 0.0873F, 0.0F, 0.0F);
        this.bone1.cuboids.add(new Cuboid(this.bone1, 84, 29, -6.0F, -22.0F, -7.0F, 17, 22, 12, 0.0F, false));
        this.bone1.cuboids.add(new Cuboid(this.bone1, 0, 66, -13.0F, -6.0F, -8.0F, 27, 6, 15, 0.0F, false));
        this.bone2 = new ModelPart(this);
        this.bone2.setPivots(0.0F, -69.0F, -2.0F);
        this.torso.addChild(this.bone2);
        this.setRotation(this.bone2, 0.2182F, 0.0F, 0.0F);
        this.bone2.cuboids.add(new Cuboid(this.bone2, 0, 0, -16.0F, -7.0F, -14.0F, 33, 11, 18, 0.0F, false));
        this.bone3 = new ModelPart(this);
        this.bone3.setPivots(0.0F, -48.0F, -2.0F);
        this.torso.addChild(this.bone3);
        this.setRotation(this.bone3, 0.2182F, 0.0F, 0.0F);
        this.bone3.cuboids.add(new Cuboid(this.bone3, 0, 29, -13.0F, -22.0F, -8.0F, 27, 22, 15, 0.0F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(-15.0F, -37.0F, -3.0F);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 73, 76, -9.0F, -9.0F, -10.0F, 10, 50, 11, 0.0F, false));
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(24.0F, -37.0F, -3.0F);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 0, 87, -9.0F, -9.0F, -10.0F, 9, 51, 11, 0.0F, false));
        this.skull = new ModelPart(this);
        this.skull.setPivots(-1.0F, -43.0F, -19.0F);
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 2.0F, -1.0F);
        this.skull.addChild(this.head);
        this.setRotation(this.head, 0.5594F, -0.0141F, 0.1249F);
        this.head.cuboids.add(new Cuboid(this.head, 102, 0, -6.0F, -21.0F, -1.0F, 15, 15, 13, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.leftLeg.render(tick);
        this.rightLeg.render(tick);
        this.torso.render(tick);
        this.leftArm.render(tick);
        this.rightArm.render(tick);
        this.skull.render(tick);
    }

    @Override
    public void animate(float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        this.skull.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.skull.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.leftLeg.rotationX = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * animationSpeed;
        this.rightLeg.rotationX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leftLeg.rotationZ = 0.0F;
        this.rightLeg.rotationZ = 0.0F;
    }

    @Override
    public void animateSpecial(Mob entity, float armSwing, float animSpd, float angle) {
        if (entity.deathTimer <= 0) {
            if (entity.attackFrames > 0) {
                this.leftArm.rotationX = -2.0F + 1.5F * this.performAttackAnim(entity.attackFrames - angle, 10.0F);
                this.rightArm.rotationX = -2.0F + 1.5F * this.performAttackAnim(entity.attackFrames - angle, 10.0F);
            } else {
                this.leftArm.rotationX = (-0.2F + 1.5F * this.performAttackAnim(armSwing, 13.0F)) * animSpd;
                this.rightArm.rotationX = (-0.2F - 1.5F * this.performAttackAnim(armSwing, 13.0F)) * animSpd;
            }
        } else {
            this.leftArm.rotationX = 0.0F;
            this.rightArm.rotationX = 0.0F;
        }
    }

    private float performAttackAnim(float currTime, float prevTime) {
        return (Math.abs(currTime % prevTime - prevTime * 0.5F) - prevTime * 0.25F) / (prevTime * 0.25F);
    }
}
