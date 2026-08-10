package net.minecraft.client.render.game;

import java.awt.image.BufferedImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Lightmap {
    private Minecraft mc;
    public int lightmapTexture;
    float lightmapFlickerX = 0.0F;
    float lightmapFlickerDX = 0.0F;
    float lightmapFlickerY = 0.0F;
    float lightmapFlickerDY = 0.0F;

    public Lightmap(Minecraft mc) {
        this.mc = mc;
        this.lightmapTexture = this.mc.textureLoader.allocateTexture(new BufferedImage(16, 16, 1));
    }

    public void disable() {
        GL13.glClientActiveTexture(33985);
        GL13.glActiveTexture(33985);
        GL11.glDisable(3553);
        GL13.glClientActiveTexture(33984);
        GL13.glActiveTexture(33984);
    }

    public void enable() {
        GL13.glClientActiveTexture(33985);
        GL13.glActiveTexture(33985);
        GL11.glMatrixMode(5890);
        GL11.glLoadIdentity();
        float scale = 0.00390625F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glMatrixMode(5888);
        this.mc.textureLoader.bindTexture(this.lightmapTexture);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        GL13.glClientActiveTexture(33984);
        GL13.glActiveTexture(33984);
    }

    public void flicker() {
        this.lightmapFlickerDX = (float)(this.lightmapFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.lightmapFlickerDY = (float)(this.lightmapFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.lightmapFlickerDX = (float)(this.lightmapFlickerDX * 0.9);
        this.lightmapFlickerDY = (float)(this.lightmapFlickerDY * 0.9);
        this.lightmapFlickerX = this.lightmapFlickerX + (this.lightmapFlickerDX - this.lightmapFlickerX) * 1.0F;
        this.lightmapFlickerY = this.lightmapFlickerY + (this.lightmapFlickerDY - this.lightmapFlickerY) * 1.0F;
    }

    public void update(float deltaTick) {
        if (this.mc.world != null) {
            this.mc
                .textureLoader
                .createTexture(
                    this.mc
                        .world
                        .currDim
                        .getLightmapColors(
                            OptionValues.torchFlicker, this.lightmapFlickerX, OptionValues.gamma, this.mc.player, deltaTick, OptionValues.fancyGraphics
                        ),
                    16,
                    16,
                    this.lightmapTexture
                );
        }
    }
}
