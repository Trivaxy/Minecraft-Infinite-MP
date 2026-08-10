package net.minecraft.client.particle.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.world.World;

public class SoulParticle extends Particle {
    public SoulParticle(World world, Entity particle) {
        super(world, particle.posX, particle.boundingBox.minY + particle.height / 2.0F, particle.posZ, particle.motionX, particle.motionY, particle.motionZ);
        this.motionY *= 0.2;
        this.motionX *= 0.1;
        this.motionZ *= 0.1;
        this.maxAge = 40;
        this.noClip = false;
    }

    public SoulParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionY *= 0.2;
        this.motionX *= 0.1;
        this.motionZ *= 0.1;
        this.maxAge = 40;
        this.noClip = false;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }

        this.textureIndex = 216 + 10 + this.age / 2;
        this.motionY += 0.008;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }

        this.motionX *= 0.96;
        this.motionY *= 0.96;
        this.motionZ *= 0.96;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
