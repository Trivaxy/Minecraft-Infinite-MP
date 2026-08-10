package net.minecraft.client.render.entity.misc;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.hanging.EnumArt;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PaintingRenderer extends EntityRenderer<Painting> {
    public void render(Painting entity, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826);
        EnumArt art = entity.art;
        this.loadTexture("/art/" + art.title + ".png");
        float scale = 0.0625F;
        GL11.glScalef(scale, scale, scale);
        this.renderSide(entity, art.width, art.height, false);
        this.loadTexture("/art/back.png");
        this.renderSide(entity, art.width, art.height, true);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    private void renderSide(Painting painting, int x, int y, boolean renderBack) {
        Tessellator tess = Tessellator.instance;
        float startX = -x / 2.0F;
        float startY = -y / 2.0F;
        float offsetX = -0.5F;
        float offsetY = 0.5F;

        for (int pixelsX = 0; pixelsX < x / 16; pixelsX++) {
            for (int pixelsY = 0; pixelsY < y / 16; pixelsY++) {
                float minU = startX + (pixelsX + 1) * 16;
                float maxU = startX + pixelsX * 16;
                float minV = startY + (pixelsY + 1) * 16;
                float maxV = startY + pixelsY * 16;
                this.getOffset(painting, (minU + maxU) / 2.0F, (minV + maxV) / 2.0F);
                float u0 = (x - pixelsX * 16.0F) / x;
                float u1 = (x - (pixelsX + 1) * 16.0F) / x;
                float v0 = (y - pixelsY * 16.0F) / y;
                float v1 = (y - (pixelsY + 1) * 16.0F) / y;
                float u3 = 0.75F;
                float u4 = 0.8125F;
                float v3 = 0.001953125F;
                float v4 = 0.001953125F;
                float u5 = 0.7519531F;
                float u6 = 0.7519531F;
                float v5 = 0.0F;
                float v6 = 0.0625F;
                if (!renderBack) {
                    tess.begin();
                    tess.setNormals(0.0F, 0.0F, -1.0F);
                    tess.addTexturedVertex(minU, maxV, offsetX, u1, v0);
                    tess.addTexturedVertex(maxU, maxV, offsetX, u0, v0);
                    tess.addTexturedVertex(maxU, minV, offsetX, u0, v1);
                    tess.addTexturedVertex(minU, minV, offsetX, u1, v1);
                    tess.draw();
                } else {
                    tess.begin();
                    tess.setNormals(0.0F, 0.0F, 1.0F);
                    tess.addTexturedVertex(minU, minV, offsetY, 0.0F, 0.0F);
                    tess.addTexturedVertex(maxU, minV, offsetY, 1.0F, 0.0F);
                    tess.addTexturedVertex(maxU, maxV, offsetY, 1.0F, 1.0F);
                    tess.addTexturedVertex(minU, maxV, offsetY, 0.0F, 1.0F);
                    tess.setNormals(0.0F, -1.0F, 0.0F);
                    tess.addTexturedVertex(minU, minV, offsetX, u3, v3);
                    tess.addTexturedVertex(maxU, minV, offsetX, u4, v3);
                    tess.addTexturedVertex(maxU, minV, offsetY, u4, v4);
                    tess.addTexturedVertex(minU, minV, offsetY, u3, v4);
                    tess.setNormals(0.0F, 1.0F, 0.0F);
                    tess.addTexturedVertex(minU, maxV, offsetY, u3, v3);
                    tess.addTexturedVertex(maxU, maxV, offsetY, u4, v3);
                    tess.addTexturedVertex(maxU, maxV, offsetX, u4, v4);
                    tess.addTexturedVertex(minU, maxV, offsetX, u3, v4);
                    tess.setNormals(-1.0F, 0.0F, 0.0F);
                    tess.addTexturedVertex(minU, minV, offsetY, u6, v5);
                    tess.addTexturedVertex(minU, maxV, offsetY, u6, v6);
                    tess.addTexturedVertex(minU, maxV, offsetX, u5, v6);
                    tess.addTexturedVertex(minU, minV, offsetX, u5, v5);
                    tess.setNormals(1.0F, 0.0F, 0.0F);
                    tess.addTexturedVertex(maxU, minV, offsetX, u6, v5);
                    tess.addTexturedVertex(maxU, maxV, offsetX, u6, v6);
                    tess.addTexturedVertex(maxU, maxV, offsetY, u5, v6);
                    tess.addTexturedVertex(maxU, minV, offsetY, u5, v5);
                    tess.draw();
                }
            }
        }
    }

    private void getOffset(Painting painting, float horizontalOffset, float verticalOffset) {
        int x = MathHelper.floor_double(painting.posX);
        int y = MathHelper.floor_double(painting.posY + verticalOffset / 16.0F);
        int z = MathHelper.floor_double(painting.posZ);
        if (painting.direction == 0) {
            x = MathHelper.floor_double(painting.posX + horizontalOffset / 16.0F);
        }

        if (painting.direction == 1) {
            z = MathHelper.floor_double(painting.posZ - horizontalOffset / 16.0F);
        }

        if (painting.direction == 2) {
            x = MathHelper.floor_double(painting.posX - horizontalOffset / 16.0F);
        }

        if (painting.direction == 3) {
            z = MathHelper.floor_double(painting.posZ + horizontalOffset / 16.0F);
        }

        int brightness = this.renderManager.world.getBrightness(x, y, z, 0);
        int colorX = brightness % 65536;
        int colorY = brightness / 65536;
        GL13.glMultiTexCoord2f(33985, colorX / 1.0F, colorY / 1.0F);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }
}
