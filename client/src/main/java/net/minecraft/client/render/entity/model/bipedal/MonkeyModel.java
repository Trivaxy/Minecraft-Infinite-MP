package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class MonkeyModel extends Model {
    public final ModelPart root;
    public final ModelPart torso;
    private final ModelPart tail;
    public final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart head;

    public MonkeyModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.root = new ModelPart(this);
        this.root.setPivots(0.5F, 20.0F, -1.5F);
        this.setRotation(this.root, 0.0F, 1.5708F, 0.0F);
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 0.0F, 0.0F);
        this.root.addChild(this.torso);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 10, -1.5F, -4.0F, -2.5F, 3, 4, 5, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(-1.5F, -1.0F, 0.0F);
        this.torso.addChild(this.tail);
        this.setRotation(this.tail, -0.0436F, 0.0F, -0.2618F);
        this.tail.cuboids.add(new Cuboid(this.tail, 16, 11, -0.2412F, -3.465F, -2.0421F, 1, 4, 4, 0.0F, false));
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(0.0F, -4.0F, -2.5F);
        this.torso.addChild(this.rightArm);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 0, 19, -1.0F, 0.0F, -1.0F, 2, 6, 1, 0.0F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(0.0F, -4.0F, 2.5F);
        this.torso.addChild(this.leftArm);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 0, 19, -1.0F, 0.0F, 0.0F, 2, 6, 1, 0.0F, false));
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivots(0.0F, 0.0F, 1.5F);
        this.root.addChild(this.leftLeg);
        this.leftLeg.cuboids.add(new Cuboid(this.leftLeg, 0, 26, -1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F, false));
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivots(0.0F, 0.0F, -1.5F);
        this.root.addChild(this.rightLeg);
        this.rightLeg.cuboids.add(new Cuboid(this.rightLeg, 0, 26, -1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.5F, 16.0F, -1.5F);
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -2.5F, -5.0F, -2.5F, 5, 5, 5, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 2, 2, 1.5F, -6.0F, -1.5F, 1, 1, 3, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 0, -1.5F, -6.0F, -2.5F, 3, 1, 5, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 2, 2, -2.5F, -6.0F, -1.5F, 1, 1, 3, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 20, 5, 2.5F, -4.5F, -0.5F, 2, 4, 1, 0.0F, true));
        this.head.cuboids.add(new Cuboid(this.head, 20, 5, -4.5F, -4.5F, -0.5F, 2, 4, 1, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.root.render(tick);
        this.head.render(tick);
    }

    @Override
    public void animate(float swing, float spd, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (180.0F / (float)Math.PI));
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.rightLeg.rotationZ = MathHelper.cos(swing * 0.6662F) * 1.0F * spd;
        this.leftLeg.rotationZ = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.0F * spd;
        this.leftArm.rotationZ = MathHelper.cos(swing * 0.6662F) * 1.0F * spd;
        this.rightArm.rotationZ = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.0F * spd;
    }
}
