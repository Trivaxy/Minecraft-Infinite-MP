package net.minecraft.client.render.entity.model.elemental;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.monster.Wisp;
import net.minecraft.util.math.MathHelper;

public class SlimeModel extends Model {
    private ModelPart body;
    private ModelPart rightEye;
    private ModelPart leftEye;
    private ModelPart nose;
    private int scale;

    public SlimeModel(int slimeSize, float scale) {
        this.scale = slimeSize;
        this.body = new ModelPart(this, 0, slimeSize);
        this.body.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8, scale, false);
        if (this.scale > 0) {
            this.body = new ModelPart(this, 0, slimeSize);
            this.body.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6, scale, false);
            this.rightEye = new ModelPart(this, 32, 0);
            this.rightEye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2, scale, false);
            this.leftEye = new ModelPart(this, 32, 4);
            this.leftEye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2, scale, false);
            this.nose = new ModelPart(this, 32, 8);
            this.nose.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1, scale, false);
        }
    }

    @Override
    public void render(Entity entity, float swing, float anim, float rotation, float yaw, float pitch, float tick) {
        this.animate(swing, anim, rotation, yaw, pitch, tick);
        this.body.render(tick);
        if (this.scale > 0) {
            this.rightEye.render(tick);
            this.leftEye.render(tick);
            this.nose.render(tick);
        }

        if (entity instanceof Wisp) {
            this.body.pivotY = MathHelper.cos(rotation * 0.1F) * 2.5F;
        }
    }
}
