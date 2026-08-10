package net.minecraft.client.render.entity.model.quadruped;

import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;

public class PigModel extends QuadrupedModel {
    public ModelPart snout = new ModelPart(this, 16, 16);

    public PigModel(float scaleFactor) {
        super(6, scaleFactor);
        this.snout.addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, 0.0F, false);
        this.snout.setPivots(0.0F, 12.0F, -6.0F);
    }

    @Override
    public final void render(Entity entity, float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        super.render(entity, limbAngle, animationSpeed, angle, headYaw, headPitch, tick);
        this.snout.render(tick);
    }

    @Override
    public final void animate(float limbAngle, float animationSpeed, float angle, float headYaw, float headPitch, float tick) {
        super.animate(limbAngle, animationSpeed, angle, headYaw, headPitch, tick);
        this.snout.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.snout.rotationX = headPitch / (180.0F / (float)Math.PI);
    }
}
