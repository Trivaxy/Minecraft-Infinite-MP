package net.minecraft.client.render.entity.model.bipedal;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WitchModel extends Model {
    private ModelPart head;
    private ModelPart body;
    private ModelPart arms;
    private ModelPart rightLeg;
    private ModelPart leftLeg;
    private ModelPart nose;
    private ModelPart mole;
    private ModelPart hat;

    public WitchModel(float scale) {
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.head = new ModelPart(this);
        this.head.setPivots(0.0F, 0.0F, 0.0F);
        this.head.setTextureOffset(0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale, false);
        this.nose = new ModelPart(this);
        this.nose.setPivots(0.0F, -2.0F, 0.0F);
        this.nose.setTextureOffset(24, 0);
        this.nose.addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, scale, false);
        this.head.addChild(this.nose);
        this.body = new ModelPart(this);
        this.body.setPivots(0.0F, 0.0F, 0.0F);
        this.body.setTextureOffset(16, 20);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, scale, false);
        this.body.setTextureOffset(0, 38);
        this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, scale + 0.5F, false);
        this.arms = new ModelPart(this);
        this.arms.setPivots(0.0F, 2.0F, 0.0F);
        this.arms.setTextureOffset(44, 22);
        this.arms.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, scale, false);
        this.arms.setTextureOffset(44, 22);
        this.arms.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, scale, false);
        this.arms.setTextureOffset(40, 38);
        this.arms.addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, scale, false);
        this.rightLeg = new ModelPart(this, 0, 22);
        this.rightLeg.setPivots(-2.0F, 12.0F, 0.0F);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, false);
        this.leftLeg = new ModelPart(this, 0, 22);
        this.leftLeg.setPivots(2.0F, 12.0F, 0.0F);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale, true);
        this.mole = new ModelPart(this);
        this.mole.setTextureSize(64, 128);
        this.mole.setPivots(0.0F, -2.0F, 0.0F);
        this.mole.setTextureOffset(0, 0);
        this.mole.addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F, false);
        this.nose.addChild(this.mole);
        this.hat = new ModelPart(this);
        this.hat.setTextureSize(64, 128);
        this.hat.setPivots(-5.0F, -8.03125F, -5.0F);
        this.hat.setTextureOffset(0, 64);
        this.hat.addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.0F, false);
        this.head.addChild(this.hat);
        ModelPart hatBase = new ModelPart(this);
        hatBase.setTextureSize(64, 128);
        hatBase.setPivots(1.75F, -4.0F, 2.0F);
        hatBase.setTextureOffset(0, 76);
        hatBase.addBox(0.0F, 0.0F, 0.0F, 7, 4, 7, 0.0F, false);
        hatBase.rotationX = -0.05235988F;
        hatBase.rotationZ = 0.02617994F;
        this.hat.addChild(hatBase);
        ModelPart hatMain = new ModelPart(this);
        hatMain.setTextureSize(64, 128);
        hatMain.setPivots(1.75F, -4.0F, 2.0F);
        hatMain.setTextureOffset(0, 87);
        hatMain.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F, false);
        hatMain.rotationX = -0.10471976F;
        hatMain.rotationZ = 0.05235988F;
        hatBase.addChild(hatMain);
        ModelPart hatTop = new ModelPart(this);
        hatTop.setTextureSize(64, 128);
        hatTop.setPivots(1.75F, -2.0F, 2.0F);
        hatTop.setTextureOffset(0, 95);
        hatTop.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F, false);
        hatTop.rotationX = (float) (-Math.PI / 15);
        hatTop.rotationZ = 0.10471976F;
        hatMain.addChild(hatTop);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float rot, float yaw, float pitch, float tick) {
        this.animate(swing, anim, rot, yaw, pitch, tick);
        this.head.render(tick);
        this.body.render(tick);
        this.rightLeg.render(tick);
        this.leftLeg.render(tick);
        this.arms.render(tick);
    }

    @Override
    public void animate(float swing, float anim, float rot, float yaw, float pitch, float tick) {
        this.head.rotationY = yaw / (180.0F / (float)Math.PI);
        this.head.rotationX = pitch / (180.0F / (float)Math.PI);
        this.arms.pivotY = 3.0F;
        this.arms.pivotZ = -1.0F;
        this.arms.rotationX = -0.75F;
        this.rightLeg.rotationX = MathHelper.cos(swing * 0.6662F) * 1.4F * anim * 0.5F;
        this.leftLeg.rotationX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * anim * 0.5F;
        this.rightLeg.rotationY = 0.0F;
        this.leftLeg.rotationY = 0.0F;
        this.nose.pivotX = this.nose.pivotZ = 0.0F;
        this.nose.pivotY = -2.0F;
    }
}
