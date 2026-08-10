package net.minecraft.client.particle.effect;

import net.minecraft.game.world.World;

public class HeartParticle extends GlowParticle {
    public HeartParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.01;
        this.motionY *= 0.01;
        this.motionZ *= 0.01;
        this.motionY += 0.1;
        this.scale *= 0.75F;
        this.maxAge = 16;
        this.noClip = false;
        this.textureIndex = 129;
    }
}
