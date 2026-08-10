package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.world.World;

public class FlameParticle extends Particle {
    private float flameScale;

    public FlameParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, int tex) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = this.motionX * 0.01 + motionX;
        this.motionY = this.motionY * 0.01 + motionY;
        this.motionZ = this.motionZ * 0.01 + motionZ;
        this.flameScale = this.scale;
        this.red = this.green = this.blue = 1.0F;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
        this.noClip = true;
        this.glows = true;
        this.textureIndex = tex;
    }

    public FlameParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.flameScale = this.scale;
        this.red = this.green = this.blue = 1.0F;
        this.noClip = true;
        this.glows = true;
        this.textureIndex = 48;
        this.maxAge = 20;
    }

    @Override
    public void renderParticle(Tessellator tesselator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float scaleOffset = (this.age + deltaTime) / this.maxAge;
        this.scale = this.flameScale * (1.0F - scaleOffset * scaleOffset * 0.5F);
        super.renderParticle(tesselator, deltaTime, yaw, pitch, yawOffset, pitchOffset, offset);
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.96;
        this.motionY *= 0.96;
        this.motionZ *= 0.96;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
