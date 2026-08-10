package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GustParticle extends Particle {
    private TextureLoader renderer;

    public GustParticle(TextureLoader renderer, World world, double x, double y, double z, double scale, double unused1, double unused) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.renderer = renderer;
        this.maxAge = 6 + this.rand.nextInt(4);
        this.red = this.green = this.blue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.scale = 1.0F - (float)scale * 0.5F;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float tickDelta, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        int existTime = (int)((this.age + tickDelta) * 15.0F / this.maxAge);
        if (existTime <= 15) {
            int brightness = this.getBrightnessInt(tickDelta);
            int u = brightness % 65536;
            int v = brightness / 65536;
            GL13.glMultiTexCoord2f(33985, u / 1.0F, v / 1.0F);
            this.renderer.bindTexture(this.renderer.getTexture("/particles/explosion.png"));
            float vertU = existTime % 4 / 4.0F;
            float imgStart = vertU + 0.24975F;
            float vertV = existTime / 4 / 4.0F;
            float imgEnd = vertV + 0.24975F;
            float imgScale = 1.0F * this.scale;
            double newX = this.prevX + (this.posX - this.prevX) * tickDelta - interpX;
            double newY = this.prevY + (this.posY - this.prevY) * tickDelta - interpY;
            double newZ = this.prevZ + (this.posZ - this.prevZ) * tickDelta - interpZ;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tessellator.begin();
            tessellator.setColorAlpha(this.red, this.green, this.blue, 1.0F);
            tessellator.setNormals(0.0F, 1.0F, 0.0F);
            tessellator.addTexturedVertex(
                newX - yaw * imgScale - pitchOffset * imgScale, newY - pitch * imgScale, newZ - yawOffset * imgScale - offset * imgScale, imgStart, imgEnd
            );
            tessellator.addTexturedVertex(
                newX - yaw * imgScale + pitchOffset * imgScale, newY + pitch * imgScale, newZ - yawOffset * imgScale + offset * imgScale, imgStart, vertV
            );
            tessellator.addTexturedVertex(
                newX + yaw * imgScale + pitchOffset * imgScale, newY + pitch * imgScale, newZ + yawOffset * imgScale + offset * imgScale, vertU, vertV
            );
            tessellator.addTexturedVertex(
                newX + yaw * imgScale - pitchOffset * imgScale, newY - pitch * imgScale, newZ + yawOffset * imgScale - offset * imgScale, vertU, imgEnd
            );
            tessellator.draw();
            GL11.glPolygonOffset(0.0F, 0.0F);
        }
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.age++;
        if (this.age == this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public int layer() {
        return 3;
    }
}
