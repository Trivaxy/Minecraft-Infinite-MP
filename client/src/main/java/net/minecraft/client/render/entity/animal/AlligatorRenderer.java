package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.Alligator;
import org.lwjgl.opengl.GL11;

public class AlligatorRenderer extends MobRenderer<Alligator> {
    public AlligatorRenderer(Model mainModel, float shadowSize) {
        super(mainModel, shadowSize);
    }

    protected void preRender(Alligator alligator, float tick) {
        if (alligator.type == 1) {
            GL11.glScalef(1.2F, 1.2F, 1.2F);
        }
    }
}
