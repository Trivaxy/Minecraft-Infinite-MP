package net.minecraft.client.particle.effect;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class CritParticle extends Particle {
    public CritParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.motionX += motionX * 0.4;
        this.motionY += motionY * 0.4;
        this.motionZ += motionZ * 0.4;
        this.red = this.green = this.blue = (float)(Math.random() * 0.3 + 0.6);
        this.maxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
        this.noClip = false;
        this.textureIndex = 130;
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
        this.green = (float)(this.green * 0.96);
        this.blue = (float)(this.blue * 0.9);
        this.motionX *= 0.7;
        this.motionY *= 0.7;
        this.motionZ *= 0.7;
        this.motionY -= 0.02;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }
}
