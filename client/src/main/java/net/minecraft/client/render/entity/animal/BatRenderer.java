package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.avian.BatModel;
import net.minecraft.game.entity.mob.animal.flying.Bat;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BatRenderer extends MobRenderer<Bat> {
    public BatRenderer() {
        super(new BatModel(), 0.25F);
    }

    protected void rotate(Bat bat, float x, float y, float z) {
        if (!bat.isAsleep()) {
            GL11.glTranslatef(0.0F, MathHelper.cos(x * 0.3F) * 0.1F, 0.0F);
        } else {
            GL11.glTranslatef(0.0F, -0.1F, 0.0F);
        }

        super.rotate(bat, x, y, z);
    }

    protected void preRender(Bat bat, float tick) {
        GL11.glScalef(0.35F, 0.35F, 0.35F);
    }
}
