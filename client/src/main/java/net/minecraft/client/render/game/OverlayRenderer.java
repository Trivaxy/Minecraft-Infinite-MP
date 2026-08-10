package net.minecraft.client.render.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.texture.Icon;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class OverlayRenderer {
    private Minecraft mc;

    public OverlayRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void renderOverlays(float tick) {
        if (OptionValues.cameraMode == 0) {
            GL11.glDisable(3008);
            if (this.mc.gameRenderer.cameraMob.fire >= 0) {
                int texture = this.mc.textureLoader.getTexture("/terrain.png");
                GL11.glBindTexture(3553, texture);
                this.renderFire(tick);
            }

            boolean renderWater = this.mc.gameRenderer.cameraMob.isInMaterial(MaterialList.water) && OptionValues.fancyGraphics;
            if (this.mc.gameRenderer.cameraMob.isInsideBlock() || renderWater) {
                int x = MathHelper.floor_double(this.mc.gameRenderer.cameraMob.posX);
                int y = MathHelper.floor_double(this.mc.gameRenderer.cameraMob.posY);
                int z = MathHelper.floor_double(this.mc.gameRenderer.cameraMob.posZ);
                int texture = this.mc.textureLoader.getTexture("/terrain.png");
                GL11.glBindTexture(3553, texture);
                int block = this.mc.world.getBlockId(x, y, z);
                if (BlockList.blocks[block] != null) {
                    this.renderInsideOfBlock(
                        tick, renderWater ? 0.8F : 0.1F, renderWater ? 0.3F : 1.0F, renderWater ? -0.9F : -0.5F, BlockList.blocks[block].getSideIcon(0)
                    );
                }
            }

            GL11.glEnable(3008);
        }
    }

    private void renderInsideOfBlock(float tick, float brightness, float opacity, float zoom, Icon icon) {
        Tessellator tess = Tessellator.instance;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        float startU = -1.0F;
        float endU = 1.0F;
        float startV = -1.0F;
        float endV = 1.0F;
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        GL11.glColor4f(brightness, brightness, brightness, opacity);
        GL11.glPushMatrix();
        tess.begin();
        tess.addTexturedVertex(startU, startV, zoom, maxU, maxV);
        tess.addTexturedVertex(endU, startV, zoom, minU, maxV);
        tess.addTexturedVertex(endU, endV, zoom, minU, minV);
        tess.addTexturedVertex(startU, endV, zoom, maxU, minV);
        tess.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    private void renderFire(float tick) {
        Tessellator tess = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        float offset = 1.0F;

        for (int pass = 0; pass < 2; pass++) {
            Icon icon = BlockList.fire.getIcon(1);
            float minU = icon.getMinU();
            float maxU = icon.getMaxU();
            float minV = icon.getMinV();
            float maxV = icon.getMaxV();
            float startU = (0.0F - offset) / 2.0F;
            float endU = startU + offset;
            float startV = 0.0F - offset / 2.0F;
            float endV = startV + offset;
            float length = -0.5F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-(pass * 2 - 1) * 0.24F, -0.5F, 0.0F);
            GL11.glRotatef((pass * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            tess.begin();
            tess.addTexturedVertex(startU, startV, length, maxU, maxV);
            tess.addTexturedVertex(endU, startV, length, minU, maxV);
            tess.addTexturedVertex(endU, endV, length, minU, minV);
            tess.addTexturedVertex(startU, endV, length, maxU, minV);
            tess.draw();
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }
}
