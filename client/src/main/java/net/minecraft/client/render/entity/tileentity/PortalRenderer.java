package net.minecraft.client.render.entity.tileentity;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.tileentity.other.PortalEntity;
import org.lwjgl.opengl.GL11;

public class PortalRenderer extends TileEntityRenderer<PortalEntity> {
    FloatBuffer buffer = Renderer.createFloatBuffer(16);

    public void render(PortalEntity portal, double x, double y, double z, float tick) {
        double playerX = this.renderManager.playerX;
        double playerY = this.renderManager.playerY;
        double playerZ = this.renderManager.playerZ;
        GL11.glDisable(2896);
        Random random = new Random(31100L);
        double offset = 0.75;

        for (int pass = 0; pass < 16; pass++) {
            GL11.glPushMatrix();
            double xOffset = 16 - pass;
            double yOffset = 0.0625;
            double zOffset = 1.0 / (xOffset + 1.0);
            if (pass == 0) {
                this.bindTexture("/misc/tunnel.png");
                zOffset = 0.1;
                xOffset = 65.0;
                yOffset = 0.125;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }

            if (pass == 1) {
                this.bindTexture("/misc/particlefield.png");
                GL11.glEnable(3042);
                GL11.glBlendFunc(1, 1);
                yOffset = 0.5;
            }

            double rotVert = -(y + offset);
            double rotHoz = rotVert + xOffset;
            double rotFinal = rotVert / rotHoz;
            rotFinal += y + offset;
            GL11.glTranslated(playerX, rotFinal, playerZ);
            GL11.glTexGeni(8192, 9472, 9217);
            GL11.glTexGeni(8193, 9472, 9217);
            GL11.glTexGeni(8194, 9472, 9217);
            GL11.glTexGeni(8195, 9472, 9216);
            GL11.glTexGen(8192, 9473, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexGen(8193, 9473, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GL11.glTexGen(8194, 9473, this.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GL11.glTexGen(8195, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GL11.glEnable(3168);
            GL11.glEnable(3169);
            GL11.glEnable(3170);
            GL11.glEnable(3171);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslated(0.0, System.currentTimeMillis() % 700000L / 700000.0, 0.0);
            GL11.glScaled(yOffset, yOffset, yOffset);
            GL11.glTranslated(0.5, 0.5, 0.0);
            GL11.glRotated((pass * pass * 4321 + pass * 9) * 2.0, 0.0, 0.0, 1.0);
            GL11.glTranslated(-0.5, -0.5, 0.0);
            GL11.glTranslated(-playerX, -playerZ, -playerY);
            GL11.glTranslated(xOffset / rotVert, xOffset / rotVert, -playerY);
            Tessellator tess = Tessellator.instance;
            float red = random.nextFloat() * 0.5F + 0.1F;
            float green = random.nextFloat() * 0.5F + 0.4F;
            float blue = random.nextFloat() * 0.5F + 0.5F;
            if (pass == 0) {
                blue = 1.0F;
                green = 1.0F;
                rotFinal = 1.0;
            }

            tess.begin();
            tess.setColorAlpha(red * (float)zOffset, green * (float)zOffset, blue * (float)zOffset, 1.0F);
            tess.addVertex(x, y + offset, z);
            tess.addVertex(x, y + offset, z + 1.0);
            tess.addVertex(x + 1.0, y + offset, z + 1.0);
            tess.addVertex(x + 1.0, y + offset, z);
            tess.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
        }

        GL11.glDisable(3042);
        GL11.glDisable(3168);
        GL11.glDisable(3169);
        GL11.glDisable(3170);
        GL11.glDisable(3171);
        GL11.glEnable(2896);
    }

    private FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        ((Buffer)this.buffer).clear();
        this.buffer.put(f1).put(f2).put(f3).put(f4);
        ((Buffer)this.buffer).flip();
        return this.buffer;
    }
}
