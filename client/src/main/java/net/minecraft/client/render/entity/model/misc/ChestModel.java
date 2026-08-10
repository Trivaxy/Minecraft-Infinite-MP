package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;

public class ChestModel extends Model {
    public ModelPart lid = new ModelPart(this, 0, 0);
    public ModelPart box;
    public ModelPart knob;

    public ChestModel() {
        this.lid.setTextureSize(64, 64);
        this.lid.addBox(0.0F, -5.0F, -14.0F, 16, 4, 16, 0.0F, false);
        this.lid.pivotX = 0.0F;
        this.lid.pivotY = 5.0F;
        this.lid.pivotZ = 14.0F;
        this.knob = new ModelPart(this, 0, 0);
        this.knob.setTextureSize(64, 64);
        this.knob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F, false);
        this.knob.pivotX = 8.0F;
        this.knob.pivotY = 5.0F;
        this.knob.pivotZ = 14.99F;
        this.box = new ModelPart(this, 0, 19);
        this.box.setTextureSize(64, 64);
        this.box.addBox(0.0F, 0.0F, 0.0F, 16, 12, 16, 0.0F, false);
        this.box.pivotY = 4.0F;
    }

    public void renderAll() {
        this.knob.rotationX = this.lid.rotationX;
        this.lid.render(0.0625F);
        this.knob.render(0.0625F);
        this.box.render(0.0625F);
    }

    public static class LongChest extends ChestModel {
        public LongChest() {
            this.lid = new ModelPart(this, 0, 0);
            this.lid.setTextureSize(128, 64);
            this.lid.addBox(0.0F, -5.0F, -14.0F, 32, 4, 16, 0.0F, false);
            this.lid.pivotX = 0.0F;
            this.lid.pivotY = 5.0F;
            this.lid.pivotZ = 14.0F;
            this.knob = new ModelPart(this, 0, 0);
            this.knob.setTextureSize(128, 64);
            this.knob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F, false);
            this.knob.pivotX = 16.0F;
            this.knob.pivotY = 5.0F;
            this.knob.pivotZ = 14.99F;
            this.box = new ModelPart(this, 0, 21);
            this.box.setTextureSize(128, 64);
            this.box.addBox(0.0F, 0.0F, 0.0F, 32, 12, 16, 0.0F, false);
            this.box.pivotY = 4.0F;
        }
    }
}
