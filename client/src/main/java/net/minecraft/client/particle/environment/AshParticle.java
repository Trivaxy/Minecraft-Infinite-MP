package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class AshParticle extends Particle {
    public AshParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        float color = this.rand.nextFloat() * 0.1F + 0.2F;
        this.red = color;
        this.green = color;
        this.blue = color;
        this.textureIndex = 0;
        this.setSize(0.02F, 0.02F);
        this.scale = this.scale * (this.rand.nextFloat() * 0.6F + 0.5F);
        this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
        this.noClip = true;
        this.motionX *= 0.02F;
        this.motionY *= 0.02F;
        this.motionZ *= 0.02F;
    }

    public AshParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float blu, float grun) {
        this(world, x, y, z, motionX, motionY, motionZ);
        this.red = red;
        this.green = grun;
        this.blue = blu;
        this.motionX = this.motionX * (motionX + (Math.random() * 2.0 - 1.0) * 0.02);
        this.motionY = this.motionY * (motionY + (Math.random() * 2.0 - 1.0) * 0.02);
        this.motionZ = this.motionZ * (motionZ + (Math.random() * 2.0 - 1.0) * 0.02);
    }

    public AshParticle(World world, double x, double y, double z) {
        super(world, x, y - 0.125, z, 0.0, 0.0, 0.0);
        this.red = 0.4F;
        this.green = 0.4F;
        this.blue = 0.7F;
        this.textureIndex = 0;
        this.setSize(0.01F, 0.01F);
        this.scale = this.scale * (this.rand.nextFloat() * 0.6F + 0.2F);
        this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.motionX *= 0.0;
        this.motionY *= 0.0;
        this.motionZ *= 0.0;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
        this.motionY += 1.0E-4;
        if (this.maxAge-- <= 0) {
            this.setDead();
        }
    }
}
