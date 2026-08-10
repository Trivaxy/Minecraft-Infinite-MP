package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.monster.slime.AquaCube;
import net.minecraft.game.entity.mob.monster.slime.Slime;
import org.lwjgl.opengl.GL11;

public class SlimeRenderer extends MobRenderer<Slime> {
    private Model scaledModel;

    public SlimeRenderer(Model model, Model innerModel, float shadow) {
        super(model, shadow);
        this.scaledModel = innerModel;
    }

    protected void preRender(Slime entity, float tick) {
        int scale = entity.getSlimeSize();
        float offset = (entity.offsetX + (entity.offsetY - entity.offsetX) * tick) / (scale * 0.5F + 1.0F);
        float size = 1.0F / (offset + 1.0F);
        GL11.glScalef(size * scale, 1.0F / size * scale, size * scale);
        this.setShadowSize(0.35F * entity.getSlimeSize());
    }

    protected boolean renderOverlay(Slime entity, int layer, float tick) {
        if (entity instanceof AquaCube) {
            AquaCube gel = (AquaCube)entity;
            if (gel.block != null) {
                this.loadTexture("/terrain.png");
                float scale = 0.375F;
                GL11.glPushMatrix();
                GL11.glScalef(scale, scale, scale);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -3.3F, 0.0F);
                BlocksRenderer.baseModel.renderInGUI(gel.block, gel.meta);
                GL11.glPopMatrix();
                this.loadTexture(entity.texture);
            }
        }

        if (layer == 0) {
            this.setOverlay(this.scaledModel);
            GL11.glEnable(2977);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            return true;
        }

        if (layer == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        return false;
    }
}
