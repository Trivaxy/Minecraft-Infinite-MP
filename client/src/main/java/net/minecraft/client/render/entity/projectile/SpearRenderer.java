package net.minecraft.client.render.entity.projectile;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.arrow.Trident;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public final class SpearRenderer extends EntityRenderer<Trident> {
    public final void render(Trident entity, double x, double y, double z, float yaw, float pitch) {
        this.loadTexture("/entity/spears.png");
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(entity.prevYaw + (entity.yaw - entity.prevYaw) * pitch - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevPitch + (entity.pitch - entity.prevPitch) * pitch, 0.0F, 0.0F, 1.0F);
        Tessellator tess = Tessellator.instance;
        GL11.glEnable(2977);
        if ((x = entity.shakeFactor - pitch) > 0.0) {
            GL11.glRotated(-MathHelper.sin((float)x) * x, 0.0, 0.0, 1.0);
        }

        float vert1 = (0 + (entity.id - 400) * 5) / 64.0F;
        float vert2 = (5 + (entity.id - 400) * 5) / 64.0F;
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(0.05625F, 0.05625F, 0.05625F);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(0.05625F, 0.0F, 0.0F);

        for (int side = 0; side < 4; side++) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, 0.05625F);
            tess.begin();
            tess.addTexturedVertex(-16.0, -2.0, 0.0, 0.0F, vert1);
            tess.addTexturedVertex(16.0, -2.0, 0.0, 0.5F, vert1);
            tess.addTexturedVertex(16.0, 2.0, 0.0, 0.5F, vert2);
            tess.addTexturedVertex(-16.0, 2.0, 0.0, 0.0F, vert2);
            tess.draw();
        }

        GL11.glDisable(2977);
        GL11.glPopMatrix();
    }
}
