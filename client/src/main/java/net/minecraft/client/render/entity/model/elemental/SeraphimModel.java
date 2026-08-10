package net.minecraft.client.render.entity.model.elemental;

import net.minecraft.client.render.entity.model.Cuboid;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.util.math.MathHelper;

public class SeraphimModel extends Model {
    private final ModelPart eye;
    private final ModelPart spinningInnerRing;
    private final ModelPart spinningOuterRing;
    private final ModelPart largeWingLeft;
    private final ModelPart mediumWingLeft;
    private final ModelPart smallWingLeft;
    private final ModelPart largeWingRight;
    private final ModelPart mediumWingRight;
    private final ModelPart smallWingRight;

    public SeraphimModel() {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.eye = new ModelPart(this);
        this.eye.setPivots(0.0F, -6.0F, 0.0F);
        this.eye.cuboids.add(new Cuboid(this.eye, 0, 0, -16.0F, -18.0F, -17.0F, 32, 32, 32, 0.0F, false));
        this.spinningInnerRing = new ModelPart(this);
        this.spinningInnerRing.setPivots(-0.5F, -9.0F, 0.0F);
        this.setRotation(this.spinningInnerRing, 0.0F, 0.3054F, 0.0F);
        this.spinningInnerRing.cuboids.add(new Cuboid(this.spinningInnerRing, 0, 103, -24.5F, -24.0F, -4.0F, 50, 7, 6, 0.0F, false));
        this.spinningInnerRing.cuboids.add(new Cuboid(this.spinningInnerRing, 40, 116, -23.5F, -23.0F, -3.0F, 6, 48, 4, 0.0F, false));
        this.spinningInnerRing.cuboids.add(new Cuboid(this.spinningInnerRing, 20, 116, 18.5F, -23.0F, -3.0F, 6, 48, 4, 0.0F, false));
        this.spinningInnerRing.cuboids.add(new Cuboid(this.spinningInnerRing, 0, 90, -24.5F, 19.0F, -4.0F, 50, 7, 6, 0.0F, false));
        this.spinningOuterRing = new ModelPart(this);
        this.spinningOuterRing.setPivots(-0.5F, -9.0F, 0.0F);
        this.setRotation(this.spinningOuterRing, 0.0F, -0.5236F, 0.0F);
        this.spinningOuterRing.cuboids.add(new Cuboid(this.spinningOuterRing, 0, 64, -34.5F, -33.0F, -4.0F, 65, 7, 6, 0.0F, false));
        this.spinningOuterRing.cuboids.add(new Cuboid(this.spinningOuterRing, 0, 116, -32.5F, -28.0F, -3.0F, 6, 58, 4, 0.0F, false));
        this.spinningOuterRing.cuboids.add(new Cuboid(this.spinningOuterRing, 112, 90, 23.5F, -30.0F, -3.0F, 6, 59, 4, 0.0F, false));
        this.spinningOuterRing.cuboids.add(new Cuboid(this.spinningOuterRing, 0, 77, -33.5F, 28.0F, -4.0F, 64, 7, 6, 0.0F, false));
        this.largeWingLeft = new ModelPart(this);
        this.largeWingLeft.setPivots(-17.0F, -17.0F, -2.0F);
        this.setRotation(this.largeWingLeft, 0.0117F, 0.2615F, 0.0452F);
        this.largeWingLeft.cuboids.add(new Cuboid(this.largeWingLeft, 35, 176, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
        this.mediumWingLeft = new ModelPart(this);
        this.mediumWingLeft.setPivots(-25.0F, -24.0F, 4.0F);
        this.setRotation(this.mediumWingLeft, 0.0022F, 0.7812F, 0.4442F);
        this.mediumWingLeft.cuboids.add(new Cuboid(this.mediumWingLeft, 7, 223, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
        this.smallWingLeft = new ModelPart(this);
        this.smallWingLeft.setPivots(-24.0F, 3.0F, -8.0F);
        this.setRotation(this.smallWingLeft, 0.1788F, 0.8123F, -0.2442F);
        this.smallWingLeft.cuboids.add(new Cuboid(this.smallWingLeft, 7, 223, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
        this.largeWingRight = new ModelPart(this);
        this.largeWingRight.setPivots(16.0F, -17.0F, 0.0F);
        this.setRotation(this.largeWingRight, 3.1359F, 0.1308F, 3.0976F);
        this.largeWingRight.cuboids.add(new Cuboid(this.largeWingRight, 35, 176, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
        this.mediumWingRight = new ModelPart(this);
        this.mediumWingRight.setPivots(24.0F, -29.0F, 6.0F);
        this.setRotation(this.mediumWingRight, -3.1142F, 0.8329F, 2.7259F);
        this.mediumWingRight.cuboids.add(new Cuboid(this.mediumWingRight, 35, 176, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
        this.smallWingRight = new ModelPart(this);
        this.smallWingRight.setPivots(24.0F, -1.0F, -11.0F);
        this.setRotation(this.smallWingRight, -2.9509F, 0.9864F, -2.8739F);
        this.smallWingRight.cuboids.add(new Cuboid(this.smallWingRight, 35, 176, -96.0F, -12.0F, 1.0F, 96, 26, 0, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.eye.render(tick);
        this.spinningInnerRing.render(tick);
        this.spinningOuterRing.render(tick);
        if (((Seraphim)entity).healingCrystal == null) {
            this.largeWingLeft.render(tick);
            this.mediumWingLeft.render(tick);
            this.smallWingLeft.render(tick);
            this.largeWingRight.render(tick);
            this.mediumWingRight.render(tick);
            this.smallWingRight.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.largeWingLeft.setPivots(0.0F, 0.0F, 0.0F);
        this.largeWingRight.setPivots(0.0F, 0.0F, 0.0F);
        this.largeWingLeft.rotationY = this.largeWingRight.rotationY = MathHelper.cos(angle) * (float) Math.PI * 0.25F;
        this.mediumWingLeft.setPivots(0.0F, 0.0F, 0.0F);
        this.smallWingLeft.setPivots(0.0F, 0.0F, 0.0F);
        this.mediumWingLeft.rotationY = this.smallWingLeft.rotationY = MathHelper.cos(angle) * (float) Math.PI * 0.15F;
        this.mediumWingRight.setPivots(0.0F, 0.0F, 0.0F);
        this.smallWingRight.setPivots(0.0F, 0.0F, 0.0F);
        this.mediumWingRight.rotationY = this.smallWingRight.rotationY = MathHelper.cos(angle) * (float) Math.PI * 0.15F;
        float spinValue = 0.03141593F;
        this.spinningInnerRing.pivotY = -10.0F + MathHelper.cos(angle * 0.15F);
        this.spinningInnerRing.pivotX = MathHelper.cos(spinValue) * 5.0F - 3.0F;
        this.spinningInnerRing.pivotZ = MathHelper.sin(spinValue) * 5.0F;
        spinValue = angle * (float) Math.PI * 0.03F;
        this.spinningInnerRing.rotationY = spinValue * 2.0F;
        spinValue += 1.0995574F;
        spinValue = 0.03141593F;
        this.spinningOuterRing.pivotY = -10.0F + MathHelper.cos(angle * 0.15F);
        this.spinningOuterRing.pivotX = MathHelper.cos(spinValue) * 5.0F - 3.0F;
        this.spinningOuterRing.pivotZ = MathHelper.sin(spinValue) * 5.0F;
        spinValue = angle * (float) Math.PI * 0.03F;
        this.spinningOuterRing.rotationY = -(spinValue * 2.0F);
        spinValue += 1.0995574F;
    }
}
