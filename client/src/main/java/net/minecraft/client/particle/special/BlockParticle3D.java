package net.minecraft.client.particle.special;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public final class BlockParticle3D extends Particle {
    protected Icon particleIcon;

    public BlockParticle3D(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Block block, int meta, int side) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleIcon = block.getIcon(side, meta);
        this.gravity = block.particleGravity;
        this.red = this.green = this.blue = 1.0F;
        this.scale /= 2.0F;
    }

    @Override
    public final void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float vertU = this.textureIndex + this.textureJitterX / 4.0F;
        float imgStart = vertU + 0.0078046876F;
        float vertV = this.textureIndex + this.textureJitterY / 4.0F;
        float imgEnd = vertV + 0.0078046876F;
        float imgScale = 0.1F * this.scale;
        if (this.particleIcon != null) {
            vertU = this.particleIcon.getInterpolatedU(this.textureJitterX / 4.0F * 16.0F);
            imgStart = this.particleIcon.getInterpolatedU((this.textureJitterX + 1.0F) / 4.0F * 16.0F);
            vertV = this.particleIcon.getInterpolatedV(this.textureJitterY / 4.0F * 16.0F);
            imgEnd = this.particleIcon.getInterpolatedV((this.textureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        if (this.age >= this.maxAge - 2) {
            imgScale *= 1.0F - (this.age + deltaTime - (this.maxAge - 2)) / 2.0F;
        }

        double x = this.prevX + (this.posX - this.prevX) * deltaTime - interpX;
        double y = this.prevY + (this.posY - this.prevY) * deltaTime - interpY;
        double z = this.prevZ + (this.posZ - this.prevZ) * deltaTime - interpZ;
        y += imgScale - 0.1F;
        if (imgScale > 0.0F) {
            tessellator.setColor(this.red, this.green, this.blue);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z - imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z + imgScale, vertU, vertV);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z + imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z - imgScale, imgStart, imgEnd);
            tessellator.setColor(this.red * 0.5F, this.green * 0.5F, this.blue * 0.5F);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z - imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z - imgScale, imgStart, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z + imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z + imgScale, vertU, vertV);
            tessellator.setColor(this.red * 0.8F, this.green * 0.8F, this.blue * 0.8F);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z - imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z - imgScale, vertU, vertV);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z - imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z - imgScale, imgStart, imgEnd);
            tessellator.setColor(this.red * 0.8F, this.green * 0.8F, this.blue * 0.8F);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z + imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z + imgScale, imgStart, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z + imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z + imgScale, vertU, vertV);
            tessellator.setColor(this.red * 0.6F, this.green * 0.6F, this.blue * 0.6F);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z - imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z - imgScale, imgStart, imgEnd);
            tessellator.addTexturedVertex(x + imgScale, y + imgScale, z + imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x + imgScale, y - imgScale, z + imgScale, vertU, vertV);
            tessellator.setColor(this.red * 0.6F, this.green * 0.6F, this.blue * 0.6F);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z - imgScale, vertU, imgEnd);
            tessellator.addTexturedVertex(x - imgScale, y - imgScale, z + imgScale, vertU, vertV);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z + imgScale, imgStart, vertV);
            tessellator.addTexturedVertex(x - imgScale, y + imgScale, z - imgScale, imgStart, imgEnd);
        }
    }

    @Override
    public final int layer() {
        return 1;
    }
}
