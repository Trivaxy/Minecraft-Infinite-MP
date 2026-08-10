package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.aquatic.Jellyfish;
import org.lwjgl.opengl.GL11;

public class JellyfishRenderer extends MobRenderer<Jellyfish> {
    public JellyfishRenderer(Model model, float shadowSize) {
        super(model, shadowSize);
    }

    protected void preRender(Jellyfish entity, float tick) {
        super.preRender(entity, tick);
        GL11.glDisable(3042);
    }

    protected float getDefaultAngle(Jellyfish squid, float tick) {
        return squid.prevPulse + (squid.pulse - squid.prevPulse) * tick;
    }

    protected void rotate(Jellyfish squid, float x, float y, float z) {
        float tentacleYaw = squid.prevBodyPitch + (squid.bodyPitch - squid.prevBodyPitch) * z;
        float tentaclePitch = squid.prevBodyYaw + (squid.bodyYaw - squid.prevBodyYaw) * z;
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(180.0F - y, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(tentacleYaw, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(tentaclePitch, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.2F, 0.0F);
    }
}
