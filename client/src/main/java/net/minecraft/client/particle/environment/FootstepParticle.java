package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class FootstepParticle extends Particle {
    private Material material;
    private TextureLoader renderer;

    public FootstepParticle(TextureLoader render, World world, double x, double y, double z, Material mat) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.renderer = render;
        this.motionX = this.motionY = this.motionZ = 0.0;
        this.maxAge = 200;
        this.material = mat;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float scaleFactor = (this.age + deltaTime) / this.maxAge;
        scaleFactor *= scaleFactor;
        float opacity = 2.0F - scaleFactor * 2.0F;
        if (opacity > 1.0F) {
            opacity = 1.0F;
        }

        opacity *= 0.2F;
        GL11.glDisable(2896);
        double uvOffset = 0.125;
        double posX = this.posX - interpX;
        double posY = this.posY - interpY;
        double posZ = this.posZ - interpZ;
        int brightness = this.world
            .getBrightness(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
        int u = brightness % 65536;
        int v = brightness / 65536;
        GL13.glMultiTexCoord2f(33985, u / 1.0F, v / 1.0F);
        this.renderer.bindTexture(this.renderer.getTexture("/misc/footprint.png"));
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        tessellator.begin();
        if (this.material == MaterialList.snow) {
            tessellator.setColorAlpha(0.5F, 0.5F, 0.5F, opacity);
        } else {
            tessellator.setColorAlpha(0.5F, 0.5F, 0.2F, opacity);
        }

        tessellator.addTexturedVertex(posX - uvOffset, posY, posZ + uvOffset, 0.0F, 1.0F);
        tessellator.addTexturedVertex(posX + uvOffset, posY, posZ + uvOffset, 1.0F, 1.0F);
        tessellator.addTexturedVertex(posX + uvOffset, posY, posZ - uvOffset, 1.0F, 0.0F);
        tessellator.addTexturedVertex(posX - uvOffset, posY, posZ - uvOffset, 0.0F, 0.0F);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glEnable(2896);
    }

    @Override
    public void onTick() {
        this.age++;
        if (this.age == this.maxAge
            || this.world.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ)) == null) {
            this.setDead();
        }
    }

    @Override
    public int layer() {
        return 3;
    }
}
