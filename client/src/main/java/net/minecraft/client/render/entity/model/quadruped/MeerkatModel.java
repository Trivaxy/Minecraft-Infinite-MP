package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class MeerkatModel extends Model {
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart tail;
    private final ModelPart tailBone;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public MeerkatModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.torso = new ModelPart(this);
        this.torso.setPivots(-0.5F, 20.0F, 3.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -2.0F, -2.0F, -9.0F, 4, 4, 10, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -1.0F, -9.0F);
        this.torso.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 0, 14, -2.5F, -2.0F, -4.0F, 5, 4, 4, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 17, 4, -1.5F, 0.0F, -5.5F, 3, 2, 2, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, 2.5F, -1.0F, -2.0F, 1, 2, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -3.5F, -1.0F, -2.0F, 1, 2, 1, 0.0F, false));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-2.5F, -1.0F, -7.0F);
        this.torso.addChild(this.frontLeftLeg);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 18, 14, -0.5F, 0.0F, -1.0F, 2, 5, 2, 0.0F, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(1.5F, -1.0F, -7.0F);
        this.torso.addChild(this.frontRightLeg);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 18, 14, -0.5F, 0.0F, -1.0F, 2, 5, 2, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, -2.0F, 0.7F);
        this.torso.addChild(this.tail);
        this.tailBone = new ModelPart(this);
        this.tailBone.setPivots(0.1F, -0.1F, -1.0F);
        this.tail.addChild(this.tailBone);
        this.setRotation(this.tailBone, 0.5236F, 0.0F, 0.0F);
        this.tailBone.cuboids.add(new Cuboid(this.tailBone, 0, 0, -0.6F, 0.4F, 0.3F, 1, 1, 4, 0.0F, false));
        this.backRightLeg = new ModelPart(this);
        this.backRightLeg.setPivots(2.0F, 19.0F, 3.0F);
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 8, 22, -0.5F, 0.0F, -2.0F, 1, 4, 3, 0.0F, false));
        this.backRightLeg.cuboids.add(new Cuboid(this.backRightLeg, 0, 5, -0.5F, 4.0F, -3.0F, 1, 1, 4, 0.0F, true));
        this.backLeftLeg = new ModelPart(this);
        this.backLeftLeg.setPivots(-2.0F, 19.0F, 3.0F);
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 8, 22, -1.5F, 0.0F, -2.0F, 1, 4, 3, 0.0F, true));
        this.backLeftLeg.cuboids.add(new Cuboid(this.backLeftLeg, 0, 5, -1.5F, 4.0F, -3.0F, 1, 1, 4, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        if (entity.sitting) {
            this.setRotation(this.torso, -1.4399F, 0.0F, 0.0F);
            this.setRotation(this.head, 1.4399F, 0.0F, 0.0F);
            this.setRotation(this.frontLeftLeg, 1.109F, 0.0F, 0.0F);
            this.setRotation(this.frontRightLeg, 1.109F, 0.0F, 0.0F);
            this.setRotation(this.backRightLeg, 0.0F, 0.0F, 0.0F);
            this.setRotation(this.backLeftLeg, 0.0F, 0.0F, 0.0F);
        } else {
            this.setRotation(this.torso, 0.0F, 0.0F, 0.0F);
            this.head.rotationX = -(headPitch / (float) (180.0 / Math.PI));
            this.head.rotationY = headYaw / (float) (180.0 / Math.PI);
            this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
            this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
            this.backRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
            this.backLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
        }

        this.torso.render(tick);
        this.backRightLeg.render(tick);
        this.backLeftLeg.render(tick);
    }
}
