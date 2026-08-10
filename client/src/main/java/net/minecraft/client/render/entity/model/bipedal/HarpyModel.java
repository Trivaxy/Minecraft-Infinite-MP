package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class HarpyModel extends Model {
    private ModelPart head = new ModelPart(this, 0, 0);
    private ModelPart body;
    private ModelPart legRight;
    private ModelPart legLeft;
    private ModelPart rightWing;
    private ModelPart leftWing;
    private ModelPart outerRightWing;
    private ModelPart outerLeftWing;

    public HarpyModel() {
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.head.setTextureSize(64, 64);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false);
        this.body = new ModelPart(this, 0, 48);
        this.body.setTextureSize(64, 64);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false);
        this.legRight = new ModelPart(this, 24, 48);
        this.legRight.setTextureSize(64, 64);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false);
        this.legRight.setPivots(-2.0F, 12.0F, 0.0F);
        this.legLeft = new ModelPart(this, 24, 48);
        this.legLeft.setTextureSize(64, 64);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true);
        this.legLeft.setPivots(2.0F, 12.0F, 0.0F);
        this.rightWing = new ModelPart(this, 42, 0);
        this.rightWing.setPivots(0.0F, 0.0F, 0.0F);
        this.rightWing.setTextureSize(64, 64);
        this.rightWing.addBox(-12.0F, 0.0F, 1.5F, 10, 16, 1, 0.0F, false);
        this.outerRightWing = new ModelPart(this, 24, 16);
        this.outerRightWing.setTextureSize(64, 64);
        this.outerRightWing.setPivots(-12.0F, 3.0F, 1.5F);
        this.outerRightWing.addBox(-8.0F, -2.0F, 0.0F, 8, 12, 1, 0.0F, false);
        this.leftWing = new ModelPart(this, 42, 0);
        this.leftWing.setPivots(0.0F, 0.0F, 0.0F);
        this.leftWing.setTextureSize(64, 64);
        this.leftWing.addBox(2.0F, 0.0F, 1.5F, 10, 16, 1, 0.0F, true);
        this.outerLeftWing = new ModelPart(this, 24, 32);
        this.outerLeftWing.setTextureSize(64, 64);
        this.outerLeftWing.setPivots(12.0F, 3.0F, 1.5F);
        this.outerLeftWing.addBox(0.0F, -2.0F, 0.0F, 8, 12, 1, 0.0F, false);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.outerRightWing);
        this.leftWing.addChild(this.outerLeftWing);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float ticks) {
        this.animate(swing, anim, angle, yaw, pitch, ticks);
        this.head.render(ticks);
        this.body.render(ticks);
        this.legRight.render(ticks);
        this.legLeft.render(ticks);
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.legRight.rotationX = this.legLeft.rotationX = (float) (Math.PI / 4) + MathHelper.cos(angle * 0.1F) * 0.15F;
        this.legRight.rotationY = this.legLeft.rotationY = 0.0F;
        this.rightWing.rotationY = MathHelper.cos(angle) * (float) Math.PI * 0.25F;
        this.leftWing.rotationY = -this.rightWing.rotationY;
        this.outerRightWing.rotationY = this.rightWing.rotationY * 0.5F;
        this.outerLeftWing.rotationY = -this.rightWing.rotationY * 0.5F;
    }
}
