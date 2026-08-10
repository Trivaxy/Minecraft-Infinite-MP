package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.bipedal.CreeperModel;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class CreeperRenderer extends MobRenderer<Creeper> {
    private Model overlayModel = new CreeperModel(2.0F);

    public CreeperRenderer() {
        super(new CreeperModel(0.0F), 0.5F);
    }

    protected void preRender(Creeper creeper, float tick) {
        float flashTime = creeper.getFlashTimer(tick);
        float multiplier = 1.0F + MathHelper.sin(flashTime * 100.0F) * flashTime * 0.01F;
        flashTime = MathHelper.clamp(flashTime, 0.0F, 1.0F);
        flashTime *= flashTime;
        flashTime *= flashTime;
        float scaleHorizontal = (1.0F + flashTime * 0.4F) * multiplier;
        float scaleVertical = (1.0F + flashTime * 0.1F) / multiplier;
        GL11.glScalef(scaleHorizontal * 1.1F, scaleVertical * 1.1F, scaleHorizontal * 1.1F);
        GL11.glTranslatef(0.0F, 0.1F, 0.0F);
    }

    protected boolean renderOverlay(Creeper creeper, int layer, float tick) {
        if (creeper.getPowered()) {
            if (layer == 1) {
                float aliveTime = creeper.ticksExisted + tick;
                float x = aliveTime * 0.01F;
                float y = aliveTime * 0.01F;
                float brightness = 0.5F;
                this.loadTexture("/armor/power.png");
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glTranslatef(x, y, 0.0F);
                this.setOverlay(this.overlayModel);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                GL11.glColor4f(brightness, brightness, brightness, 1.0F);
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
        }

        return false;
    }

    protected int getColorMultiplier(Creeper creeper, float offset, float tick) {
        float flash = creeper.getFlashTimer(tick);
        if ((int)(flash * 10.0F) % 2 == 0) {
            return 0;
        }

        int opacity = (int)(flash * 0.2F * 255.0F);
        opacity = MathHelper.clamp(opacity, 0, 255);
        short red = 255;
        short green = 255;
        short blue = 255;
        return opacity << 24 | red << 16 | green << 8 | blue;
    }
}
