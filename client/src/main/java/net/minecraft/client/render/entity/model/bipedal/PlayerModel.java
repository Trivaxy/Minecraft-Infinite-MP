package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import org.lwjgl.opengl.GL11;

public class PlayerModel extends BipedalModel {
    public ModelPart leftArmOverlay;
    public ModelPart rightArmOverlay;
    public ModelPart leftLegOverlay;
    public ModelPart rightLegOverlay;
    public ModelPart bodyOverlay;
    public ModelPart cloak;

    public PlayerModel(float scale, float armOffset) {
        this.aimedBow = false;
        this.sneaking = false;
        this.crawling = false;
        this.blocking = false;
        this.cloak = new ModelPart(this, 0, 0);
        this.cloak.setTextureSize(64, 32);
        this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale, false);
        this.leftArmOverlay = new ModelPart(this, 48, 48);
        this.leftArmOverlay.setTextureSize(64, 64);
        this.leftArmOverlay.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F, false);
        this.leftArmOverlay.setPivots(5.0F, 2.0F, 0.0F);
        this.rightArmOverlay = new ModelPart(this, 40, 32);
        this.rightArmOverlay.setTextureSize(64, 64);
        this.rightArmOverlay.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F, false);
        this.rightArmOverlay.setPivots(-5.0F, 2.0F, 10.0F);
        this.leftLegOverlay = new ModelPart(this, 0, 48);
        this.leftLegOverlay.setTextureSize(64, 64);
        this.leftLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F, false);
        this.leftLegOverlay.setPivots(1.9F, 12.0F, 0.0F);
        this.rightLegOverlay = new ModelPart(this, 0, 32);
        this.rightLegOverlay.setTextureSize(64, 64);
        this.rightLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F, false);
        this.rightLegOverlay.setPivots(-1.9F, 12.0F, 0.0F);
        this.bodyOverlay = new ModelPart(this, 16, 32);
        this.bodyOverlay.setTextureSize(64, 64);
        this.bodyOverlay.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F, false);
        this.bodyOverlay.setPivots(0.0F, 0.0F, 0.0F);
        this.leftArm = new ModelPart(this, 32, 48);
        this.leftArm.setTextureSize(64, 64);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale, false);
        this.leftArm.setPivots(5.0F, 2.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.setTextureSize(64, 64);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.leftLeg.setPivots(1.9F, 12.0F, 0.0F);
        this.head = new ModelPart(this, 0, 0);
        this.head.setTextureSize(64, 64);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.head.setPivots(0.0F, 0.0F + armOffset, 0.0F);
        this.hair = new ModelPart(this, 32, 0);
        this.hair.setTextureSize(64, 64);
        this.hair.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F, false);
        this.hair.setPivots(0.0F, 0.0F + armOffset, 0.0F);
        this.body = new ModelPart(this, 16, 16);
        this.body.setTextureSize(64, 64);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false);
        this.body.setPivots(0.0F, 0.0F + armOffset, 0.0F);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.setTextureSize(64, 64);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightArm.setPivots(-5.0F, 2.0F + armOffset, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.setTextureSize(64, 64);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightLeg.setPivots(-2.0F, 12.0F + armOffset, 0.0F);
    }

    @Override
    public void render(Entity entity, float forward, float haltDelta, float angle, float yaw, float pitch, float ticks) {
        GL11.glEnable(2884);
        GL11.glCullFace(1029);
        super.render(entity, forward, haltDelta, angle, yaw, pitch, ticks);
        this.animate(forward, haltDelta, angle, yaw, pitch, ticks);
        GL11.glPushMatrix();
        this.leftArmOverlay.render(ticks);
        this.rightArmOverlay.render(ticks);
        this.leftLegOverlay.render(ticks);
        this.rightLegOverlay.render(ticks);
        this.bodyOverlay.render(ticks);
        GL11.glPopMatrix();
        GL11.glDisable(2884);
    }

    @Override
    public void animate(float forward, float haltDelta, float angle, float yaw, float pitch, float tick) {
        super.animate(forward, haltDelta, angle, yaw, pitch, tick);
        this.syncLayerAngles(this.leftLeg, this.leftLegOverlay);
        this.syncLayerAngles(this.rightLeg, this.rightLegOverlay);
        this.syncLayerAngles(this.leftArm, this.leftArmOverlay);
        this.syncLayerAngles(this.rightArm, this.rightArmOverlay);
        this.syncLayerAngles(this.body, this.bodyOverlay);
    }

    public void syncLayerAngles(ModelPart mainPart, ModelPart overlayPart) {
        overlayPart.rotationX = mainPart.rotationX;
        overlayPart.rotationY = mainPart.rotationY;
        overlayPart.rotationZ = mainPart.rotationZ;
        overlayPart.pivotX = mainPart.pivotX;
        overlayPart.pivotY = mainPart.pivotY;
        overlayPart.pivotZ = mainPart.pivotZ;
    }

    public static class SlimPlayerModel extends PlayerModel {
        public SlimPlayerModel(float scale) {
            super(scale, 0.0F);
            this.leftArmOverlay = new ModelPart(this, 48, 48);
            this.leftArmOverlay.setTextureSize(64, 64);
            this.leftArmOverlay.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F, false);
            this.leftArmOverlay.setPivots(5.0F, 2.5F, 0.0F);
            this.rightArmOverlay = new ModelPart(this, 40, 32);
            this.rightArmOverlay.setTextureSize(64, 64);
            this.rightArmOverlay.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F, false);
            this.rightArmOverlay.setPivots(-5.0F, 2.5F, 10.0F);
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.setTextureSize(64, 64);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale, false);
            this.leftArm.setPivots(5.0F, 2.5F, 0.0F);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.setTextureSize(64, 64);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale, false);
            this.rightArm.setPivots(-5.0F, 2.5F, 0.0F);
        }
    }
}
