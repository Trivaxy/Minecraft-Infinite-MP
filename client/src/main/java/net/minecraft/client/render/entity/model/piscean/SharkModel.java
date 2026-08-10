package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.aquatic.Shark;
import net.minecraft.util.math.MathHelper;

public class SharkModel extends Model {
    private final ModelPart root;
    private final ModelPart tailFinLeft;
    private final ModelPart tailFinRight;
    private final ModelPart tailEnd;
    private final ModelPart tail;
    private final ModelPart rightFin;
    private final ModelPart leftFin;
    private final ModelPart torso;
    private final ModelPart topFin;
    private final ModelPart rightOuterFin;
    private final ModelPart leftOuterFin;
    private final ModelPart head;
    private final ModelPart jawTop;
    private final ModelPart muzzle;
    private final ModelPart jawBottom;

    public SharkModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.root = new ModelPart(this);
        this.root.setPivots(1.0F, 20.0F, -12.0F);
        this.tailFinLeft = new ModelPart(this);
        this.tailFinLeft.setPivots(-1.0F, -4.5F, 16.0F);
        this.root.addChild(this.tailFinLeft);
        this.tailFinLeft.cuboids.add(new Cuboid(this.tailFinLeft, 33, 15, -3.0F, -5.0F, -1.0F, 6, 9, 13, 0.0F, false));
        this.tailFinRight = new ModelPart(this);
        this.tailFinRight.setPivots(0.5F, 1.0F, 13.0F);
        this.tailFinLeft.addChild(this.tailFinRight);
        this.tailEnd = new ModelPart(this);
        this.tailEnd.setPivots(0.5F, 4.0F, 16.0F);
        this.tailFinRight.addChild(this.tailEnd);
        this.setRotation(this.tailEnd, -0.0436F, 0.0F, 0.0F);
        this.tailEnd.cuboids.add(new Cuboid(this.tailEnd, -1, 79, -3.0F, -8.0F, -18.0F, 4, 7, 19, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(-1.0F, 0.0F, 18.0F);
        this.tailFinRight.addChild(this.tail);
        this.setRotation(this.tail, -0.0873F, 0.0F, 0.0F);
        this.rightFin = new ModelPart(this);
        this.rightFin.setPivots(0.0F, 7.0F, -5.0F);
        this.tail.addChild(this.rightFin);
        this.setRotation(this.rightFin, 0.2618F, 0.0F, 0.0F);
        this.rightFin.cuboids.add(new Cuboid(this.rightFin, 64, 30, 0.0F, -7.0F, -1.0F, 1, 11, 7, 0.0F, false));
        this.leftFin = new ModelPart(this);
        this.leftFin.setPivots(0.0F, -4.0F, -6.0F);
        this.tail.addChild(this.leftFin);
        this.setRotation(this.leftFin, -0.1309F, 0.0F, 0.0F);
        this.leftFin.cuboids.add(new Cuboid(this.leftFin, 64, 30, 0.0F, -7.0F, -1.0F, 1, 11, 7, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 1.0F, 12.0F);
        this.root.addChild(this.torso);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -5.0F, -13.0F, -12.0F, 8, 13, 15, 0.0F, false));
        this.topFin = new ModelPart(this);
        this.topFin.setPivots(-1.0F, -12.0F, -9.0F);
        this.torso.addChild(this.topFin);
        this.setRotation(this.topFin, -0.48F, 0.0F, 0.0F);
        this.topFin.cuboids.add(new Cuboid(this.topFin, 34, 56, -1.0F, -10.0F, -1.0F, 2, 14, 7, 0.0F, false));
        this.rightOuterFin = new ModelPart(this);
        this.rightOuterFin.setPivots(-5.0F, -3.0F, -7.5F);
        this.torso.addChild(this.rightOuterFin);
        this.setRotation(this.rightOuterFin, -0.0495F, 0.4321F, -0.1894F);
        this.rightOuterFin.cuboids.add(new Cuboid(this.rightOuterFin, 55, 2, -7.0F, -4.0F, -1.0F, 8, 2, 8, 0.0F, true));
        this.rightOuterFin.cuboids.add(new Cuboid(this.rightOuterFin, 58, 12, -15.0F, -3.5F, 0.0F, 8, 1, 7, 0.0F, false));
        this.leftOuterFin = new ModelPart(this);
        this.leftOuterFin.setPivots(0.0F, -3.0F, -2.0F);
        this.torso.addChild(this.leftOuterFin);
        this.setRotation(this.leftOuterFin, -3.0785F, 0.3435F, -2.9561F);
        this.leftOuterFin.cuboids.add(new Cuboid(this.leftOuterFin, 31, 0, -7.0F, -4.0F, -1.0F, 8, 2, 8, 0.0F, false));
        this.leftOuterFin.cuboids.add(new Cuboid(this.leftOuterFin, 52, 56, -15.0F, -3.5F, -1.0F, 8, 1, 7, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.root.addChild(this.head);
        this.setRotation(this.head, 0.0873F, 0.0F, 0.0F);
        this.jawTop = new ModelPart(this);
        this.jawTop.setPivots(-1.0F, -2.0F, 1.0F);
        this.head.addChild(this.jawTop);
        this.jawTop.cuboids.add(new Cuboid(this.jawTop, 51, 64, -4.0F, -8.0F, -16.0F, 8, 6, 4, 0.0F, false));
        this.jawTop.cuboids.add(new Cuboid(this.jawTop, 88, 0, -4.0F, -9.0F, -12.0F, 8, 8, 12, 0.0F, false));
        this.muzzle = new ModelPart(this);
        this.muzzle.setPivots(1.0F, 0.0F, -1.0F);
        this.jawTop.addChild(this.muzzle);
        this.setRotation(this.muzzle, -0.0873F, 0.0F, 0.0F);
        this.muzzle.cuboids.add(new Cuboid(this.muzzle, 82, 24, -3.0F, -1.0F, -14.0F, 4, 1, 15, 0.0F, false));
        this.jawBottom = new ModelPart(this);
        this.jawBottom.setPivots(-1.0F, -2.0F, 1.0F);
        this.head.addChild(this.jawBottom);
        this.setRotation(this.jawBottom, -0.1309F, 0.0F, 0.0F);
        this.jawBottom.cuboids.add(new Cuboid(this.jawBottom, 0, 28, -3.0F, -1.0F, -16.0F, 6, 3, 16, 0.0F, false));
        this.jawBottom.cuboids.add(new Cuboid(this.jawBottom, 82, 24, -2.0F, -1.5F, -15.0F, 4, 1, 15, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        if (((Shark)entity).attackFrames > 0) {
            this.jawTop.rotationX = (float)Math.toRadians(-30.0);
        } else {
            this.jawTop.rotationX = 0.0F;
            this.jawBottom.rotationY = this.jawTop.rotationY = MathHelper.cos(angle * 0.9F + 0.47123894F) * (float) Math.PI * 0.01F;
        }

        this.root.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = this.rightFin.rotationY = this.leftFin.rotationY = this.topFin.rotationY = this.muzzle.rotationY = this.tailEnd.rotationY = MathHelper.cos(
                angle * 0.9F + 0.47123894F
            )
            * (float) Math.PI
            * 0.01F;
        this.tail.rotationY = this.tailFinRight.rotationY = this.tailFinLeft.rotationY = -MathHelper.cos(angle * 0.9F + 0.47123894F) * (float) Math.PI * 0.01F;
    }
}
