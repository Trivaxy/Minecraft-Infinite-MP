package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class VultureModel extends Model {
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart torso;
    private final ModelPart body;
    private final ModelPart armLeft;
    private final ModelPart wingLeft;
    private final ModelPart armRight;
    private final ModelPart wingRight;
    private final ModelPart legLeft;
    private final ModelPart legRight;
    private final ModelPart tail;
    private final ModelPart tailFeathers;
    private boolean isFlying = false;

    public VultureModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 17.25F, -0.75F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -1.5F, -4.0F, -7.25F, 3, 4, 4, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 8, -1.0F, -2.0F, -10.0F, 2, 1, 3, 0.0F, false));
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.neck);
        this.setRotation(this.neck, -0.3054F, 0.0F, 0.0F);
        this.neck.cuboids.add(new Cuboid(this.neck, 14, 1, -1.0F, -1.1081F, -5.0045F, 2, 2, 5, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 17.8672F, 3.4115F);
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.torso.addChild(this.body);
        this.setRotation(this.body, -0.4363F, 0.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 8, 8, -2.5F, -3.0F, -4.5F, 5, 6, 9, 0.0F, false));
        this.armLeft = new ModelPart(this);
        this.armLeft.setPivots(3.0F, 14.8856F, 4.7265F);
        this.wingLeft = new ModelPart(this);
        this.wingLeft.setPivots(0.0F, 0.0F, 0.0F);
        this.armLeft.addChild(this.wingLeft);
        this.setRotation(this.wingLeft, -0.2618F, 0.0F, 0.0F);
        this.wingLeft.cuboids.add(new Cuboid(this.wingLeft, 36, 5, -0.5F, 0.0272F, -5.7065F, 1, 6, 12, 0.0F, false));
        this.armRight = new ModelPart(this);
        this.armRight.setPivots(-3.0F, 14.8856F, 4.7265F);
        this.wingRight = new ModelPart(this);
        this.wingRight.setPivots(0.0F, 0.0F, 0.0F);
        this.armRight.addChild(this.wingRight);
        this.setRotation(this.wingRight, -0.2618F, 0.0F, 0.0F);
        this.wingRight.cuboids.add(new Cuboid(this.wingRight, 36, 23, -0.5F, 0.0272F, -5.7065F, 1, 6, 12, 0.0F, false));
        this.legLeft = new ModelPart(this);
        this.legLeft.setPivots(2.0F, 21.0F, 3.0F);
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, -3, 17, -1.0F, 3.0F, -3.0F, 2, 0, 3, 0.01F, false));
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, 4, 17, -1.0F, 1.0F, 0.0F, 2, 2, 0, 0.01F, false));
        this.legLeft.cuboids.add(new Cuboid(this.legLeft, 0, 37, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.01F, false));
        this.legRight = new ModelPart(this);
        this.legRight.setPivots(-2.0F, 21.0F, 3.0F);
        this.legRight.cuboids.add(new Cuboid(this.legRight, -3, 20, -1.0F, 3.0F, -3.0F, 2, 0, 3, 0.01F, false));
        this.legRight.cuboids.add(new Cuboid(this.legRight, 4, 20, -1.0F, 1.0F, 0.0F, 2, 2, 0, 0.01F, false));
        this.legRight.cuboids.add(new Cuboid(this.legRight, 8, 37, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.01F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 17.2725F, 7.9126F);
        this.tailFeathers = new ModelPart(this);
        this.tailFeathers.setPivots(0.0F, 0.0F, 0.0F);
        this.tail.addChild(this.tailFeathers);
        this.setRotation(this.tailFeathers, 0.0873F, 0.0F, 0.0F);
        this.tailFeathers.cuboids.add(new Cuboid(this.tailFeathers, 16, 35, -2.5F, 0.0123F, -0.4688F, 5, 5, 1, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.isFlying = !entity.onGround;
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.head.render(tick);
        this.torso.render(tick);
        this.armLeft.render(tick);
        this.armRight.render(tick);
        this.legLeft.render(tick);
        this.legRight.render(tick);
        this.tail.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (180.0F / (float)Math.PI));
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        if (this.isFlying) {
            this.legLeft.rotationX = 1.0F;
            this.legRight.rotationX = 1.0F;
        } else {
            this.legLeft.rotationX = MathHelper.cos(swing * 0.6662F) * anim;
            this.legRight.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * anim;
        }

        this.wingRight.rotationZ = angle;
        this.wingLeft.rotationZ = -angle;
    }
}
