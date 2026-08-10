package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class OcelotModel extends Model {
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart head;
    private final ModelPart headNose;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart tailStart;
    private final ModelPart tailMain;
    private final ModelPart tailEnd;
    private int state = 1;

    public OcelotModel() {
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-2.5F, -2.0F, -3.0F, 5, 4, 5, 0.0F, false);
        this.head.setPivots(0.0F, 15.0F, -9.0F);
        this.headNose = new ModelPart(this, 0, 24);
        this.headNose.addBox(-1.5F, 0.0F, -4.0F, 3, 2, 2, 0.0F, false);
        this.headNose.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.headNose);
        this.leftEar = new ModelPart(this, 0, 10);
        this.leftEar.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 2, 0.0F, false);
        this.leftEar.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.leftEar);
        this.rightEar = new ModelPart(this, 6, 10);
        this.rightEar.addBox(1.0F, -3.0F, 0.0F, 1, 1, 2, 0.0F, false);
        this.rightEar.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.rightEar);
        this.tailEnd = new ModelPart(this, 20, 0);
        this.tailEnd.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F, false);
        this.tailEnd.setPivots(0.0F, 12.0F, -10.0F);
        this.tailStart = new ModelPart(this, 0, 15);
        this.tailStart.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, 0.0F, false);
        this.tailStart.rotationX = 0.9F;
        this.tailStart.setPivots(0.0F, 15.0F, 8.0F);
        this.tailMain = new ModelPart(this, 4, 15);
        this.tailMain.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, 0.0F, false);
        this.tailMain.setPivots(0.0F, 20.0F, 14.0F);
        this.frontLeftLeg = new ModelPart(this, 8, 13);
        this.frontLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, 0.0F, false);
        this.frontLeftLeg.setPivots(1.1F, 18.0F, 5.0F);
        this.frontRightLeg = new ModelPart(this, 8, 13);
        this.frontRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, 0.0F, false);
        this.frontRightLeg.setPivots(-1.1F, 18.0F, 5.0F);
        this.backLeftLeg = new ModelPart(this, 40, 0);
        this.backLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F, false);
        this.backLeftLeg.setPivots(1.2F, 13.8F, -5.0F);
        this.backRightLeg = new ModelPart(this, 40, 0);
        this.backRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F, false);
        this.backRightLeg.setPivots(-1.2F, 13.8F, -5.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        if (this.isChild) {
            float scaleOffset = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / scaleOffset, 1.5F / scaleOffset, 1.5F / scaleOffset);
            GL11.glTranslatef(0.0F, 10.0F * tick, 4.0F * tick);
            this.head.render(tick);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / scaleOffset, 1.0F / scaleOffset, 1.0F / scaleOffset);
            GL11.glTranslatef(0.0F, 24.0F * tick, 0.0F);
            this.tailEnd.render(tick);
            this.frontLeftLeg.render(tick);
            this.frontRightLeg.render(tick);
            this.backLeftLeg.render(tick);
            this.backRightLeg.render(tick);
            this.tailStart.render(tick);
            this.tailMain.render(tick);
            GL11.glPopMatrix();
        } else {
            this.head.render(tick);
            this.tailEnd.render(tick);
            this.tailStart.render(tick);
            this.tailMain.render(tick);
            this.frontLeftLeg.render(tick);
            this.frontRightLeg.render(tick);
            this.backLeftLeg.render(tick);
            this.backRightLeg.render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        if (this.state != 3) {
            this.tailEnd.rotationX = (float) (Math.PI / 2);
            if (this.state == 2) {
                this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
                this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + 0.3F) * 1.0F * anim;
                this.backLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI + 0.3F) * 1.0F * anim;
                this.backRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.0F * anim;
                this.tailMain.rotationX = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(swing) * anim;
            } else {
                this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
                this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.0F * anim;
                this.backLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.0F * anim;
                this.backRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.0F * anim;
                if (this.state == 1) {
                    this.tailMain.rotationX = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(swing) * anim;
                } else {
                    this.tailMain.rotationX = 1.7278761F + 0.47123894F * MathHelper.cos(swing) * anim;
                }
            }
        }
    }

    @Override
    public void animateSpecial(Mob animal, float swing, float animSpd, float angle) {
        Ocelot ocelot = (Ocelot)animal;
        this.tailEnd.pivotY = 12.0F;
        this.tailEnd.pivotZ = -10.0F;
        this.head.pivotY = 15.0F;
        this.head.pivotZ = -9.0F;
        this.tailStart.pivotY = 15.0F;
        this.tailStart.pivotZ = 8.0F;
        this.tailMain.pivotY = 20.0F;
        this.tailMain.pivotZ = 14.0F;
        this.backLeftLeg.pivotY = this.backRightLeg.pivotY = 13.8F;
        this.backLeftLeg.pivotZ = this.backRightLeg.pivotZ = -5.0F;
        this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY = 18.0F;
        this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ = 5.0F;
        this.tailStart.rotationX = 0.9F;
        if (ocelot.sneaking) {
            this.tailEnd.pivotY++;
            this.head.pivotY += 2.0F;
            this.tailStart.pivotY++;
            this.tailMain.pivotY += -4.0F;
            this.tailMain.pivotZ += 2.0F;
            this.tailStart.rotationX = (float) (Math.PI / 2);
            this.tailMain.rotationX = (float) (Math.PI / 2);
            this.state = 0;
        } else if (ocelot.isSprinting()) {
            this.tailMain.pivotY = this.tailStart.pivotY;
            this.tailMain.pivotZ += 2.0F;
            this.tailStart.rotationX = (float) (Math.PI / 2);
            this.tailMain.rotationX = (float) (Math.PI / 2);
            this.state = 2;
        } else if (ocelot.isSitting()) {
            this.tailEnd.rotationX = (float) (Math.PI / 4);
            this.tailEnd.pivotY += -4.0F;
            this.tailEnd.pivotZ += 5.0F;
            this.head.pivotY += -3.3F;
            this.head.pivotZ++;
            this.tailStart.pivotY += 8.0F;
            this.tailStart.pivotZ += -2.0F;
            this.tailMain.pivotY += 2.0F;
            this.tailMain.pivotZ += -0.8F;
            this.tailStart.rotationX = 1.7278761F;
            this.tailMain.rotationX = 2.670354F;
            this.backLeftLeg.rotationX = this.backRightLeg.rotationX = (float) (-Math.PI / 20);
            this.backLeftLeg.pivotY = this.backRightLeg.pivotY = 15.8F;
            this.backLeftLeg.pivotZ = this.backRightLeg.pivotZ = -7.0F;
            this.frontLeftLeg.rotationX = this.frontRightLeg.rotationX = (float) (-Math.PI / 2);
            this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY = 21.0F;
            this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ = 1.0F;
            this.state = 3;
        } else {
            this.state = 1;
        }
    }
}
