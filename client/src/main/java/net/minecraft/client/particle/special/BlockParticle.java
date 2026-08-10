package net.minecraft.client.particle.special;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.Block;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class BlockParticle extends Particle {
    protected Icon particleIcon;

    public BlockParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Block target, int metadata, int side) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleIcon = target.getIcon(side, metadata);
        this.gravity = target.particleGravity;
        this.red = this.green = this.blue = 0.6F;
        this.scale /= 2.0F;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float vertU = this.textureIndex + this.textureJitterX / 4.0F;
        float imgStart = vertU + 0.015609375F;
        float vertV = this.textureIndex + this.textureJitterY / 4.0F;
        float imgEnd = vertV + 0.015609375F;
        float imgScale = 0.1F * this.scale;
        if (this.particleIcon != null) {
            vertU = this.particleIcon.getInterpolatedU(this.textureJitterX / 4.0F * 16.0F);
            imgStart = this.particleIcon.getInterpolatedU((this.textureJitterX + 1.0F) / 4.0F * 16.0F);
            vertV = this.particleIcon.getInterpolatedV(this.textureJitterY / 4.0F * 16.0F);
            imgEnd = this.particleIcon.getInterpolatedV((this.textureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        double x = this.prevX + (this.posX - this.prevX) * deltaTime - interpX;
        double y = this.prevY + (this.posY - this.prevY) * deltaTime - interpY;
        double z = this.prevZ + (this.posZ - this.prevZ) * deltaTime - interpZ;
        tessellator.setColor(this.red, this.green, this.blue);
        tessellator.addTexturedVertex(
            x - yaw * imgScale - pitchOffset * imgScale, y - pitch * imgScale, z - yawOffset * imgScale - offset * imgScale, vertU, imgEnd
        );
        tessellator.addTexturedVertex(
            x - yaw * imgScale + pitchOffset * imgScale, y + pitch * imgScale, z - yawOffset * imgScale + offset * imgScale, vertU, vertV
        );
        tessellator.addTexturedVertex(
            x + yaw * imgScale + pitchOffset * imgScale, y + pitch * imgScale, z + yawOffset * imgScale + offset * imgScale, imgStart, vertV
        );
        tessellator.addTexturedVertex(
            x + yaw * imgScale - pitchOffset * imgScale, y - pitch * imgScale, z + yawOffset * imgScale - offset * imgScale, imgStart, imgEnd
        );
    }

    @Override
    public int layer() {
        return 1;
    }
}
