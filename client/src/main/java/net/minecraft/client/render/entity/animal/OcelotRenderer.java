package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import org.lwjgl.opengl.GL11;

public class OcelotRenderer extends MobRenderer<Ocelot> {
    public OcelotRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected void preRender(Ocelot ocelot, float tick) {
        super.preRender(ocelot, tick);
        if (ocelot.isTamed()) {
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
    }
}
