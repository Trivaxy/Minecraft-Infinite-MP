package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.elemental.SlimeModel;
import net.minecraft.game.entity.mob.monster.Wisp;
import org.lwjgl.opengl.GL11;

public class WispRenderer extends MobRenderer<Wisp> {
    private SlimeModel overlayModel = new SlimeModel(0, 0.5F);

    public WispRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected void preRender(Wisp entity, float tick) {
        GL11.glEnable(2977);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);
    }

    protected boolean renderOverlay(Wisp wisp, int layer, float tick) {
        if (layer == 1) {
            float aliveTime = wisp.ticksExisted + tick;
            float offsetX = aliveTime * 0.01F;
            float offsetY = aliveTime * 0.01F;
            float glintStrength = 0.5F;
            this.loadTexture("/armor/power.png");
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glTranslatef(offsetX, offsetY, 0.0F);
            this.setOverlay(this.overlayModel);
            GL11.glMatrixMode(5888);
            GL11.glEnable(3042);
            GL11.glColor4f(glintStrength, glintStrength, glintStrength, 1.0F);
            GL11.glDisable(2896);
            GL11.glBlendFunc(1, 1);
            return true;
        }

        if (layer == 2) {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
        }

        return false;
    }
}
