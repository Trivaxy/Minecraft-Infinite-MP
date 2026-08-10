package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SnowmanModel extends Model {
    public ModelPart stickRight;
    public ModelPart stickLeft;
    public ModelPart head;
    public ModelPart bodyUpper;
    public ModelPart bodyLower;

    public SnowmanModel() {
        float yOffset = 4.0F;
        this.head = new ModelPart(this, 0, 0);
        this.head.setTextureSize(64, 64);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F, false);
        this.head.setPivots(0.0F, 0.0F + yOffset, 0.0F);
        this.bodyUpper = new ModelPart(this, 32, 0);
        this.bodyUpper.setTextureSize(64, 64);
        this.bodyUpper.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F, false);
        this.bodyUpper.setPivots(0.0F, 0.0F + yOffset + 9.0F - 7.0F, 0.0F);
        this.bodyLower = new ModelPart(this, 32, 0);
        this.bodyLower.setTextureSize(64, 64);
        this.bodyLower.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F, false);
        this.bodyLower.setPivots(0.0F, 0.0F + yOffset + 9.0F - 7.0F, 0.0F);
        this.stickRight = new ModelPart(this, 0, 16);
        this.stickRight.setTextureSize(64, 64);
        this.stickRight.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F, false);
        this.stickRight.setPivots(0.0F, 0.0F + yOffset + 9.0F, 0.0F);
        this.stickLeft = new ModelPart(this, 0, 36);
        this.stickLeft.setTextureSize(64, 64);
        this.stickLeft.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F, false);
        this.stickLeft.setPivots(0.0F, 0.0F + yOffset + 20.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);
        this.head.render(tick);
        this.stickRight.render(tick);
        this.stickLeft.render(tick);
        this.bodyUpper.render(tick);
        this.bodyLower.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        this.stickRight.rotationY = yaw / (180.0F / (float)Math.PI) * 0.25F;
        float pivotZ = MathHelper.sin(this.stickRight.rotationY);
        float pivotX = MathHelper.cos(this.stickRight.rotationY);
        this.bodyUpper.rotationZ = 1.0F;
        this.bodyLower.rotationZ = -1.0F;
        this.bodyUpper.rotationY = 0.0F + this.stickRight.rotationY;
        this.bodyLower.rotationY = (float) Math.PI + this.stickRight.rotationY;
        this.bodyUpper.pivotX = pivotX * 5.0F;
        this.bodyUpper.pivotZ = -pivotZ * 5.0F;
        this.bodyLower.pivotX = -pivotX * 5.0F;
        this.bodyLower.pivotZ = pivotZ * 5.0F;
    }
}
