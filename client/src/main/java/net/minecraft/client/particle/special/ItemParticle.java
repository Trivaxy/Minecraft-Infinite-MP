package net.minecraft.client.particle.special;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class ItemParticle extends Particle {
    protected Icon particleIcon;

    public ItemParticle(World world, double x, double y, double z, Item target) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleIcon = target.getIconFromMeta(0);
        this.red = this.green = this.blue = 1.0F;
        this.gravity = BlockList.stone.particleGravity;
        this.scale /= 2.0F;
    }

    public ItemParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Item target) {
        this(world, x, y, z, target);
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
    }

    public ItemParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, Item target, int id) {
        this(world, x, y, z, motionX, motionY, motionZ, target);
        this.particleIcon = target.getIconFromMeta(id);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float tickDelta, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float vertU = (this.textureIndex + this.textureJitterX / 4.0F) / 16.0F;
        float imgStart = vertU + 0.015609375F;
        float vertV = (this.textureIndex + this.textureJitterY / 4.0F) / 16.0F;
        float imgEnd = vertV + 0.015609375F;
        float imgScale = 0.1F * this.scale;
        if (this.particleIcon != null) {
            vertU = this.particleIcon.getInterpolatedU(this.textureJitterX / 4.0F * 16.0F);
            imgStart = this.particleIcon.getInterpolatedU((this.textureJitterX + 1.0F) / 4.0F * 16.0F);
            vertV = this.particleIcon.getInterpolatedV(this.textureJitterY / 4.0F * 16.0F);
            imgEnd = this.particleIcon.getInterpolatedV((this.textureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        double newX = this.prevX + (this.posX - this.prevX) * tickDelta - interpX;
        double newY = this.prevY + (this.posY - this.prevY) * tickDelta - interpY;
        double newZ = this.prevZ + (this.posZ - this.prevZ) * tickDelta - interpZ;
        tessellator.setColor(this.red, this.green, this.blue);
        tessellator.addTexturedVertex(
            newX - yaw * imgScale - pitchOffset * imgScale, newY - pitch * imgScale, newZ - yawOffset * imgScale - offset * imgScale, vertU, imgEnd
        );
        tessellator.addTexturedVertex(
            newX - yaw * imgScale + pitchOffset * imgScale, newY + pitch * imgScale, newZ - yawOffset * imgScale + offset * imgScale, vertU, vertV
        );
        tessellator.addTexturedVertex(
            newX + yaw * imgScale + pitchOffset * imgScale, newY + pitch * imgScale, newZ + yawOffset * imgScale + offset * imgScale, imgStart, vertV
        );
        tessellator.addTexturedVertex(
            newX + yaw * imgScale - pitchOffset * imgScale, newY - pitch * imgScale, newZ + yawOffset * imgScale - offset * imgScale, imgStart, imgEnd
        );
    }

    @Override
    public int layer() {
        return 2;
    }
}
