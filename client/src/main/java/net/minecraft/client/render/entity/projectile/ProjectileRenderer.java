package net.minecraft.client.render.entity.projectile;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.projectile.Orb;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.ScytheHead;
import org.lwjgl.opengl.GL11;

public class ProjectileRenderer extends EntityRenderer<Projectile> {
    public void render(Projectile entity, double x, double y, double z, float yaw, float pitch) {
        Tessellator tess = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(32826);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.loadTexture("/gui/items.png");
        int dmg = entity.damage;
        if (entity.damage == 10) {
            GL11.glScalef(2.0F, 2.0F, 2.0F);
        } else if (entity instanceof Orb) {
            dmg = -1;
        } else if (entity instanceof ScytheHead) {
            dmg = -2;
        }

        float minU = entity.itemAssigned.getIconFromMeta(dmg).getMinU();
        float maxU = entity.itemAssigned.getIconFromMeta(dmg).getMaxU();
        float minV = entity.itemAssigned.getIconFromMeta(dmg).getMinV();
        float maxV = entity.itemAssigned.getIconFromMeta(dmg).getMaxV();
        GL11.glRotatef(180.0F - this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
        tess.begin();
        tess.setNormals(0.0F, 1.0F, 0.0F);
        tess.addTexturedVertex(-0.5, -0.25, 0.0, minU, maxV);
        tess.addTexturedVertex(0.5, -0.25, 0.0, maxU, maxV);
        tess.addTexturedVertex(0.5, 0.75, 0.0, maxU, minV);
        tess.addTexturedVertex(-0.5, 0.75, 0.0, minU, minV);
        tess.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}
