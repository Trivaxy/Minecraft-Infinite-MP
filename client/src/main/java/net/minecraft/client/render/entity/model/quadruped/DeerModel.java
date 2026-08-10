package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DeerModel extends Model {
    private final ModelPart torso;
    private final ModelPart neck;
    private final ModelPart neckPart;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart rightAntler;
    private final ModelPart leftAntler;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;

    public DeerModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 12.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -4.0F, -8.0F, -9.0F, 8, 9, 19, 0.0F, false));
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, -5.0F, -6.0F);
        this.torso.addChild(this.neck);
        this.neckPart = new ModelPart(this);
        this.neckPart.setPivots(0.0F, 0.0F, 0.0F);
        this.neck.addChild(this.neckPart);
        this.setRotation(this.neckPart, 0.3927F, 0.0F, 0.0F);
        this.neckPart.cuboids.add(new Cuboid(this.neckPart, 0, 0, -2.0F, -10.0F, -3.0F, 4, 10, 4, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -7.5F, -4.0F);
        this.neck.addChild(this.head);
        this.nose = new ModelPart(this);
        this.nose.setPivots(4.25F, -3.0F, -2.0F);
        this.head.addChild(this.nose);
        this.setRotation(this.nose, 0.3491F, 0.0F, 0.0F);
        this.nose.cuboids.add(new Cuboid(this.nose, 0, 24, -2.0F, -6.0F, -1.0F, 0, 6, 4, 0.0F, false));
        this.nose.cuboids.add(new Cuboid(this.nose, 0, 24, -6.5F, -6.0F, -1.0F, 0, 6, 4, 0.0F, false));
        this.rightAntler = new ModelPart(this);
        this.rightAntler.setPivots(-1.0F, 3.0F, -8.5F);
        this.head.addChild(this.rightAntler);
        this.setRotation(this.rightAntler, 0.3491F, 0.0F, 0.0F);
        this.rightAntler.cuboids.add(new Cuboid(this.rightAntler, 0, 35, -1.0F, -3.0F, 2.0F, 4, 3, 3, 0.0F, false));
        this.leftAntler = new ModelPart(this);
        this.leftAntler.setPivots(-0.5F, 1.0F, -2.0F);
        this.head.addChild(this.leftAntler);
        this.setRotation(this.leftAntler, 0.3491F, 0.0F, 0.0F);
        this.leftAntler.cuboids.add(new Cuboid(this.leftAntler, 40, 28, -2.0F, -4.0F, -2.0F, 5, 4, 7, 0.0F, false));
        this.leftFrontLeg = new ModelPart(this);
        this.leftFrontLeg.setPivots(-2.5F, 1.0F, -7.0F);
        this.torso.addChild(this.leftFrontLeg);
        this.leftFrontLeg.cuboids.add(new Cuboid(this.leftFrontLeg, 35, 0, -1.5F, 0.0F, -2.0F, 3, 11, 3, 0.0F, false));
        this.rightFrontLeg = new ModelPart(this);
        this.rightFrontLeg.setPivots(2.5F, 1.0F, -7.0F);
        this.torso.addChild(this.rightFrontLeg);
        this.rightFrontLeg.cuboids.add(new Cuboid(this.rightFrontLeg, 35, 0, -1.5F, 0.0F, -2.0F, 3, 11, 3, 0.0F, true));
        this.leftHindLeg = new ModelPart(this);
        this.leftHindLeg.setPivots(-2.5F, 1.0F, 8.5F);
        this.torso.addChild(this.leftHindLeg);
        this.leftHindLeg.cuboids.add(new Cuboid(this.leftHindLeg, 35, 0, -1.5F, 0.0F, -1.5F, 3, 11, 3, 0.0F, false));
        this.rightHindLeg = new ModelPart(this);
        this.rightHindLeg.setPivots(2.5F, 1.0F, 8.5F);
        this.torso.addChild(this.rightHindLeg);
        this.rightHindLeg.cuboids.add(new Cuboid(this.rightHindLeg, 35, 0, -1.5F, 0.0F, -1.5F, 3, 11, 3, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.torso.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = this.neck.rotationY = MathHelper.clamp(headYaw / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.head.rotationX = this.neck.rotationX = MathHelper.clamp(headPitch / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.leftFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.rightFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.4F * anim;
        this.leftHindLeg.rotationX = MathHelper.cos(swing * 0.6662F + 3.141593F) * 1.4F * anim;
        this.rightHindLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
