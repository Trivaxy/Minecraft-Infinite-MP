package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class WolfModel extends Model {
    public ModelPart head = new ModelPart(this, 0, 0);
    public ModelPart body;
    public ModelPart frontLeftLeg;
    public ModelPart frontRightLeg;
    public ModelPart backLeftLeg;
    public ModelPart backRightLeg;
    public ModelPart tail;
    public ModelPart mane;

    public WolfModel(float scale) {
        this.head.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, scale, false);
        this.head.setPivots(-1.0F, 13.5F, -7.0F);
        this.body = new ModelPart(this, 18, 14);
        this.body.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, scale, false);
        this.body.setPivots(0.0F, 14.0F, 2.0F);
        this.mane = new ModelPart(this, 21, 0);
        this.mane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, scale, false);
        this.mane.setPivots(-1.0F, 14.0F, 2.0F);
        this.frontLeftLeg = new ModelPart(this, 0, 18);
        this.frontLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, scale, false);
        this.frontLeftLeg.setPivots(-2.5F, 16.0F, 7.0F);
        this.frontRightLeg = new ModelPart(this, 0, 18);
        this.frontRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, scale, false);
        this.frontRightLeg.setPivots(0.5F, 16.0F, 7.0F);
        this.backLeftLeg = new ModelPart(this, 0, 18);
        this.backLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, scale, false);
        this.backLeftLeg.setPivots(-2.5F, 16.0F, -4.0F);
        this.backRightLeg = new ModelPart(this, 0, 18);
        this.backRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, scale, false);
        this.backRightLeg.setPivots(0.5F, 16.0F, -4.0F);
        this.tail = new ModelPart(this, 9, 18);
        this.tail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, scale, false);
        this.tail.setPivots(-1.0F, 12.0F, 8.0F);
        this.head.setTextureOffset(16, 14);
        this.head.addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, scale, false);
        this.head.setTextureOffset(16, 14);
        this.head.addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, scale, false);
        this.head.setTextureOffset(0, 10);
        this.head.addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, scale, false);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        if (this.isChild) {
            float scaleFactor = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 5.0F * tick, 2.0F * tick);
            this.renderWithRotation(this.head, tick);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / scaleFactor, 1.0F / scaleFactor, 1.0F / scaleFactor);
            GL11.glTranslatef(0.0F, 24.0F * tick, 0.0F);
            this.body.render(tick);
            this.frontLeftLeg.render(tick);
            this.frontRightLeg.render(tick);
            this.backLeftLeg.render(tick);
            this.backRightLeg.render(tick);
            this.renderWithRotation(this.tail, tick);
            this.mane.render(tick);
            GL11.glPopMatrix();
        } else {
            this.renderWithRotation(this.head, tick);
            this.body.render(tick);
            this.frontLeftLeg.render(tick);
            this.frontRightLeg.render(tick);
            this.backLeftLeg.render(tick);
            this.backRightLeg.render(tick);
            this.renderWithRotation(this.tail, tick);
            this.mane.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.tail.rotationX = angle;
    }

    @Override
    public void animateSpecial(Mob entity, float swing, float anim, float angle) {
        Wolf wolf = (Wolf)entity;
        if (wolf.isAngry()) {
            this.tail.rotationY = 0.0F;
        } else {
            this.tail.rotationY = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        }

        if (wolf.isSitting()) {
            this.mane.setPivots(-1.0F, 16.0F, -3.0F);
            this.mane.rotationX = (float) (Math.PI * 2.0 / 5.0);
            this.mane.rotationY = 0.0F;
            this.body.setPivots(0.0F, 18.0F, 0.0F);
            this.body.rotationX = (float) (Math.PI / 4);
            this.tail.setPivots(-1.0F, 21.0F, 6.0F);
            this.frontLeftLeg.setPivots(-2.5F, 22.0F, 2.0F);
            this.frontLeftLeg.rotationX = (float) (Math.PI * 3.0 / 2.0);
            this.frontRightLeg.setPivots(0.5F, 22.0F, 2.0F);
            this.frontRightLeg.rotationX = (float) (Math.PI * 3.0 / 2.0);
            this.backLeftLeg.rotationX = 5.811947F;
            this.backLeftLeg.setPivots(-2.49F, 17.0F, -4.0F);
            this.backRightLeg.rotationX = 5.811947F;
            this.backRightLeg.setPivots(0.51F, 17.0F, -4.0F);
        } else {
            this.body.setPivots(0.0F, 14.0F, 2.0F);
            this.body.rotationX = (float) (Math.PI / 2);
            this.mane.setPivots(-1.0F, 14.0F, -3.0F);
            this.mane.rotationX = this.body.rotationX;
            this.tail.setPivots(-1.0F, 12.0F, 8.0F);
            this.frontLeftLeg.setPivots(-2.5F, 16.0F, 7.0F);
            this.frontRightLeg.setPivots(0.5F, 16.0F, 7.0F);
            this.backLeftLeg.setPivots(-2.5F, 16.0F, -4.0F);
            this.backRightLeg.setPivots(0.5F, 16.0F, -4.0F);
            this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
            this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
            this.backLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
            this.backRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        }

        this.head.rotationZ = wolf.getHeadTilt(angle) + wolf.getShakeAngle(angle, 0.0F);
        this.mane.rotationZ = wolf.getShakeAngle(angle, -0.08F);
        this.body.rotationZ = wolf.getShakeAngle(angle, -0.16F);
        this.tail.rotationZ = wolf.getShakeAngle(angle, -0.2F);
        if (wolf.getWolfShaking()) {
            float color = wolf.getBrightness(angle) * wolf.getShading(angle);
            GL11.glColor3f(color, color, color);
        }
    }

    public void renderWithRotation(ModelPart model, float deltaTime) {
        if (model.render) {
            if (!model.compiled) {
                model.compileDisplayList(deltaTime);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(model.pivotX * deltaTime, model.pivotY * deltaTime, model.pivotZ * deltaTime);
            if (model.rotationY != 0.0F) {
                GL11.glRotatef(model.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (model.rotationX != 0.0F) {
                GL11.glRotatef(model.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (model.rotationZ != 0.0F) {
                GL11.glRotatef(model.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GL11.glCallList(model.displayList);
            GL11.glPopMatrix();
        }
    }
}
