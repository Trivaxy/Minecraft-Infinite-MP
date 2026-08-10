package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class DustParticle extends Particle {
    public DustParticle(World world, double x, double y, double z, double red, double green, double blue) {
        super(world, x, y, z, red, green, blue);
        this.red = (float)red;
        this.green = (float)green;
        this.blue = (float)blue;
        this.gravity = 0.001F;
        this.scale *= 0.67499995F;
        this.maxAge = (int)(16.0 / (this.rand.nextFloat() * 0.8 + 0.2)) + 2;
        this.motionX = this.motionY = this.motionZ = 0.0;
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
        this.motionY -= 0.004;
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
