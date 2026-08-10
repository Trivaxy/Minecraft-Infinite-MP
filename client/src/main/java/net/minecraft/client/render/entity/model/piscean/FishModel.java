package net.minecraft.client.render.entity.model.piscean;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class FishModel extends Model {
    private final ModelPart body = new ModelPart(this, 0, 0);
    private final ModelPart head;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart topFin;
    private final ModelPart tailFin;

    public FishModel() {
        this.body.setPivots(0.0F, 24.0F, 0.0F);
        this.body.addBox(-1.0F, -4.0F, 1.0F, 2, 4, 7, 0.0F, false);
        this.head = new ModelPart(this, 11, 0);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.head.addBox(-1.0F, -4.0F, -2.0F, 2, 4, 3, 0.0F, false);
        this.body.addChild(this.head);
        this.topFin = new ModelPart(this, 0, 11);
        this.topFin.setPivots(0.0F, 0.0F, 0.0F);
        this.topFin.addBox(0.0F, -5.0F, 0.0F, 0, 1, 6, 0.0F, false);
        this.body.addChild(this.topFin);
        this.leftFin = new ModelPart(this, 12, 14);
        this.leftFin.setPivots(-1.0F, 23.0F, 0.0F);
        this.setRotation(this.leftFin, 0.0F, 0.0F, -0.6109F);
        this.leftFin.addBox(-2.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F, false);
        this.rightFin = new ModelPart(this, 12, 18);
        this.rightFin.setPivots(1.0F, 23.0F, 0.0F);
        this.setRotation(this.rightFin, 0.0F, 0.0F, 0.6109F);
        this.rightFin.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F, false);
        this.tailFin = new ModelPart(this, 26, 0);
        this.tailFin.setPivots(0.0F, 0.0F, 8.0F);
        this.tailFin.addBox(0.0F, -4.0F, -2.0F, 0, 4, 6, 0.0F, false);
        this.body.addChild(this.tailFin);
    }

    @Override
    public void render(Entity entity, float swingCrap, float animSpeed, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swingCrap, animSpeed, angle, headYaw, headPitch, tick);
        this.body.render(tick);
        this.rightFin.render(tick);
        this.leftFin.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.body.rotationY = this.rightFin.rotationY = this.leftFin.rotationY = MathHelper.cos(angle * 0.9F + 0.47123894F) * (float) Math.PI * 0.05F;
        this.tailFin.rotationY = -(MathHelper.cos(angle * 0.9F + 0.47123894F) * (float) Math.PI * 0.1F * Math.abs(2));
    }
}
