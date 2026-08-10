package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.flying.Penguin;
import net.minecraft.util.math.MathHelper;

public class PenguinModel extends Model {
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart torso;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;

    public PenguinModel() {
        this.textureWidth = 48;
        this.textureHeight = 48;
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(-1.5F, 23.0F, -0.75F);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 0, 43, -1.0F, -1.0F, -3.0F, 2, 2, 3, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(1.5F, 23.0F, -0.75F);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 10, 43, -1.0F, -1.0F, -3.0F, 2, 2, 3, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 23.0F, -1.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 13, -3.0F, -10.0F, -2.5F, 6, 10, 5, 0.0F, false));
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.torso.addChild(this.body);
        this.setRotation(this.body, 0.2182F, 0.0F, 0.0F);
        this.body.cuboids.add(new Cuboid(this.body, 22, 23, -1.0F, -3.25F, 2.5F, 2, 4, 1, 0.0F, false));
        this.leftWing = new ModelPart(this);
        this.leftWing.setPivots(-3.0F, -9.5F, 0.0F);
        this.torso.addChild(this.leftWing);
        this.leftWing.cuboids.add(new Cuboid(this.leftWing, 38, 34, -1.0F, 0.0F, -2.0F, 1, 7, 4, 0.0F, false));
        this.rightWing = new ModelPart(this);
        this.rightWing.setPivots(3.0F, -9.5F, 0.0F);
        this.torso.addChild(this.rightWing);
        this.rightWing.cuboids.add(new Cuboid(this.rightWing, 38, 20, 0.0F, 0.0F, -2.0F, 1, 7, 4, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -10.0F, 0.0F);
        this.torso.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -3.0F, -6.0F, -3.0F, 6, 6, 6, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 18, 0, -1.0F, -1.75F, -5.25F, 2, 1, 3, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        if (((Penguin)entity).attacking) {
            this.torso.rotationX = 1.5F;
            this.leftLeg.rotationX = -1.5F;
            this.rightLeg.rotationX = -1.5F;
            this.leftWing.rotationZ = -1.7F;
            this.rightWing.rotationZ = 1.7F;
            this.head.rotationX = -1.5F;
            this.head.rotationY = 0.0F;
        } else {
            this.torso.rotationX = 0.0F;
            this.leftLeg.rotationX = 0.0F;
            this.rightLeg.rotationX = 0.0F;
            this.leftWing.rotationZ = 0.0F;
            this.rightWing.rotationZ = 0.0F;
            this.head.rotationX = 0.0F;
            this.head.rotationY = 0.0F;
            this.animate(swing, anim, angle, headYaw, headPitch, tick);
        }

        this.leftLeg.render(tick);
        this.rightLeg.render(tick);
        this.torso.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (180.0F / (float)Math.PI));
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.leftWing.rotationZ = angle;
        this.rightWing.rotationZ = -angle;
    }
}
