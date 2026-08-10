package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.entity.player.Player;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BipedalModel extends Model {
    protected Entity entity;
    public ModelPart head;
    public ModelPart hair;
    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public boolean sneaking = false;
    public boolean crawling = false;
    public boolean inWater = false;
    public boolean heldItemLeft = false;
    public boolean heldItemRight = false;
    public boolean riding = false;
    public boolean aimedBow = false;
    public boolean leftHanded = false;
    public boolean blocking = false;
    public boolean blockingLeft = false;
    public boolean flying = false;

    public BipedalModel() {
        this(0.0F);
    }

    public BipedalModel(float scale) {
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.hair = new ModelPart(this, 32, 0);
        this.hair.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F, false);
        this.hair.setPivots(0.0F, 0.0F, 0.0F);
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightArm.setPivots(-5.0F, 2.0F, 0.0F);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale, true);
        this.leftArm.setPivots(5.0F, 2.0F, 0.0F);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.rightLeg.setPivots(-2.0F, 12.0F, 0.0F);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, true);
        this.leftLeg.setPivots(2.0F, 12.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float ticks) {
        this.entity = entity;
        this.animate(limbAngle, animationSpeed, angle, headYaw, headPitch, ticks);
        if (entity instanceof Player) {
            GL11.glEnable(2884);
            GL11.glCullFace(1029);
        }

        this.head.render(ticks);
        this.body.render(ticks);
        this.rightArm.render(ticks);
        this.leftArm.render(ticks);
        this.rightLeg.render(ticks);
        this.leftLeg.render(ticks);
        this.hair.render(ticks);
        GL11.glDisable(2884);
    }

    @Override
    public void animate(float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        if (this.entity instanceof Pigman && ((Pigman)this.entity).item != null) {
            this.head.rotationX = -headPitch / (180.0F / (float)Math.PI);
        } else {
            this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        }

        if (OptionValues.armFlailing) {
            this.rightArm.rotationX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 2.0F * animationSpeed;
            this.rightArm.rotationZ = (MathHelper.cos(limbAngle * 0.2312F) + 1.0F) * animationSpeed;
            this.leftArm.rotationX = MathHelper.cos(limbAngle * 0.6662F) * 2.0F * animationSpeed;
            this.leftArm.rotationZ = (MathHelper.cos(limbAngle * 0.2812F) - 1.0F) * animationSpeed;
        } else {
            this.rightArm.rotationX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 2.0F * animationSpeed * 0.5F;
            this.leftArm.rotationX = MathHelper.cos(limbAngle * 0.6662F) * 2.0F * animationSpeed * 0.5F;
            this.rightArm.rotationZ = 0.0F;
            this.leftArm.rotationZ = 0.0F;
        }

        this.rightLeg.rotationX = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * animationSpeed;
        this.leftLeg.rotationX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.rightArm.rotationZ = this.rightArm.rotationZ + (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
        this.leftArm.rotationZ = this.leftArm.rotationZ - (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
        this.rightArm.rotationX = this.rightArm.rotationX + MathHelper.sin(angle * 0.067F) * 0.05F;
        this.leftArm.rotationX = this.leftArm.rotationX - MathHelper.sin(angle * 0.067F) * 0.05F;
        this.rightArm.rotationY = 0.0F;
        this.leftArm.rotationY = 0.0F;
        this.swingArmAnimation();
        this.poseModel(angle);
        this.hair.pivotY = this.head.pivotY;
        this.hair.rotationY = this.head.rotationY;
        this.hair.rotationX = this.head.rotationX;
    }

    private void swingArmAnimation() {
        float swingProgress = this.leftHanded ? this.swingOff : this.swingMain;
        if (swingProgress > -9990.0F) {
            ModelPart mainArm = this.leftHanded ? this.leftArm : this.rightArm;
            float bodyRotationY = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F;
            this.body.rotationY = this.leftHanded ? -bodyRotationY : bodyRotationY;
            this.rightArm.pivotZ = MathHelper.sin(this.body.rotationY) * 5.0F;
            this.rightArm.pivotX = -MathHelper.cos(this.body.rotationY) * 5.0F;
            this.leftArm.pivotZ = -MathHelper.sin(this.body.rotationY) * 5.0F;
            this.leftArm.pivotX = MathHelper.cos(this.body.rotationY) * 5.0F;
            this.rightArm.rotationY = this.rightArm.rotationY + this.body.rotationY;
            this.leftArm.rotationY = this.leftArm.rotationY + this.body.rotationY;
            if (this.leftHanded) {
                this.rightArm.rotationX = this.rightArm.rotationX + this.body.rotationY;
            } else {
                this.leftArm.rotationX = this.leftArm.rotationX + this.body.rotationY;
            }

            swingProgress = 1.0F - swingProgress;
            swingProgress *= swingProgress * swingProgress;
            float swing = 1.0F - swingProgress;
            float swingAngleX = MathHelper.sin(swing * (float) Math.PI);
            float swingAngleY = MathHelper.sin(swingProgress * (float) Math.PI) * -(this.head.rotationX - 0.7F) * 0.75F;
            mainArm.rotationX -= swingAngleX * 1.2F + swingAngleY;
            mainArm.rotationY = mainArm.rotationY + this.body.rotationY * 2.0F;
            float zSwing = MathHelper.sin(swingProgress * (float) Math.PI) * (this.leftHanded ? -0.2F : -0.4F);
            mainArm.rotationZ = mainArm.rotationZ + (this.leftHanded ? -zSwing : zSwing);
        }
    }

    private void poseModel(float angle) {
        if (this.riding) {
            this.ridingPose();
        } else {
            this.rightLeg.rotationY = 0.0F;
            this.leftLeg.rotationY = 0.0F;
        }

        this.poseArms();
        if (this.sneaking && !this.inWater) {
            this.sneakPose();
        } else if (this.crawling) {
            this.crawlPose();
        } else if (this.flying) {
            this.flyPose();
        } else {
            this.uprightPose();
        }

        if (this.aimedBow) {
            this.aimPose(angle);
        }
    }

    private void poseArms() {
        if (this.heldItemLeft) {
            this.leftArm.rotationX = this.leftArm.rotationX * 0.5F - (float) (Math.PI / 10);
        }

        if (this.heldItemRight) {
            this.rightArm.rotationX = this.rightArm.rotationX * 0.5F - (float) (Math.PI / 10);
        }

        if (this.blocking) {
            if (this.blockingLeft) {
                this.leftArm.rotationX = this.leftArm.rotationX * 0.5F - (float) (Math.PI * 3.0 / 10.0);
            } else {
                this.rightArm.rotationX = this.rightArm.rotationX * 0.5F - (float) (Math.PI * 3.0 / 10.0);
            }
        }
    }

    private void ridingPose() {
        this.rightArm.rotationX += (float) (-Math.PI / 5);
        this.leftArm.rotationX += (float) (-Math.PI / 5);
        this.rightLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
        this.leftLeg.rotationX = (float) (-Math.PI * 2.0 / 5.0);
        this.rightLeg.rotationY = (float) (Math.PI / 10);
        this.leftLeg.rotationY = (float) (-Math.PI / 10);
    }

    protected void uprightPose() {
        this.body.rotationX = 0.0F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
        this.rightLeg.pivotY = 12.0F;
        this.leftLeg.pivotY = 12.0F;
        this.head.pivotY = 0.0F;
        this.hair.pivotY = 0.0F;
        this.body.pivotY = 0.0F;
        this.leftArm.pivotY = 2.0F;
        this.rightArm.pivotY = 2.0F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
    }

    private void flyPose() {
        this.body.rotationX = 1.5F;
        this.rightLeg.rotationX -= -20.5F;
        this.leftLeg.rotationX -= -20.5F;
        this.leftLeg.pivotY = 0.0F;
        this.rightLeg.pivotY = 0.0F;
        this.rightArm.rotationX -= 7.5F;
        this.leftArm.rotationX -= 7.5F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
        this.rightLeg.pivotZ = 9.0F;
        this.leftLeg.pivotZ = 9.0F;
    }

    private void crawlPose() {
        this.body.rotationX = 1.5F;
        this.rightLeg.rotationX -= -20.5F;
        this.leftLeg.rotationX -= -20.5F;
        this.rightArm.rotationX -= 7.5F;
        this.leftArm.rotationX -= 7.5F;
        this.rightLeg.pivotZ = 0.0F;
        this.leftLeg.pivotZ = 0.0F;
        this.body.pivotY = 21.0F;
        this.head.pivotY = 21.0F;
        this.leftLeg.pivotY = 22.0F;
        this.rightLeg.pivotY = 22.0F;
        this.leftArm.pivotY = 21.0F;
        this.rightArm.pivotY = 21.0F;
        this.rightLeg.pivotZ = 9.0F;
        this.leftLeg.pivotZ = 9.0F;
    }

    private void sneakPose() {
        this.body.rotationX = 0.5F;
        this.rightLeg.rotationX -= 0.0F;
        this.leftLeg.rotationX -= 0.0F;
        this.rightArm.rotationX += 0.4F;
        this.leftArm.rotationX += 0.4F;
        this.rightLeg.pivotZ = 4.0F;
        this.leftLeg.pivotZ = 4.0F;
        this.rightLeg.pivotY = 12.0F;
        this.leftLeg.pivotY = 12.0F;
        this.head.pivotY = 3.0F;
        this.body.pivotY = 3.0F;
        this.leftArm.pivotY = 4.5F;
        this.rightArm.pivotY = 4.5F;
    }

    private void aimPose(float angle) {
        this.rightArm.rotationZ = 0.0F;
        this.leftArm.rotationZ = 0.0F;
        this.rightArm.rotationY = -0.1F + this.head.rotationY;
        this.leftArm.rotationY = 0.1F + this.head.rotationY + 0.4F;
        this.rightArm.rotationX = (float) (-Math.PI / 2) + this.head.rotationX;
        this.leftArm.rotationX = (float) (-Math.PI / 2) + this.head.rotationX;
        this.rightArm.rotationZ = this.rightArm.rotationZ + (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
        this.leftArm.rotationZ = this.leftArm.rotationZ - (MathHelper.cos(angle * 0.09F) * 0.05F + 0.05F);
        this.rightArm.rotationX = this.rightArm.rotationX + MathHelper.sin(angle * 0.067F) * 0.05F;
        this.leftArm.rotationX = this.leftArm.rotationX - MathHelper.sin(angle * 0.067F) * 0.05F;
    }
}
