package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.monster.Golem;
import org.lwjgl.opengl.GL11;

public class GolemRenderer extends MobRenderer<Golem> {
    private float scale;

    public GolemRenderer(Model model, float shadow, float scale) {
        super(model, shadow);
        this.scale = scale;
    }

    protected void preRender(Golem entity, float tick) {
        GL11.glScalef(this.scale, this.scale, this.scale);
    }

    protected void rotate(Golem golem, float x, float y, float z) {
        super.rotate(golem, x, y, z);
        if (golem.limbYaw >= 0.01) {
            float maxOffset = 13.0F;
            float swingAmount = golem.limbSwing - golem.limbYaw * (1.0F - z) + 6.0F;
            float finalRotation = (Math.abs(swingAmount % maxOffset - maxOffset * 0.5F) - maxOffset * 0.25F) / (maxOffset * 0.25F);
            GL11.glRotatef(6.5F * finalRotation, 0.0F, 0.0F, 1.0F);
        }
    }
}
