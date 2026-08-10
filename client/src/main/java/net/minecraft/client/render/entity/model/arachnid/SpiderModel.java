package net.minecraft.client.render.entity.model.arachnid;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SpiderModel extends Model {
    public ModelPart head;
    public ModelPart neck;
    public ModelPart body;
    public ModelPart rightLeg1;
    public ModelPart rightLeg2;
    public ModelPart rightLeg3;
    public ModelPart rightLeg4;
    public ModelPart leftLeg1;
    public ModelPart leftLeg2;
    public ModelPart leftLeg3;
    public ModelPart leftLeg4;

    public SpiderModel() {
        byte offset = 15;
        this.head = new ModelPart(this, 32, 4);
        this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F, false);
        this.head.setPivots(0.0F, 0 + offset, -3.0F);
        this.neck = new ModelPart(this, 0, 0);
        this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false);
        this.neck.setPivots(0.0F, offset, 0.0F);
        this.body = new ModelPart(this, 0, 12);
        this.body.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F, false);
        this.body.setPivots(0.0F, 0 + offset, 9.0F);
        this.rightLeg1 = new ModelPart(this, 18, 0);
        this.rightLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg1.setPivots(-4.0F, 0 + offset, 2.0F);
        this.rightLeg2 = new ModelPart(this, 18, 0);
        this.rightLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg2.setPivots(4.0F, 0 + offset, 2.0F);
        this.rightLeg3 = new ModelPart(this, 18, 0);
        this.rightLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg3.setPivots(-4.0F, 0 + offset, 1.0F);
        this.rightLeg4 = new ModelPart(this, 18, 0);
        this.rightLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.rightLeg4.setPivots(4.0F, 0 + offset, 1.0F);
        this.leftLeg1 = new ModelPart(this, 18, 0);
        this.leftLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg1.setPivots(-4.0F, 0 + offset, 0.0F);
        this.leftLeg2 = new ModelPart(this, 18, 0);
        this.leftLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg2.setPivots(4.0F, 0 + offset, 0.0F);
        this.leftLeg3 = new ModelPart(this, 18, 0);
        this.leftLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg3.setPivots(-4.0F, 0 + offset, -1.0F);
        this.leftLeg4 = new ModelPart(this, 18, 0);
        this.leftLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F, false);
        this.leftLeg4.setPivots(4.0F, 0 + offset, -1.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.head.render(tick);
        this.neck.render(tick);
        this.body.render(tick);
        this.rightLeg1.render(tick);
        this.rightLeg2.render(tick);
        this.rightLeg3.render(tick);
        this.rightLeg4.render(tick);
        this.leftLeg1.render(tick);
        this.leftLeg2.render(tick);
        this.leftLeg3.render(tick);
        this.leftLeg4.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        float rotation = (float) (Math.PI / 4);
        this.rightLeg1.rotationZ = -rotation;
        this.rightLeg2.rotationZ = rotation;
        this.rightLeg3.rotationZ = -rotation * 0.74F;
        this.rightLeg4.rotationZ = rotation * 0.74F;
        this.leftLeg1.rotationZ = -rotation * 0.74F;
        this.leftLeg2.rotationZ = rotation * 0.74F;
        this.leftLeg3.rotationZ = -rotation;
        this.leftLeg4.rotationZ = rotation;
        rotation = (float) (Math.PI / 8);
        this.rightLeg1.rotationY = rotation * 2.0F;
        this.rightLeg2.rotationY = -rotation * 2.0F;
        this.rightLeg3.rotationY = rotation * 1.0F;
        this.rightLeg4.rotationY = -rotation * 1.0F;
        this.leftLeg1.rotationY = -rotation * 1.0F;
        this.leftLeg2.rotationY = rotation * 1.0F;
        this.leftLeg3.rotationY = -rotation * 2.0F;
        this.leftLeg4.rotationY = rotation * 2.0F;
        float rotY1 = -(MathHelper.cos(swing * 0.6662F * 2.0F + 0.0F) * 0.4F) * anim;
        float rotY2 = -(MathHelper.cos(swing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * anim;
        float rotY3 = -(MathHelper.cos(swing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * anim;
        float rotY4 = -(MathHelper.cos(swing * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * anim;
        float rotZ1 = Math.abs(MathHelper.sin(swing * 0.6662F + 0.0F) * 0.4F) * anim;
        float rotZ2 = Math.abs(MathHelper.sin(swing * 0.6662F + (float) Math.PI) * 0.4F) * anim;
        float rotZ3 = Math.abs(MathHelper.sin(swing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * anim;
        float rotZ4 = Math.abs(MathHelper.sin(swing * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * anim;
        this.rightLeg1.rotationY += rotY1;
        this.rightLeg2.rotationY += -rotY1;
        this.rightLeg3.rotationY += rotY2;
        this.rightLeg4.rotationY += -rotY2;
        this.leftLeg1.rotationY += rotY3;
        this.leftLeg2.rotationY += -rotY3;
        this.leftLeg3.rotationY += rotY4;
        this.leftLeg4.rotationY += -rotY4;
        this.rightLeg1.rotationZ += rotZ1;
        this.rightLeg2.rotationZ += -rotZ1;
        this.rightLeg3.rotationZ += rotZ2;
        this.rightLeg4.rotationZ += -rotZ2;
        this.leftLeg1.rotationZ += rotZ3;
        this.leftLeg2.rotationZ += -rotZ3;
        this.leftLeg3.rotationZ += rotZ4;
        this.leftLeg4.rotationZ += -rotZ4;
    }
}
