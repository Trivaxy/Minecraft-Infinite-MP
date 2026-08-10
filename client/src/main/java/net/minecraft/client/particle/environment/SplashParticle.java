package net.minecraft.client.particle.environment;

import net.minecraft.game.world.World;

public class SplashParticle extends RaindropParticle {
    public SplashParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.gravity = 0.04F;
        this.textureIndex = 17;
        if (motionY == 0.0 && (motionX != 0.0 || motionZ != 0.0)) {
            this.motionX = motionX;
            this.motionY = motionY + 0.1;
            this.motionZ = motionZ;
        }
    }
}
