package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.Moose;
import org.lwjgl.opengl.GL11;

public class MooseRenderer extends MobRenderer<Moose> {
    public MooseRenderer(Model model, float shadow) {
        super(model, shadow);
    }

    protected void preRender(Moose moose, float tick) {
        GL11.glScalef(0.7F, 0.7F, 0.7F);
    }
}
