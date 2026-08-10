package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class FoxModel extends Model {
    public final ModelPart head = new ModelPart(this, 1, 5);
    private final ModelPart earLeft;
    private final ModelPart earRight;
    public final ModelPart nose;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart tail;
    public boolean isSleeping;
    public boolean isSitting;

    public FoxModel() {
        this.head.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6, 0.0F, false);
        this.head.setPivots(-1.0F, 16.5F, -3.0F);
        this.earLeft = new ModelPart(this, 8, 1);
        this.head.addChild(this.earLeft);
        this.earLeft.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1, 0.0F, false);
        this.earLeft.setPivots(0.0F, 0.0F, 0.0F);
        this.earRight = new ModelPart(this, 15, 1);
        this.head.addChild(this.earRight);
        this.earRight.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1, 0.0F, false);
        this.earRight.setPivots(0.0F, 0.0F, 0.0F);
        this.nose = new ModelPart(this, 6, 18);
        this.head.addChild(this.nose);
        this.nose.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3, 0.0F, false);
        this.nose.setPivots(0.0F, 0.0F, 0.0F);
        this.body = new ModelPart(this, 24, 15);
        this.body.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6, 0.0F, false);
        this.body.setPivots(0.0F, 16.0F, -6.0F);
        this.leftHindLeg = new ModelPart(this, 13, 24);
        this.leftHindLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F, false);
        this.leftHindLeg.setPivots(-5.0F, 17.5F, 7.0F);
        this.rightHindLeg = new ModelPart(this, 4, 24);
        this.rightHindLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F, false);
        this.rightHindLeg.setPivots(-1.0F, 17.5F, 7.0F);
        this.leftFrontLeg = new ModelPart(this, 13, 24);
        this.leftFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F, false);
        this.leftFrontLeg.setPivots(-5.0F, 17.5F, 0.0F);
        this.rightFrontLeg = new ModelPart(this, 4, 24);
        this.rightFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F, false);
        this.rightFrontLeg.setPivots(-1.0F, 17.5F, 0.0F);
        this.tail = new ModelPart(this, 30, 0);
        this.tail.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5, 0.0F, false);
        this.tail.setPivots(-4.0F, 18.0F, 7.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float rot, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, rot, headYaw, headPitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.tail.render(tick);
        if (!this.isSleeping) {
            this.leftHindLeg.render(tick);
            this.rightHindLeg.render(tick);
            this.leftFrontLeg.render(tick);
            this.rightFrontLeg.render(tick);
        }
    }

    @Override
    public void animate(float swing, float animSpd, float rot, float headYaw, float headPitch, float tick) {
        this.head.setPivots(-1.0F, 14.5F, -3.0F);
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.body.rotationX = (float) (Math.PI / 2);
        this.body.setPivots(0.0F, 16.0F, -6.0F);
        this.body.rotationZ = 0.0F;
        this.tail.setPivots(-4.0F, 18.0F, 7.0F);
        this.tail.rotationX = (float) (Math.PI / 2);
        this.tail.rotationZ = 0.0F;
        this.leftHindLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * animSpd;
        this.rightHindLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * animSpd;
        this.leftFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * animSpd;
        this.rightFrontLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * animSpd;
        this.leftHindLeg.setPivots(-5.0F, 17.5F, 7.0F);
        this.rightHindLeg.setPivots(-1.0F, 17.5F, 7.0F);
        if (this.isSleeping) {
            this.body.rotationZ = (float) (-Math.PI / 2);
            this.body.setPivots(0.0F, 21.0F, -6.0F);
            this.tail.rotationX = (float) (-Math.PI * 5.0 / 6.0);
            this.tail.rotationZ = 1.6179938F;
            this.tail.setPivots(2.0F, 17.5F, 9.0F);
            this.head.setPivots(1.0F, 19.49F, -3.0F);
            this.head.rotationY = (float) (-Math.PI * 2.0 / 3.0);
        } else if (this.isSitting) {
            this.body.rotationX = (float) (Math.PI / 6);
            this.body.setPivots(0.0F, 9.0F, -3.0F);
            this.tail.setPivots(-4.0F, 22.0F, 4.0F);
            this.head.setPivots(-1.0F, 10.0F, -0.25F);
            this.head.rotationX = 0.0F;
            this.head.rotationY = 0.0F;
            this.leftHindLeg.rotationX = (float) (-Math.PI * 5.0 / 12.0);
            this.leftHindLeg.setPivots(-5.0F, 21.5F, 6.75F);
            this.rightHindLeg.rotationX = (float) (-Math.PI * 5.0 / 12.0);
            this.rightHindLeg.setPivots(-1.0F, 21.5F, 6.75F);
            this.leftFrontLeg.rotationX = (float) (-Math.PI / 12);
            this.rightFrontLeg.rotationX = (float) (-Math.PI / 12);
        }
    }
}
