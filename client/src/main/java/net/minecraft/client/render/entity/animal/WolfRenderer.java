package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.quadruped.WolfModel;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import org.lwjgl.opengl.GL11;

public class WolfRenderer extends MobRenderer<Wolf> {
    private WolfModel armorModel = new WolfModel(0.5F);

    public WolfRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected float getDefaultAngle(Wolf wolf, float tick) {
        return wolf.setTailRotation();
    }

    protected boolean renderOverlay(Wolf doggo, int type, float tick) {
        if (doggo.armored) {
            this.loadTexture("/mob/wolf/wolf_armor.png");
            this.setOverlay(this.armorModel);
            int armorColor = doggo.color;
            float red = (armorColor >> 16 & 0xFF) / 255.0F;
            float green = (armorColor >> 8 & 0xFF) / 255.0F;
            float blue = (armorColor & 0xFF) / 255.0F;
            GL11.glColor3f(red, green, blue);
            return true;
        } else {
            return false;
        }
    }
}
