package net.minecraft.client.render.entity.model.arachnid;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public final class AntlionModel extends Model {
    private ModelPart head;
    private final ModelPart clawLeft;
    private final ModelPart pincerLeft;
    private final ModelPart clawRight;
    private final ModelPart pincerRight;
    private final ModelPart innerRing;
    private final ModelPart middleRing;
    private final ModelPart outerRing;
    private final ModelPart tail;
    private final ModelPart tailBase;
    private ModelPart neck;
    private ModelPart body;
    private ModelPart leftLeg1;
    private ModelPart rightLeg1;
    private ModelPart leftLeg2;
    private ModelPart rightLeg2;
    private ModelPart leftLeg3;
    private ModelPart rightLeg3;
    public boolean isBuried = false;

    public AntlionModel() {
        this.head = new ModelPart(this, 32, 4);
        this.head.setTextureSize(64, 48);
        this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F, false);
        this.head.setPivots(0.0F, 15.0F, -3.0F);
        this.clawLeft = new ModelPart(this, 49, 32);
        this.clawLeft.setTextureSize(64, 48);
        this.clawLeft.addBox(-2.0F, 1.0F, 1.0F, 3, 1, 1, 0.0F, false);
        this.clawLeft.setPivots(1.17F, 1.0F, -12.0F);
        this.head.addChild(this.clawLeft);
        this.pincerLeft = new ModelPart(this, 49, 32);
        this.pincerLeft.setTextureSize(64, 48);
        this.pincerLeft.addBox(2.0F, 1.0F, -12.0F, 2, 2, 4, 0.0F, false);
        this.head.addChild(this.pincerLeft);
        this.clawRight = new ModelPart(this, 49, 32);
        this.clawRight.setTextureSize(64, 48);
        this.clawRight.addBox(0.0F, 1.0F, 0.0F, 3, 1, 1, 0.0F, false);
        this.clawRight.setPivots(-4.0F, 1.0F, -12.0F);
        this.head.addChild(this.clawRight);
        this.pincerRight = new ModelPart(this, 49, 32);
        this.pincerRight.setTextureSize(64, 48);
        this.pincerRight.addBox(-4.0F, 1.0F, -12.0F, 2, 2, 4, 0.0F, false);
        this.head.addChild(this.pincerRight);
        this.neck = new ModelPart(this, 0, 0);
        this.neck.setTextureSize(64, 48);
        this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false);
        this.neck.setPivots(0.0F, 15.0F, 0.0F);
        this.body = new ModelPart(this, 0, 12);
        this.body.setTextureSize(64, 48);
        this.body.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F, false);
        this.body.setPivots(0.0F, 15.0F, 9.0F);
        this.innerRing = new ModelPart(this, 0, 32);
        this.innerRing.setTextureSize(64, 48);
        this.innerRing.addBox(-6.0F, -5.0F, -4.0F, 12, 10, 1, 0.0F, false);
        this.body.addChild(this.innerRing);
        this.middleRing = new ModelPart(this, 0, 32);
        this.middleRing.setTextureSize(64, 48);
        this.middleRing.addBox(-6.0F, -5.0F, -1.0F, 12, 10, 1, 0.0F, false);
        this.body.addChild(this.middleRing);
        this.outerRing = new ModelPart(this, 0, 32);
        this.outerRing.setTextureSize(64, 48);
        this.outerRing.addBox(-6.0F, -5.0F, 2.0F, 12, 10, 1, 0.0F, false);
        this.body.addChild(this.outerRing);
        this.tail = new ModelPart(this, 0, 32);
        this.tail.setTextureSize(64, 48);
        this.tail.addBox(-1.0F, -1.0F, 7.0F, 2, 2, 2, 0.0F, false);
        this.body.addChild(this.tail);
        this.tailBase = new ModelPart(this, 0, 32);
        this.tailBase.setTextureSize(64, 48);
        this.tailBase.addBox(-2.0F, -2.0F, 6.0F, 4, 4, 1, 0.0F, false);
        this.body.addChild(this.tailBase);
        this.leftLeg1 = new ModelPart(this, 18, 0);
        this.leftLeg1.setTextureSize(64, 48);
        this.leftLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg1.setPivots(-4.0F, 15.0F, 2.0F);
        this.rightLeg1 = new ModelPart(this, 18, 0);
        this.rightLeg1.setTextureSize(64, 48);
        this.rightLeg1.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg1.setPivots(4.0F, 15.0F, 2.0F);
        this.leftLeg2 = new ModelPart(this, 18, 0);
        this.leftLeg2.setTextureSize(64, 48);
        this.leftLeg2.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg2.setPivots(-4.0F, 15.0F, 0.0F);
        this.rightLeg2 = new ModelPart(this, 18, 0);
        this.rightLeg2.setTextureSize(64, 48);
        this.rightLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg2.setPivots(4.0F, 15.0F, 0.0F);
        this.leftLeg3 = new ModelPart(this, 18, 0);
        this.leftLeg3.setTextureSize(64, 48);
        this.leftLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg3.setPivots(-4.0F, 15.0F, -2.0F);
        this.rightLeg3 = new ModelPart(this, 18, 0);
        this.rightLeg3.setTextureSize(64, 48);
        this.rightLeg3.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg3.setPivots(4.0F, 15.0F, -2.0F);
    }

    @Override
    public void render(Entity entity, float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        this.animate(limbAngle, animationSpeed, angle, headYaw, headPitch, tick);
        this.head.render(tick);
        this.neck.render(tick);
        if (!this.isBuried) {
            this.body.render(tick);
            this.leftLeg1.render(tick);
            this.rightLeg1.render(tick);
            this.leftLeg2.render(tick);
            this.rightLeg2.render(tick);
            this.leftLeg3.render(tick);
            this.rightLeg3.render(tick);
        }
    }

    @Override
    public void animate(float limbAngle, float animationSpeed, float rot, float headYaw, float headPitch, float tick) {
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.head.rotationX = headPitch / (180.0F / (float)Math.PI);
        this.neck.rotationX = 0.0F;
        this.neck.pivotY = 15.0F;
        this.neck.pivotZ = 0.0F;
        this.leftLeg1.rotationZ = (float) (-Math.PI / 4);
        this.rightLeg1.rotationZ = (float) (Math.PI / 4);
        this.leftLeg2.rotationZ = -0.58119464F;
        this.rightLeg2.rotationZ = 0.58119464F;
        this.leftLeg3.rotationZ = -0.58119464F;
        this.rightLeg3.rotationZ = 0.58119464F;
        this.leftLeg1.rotationY = (float) (Math.PI / 8);
        this.rightLeg1.rotationY = (float) (-Math.PI / 8);
        this.leftLeg2.rotationY = -0.08119464F;
        this.rightLeg2.rotationY = 0.08119464F;
        this.leftLeg3.rotationY = (float) (-Math.PI / 8);
        this.rightLeg3.rotationY = (float) (Math.PI / 8);
        this.clawLeft.rotationY = (float) (Math.PI * 3.0 / 4.0);
        this.clawRight.rotationY = (float) (Math.PI / 4);
        if (this.isBuried) {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            this.head.rotationX = (float) (-Math.PI / 8);
            this.neck.rotationX = -0.6926991F;
            this.neck.pivotY = 17.0F;
            this.neck.pivotZ = -1.5F;
        }

        float rotX = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F) * 0.4F) * animationSpeed;
        float rotY = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * animationSpeed;
        float rotZ = -(MathHelper.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * animationSpeed;
        float rotX2 = Math.abs(MathHelper.sin(limbAngle * 0.6662F) * 0.4F) * animationSpeed;
        float rotY2 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * animationSpeed;
        float rotZ2 = Math.abs(MathHelper.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * animationSpeed;
        this.leftLeg1.rotationY += rotX;
        this.rightLeg1.rotationY -= rotX;
        this.leftLeg2.rotationY += rotY;
        this.rightLeg2.rotationY -= rotY;
        this.leftLeg3.rotationY += rotZ;
        this.rightLeg3.rotationY -= rotZ;
        this.leftLeg1.rotationZ += rotX2;
        this.rightLeg1.rotationZ -= rotX2;
        this.leftLeg2.rotationZ += rotY2;
        this.rightLeg2.rotationZ -= rotY2;
        this.leftLeg3.rotationZ += rotZ2;
        this.rightLeg3.rotationZ -= rotZ2;
    }
}
