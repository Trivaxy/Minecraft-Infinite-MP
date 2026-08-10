package net.minecraft.client.particle.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class MarkerParticle extends Particle {
    protected Icon particleIcon;

    public MarkerParticle(World world, double x, double y, double z, Block target, int metadata) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleIcon = target.getIcon(2, metadata);
        this.red = this.green = this.blue = 1.0F;
        this.maxAge = 40;
    }

    @Override
    public void renderParticle(
        Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ
    ) {
        float minU = this.particleIcon.getMinU();
        float maxU = this.particleIcon.getMaxU();
        float minV = this.particleIcon.getMinV();
        float maxV = this.particleIcon.getMaxV();
        double newX = this.prevX + (this.posX - this.prevX) * partialTicks - interpX;
        double newY = this.prevY + (this.posY - this.prevY) * partialTicks - interpY;
        double newZ = this.prevZ + (this.posZ - this.prevZ) * partialTicks - interpZ;
        tessellator.setColor(this.red, this.green, this.blue);
        tessellator.addTexturedVertex(newX - rotationX * 0.5 - rotationXY * 0.5, newY - rotationZ * 0.5, newZ - rotationYZ * 0.5 - rotationXZ * 0.5, maxU, maxV);
        tessellator.addTexturedVertex(newX - rotationX * 0.5 + rotationXY * 0.5, newY + rotationZ * 0.5, newZ - rotationYZ * 0.5 + rotationXZ * 0.5, maxU, minV);
        tessellator.addTexturedVertex(newX + rotationX * 0.5 + rotationXY * 0.5, newY + rotationZ * 0.5, newZ + rotationYZ * 0.5 + rotationXZ * 0.5, minU, minV);
        tessellator.addTexturedVertex(newX + rotationX * 0.5 - rotationXY * 0.5, newY - rotationZ * 0.5, newZ + rotationYZ * 0.5 - rotationXZ * 0.5, minU, maxV);
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public int layer() {
        return 1;
    }
}
