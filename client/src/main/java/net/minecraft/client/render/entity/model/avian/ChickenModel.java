package net.minecraft.client.render.entity.model.avian;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ChickenModel extends Model {
    public ModelPart head;
    public ModelPart body;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart rightWing;
    public ModelPart leftWing;
    public ModelPart beak;
    public ModelPart chin;

    public ChickenModel() {
        byte pivotOffset = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F, false);
        this.head.setPivots(0.0F, -1 + pivotOffset, -4.0F);
        this.beak = new ModelPart(this, 14, 0);
        this.beak.setPivots(0.0F, 0.0F, -0.0F);
        this.head.addChild(this.beak);
        this.beak.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F, false);
        this.chin = new ModelPart(this, 14, 4);
        this.chin.setPivots(0.0F, 0.0F, -0.0F);
        this.head.addChild(this.chin);
        this.chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F, false);
        this.body = new ModelPart(this, 0, 9);
        this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F, false);
        this.body.setPivots(0.0F, 0 + pivotOffset, 0.0F);
        this.rightLeg = new ModelPart(this, 26, 0);
        this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false);
        this.rightLeg.setPivots(-2.0F, 3 + pivotOffset, 1.0F);
        this.leftLeg = new ModelPart(this, 26, 0);
        this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F, false);
        this.leftLeg.setPivots(1.0F, 3 + pivotOffset, 1.0F);
        this.rightWing = new ModelPart(this, 24, 13);
        this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, false);
        this.rightWing.setPivots(-4.0F, -3 + pivotOffset, 0.0F);
        this.leftWing = new ModelPart(this, 24, 13);
        this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F, false);
        this.leftWing.setPivots(4.0F, -3 + pivotOffset, 0.0F);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.animate(swing, anim, angle, headYaw, headPitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.rightLeg.render(tick);
        this.leftLeg.render(tick);
        this.rightWing.render(tick);
        this.leftWing.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float angle, float headYaw, float headPitch, float tick) {
        this.head.rotationX = -(headPitch / (180.0F / (float)Math.PI));
        this.head.rotationY = headYaw / (180.0F / (float)Math.PI);
        this.body.rotationX = (float) (Math.PI / 2);
        this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim;
        this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim;
        this.rightWing.rotationZ = angle;
        this.leftWing.rotationZ = -angle;
    }
}
