package net.minecraft.client.render.entity.projectile;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.projectile.Glowstick;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class GlowstickRenderer extends EntityRenderer<Glowstick> {
    public GlowstickRenderer() {
        this.setShadowSize(0.15F);
        this.shadowOpaque = 0.75F;
    }

    public void render(Glowstick glowstick, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        if (glowstick.inBlock) {
            switch (glowstick.getFacing()) {
                case 0:
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 1:
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                case 2:
                default:
                    break;
                case 3:
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 4:
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 5:
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }
        } else {
            GL11.glRotatef(glowstick.prevYaw + (glowstick.yaw - glowstick.prevYaw) * pitch, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(glowstick.prevPitch + (glowstick.pitch - glowstick.prevPitch) * pitch, 1.0F, 0.0F, 0.0F);
        }

        Icon icon = ItemList.glowStick.getIconFromMeta(0);
        this.loadTexture("/gui/items.png");
        Tessellator tess = Tessellator.instance;
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float widthDifference = 0.5F * (maxU - minU) / icon.getIconWidth();
        float heightDifference = 0.5F * (maxV - minV) / icon.getIconHeight();
        GL11.glEnable(32826);
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        float offset = 0.0625F;
        float offsetAddition = 0.021875F;
        float depth = 0.5F;
        if ((glowstick.getFacing() == 0 || glowstick.getFacing() == 1) && glowstick.inBlock) {
            GL11.glTranslatef(-depth, -depth, -0.1F);
        } else {
            GL11.glTranslatef(-depth, 0.0F, -0.1F);
        }

        GL11.glTranslatef(0.0F, 0.0F, offset + offsetAddition);
        tess.begin();
        tess.setNormals(0.0F, 0.0F, 1.0F);
        tess.addTexturedVertex(0.0, 0.0, 0.0, maxU, maxV);
        tess.addTexturedVertex(1.0, 0.0, 0.0, minU, maxV);
        tess.addTexturedVertex(1.0, 1.0, 0.0, minU, minV);
        tess.addTexturedVertex(0.0, 1.0, 0.0, maxU, minV);
        tess.setNormals(0.0F, 0.0F, -1.0F);
        tess.addTexturedVertex(0.0, 1.0, 0.0F - offset, maxU, minV);
        tess.addTexturedVertex(1.0, 1.0, 0.0F - offset, minU, minV);
        tess.addTexturedVertex(1.0, 0.0, 0.0F - offset, minU, maxV);
        tess.addTexturedVertex(0.0, 0.0, 0.0F - offset, maxU, maxV);
        tess.setNormals(-1.0F, 0.0F, 0.0F);

        for (int pixel = 0; pixel < icon.getIconWidth(); pixel++) {
            float start = (float)pixel / icon.getIconWidth();
            float end = maxU + (minU - maxU) * start - widthDifference;
            tess.addTexturedVertex(start, 0.0, 0.0F - offset, end, maxV);
            tess.addTexturedVertex(start, 0.0, 0.0, end, maxV);
            tess.addTexturedVertex(start, 1.0, 0.0, end, minV);
            tess.addTexturedVertex(start, 1.0, 0.0F - offset, end, minV);
        }

        tess.setNormals(1.0F, 0.0F, 0.0F);

        for (int var26 = 0; var26 < icon.getIconWidth(); var26++) {
            float start = (float)var26 / icon.getIconWidth();
            float end = maxU + (minU - maxU) * start - widthDifference;
            float finalOffset = start + 1.0F / icon.getIconWidth();
            tess.addTexturedVertex(finalOffset, 1.0, 0.0F - offset, end, minV);
            tess.addTexturedVertex(finalOffset, 1.0, 0.0, end, minV);
            tess.addTexturedVertex(finalOffset, 0.0, 0.0, end, maxV);
            tess.addTexturedVertex(finalOffset, 0.0, 0.0F - offset, end, maxV);
        }

        tess.setNormals(0.0F, 1.0F, 0.0F);

        for (int var27 = 0; var27 < icon.getIconHeight(); var27++) {
            float start = (float)var27 / icon.getIconHeight();
            float end = maxV + (minV - maxV) * start - heightDifference;
            float finalOffset = start + 1.0F / icon.getIconHeight();
            tess.addTexturedVertex(0.0, finalOffset, 0.0, maxU, end);
            tess.addTexturedVertex(1.0, finalOffset, 0.0, minU, end);
            tess.addTexturedVertex(1.0, finalOffset, 0.0F - offset, minU, end);
            tess.addTexturedVertex(0.0, finalOffset, 0.0F - offset, maxU, end);
        }

        tess.setNormals(0.0F, -1.0F, 0.0F);

        for (int var28 = 0; var28 < icon.getIconHeight(); var28++) {
            float start = (float)var28 / icon.getIconHeight();
            float end = maxV + (minV - maxV) * start - heightDifference;
            tess.addTexturedVertex(1.0, start, 0.0, minU, end);
            tess.addTexturedVertex(0.0, start, 0.0, maxU, end);
            tess.addTexturedVertex(0.0, start, 0.0F - offset, maxU, end);
            tess.addTexturedVertex(1.0, start, 0.0F - offset, minU, end);
        }

        tess.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    public void renderIcon(int minU, int minV, Icon icon, int maxU, int maxV) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.addTexturedVertex(minU, minV + maxV, 0.0, icon.getMinU(), icon.getMaxV());
        tess.addTexturedVertex(minU + maxU, minV + maxV, 0.0, icon.getMaxU(), icon.getMaxV());
        tess.addTexturedVertex(minU + maxU, minV, 0.0, icon.getMaxU(), icon.getMinV());
        tess.addTexturedVertex(minU, minV, 0.0, icon.getMinU(), icon.getMinV());
        tess.draw();
    }
}
