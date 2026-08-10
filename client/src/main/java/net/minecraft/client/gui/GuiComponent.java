package net.minecraft.client.gui;

import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiComponent {
    protected float zLevel = 0.0F;

    protected void drawHorizontalLine(int minX, int maxX, int minY, int maxY) {
        if (maxX < minX) {
            int offset = minX;
            minX = maxX;
            maxX = offset;
        }

        drawRectangle(minX, minY, maxX + 1, minY + 1, maxY);
    }

    protected void drawVerticalLine(int minX, int maxX, int minY, int maxY) {
        if (minY < maxX) {
            int offset = maxX;
            maxX = minY;
            minY = offset;
        }

        drawRectangle(minX, maxX + 1, minX + 1, minY, maxY);
    }

    public static void drawRectangle(int startX, int startY, int endX, int endY, int color) {
        if (startX < endX) {
            int offset = startX;
            startX = endX;
            endX = offset;
        }

        if (startY < endY) {
            int offset = startY;
            startY = endY;
            endY = offset;
        }

        float red = (color >> 24 & 0xFF) / 255.0F;
        float green = (color >> 16 & 0xFF) / 255.0F;
        float blue = (color >> 8 & 0xFF) / 255.0F;
        float alpha = (color & 0xFF) / 255.0F;
        Tessellator tessellator10 = Tessellator.instance;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(green, blue, alpha, red);
        tessellator10.begin();
        tessellator10.addVertex(startX, endY, 0.0);
        tessellator10.addVertex(endX, endY, 0.0);
        tessellator10.addVertex(endX, startY, 0.0);
        tessellator10.addVertex(startX, startY, 0.0);
        tessellator10.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    protected void drawGradient(int startX, int startY, int endX, int endY, int startColor, int endColor, float alphaMultiplier) {
        float topRed = (startColor >> 24 & 0xFF) / 255.0F;
        float topGreen = (startColor >> 16 & 0xFF) / 255.0F;
        float topBlue = (startColor >> 8 & 0xFF) / 255.0F;
        float topAlpha = (startColor & 0xFF) / 255.0F;
        float bottomRed = (endColor >> 24 & 0xFF) / 255.0F;
        float bottomGreen = (endColor >> 16 & 0xFF) / 255.0F;
        float bottomBlue = (endColor >> 8 & 0xFF) / 255.0F;
        float bottomAlpha = (endColor & 0xFF) / 255.0F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.setColorAlpha(topGreen, topBlue, topAlpha, topRed * alphaMultiplier);
        tessellator.addVertex(endX, startY, 0.0);
        tessellator.addVertex(startX, startY, 0.0);
        tessellator.setColorAlpha(bottomGreen, bottomBlue, bottomAlpha, bottomRed * alphaMultiplier);
        tessellator.addVertex(startX, endY, 0.0);
        tessellator.addVertex(endX, endY, 0.0);
        tessellator.draw();
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    public void drawGraphic(int x, int y, int minU, int minV, int maxU, int maxV) {
        float offset = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.addTexturedVertex(x + 0, y + maxV, this.zLevel, (minU + 0) * offset, (minV + maxV) * offset);
        tessellator.addTexturedVertex(x + maxU, y + maxV, this.zLevel, (minU + maxU) * offset, (minV + maxV) * offset);
        tessellator.addTexturedVertex(x + maxU, y + 0, this.zLevel, (minU + maxU) * offset, (minV + 0) * offset);
        tessellator.addTexturedVertex(x + 0, y + 0, this.zLevel, (minU + 0) * offset, (minV + 0) * offset);
        tessellator.draw();
    }

    public void drawString(Font fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawStringNoShadow(Font fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawString(text, x, y, color);
    }

    public void drawSplitString(Font fontRenderer, String text, int x, int y, int width, int color, boolean shadow, int height) {
        fontRenderer.drawSplitString(text, x, y, width, color, shadow, false, height);
    }

    public void drawSplitStringBg(Font fontRenderer, String text, int x, int y, int width, int color, boolean shadow, boolean bg, int height) {
        fontRenderer.drawSplitString(text, x, y, width, color, shadow, bg, height);
    }

    public void drawCenteredString(Font fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    public void drawCenteredStringNoShadow(Font fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    public void drawOffsettedString(Font fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text), y, color);
    }
}
