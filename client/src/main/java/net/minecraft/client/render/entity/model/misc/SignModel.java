package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;

public class SignModel extends Model {
    public ModelPart board = new ModelPart(this, 0, 0);
    public ModelPart pole;
    public ModelPart legLeft;
    public ModelPart legRight;
    public ModelPart bokluk;

    public SignModel() {
        this.board.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F, false);
        this.pole = new ModelPart(this, 0, 14);
        this.pole.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F, false);
        this.bokluk = new ModelPart(this, 0, 14);
        this.bokluk.addBox(-1.0F, -24.0F, -1.0F, 2, 10, 2, 0.0F, false);
        this.legLeft = new ModelPart(this, 0, 14);
        this.legLeft.addBox(-8.0F, -24.0F, -1.0F, 2, 10, 2, 0.0F, false);
        this.legRight = new ModelPart(this, 0, 14);
        this.legRight.addBox(6.0F, -24.0F, -1.0F, 2, 10, 2, 0.0F, false);
    }

    public void renderModel() {
        this.board.render(0.0625F);
        this.pole.render(0.0625F);
        this.bokluk.render(0.0625F);
        this.legLeft.render(0.0625F);
        this.legRight.render(0.0625F);
    }
}
