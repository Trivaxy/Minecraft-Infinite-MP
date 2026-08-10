package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class SmokeParticle extends Particle {
    protected boolean reversed = false;

    public SmokeParticle(World world, double x, double y, double z, float size) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.1;
        this.motionY *= 0.1;
        this.motionZ *= 0.1;
        this.red = this.green = this.blue = (float)(Math.random() * 0.3F);
        this.scale *= 0.75F;
        this.scale *= size;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)(this.maxAge * size);
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

        this.textureIndex = 7 - this.age * 8 / this.maxAge;
        if (this.reversed) {
            this.motionY -= 0.004;
        } else {
            this.motionY += 0.004;
        }

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
