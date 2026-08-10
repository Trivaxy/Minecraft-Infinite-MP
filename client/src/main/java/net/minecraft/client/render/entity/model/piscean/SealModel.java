package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SealModel extends Model {
    private final ModelPart head;
    private final ModelPart muzzle;
    private final ModelPart tailLeft;
    private final ModelPart whiskers;
    private final ModelPart tailRight;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public SealModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 19.0F, -8.0F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -2.5F, -2.0F, -2.0F, 5, 4, 5, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 20, 5, -1.5F, 0.0F, -4.0F, 3, 2, 2, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 34, -3.5F, -1.0F, -4.0F, 7, 3, 0, 0.0F, false));
        this.muzzle = new ModelPart(this);
        this.muzzle.setPivots(3.0F, 24.0F, 12.0F);
        this.tailLeft = new ModelPart(this);
        this.tailLeft.setPivots(0.0F, 0.0F, 1.0F);
        this.muzzle.addChild(this.tailLeft);
        this.setRotation(this.tailLeft, -0.0873F, 0.0F, 0.0F);
        this.tailLeft.cuboids.add(new Cuboid(this.tailLeft, 0, 9, -2.0F, -1.367F, -2.9505F, 4, 1, 6, 0.0F, false));
        this.whiskers = new ModelPart(this);
        this.whiskers.setPivots(-3.0F, 24.0F, 12.0F);
        this.tailRight = new ModelPart(this);
        this.tailRight.setPivots(0.0F, 0.0F, 1.0F);
        this.whiskers.addChild(this.tailRight);
        this.setRotation(this.tailRight, -0.0873F, 0.0F, 0.0F);
        this.tailRight.cuboids.add(new Cuboid(this.tailRight, 0, 21, -2.0F, -1.367F, -2.9505F, 4, 1, 6, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 24.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 22, 1, -3.5F, -5.0F, -7.0F, 7, 5, 10, 0.0F, false));
        this.body.cuboids.add(new Cuboid(this.body, 22, 16, -3.0F, -4.0F, 3.0F, 6, 4, 8, 0.0F, false));
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(-5.0F, 0.0F, -4.0F);
        this.body.addChild(this.rightArm);
        this.setRotation(this.rightArm, 0.0F, 0.0F, -0.0873F);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 18, 28, -1.731F, -1.0642F, -2.0F, 4, 1, 5, 0.0F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(6.0F, 0.0F, -4.0F);
        this.body.addChild(this.leftArm);
        this.setRotation(this.leftArm, 0.0F, 0.0F, 0.0873F);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 0, 28, -3.231F, -1.0642F, -2.0F, 4, 1, 5, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.muzzle.render(tick);
        this.whiskers.render(tick);
    }

    @Override
    public final void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (float) (180.0 / Math.PI));
        this.head.rotationY = headYaw / (float) (180.0 / Math.PI);
        this.leftArm.rotationY = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
        this.rightArm.rotationY = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
        this.muzzle.rotationY = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
        this.whiskers.rotationY = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.2F * anim;
    }
}
