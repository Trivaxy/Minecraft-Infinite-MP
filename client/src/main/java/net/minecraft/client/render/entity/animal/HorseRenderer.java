package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.HorseModel;
import net.minecraft.game.entity.mob.animal.mounts.horse.Horse;
import org.lwjgl.opengl.GL11;

public class HorseRenderer extends MobRenderer<Horse> {
    private HorseModel armorModel = new HorseModel(0.5F, 64);

    public HorseRenderer(Model mainModel, float shadowSize) {
        super(mainModel, shadowSize);
    }

    protected void preRender(Horse hons, float tick) {
        GL11.glScalef(1.2F, 1.2F, 1.2F);
    }

    protected boolean renderOverlay(Horse hons, int type, float tick) {
        if (hons.armored) {
            this.loadTexture("/mob/horse/horse_armor.png");
            this.setOverlay(this.armorModel);
            return true;
        } else {
            return false;
        }
    }
}
