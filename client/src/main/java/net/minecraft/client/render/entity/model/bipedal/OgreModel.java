package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;

public class OgreModel extends Model {
    private final ModelPart torso;
    private final ModelPart ragFront;
    private final ModelPart ragBack;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public OgreModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 9.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -9.0F, -11.0F, -6.0F, 18, 14, 10, 0.0F, false));
        this.torso.cuboids.add(new Cuboid(this.torso, 38, 37, -8.0F, -18.0F, -5.0F, 16, 7, 8, 0.0F, false));
        this.ragFront = new ModelPart(this);
        this.ragFront.setPivots(0.0F, 0.0F, -6.0F);
        this.torso.addChild(this.ragFront);
        this.ragFront.cuboids.add(new Cuboid(this.ragFront, 54, 26, -9.0F, 0.0F, -1.0F, 18, 8, 1, 0.0F, true));
        this.ragBack = new ModelPart(this);
        this.ragBack.setPivots(0.0F, 0.0F, 4.0F);
        this.torso.addChild(this.ragBack);
        this.ragBack.cuboids.add(new Cuboid(this.ragBack, 54, 26, -9.0F, 0.0F, 0.0F, 18, 8, 1, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -18.0F, -2.0F);
        this.torso.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 20, 45, -3.0F, -5.0F, -6.0F, 6, 3, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 24, -6.0F, -10.0F, -5.0F, 12, 10, 11, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 46, 0, -8.0F, -7.0F, 0.0F, 16, 5, 2, 0.0F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(8.0F, -14.0F, -1.0F);
        this.torso.addChild(this.leftArm);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 28, 52, 0.0F, -2.0F, -3.0F, 5, 20, 5, 0.0F, true));
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(-8.0F, -16.0F, -1.0F);
        this.torso.addChild(this.rightArm);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 28, 52, -5.0F, 0.0F, -3.0F, 5, 20, 5, 0.0F, false));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(5.0F, 12.0F, -1.0F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 0, 45, -4.0F, 0.0F, -4.0F, 6, 12, 8, 0.0F, true));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(-5.0F, 12.0F, -1.0F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 0, 45, -2.0F, 0.0F, -4.0F, 6, 12, 8, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.torso.render(tick);
        this.leftLeg.render(tick);
        this.rightLeg.render(tick);
    }

    @Override
    public void animate(float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.leftLeg.rotationX = -1.5F * this.performAttackAnim(limbAngle, 13.0F) * animationSpeed;
        this.rightLeg.rotationX = 1.5F * this.performAttackAnim(limbAngle, 13.0F) * animationSpeed;
        this.leftArm.rotationY = 0.0F;
        this.rightArm.rotationY = 0.0F;
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
