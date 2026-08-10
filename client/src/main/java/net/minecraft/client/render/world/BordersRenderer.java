package net.minecraft.client.render.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.entity.mob.Mob;
import org.lwjgl.opengl.GL11;

public class BordersRenderer {
    private Minecraft mc;

    public BordersRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void render(float partialTicks, long finishTimeNano) {
        Mob player = this.mc.gameRenderer.cameraMob;
        Tessellator tess = Tessellator.instance;
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        double heightOffset = this.mc.world.getWorldHeight() - y;
        double xpos = (player.chunkX << 4) - x;
        double zpos = (player.chunkZ << 4) - z;
        int shadeModel = GL11.glGetInteger(2900);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glLineWidth(1.5F);
        tess.begin(1);

        for (int yPos = 1; yPos <= this.mc.world.getWorldHeight(); yPos += 2) {
            double yOff = yPos - y;
            tess.addColoredVertex(xpos, yOff, zpos, 1.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos, yOff, zpos + 17.78, 1.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos, yOff, zpos + 16.0, 1.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos + 17.78, yOff, zpos + 16.0, 1.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos + 16.0, 1.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos, 1.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos, yOff, zpos, 1.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos + 17.78, yOff, zpos, 1.0F, 1.0F, 0.0F, 1.0F);
        }

        for (int sectionY = 0; sectionY <= this.mc.world.getWorldHeight(); sectionY += 16) {
            double yOff = sectionY - y;
            tess.addColoredVertex(xpos, yOff, zpos, 0.25F, 0.25F, 1.0F, 0.0F);
            tess.addColoredVertex(xpos, yOff, zpos + 17.78, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos, yOff, zpos + 16.0, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos + 16.0, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos + 16.0, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, yOff, zpos, 0.25F, 0.25F, 1.0F, 1.0F);
            tess.addColoredVertex(xpos, yOff, zpos, 0.25F, 0.25F, 1.0F, 1.0F);
        }

        tess.addColoredVertex(xpos, -y, zpos, 1.0F, 0.0F, 0.0F, 0.0F);
        tess.addColoredVertex(xpos, heightOffset, zpos, 1.0F, 0.0F, 0.0F, 1.0F);
        tess.addColoredVertex(xpos + 16.0, -y, zpos + 16.0, 1.0F, 0.0F, 0.0F, 0.0F);
        tess.addColoredVertex(xpos + 16.0, heightOffset, zpos + 16.0, 1.0F, 0.0F, 0.0F, 1.0F);
        tess.addColoredVertex(xpos + 16.0, -y, zpos, 1.0F, 0.0F, 0.0F, 0.0F);
        tess.addColoredVertex(xpos + 16.0, heightOffset, zpos, 1.0F, 0.0F, 0.0F, 1.0F);
        tess.addColoredVertex(xpos, -y, zpos + 16.0, 1.0F, 0.0F, 0.0F, 0.0F);
        tess.addColoredVertex(xpos, heightOffset, zpos + 16.0, 1.0F, 0.0F, 0.0F, 1.0F);

        for (int yPos = 2; yPos < 16; yPos += 2) {
            tess.addColoredVertex(xpos + yPos, -y, zpos, 0.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos + yPos, heightOffset, zpos, 0.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos, -y, zpos + yPos, 0.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos, heightOffset, zpos + yPos, 0.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos + yPos, -y, zpos + 16.0, 0.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos + yPos, heightOffset, zpos + 16.0, 0.0F, 1.0F, 0.0F, 1.0F);
            tess.addColoredVertex(xpos + 16.0, -y, zpos + yPos, 0.0F, 1.0F, 0.0F, 0.0F);
            tess.addColoredVertex(xpos + 16.0, heightOffset, zpos + yPos, 0.0F, 1.0F, 0.0F, 1.0F);
        }

        tess.draw();
        GL11.glLineWidth(1.0F);
        GL11.glEnable(3553);
        GL11.glShadeModel(shadeModel);
    }
}
