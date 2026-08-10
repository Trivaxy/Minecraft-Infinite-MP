package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.mounts.Camel;
import net.minecraft.util.math.MathHelper;

public class CamelModel extends Model {
    private final ModelPart torso;
    private final ModelPart tail;
    private final ModelPart tailEnd;
    private final ModelPart saddle;
    private final ModelPart neck;
    private final ModelPart neckEnd;
    private final ModelPart head;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;

    public CamelModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 10.0F, 0.0F);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -6.0F, -14.0F, -14.0F, 12, 14, 29, 0.0F, false));
        this.torso.cuboids.add(new Cuboid(this.torso, 86, 12, -5.0F, -18.0F, -7.0F, 10, 5, 11, 0.0F, false));
        this.tail = new ModelPart(this);
        this.tail.setPivots(0.0F, -12.0F, 16.0F);
        this.torso.addChild(this.tail);
        this.tailEnd = new ModelPart(this);
        this.tailEnd.setPivots(0.0F, 4.0F, 0.0F);
        this.tail.addChild(this.tailEnd);
        this.setRotation(this.tailEnd, 0.3491F, 0.0F, 0.0F);
        this.tailEnd.cuboids.add(new Cuboid(this.tailEnd, 1, 44, -2.0F, -4.0F, -1.0F, 4, 10, 2, 0.0F, false));
        this.saddle = new ModelPart(this);
        this.saddle.setPivots(-0.5F, 11.5F, 5.5F);
        this.saddle.cuboids.add(new Cuboid(this.saddle, 45, 84, -6.0F, -16.0F, -20.0F, 13, 14, 30, 0.0F, false));
        this.saddle.cuboids.add(new Cuboid(this.saddle, -2, 90, -5.0F, -19.75F, -13.0F, 11, 6, 12, 0.0F, false));
        this.neck = new ModelPart(this);
        this.neck.setPivots(0.0F, -3.0F, -14.0F);
        this.torso.addChild(this.neck);
        this.neckEnd = new ModelPart(this);
        this.neckEnd.setPivots(0.0F, -8.0F, -1.0F);
        this.neck.addChild(this.neckEnd);
        this.setRotation(this.neckEnd, 0.2182F, 0.0F, 0.0F);
        this.neckEnd.cuboids.add(new Cuboid(this.neckEnd, 0, 0, -3.0F, -4.0F, -10.0F, 6, 13, 6, 0.0F, false));
        this.neckEnd.cuboids.add(new Cuboid(this.neckEnd, 50, 43, -4.0F, 1.0F, -9.0F, 8, 9, 13, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, -9.0F, -9.0F);
        this.neck.addChild(this.head);
        this.head.cuboids.add(new Cuboid(this.head, 53, 0, -4.0F, -7.0F, -8.0F, 8, 7, 12, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 20, 2.0F, -10.0F, 0.0F, 2, 3, 3, 0.0F, false));
        this.head.cuboids.add(new Cuboid(this.head, 0, 20, -4.0F, -10.0F, 0.0F, 2, 3, 3, 0.0F, true));
        this.rightFrontLeg = new ModelPart(this);
        this.rightFrontLeg.setPivots(4.0F, 10.0F, -10.0F);
        this.rightFrontLeg.cuboids.add(new Cuboid(this.rightFrontLeg, 44, 65, -3.0F, 0.0F, -3.0F, 5, 14, 6, 0.0F, false));
        this.leftFrontLeg = new ModelPart(this);
        this.leftFrontLeg.setPivots(-3.0F, 10.0F, -10.0F);
        this.leftFrontLeg.cuboids.add(new Cuboid(this.leftFrontLeg, 44, 65, -3.0F, 0.0F, -3.0F, 5, 14, 6, 0.0F, true));
        this.leftBackLeg = new ModelPart(this);
        this.leftBackLeg.setPivots(-3.0F, 10.0F, 8.0F);
        this.leftBackLeg.cuboids.add(new Cuboid(this.leftBackLeg, 22, 64, -3.0F, 0.0F, -1.0F, 5, 14, 6, 0.0F, true));
        this.rightBackLeg = new ModelPart(this);
        this.rightBackLeg.setPivots(2.0F, 10.0F, 10.0F);
        this.rightBackLeg.cuboids.add(new Cuboid(this.rightBackLeg, 22, 64, -1.0F, 0.0F, -3.0F, 5, 14, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.torso.render(tick);
        this.rightFrontLeg.render(tick);
        this.leftFrontLeg.render(tick);
        this.leftBackLeg.render(tick);
        this.rightBackLeg.render(tick);
        if (((Camel)entity).isRideable()) {
            this.saddle.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = this.neck.rotationY = MathHelper.clamp(headYaw / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.head.rotationX = MathHelper.clamp(headPitch / (180.0F / (float)Math.PI), -0.5F, 0.5F);
        this.rightFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.leftFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.rightBackLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.leftBackLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
