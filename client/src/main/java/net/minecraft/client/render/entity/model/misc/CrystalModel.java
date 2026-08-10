package net.minecraft.client.render.entity.model.misc;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.VoidCrystal;
import org.lwjgl.opengl.GL11;

public class CrystalModel extends Model {
    private ModelPart mainModel;
    private ModelPart crystal = new ModelPart(this);
    private ModelPart pedestal;

    public CrystalModel() {
        this.crystal.setTextureOffset(0, 0);
        this.crystal.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F, false);
        this.mainModel = new ModelPart(this);
        this.mainModel.setTextureOffset(32, 0);
        this.mainModel.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F, false);
        this.pedestal = new ModelPart(this);
        this.pedestal.setTextureOffset(0, 16);
        this.pedestal.addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12, 0.0F, false);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        if (!((VoidCrystal)entity).playerPlaced) {
            this.pedestal.render(tick);
        }

        GL11.glRotatef(anim, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.8F + angle, 0.0F);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        this.crystal.render(tick);
        float f8 = 0.875F;
        GL11.glScalef(f8, f8, f8);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(anim, 0.0F, 1.0F, 0.0F);
        this.crystal.render(tick);
        GL11.glScalef(f8, f8, f8);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(anim, 0.0F, 1.0F, 0.0F);
        this.mainModel.render(tick);
        GL11.glPopMatrix();
    }
}
