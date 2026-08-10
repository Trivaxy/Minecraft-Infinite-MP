package net.minecraft.client.particle.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class SpellParticle extends Particle {
    public SpellParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionY *= 0.2;
        if (motionX == 0.0 && motionZ == 0.0) {
            this.motionX *= 0.1;
            this.motionZ *= 0.1;
        }

        this.scale *= 0.75F;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
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

        this.textureIndex = 140 + (9 - this.age * 8 / this.maxAge);
        this.motionY += 0.004;
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
