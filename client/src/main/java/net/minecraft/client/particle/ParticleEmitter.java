package net.minecraft.client.particle;

import net.minecraft.client.render.Tessellator;
import net.minecraft.game.ParticleType;
import net.minecraft.game.world.World;

public class ParticleEmitter extends Particle {
    private ParticleType particle;

    public ParticleEmitter(World world, double x, double y, double z, double motionX, double motionY, double motionZ, ParticleType particle) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.maxAge = 8;
        this.particle = particle;
    }

    @Override
    public void onTick() {
        for (int particlesToSpawn = 0; particlesToSpawn < 6; particlesToSpawn++) {
            double newX = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 2.0;
            double newY = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 2.0;
            double newZ = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 2.0;
            this.world.spawnParticle(this.particle, newX, newY, newZ, this.age / this.maxAge, 0.0, 0.0);
        }

        this.age++;
        if (this.age == this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float deltaTime, float yaw, float pitch, float yawOffset, float pitchOffset, float offset) {
    }

    @Override
    public int layer() {
        return 1;
    }
}
