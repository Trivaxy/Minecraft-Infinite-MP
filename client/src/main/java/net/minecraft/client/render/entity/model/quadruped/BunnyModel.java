package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BunnyModel extends Model {
    private final ModelPart rearFootLeft = new ModelPart(this);
    private final ModelPart rearFootRight;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart frontLegLeft;
    private final ModelPart frontLegRight;
    private final ModelPart head;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart tail;
    private final ModelPart nose;

    public BunnyModel() {
        this.rearFootLeft.setPivots(2.0F, 22.5F, 5.7F);
        this.rearFootLeft.cuboids.add(new Cuboid(this.rearFootLeft, 16, 15, -1.0F, -0.5F, -3.7F, 2, 2, 5, 0.0F, true));
        this.rearFootRight = new ModelPart(this);
        this.rearFootRight.setPivots(-2.0F, 22.5F, 5.7F);
        this.rearFootRight.cuboids.add(new Cuboid(this.rearFootRight, 30, 15, -1.0F, -0.5F, -3.7F, 2, 2, 5, 0.0F, true));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 19.0F, 8.0F);
        this.setRotation(this.body, -0.3491F, 0.0F, 0.0F);
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 5.0F, -8.0F);
        this.body.addChild(this.torso);
        this.setRotation(this.torso, 0.3491F, 0.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -3.0F, -3.95F, -1.8F, 6, 5, 10, 0.0F, true));
        this.frontLegLeft = new ModelPart(this);
        this.frontLegLeft.setPivots(2.0F, 22.875F, -2.0F);
        this.setRotation(this.frontLegLeft, -0.1745F, 0.0F, 0.0F);
        this.frontLegLeft.cuboids.add(new Cuboid(this.frontLegLeft, 8, 15, -1.0F, -0.8719F, -1.0F, 2, 2, 2, 0.0F, true));
        this.frontLegRight = new ModelPart(this);
        this.frontLegRight.setPivots(-2.0F, 22.875F, -2.0F);
        this.setRotation(this.frontLegRight, -0.1745F, 0.0F, 0.0F);
        this.frontLegRight.cuboids.add(new Cuboid(this.frontLegRight, 0, 15, -1.0F, -0.8719F, -1.0F, 2, 2, 2, 0.0F, true));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 16.0F, -1.0F);
        this.head.cuboids.add(new Cuboid(this.head, 33, 1, -2.5F, -1.0F, -4.0F, 5, 4, 4, 0.0F, true));
        this.earRight = new ModelPart(this);
        this.earRight.setPivots(0.0F, 16.0F, -1.0F);
        this.setRotation(this.earRight, 0.0F, -0.2618F, 0.0F);
        this.earRight.cuboids.add(new Cuboid(this.earRight, 58, 0, -2.25F, -4.5F, -1.9F, 2, 5, 1, 0.0F, true));
        this.earLeft = new ModelPart(this);
        this.earLeft.setPivots(0.0F, 16.0F, -1.0F);
        this.setRotation(this.earLeft, 0.0F, 0.2618F, 0.0F);
        this.earLeft.cuboids.add(new Cuboid(this.earLeft, 52, 0, 0.25F, -4.5F, -1.9F, 2, 5, 1, 0.0F, true));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 20.0F, 7.0F);
        this.tail.cuboids.add(new Cuboid(this.tail, 52, 6, -1.5F, -2.5F, 0.0F, 3, 3, 2, 0.0F, true));
        this.nose = new ModelPart(this);
        this.nose.setPivots(0.0F, 16.0F, -1.0F);
        this.nose.cuboids.add(new Cuboid(this.nose, 32, 9, -0.5F, 1.0F, -4.5F, 1, 1, 1, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float swing, float animSpeed, float rotValue, float headYaw, float headPitch, float tick) {
        this.animate(swing, animSpeed, rotValue, headYaw, headPitch, tick);
        this.rearFootLeft.render(tick);
        this.rearFootRight.render(tick);
        this.body.render(tick);
        this.frontLegLeft.render(tick);
        this.frontLegRight.render(tick);
        this.head.render(tick);
        this.earRight.render(tick);
        this.earLeft.render(tick);
        this.tail.render(tick);
        this.nose.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float rot, float headYaw, float headPitch, float tick) {
        this.head.rotationY = this.earRight.rotationY = this.earLeft.rotationY = this.nose.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = this.earRight.rotationX = this.earLeft.rotationX = this.nose.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.frontLegLeft.rotationX = this.frontLegRight.rotationX = MathHelper.cos(swing * 0.3662F) * 1.0F * anim;
        this.rearFootRight.rotationX = this.rearFootLeft.rotationX = MathHelper.cos(swing * 0.3662F + 3.141593F) * 0.8F * anim;
    }
}
