package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.ParticleType;
import net.minecraft.game.world.World;

public class LavaBlobParticle extends Particle {
    private float lavaParticleScale;

    public LavaBlobParticle(World world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.8;
        this.motionY *= 0.8;
        this.motionZ *= 0.8;
        this.motionY = this.rand.nextFloat() * 0.4 + 0.05;
        this.red = this.green = this.blue = 1.0F;
        this.scale = this.scale * (this.rand.nextFloat() * 2.0F + 0.2F);
        this.lavaParticleScale = this.scale;
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.noClip = false;
        this.glows = true;
        this.textureIndex = 49;
    }

    @Override
    public float getBrightness(float deltaTime) {
        return 1.0F;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
        float scaleOffset = (this.age + deltaTime) / this.maxAge;
        this.scale = this.lavaParticleScale * (1.0F - scaleOffset * scaleOffset);
        super.renderParticle(tessellator, deltaTime, yaw, pitch, yawOffset, pitchOffset, offset);
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }

        if (this.rand.nextFloat() > (float)this.age / this.maxAge) {
            this.world.spawnParticle(ParticleType.SMOKE, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
        }

        this.motionY -= 0.03;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.999;
        this.motionY *= 0.999;
        this.motionZ *= 0.999;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
