package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CreeperModel extends Model {
    public ModelPart head;
    public ModelPart headwear;
    public ModelPart body;
    public ModelPart frontLeftLeg;
    public ModelPart frontRightLeg;
    public ModelPart backLeftLeg;
    public ModelPart backRightLeg;

    public CreeperModel(float scale) {
        byte offset = 4;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.head.setPivots(0.0F, offset, 0.0F);
        this.headwear = new ModelPart(this, 32, 0);
        this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F, false);
        this.headwear.setPivots(0.0F, offset, 0.0F);
        this.body = new ModelPart(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale, false);
        this.body.setPivots(0.0F, offset, 0.0F);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale, false);
        this.frontLeftLeg.setPivots(-2.0F, 12 + offset, 4.0F);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale, false);
        this.frontRightLeg.setPivots(2.0F, 12 + offset, 4.0F);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale, false);
        this.backLeftLeg.setPivots(-2.0F, 12 + offset, -4.0F);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, scale, false);
        this.backRightLeg.setPivots(2.0F, 12 + offset, -4.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.frontLeftLeg.render(tick);
        this.frontRightLeg.render(tick);
        this.backLeftLeg.render(tick);
        this.backRightLeg.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        this.frontLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.frontRightLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.backLeftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.backRightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
    }
}
