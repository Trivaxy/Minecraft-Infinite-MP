package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.util.math.MathHelper;

public class HorseModel extends Model {
    protected ModelPart body;
    protected ModelPart tail;
    protected ModelPart frontRightLeg;
    protected ModelPart frontLeftLeg;
    protected ModelPart hindRightLeg;
    protected ModelPart hindLeftLeg;
    protected ModelPart head;
    protected ModelPart rightEar;
    protected ModelPart leftEar;
    protected ModelPart neck;
    protected ModelPart saddle;
    protected ModelPart headPart;

    public HorseModel(float scale, int width) {
        this.textureWidth = width;
        this.textureHeight = 64;
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 11.0F, 9.0F);
        this.body.cuboids.add(new Cuboid(this.body, 0, 32, -5.0F, -8.0F, -20.0F, 10, 10, 22, scale, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 4.0F, 11.0F);
        this.setRotation(this.tail, 0.5236F, 0.0F, 0.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 42, 36, -1.5F, 0.0F, -2.0F, 3, 14, 4, scale, false));
        this.frontRightLeg = new ModelPart(this);
        this.frontRightLeg.setPivots(3.0F, 13.0F, 9.0F);
        this.frontRightLeg.cuboids.add(new Cuboid(this.frontRightLeg, 48, 21, -2.0F, 0.0F, -2.0F, 4, 11, 4, scale, true));
        this.frontLeftLeg = new ModelPart(this);
        this.frontLeftLeg.setPivots(-3.0F, 13.0F, 9.0F);
        this.frontLeftLeg.cuboids.add(new Cuboid(this.frontLeftLeg, 48, 21, -2.0F, 0.0F, -2.0F, 4, 11, 4, scale, false));
        this.hindRightLeg = new ModelPart(this);
        this.hindRightLeg.setPivots(3.0F, 13.0F, -9.0F);
        this.hindRightLeg.cuboids.add(new Cuboid(this.hindRightLeg, 48, 21, -2.0F, 0.0F, -2.0F, 4, 11, 4, scale, true));
        this.hindLeftLeg = new ModelPart(this);
        this.hindLeftLeg.setPivots(-3.0F, 13.0F, -9.0F);
        this.hindLeftLeg.cuboids.add(new Cuboid(this.hindLeftLeg, 48, 21, -2.0F, 0.0F, -2.0F, 4, 11, 4, scale, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -4.0F, -11.0F);
        this.setRotation(this.head, 0.5236F, 0.0F, 0.0F);
        this.headPart = new ModelPart(this);
        this.headPart.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.headPart);
        this.headPart.cuboids.add(new Cuboid(this.headPart, 0, 13, -3.0F, -5.0F, -6.0F, 6, 5, 7, scale, false));
        this.headPart.cuboids.add(new Cuboid(this.headPart, 0, 25, -2.0F, -5.0F, -11.0F, 4, 5, 5, scale, false));
        this.rightEar = new ModelPart(this);
        this.headPart.addChild(this.rightEar);
        this.rightEar.cuboids.add(new Cuboid(this.rightEar, 19, 16, 1.0F, -7.0F, -0.99F, 2, 2, 1, scale, true));
        this.leftEar = new ModelPart(this);
        this.headPart.addChild(this.leftEar);
        this.leftEar.cuboids.add(new Cuboid(this.leftEar, 19, 16, -3.0F, -7.0F, -0.99F, 2, 2, 1, scale, false));
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.neck);
        this.neck.cuboids.add(new Cuboid(this.neck, 0, 35, -2.0F, 0.0F, -6.0F, 4, 12, 7, scale, false));
        this.neck.cuboids.add(new Cuboid(this.neck, 56, 36, -1.0F, -4.0F, 1.0F, 2, 16, 2, scale - 0.01F, false));
        this.saddle = new ModelPart(this);
        this.saddle.setPivots(0.0F, 2.0F, 2.0F);
        this.saddle.cuboids.add(new Cuboid(this.saddle, 26, 0, -5.0F, 1.0F, -5.5F, 10, 9, 9, 0.3F, false));
    }

    @Override
    public void render(Entity entity, float swipe, float anim, float angl, float headYaw, float headPitch, float tick) {
        this.animate(swipe, anim, angl, headYaw, headPitch, tick);
        this.body.render(tick);
        this.tail.render(tick);
        this.frontRightLeg.render(tick);
        this.frontLeftLeg.render(tick);
        this.hindRightLeg.render(tick);
        this.hindLeftLeg.render(tick);
        this.head.render(tick);
        if (((Mob)entity).isRideable()) {
            this.saddle.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = MathHelper.clamp(headYaw / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.headPart.rotationX = this.neck.rotationX = MathHelper.cos(swing * 0.6662F) * 0.15F * anim;
        this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.hindRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.hindLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
