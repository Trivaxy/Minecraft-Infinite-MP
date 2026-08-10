package net.minecraft.client.particle.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class SwipeParticle extends Particle {
    private TextureLoader renderer;

    public SwipeParticle(TextureLoader renderer, World world, double x, double y, double z, double scaleFactor) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.renderer = renderer;
        this.maxAge = 4;
        this.red = this.green = this.blue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.scale = 1.0F - (float)scaleFactor * 0.5F;
    }

    @Override
    public void renderParticle(
        Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ
    ) {
        int existTime = (int)((this.age + partialTicks) * 3.0F / this.maxAge);
        if (existTime <= 7) {
            int brightness = this.getBrightnessInt(partialTicks);
            int u = brightness % 65536;
            int v = brightness / 65536;
            GL13.glMultiTexCoord2f(33985, u / 1.0F, v / 1.0F);
            this.renderer.bindTexture(this.renderer.getTexture("/particles/sweep.png"));
            float vertU = existTime % 4 / 4.0F;
            float imgStart = vertU + 0.24975F;
            float vertV = existTime / 2 / 2.0F;
            float imgEnd = vertV + 0.4995F;
            float imgScale = 1.0F * this.scale;
            double newX = this.prevX + (this.posX - this.prevX) * partialTicks - interpX;
            double newY = this.prevY + (this.posY - this.prevY) * partialTicks - interpY;
            double newZ = this.prevZ + (this.posZ - this.prevZ) * partialTicks - interpZ;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tessellator.begin();
            tessellator.setColorAlpha(this.red, this.green, this.blue, 1.0F);
            tessellator.setNormals(0.0F, 1.0F, 0.0F);
            tessellator.addTexturedVertex(
                newX - rotationX * imgScale - rotationXY * imgScale,
                newY - rotationZ * imgScale * 0.5F,
                newZ - rotationYZ * imgScale - rotationXZ * imgScale,
                imgStart,
                imgEnd
            );
            tessellator.addTexturedVertex(
                newX - rotationX * imgScale + rotationXY * imgScale,
                newY + rotationZ * imgScale * 0.5F,
                newZ - rotationYZ * imgScale + rotationXZ * imgScale,
                imgStart,
                vertV
            );
            tessellator.addTexturedVertex(
                newX + rotationX * imgScale + rotationXY * imgScale,
                newY + rotationZ * imgScale * 0.5F,
                newZ + rotationYZ * imgScale + rotationXZ * imgScale,
                vertU,
                vertV
            );
            tessellator.addTexturedVertex(
                newX + rotationX * imgScale - rotationXY * imgScale,
                newY - rotationZ * imgScale * 0.5F,
                newZ + rotationYZ * imgScale - rotationXZ * imgScale,
                vertU,
                imgEnd
            );
            tessellator.draw();
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
