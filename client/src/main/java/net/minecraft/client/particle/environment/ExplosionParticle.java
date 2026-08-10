package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class ExplosionParticle extends Particle {
    public boolean goUp = true;

    public ExplosionParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = motionX + (Math.random() * 2.0 - 1.0) * 0.05;
        this.motionY = motionY + (Math.random() * 2.0 - 1.0) * 0.05;
        this.motionZ = motionZ + (Math.random() * 2.0 - 1.0) * 0.05;
        this.red = this.green = this.blue = this.rand.nextFloat() * 0.3F + 0.7F;
        this.scale = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F;
        this.maxAge = (int)(16.0 / (this.rand.nextFloat() * 0.8 + 0.2)) + 2;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setDead();
        }

        this.textureIndex = 7 - this.age * 8 / this.maxAge;
        if (this.goUp) {
            this.motionY += 0.004;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9;
        this.motionY *= 0.9;
        this.motionZ *= 0.9;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
