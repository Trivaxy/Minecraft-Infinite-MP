package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.world.World;

public final class LeafParticle extends Particle {
    public LeafParticle(World world, double x, double y, double z, double red, double green, double blue) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.1F;
        this.motionY *= 0.1F;
        this.motionZ *= 0.1F;
        int rotation = this.rand.nextInt(4);
        switch (rotation) {
            case 1:
                this.textureIndex = 65;
                break;
            case 2:
                this.textureIndex = 80;
                break;
            case 3:
                this.textureIndex = 81;
                break;
            default:
                this.textureIndex = 64;
        }

        this.red = (float)red;
        this.green = (float)green;
        this.blue = (float)blue;
        this.scale = (float)(2.0 * (Math.random() * 0.8 + 0.5));
        this.maxAge = 15;
        this.noClip = false;
    }

    @Override
    public final void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.onGround) {
            this.age++;
        }

        if (this.age >= this.maxAge) {
            this.setDead();
        }

        this.motionY -= 0.004;
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
