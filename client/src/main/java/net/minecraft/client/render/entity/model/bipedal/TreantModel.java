package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class TreantModel extends Model {
    private final ModelPart legFrontRight;
    private final ModelPart legFrontLeft;
    private final ModelPart legBackRight;
    private final ModelPart legBackLeft;
    private final ModelPart trunk;
    private final ModelPart face;
    private final ModelPart leaves;
    private final ModelPart leavesInnerLayer;
    private final ModelPart leavesMiddleLayer;
    private final ModelPart leavesOuterLayer;
    private final ModelPart arms;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public TreantModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.legFrontRight = new ModelPart(this);
        this.legFrontRight.setPivots(-8.0F, 15.0F, -8.0F);
        this.legFrontRight.cuboids.add(new Cuboid(this.legFrontRight, 18, 18, -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F, false));
        this.legFrontLeft = new ModelPart(this);
        this.legFrontLeft.setPivots(7.0F, 16.0F, -7.0F);
        this.legFrontLeft.cuboids.add(new Cuboid(this.legFrontLeft, 20, 17, -1.0F, -1.0F, -3.0F, 4, 9, 4, 0.0F, false));
        this.legBackRight = new ModelPart(this);
        this.legBackRight.setPivots(-8.0F, 15.0F, 8.0F);
        this.legBackRight.cuboids.add(new Cuboid(this.legBackRight, 18, 20, -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F, false));
        this.legBackLeft = new ModelPart(this);
        this.legBackLeft.setPivots(8.0F, 15.0F, 8.0F);
        this.legBackLeft.cuboids.add(new Cuboid(this.legBackLeft, 20, 17, -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F, false));
        this.trunk = new ModelPart(this);
        this.trunk.setPivots(7.0F, 24.0F, 7.0F);
        this.trunk.cuboids.add(new Cuboid(this.trunk, 64, 0, -15.0F, -18.0F, -15.0F, 16, 16, 16, 0.0F, false));
        this.trunk.cuboids.add(new Cuboid(this.trunk, 64, 43, -15.0F, -22.0F, -15.0F, 16, 4, 16, 0.0F, false));
        this.face = new ModelPart(this);
        this.face.setPivots(-7.0F, -22.0F, 1.0F);
        this.trunk.addChild(this.face);
        this.face.cuboids.add(new Cuboid(this.face, 0, 32, -8.0F, -12.0F, -16.0F, 16, 12, 16, 0.0F, false));
        this.face.cuboids.add(new Cuboid(this.face, 0, 0, -8.0F, -28.0F, -16.0F, 16, 16, 16, 0.0F, false));
        this.face.cuboids.add(new Cuboid(this.face, 0, 0, -8.0F, -44.0F, -16.0F, 16, 16, 16, 0.0F, false));
        this.leaves = new ModelPart(this);
        this.leaves.setPivots(0.0F, 22.0F, -8.0F);
        this.face.addChild(this.leaves);
        this.leavesInnerLayer = new ModelPart(this);
        this.leavesInnerLayer.setPivots(-27.0F, -71.0F, 0.0F);
        this.leaves.addChild(this.leavesInnerLayer);
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 1.0F, 29.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 5, 101, 38.0F, 29.0F, -5.0F, 12, 12, 11, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 3.0F, -11.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 19.0F, -27.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 3.0F, 5.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 35.0F, -11.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 35.0F, 5.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 19.0F, -11.0F, -40.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 19.0F, 5.0F, -40.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, -11.0F, -11.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, -11.0F, 5.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 51.0F, -11.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 51.0F, 5.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 19.0F, -11.0F, 24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 19.0F, 5.0F, 24.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 35.0F, 5.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 3.0F, 5.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 3.0F, -11.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesInnerLayer.cuboids.add(new Cuboid(this.leavesInnerLayer, 0, 96, 35.0F, -11.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer = new ModelPart(this);
        this.leavesMiddleLayer.setPivots(-27.0F, -71.0F, 0.0F);
        this.leaves.addChild(this.leavesMiddleLayer);
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 3.0F, -27.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 19.0F, -27.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 19.0F, -27.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 35.0F, -27.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 35.0F, 5.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 19.0F, 5.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 19.0F, 5.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesMiddleLayer.cuboids.add(new Cuboid(this.leavesMiddleLayer, 0, 96, 3.0F, 5.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesOuterLayer = new ModelPart(this);
        this.leavesOuterLayer.setPivots(-27.0F, -71.0F, 0.0F);
        this.leaves.addChild(this.leavesOuterLayer);
        this.leavesOuterLayer.cuboids.add(new Cuboid(this.leavesOuterLayer, 0, 64, 3.0F, -11.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesOuterLayer.cuboids.add(new Cuboid(this.leavesOuterLayer, 0, 64, 35.0F, -11.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.leavesOuterLayer.cuboids.add(new Cuboid(this.leavesOuterLayer, 0, 64, 19.0F, -11.0F, -24.0F, 16, 16, 16, 0.0F, false));
        this.leavesOuterLayer.cuboids.add(new Cuboid(this.leavesOuterLayer, 0, 64, 19.0F, -11.0F, 8.0F, 16, 16, 16, 0.0F, false));
        this.leavesOuterLayer.cuboids.add(new Cuboid(this.leavesOuterLayer, 0, 64, 19.0F, -27.0F, -8.0F, 16, 16, 16, 0.0F, false));
        this.arms = new ModelPart(this);
        this.arms.setPivots(7.0F, 22.0F, -1.0F);
        this.face.addChild(this.arms);
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivots(2.0F, -23.0F, -4.0F);
        this.arms.addChild(this.rightArm);
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 11, 19, -1.0F, -5.0F, -5.0F, 7, 5, 6, 0.0F, false));
        this.rightArm.cuboids.add(new Cuboid(this.rightArm, 15, 15, 6.0F, -11.0F, -5.0F, 5, 11, 6, 0.0F, false));
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivots(-16.0F, -23.0F, -9.0F);
        this.arms.addChild(this.leftArm);
        this.setRotation(this.leftArm, -3.1416F, 0.0F, 3.1416F);
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 11, 19, -1.0F, -5.0F, -5.0F, 7, 5, 6, 0.0F, false));
        this.leftArm.cuboids.add(new Cuboid(this.leftArm, 15, 15, 6.0F, -11.0F, -5.0F, 5, 11, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.legFrontRight.render(tick);
        this.legFrontLeft.render(tick);
        this.legBackRight.render(tick);
        this.legBackLeft.render(tick);
        this.trunk.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.legFrontRight.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.legFrontLeft.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.legBackRight.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.legBackLeft.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
