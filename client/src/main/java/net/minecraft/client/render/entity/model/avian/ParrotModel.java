package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ParrotModel extends Model {
    private final ModelPart head;
    private final ModelPart feather;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingClip;
    private final ModelPart rightWing;
    private final ModelPart rightWingClip;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart tail;
    public boolean sitting = false;
    private boolean isFlying = false;

    public ParrotModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 16.0F, -0.5F);
        this.head.cuboids.add(new Cuboid(this.head, 2, 2, -1.0F, -1.5F, -1.0F, 2, 3, 2, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 10, 0, -1.0F, -2.5F, -3.0F, 2, 1, 4, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 11, 7, -0.5F, -1.5F, -2.0F, 1, 2, 1, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 16, 7, -0.5F, -1.75F, -2.95F, 1, 2, 1, -0.01F, false));
        this.feather = new ModelPart(this);
        this.feather.setPivots(0.0F, -1.5F, -2.0F);
        this.head.addChild(this.feather);
        this.setRotation(this.feather, -0.2618F, 0.0F, 0.0F);
        this.feather.cuboids.add(new Cuboid(this.feather, 2, 18, 0.0F, -5.0F, 0.0F, 0, 5, 4, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 16.5F, -1.0F);
        this.body.cuboids.add(new Cuboid(this.body, 2, 8, -1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(1.5F, 14.0F, 0.7F);
        this.leftWingClip = new ModelPart(this);
        this.leftWing.addChild(this.leftWingClip);
        this.leftWingClip.cuboids.add(new Cuboid(this.leftWingClip, 19, 8, 0.0F, 0.0F, -3.0F, 1, 5, 3, 0.0F, false));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(-1.5F, 14.0F, 0.7F);
        this.rightWingClip = new ModelPart(this);
        this.rightWing.addChild(this.rightWingClip);
        this.rightWingClip.cuboids.add(new Cuboid(this.rightWingClip, 19, 8, -1.0F, 0.0F, -3.0F, 1, 5, 3, 0.0F, false));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(1.0F, 22.0F, 0.0F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 14, 18, -2.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(-1.0F, 22.0F, 0.0F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 14, 18, 1.5F, 0.0F, -0.5F, 1, 2, 1, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, 21.1F, 3.2F);
        this.tail.cuboids.add(new Cuboid(this.tail, 22, 1, -1.5F, -1.0F, -1.0F, 3, 4, 1, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.isFlying = !entity.onGround;
        this.sitting = entity.sitting;
        int offset = 0;
        if (entity.isRiding()) {
            offset = 18;
        } else if (entity.sitting) {
            offset = 2;
        }

        this.head.setPivots(0.0F, 16.0F + offset, -0.5F);
        this.body.setPivots(0.0F, 16.5F + offset, -1.0F);
        this.leftWing.setPivots(1.5F, 16.0F + offset, 0.8F);
        this.rightWing.setPivots(-1.5F, 16.0F + offset, 0.8F);
        this.leftLeg.setPivots(1.0F, 22.0F + offset, 0.0F);
        this.rightLeg.setPivots(-1.0F, 22.0F + offset, 0.0F);
        this.tail.setPivots(0.0F, 21.1F + offset, 3.2F);
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.leftWing.render(tick);
        this.rightWing.render(tick);
        this.tail.render(tick);
        if (!this.sitting) {
            this.leftLeg.render(tick);
            this.rightLeg.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (180.0F / (float)Math.PI));
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.body.rotationX = (float) (Math.PI / 8);
        this.leftWingClip.rotationX = (float) (Math.PI / 6);
        this.rightWingClip.rotationX = (float) (Math.PI / 6);
        this.tail.rotationX = (float) (Math.PI / 4);
        this.body.rotationY = this.tail.rotationY = this.rightWingClip.rotationY = this.leftWingClip.rotationY = 0.0F;
        if (this.isFlying) {
            this.leftLeg.rotationX = 1.0F;
            this.rightLeg.rotationX = 1.0F;
        } else {
            this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
            this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        }

        this.rightWing.rotationZ = angle;
        this.leftWing.rotationZ = -angle;
    }
}
