package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.arachnid.SpiderModel;
import net.minecraft.game.entity.mob.monster.arachnid.Spider;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class SpiderRenderer extends MobRenderer<Spider> {
    public SpiderRenderer() {
        super(new SpiderModel(), 1.0F);
        this.setOverlay(this.mainModel);
    }

    protected float maxRotation(Spider entitySpider1) {
        return 180.0F;
    }

    protected boolean renderOverlay(Spider entity, int layer, float tick) {
        if (layer != 0) {
            return false;
        }

        if (entity.type != 0) {
            this.loadTexture("/mob/spider/spider_eyes.png");
            GL11.glEnable(3042);
            GL11.glDisable(3008);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
        }

        return true;
    }
}
