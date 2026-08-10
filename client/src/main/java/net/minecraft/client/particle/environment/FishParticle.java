package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public class FishParticle extends Particle {
    public FishParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX *= 0.3F;
        this.motionY = Math.random() * 0.2F + 0.1F;
        this.motionZ *= 0.3F;
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.textureIndex = 18 + this.rand.nextInt(4);
        this.setSize(0.01F, 0.01F);
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.gravity = 0.0F;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.motionY = this.motionY - this.gravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        int ageOffset = 60 - this.maxAge;
        float scale = ageOffset * 0.001F;
        this.setSize(scale, scale);
        if (this.maxAge-- <= 0) {
            this.setDead();
        }
    }
}
