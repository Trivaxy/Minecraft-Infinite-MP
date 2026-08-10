package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DolphinModel extends Model {
    private final ModelPart root;
    private final ModelPart tail;
    private final ModelPart tailEnd;
    private final ModelPart rightFinEnd;
    private final ModelPart leftFinEnd;
    private final ModelPart rightTailFin;
    private final ModelPart leftTailFin;
    private final ModelPart torso;
    private final ModelPart topFin;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart head;
    private final ModelPart jawTop;
    private final ModelPart jawBottom;

    public DolphinModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.root = new ModelPart(this);
        this.root.setPivots(1.0F, 20.0F, -12.0F);
        this.tail = new ModelPart(this);
        this.tail.setPivots(-1.0F, -4.5F, 16.0F);
        this.root.addChild(this.tail);
        this.tail.cuboids.add(new Cuboid(this.tail, 33, 15, -3.0F, -4.0F, -1.0F, 6, 7, 13, 0.0F, false));
        this.tailEnd = new ModelPart(this);
        this.tailEnd.setPivots(0.5F, 1.0F, 13.0F);
        this.tail.addChild(this.tailEnd);
        this.rightFinEnd = new ModelPart(this);
        this.rightFinEnd.setPivots(0.5F, 4.0F, 16.0F);
        this.tailEnd.addChild(this.rightFinEnd);
        this.setRotation(this.rightFinEnd, -0.0436F, 0.0F, 0.0F);
        this.rightFinEnd.cuboids.add(new Cuboid(this.rightFinEnd, -1, 79, -3.0F, -7.0F, -18.0F, 4, 5, 19, 0.0F, false));
        this.leftFinEnd = new ModelPart(this);
        this.leftFinEnd.setPivots(-1.0F, -1.0F, 19.0F);
        this.tailEnd.addChild(this.leftFinEnd);
        this.setRotation(this.leftFinEnd, 0.0F, 0.0873F, 1.5708F);
        this.rightTailFin = new ModelPart(this);
        this.rightTailFin.setPivots(0.0F, 6.5F, -5.0F);
        this.leftFinEnd.addChild(this.rightTailFin);
        this.setRotation(this.rightTailFin, 0.5236F, 0.0F, 0.0F);
        this.rightTailFin.cuboids.add(new Cuboid(this.rightTailFin, 66, 32, 0.0F, -7.0F, 1.0F, 1, 11, 5, 0.0F, false));
        this.leftTailFin = new ModelPart(this);
        this.leftTailFin.setPivots(0.0F, -4.5F, -6.0F);
        this.leftFinEnd.addChild(this.leftTailFin);
        this.setRotation(this.leftTailFin, -0.48F, 0.0F, 0.0F);
        this.leftTailFin.cuboids.add(new Cuboid(this.leftTailFin, 66, 32, 0.0F, -7.0F, 1.0F, 1, 11, 5, 0.0F, false));
        this.torso = new ModelPart(this);
        this.torso.setPivots(0.0F, 1.0F, 12.0F);
        this.root.addChild(this.torso);
        this.torso.cuboids.add(new Cuboid(this.torso, 0, 0, -5.0F, -10.5F, -12.0F, 8, 9, 15, 0.0F, false));
        this.topFin = new ModelPart(this);
        this.topFin.setPivots(-1.0F, -11.0F, -3.0F);
        this.torso.addChild(this.topFin);
        this.setRotation(this.topFin, -0.3491F, 0.0F, 0.0F);
        this.topFin.cuboids.add(new Cuboid(this.topFin, 37, 59, -1.0F, -9.0F, 0.0F, 2, 13, 4, 0.0F, false));
        this.leftFin = new ModelPart(this);
        this.leftFin.setPivots(-2.0F, -3.0F, -7.5F);
        this.torso.addChild(this.leftFin);
        this.setRotation(this.leftFin, 0.0468F, -0.004F, 0.0291F);
        this.leftFin.cuboids.add(new Cuboid(this.leftFin, 55, 2, -7.5422F, -2.0441F, -1.0436F, 8, 2, 8, 0.0F, true));
        this.leftFin.cuboids.add(new Cuboid(this.leftFin, 59, 13, -15.5422F, -1.5441F, 0.4564F, 8, 1, 6, 0.0F, false));
        this.rightFin = new ModelPart(this);
        this.rightFin.setPivots(0.0F, -3.0F, -2.0F);
        this.torso.addChild(this.rightFin);
        this.setRotation(this.rightFin, -3.1416F, 0.0F, 3.1416F);
        this.rightFin.cuboids.add(new Cuboid(this.rightFin, 31, 0, -7.5209F, -2.0456F, -1.0F, 8, 2, 8, 0.0F, false));
        this.rightFin.cuboids.add(new Cuboid(this.rightFin, 72, 78, -15.5209F, -1.5456F, -0.5F, 8, 1, 6, 0.0F, false));
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 0.0F, 0.25F);
        this.root.addChild(this.head);
        this.setRotation(this.head, 0.0873F, 0.0F, 0.0F);
        this.jawTop = new ModelPart(this);
        this.jawTop.setPivots(-1.0F, -0.25F, 0.5F);
        this.head.addChild(this.jawTop);
        this.setRotation(this.jawTop, -0.0873F, 0.0F, 0.0F);
        this.jawTop.cuboids.add(new Cuboid(this.jawTop, 49, 61, -3.0F, -6.0F, -19.5F, 6, 4, 7, 0.0F, false));
        this.jawTop.cuboids.add(new Cuboid(this.jawTop, 88, 0, -4.0F, -9.0F, -12.5F, 8, 8, 12, 0.0F, false));
        this.jawBottom = new ModelPart(this);
        this.jawBottom.setPivots(-1.0F, -2.0F, 1.0F);
        this.head.addChild(this.jawBottom);
        this.setRotation(this.jawBottom, -0.1309F, 0.0F, 0.0F);
        this.jawBottom.cuboids.add(new Cuboid(this.jawBottom, -1, 26, -2.0F, -1.0F, -19.0F, 4, 3, 18, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.root.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = this.rightTailFin.rotationY = this.leftTailFin.rotationY = this.topFin.rotationY = this.rightFinEnd.rotationY = MathHelper.cos(
                angle * 0.9F + 0.47123894F
            )
            * (float) Math.PI
            * 0.01F;
        this.leftFinEnd.rotationY = this.tailEnd.rotationY = this.tail.rotationY = -MathHelper.cos(angle * 0.9F + 0.47123894F) * (float) Math.PI * 0.01F;
    }
}
