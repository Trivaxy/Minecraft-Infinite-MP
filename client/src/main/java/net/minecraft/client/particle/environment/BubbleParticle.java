package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class BubbleParticle extends Particle {
    private double raiseSpd = 0.002;

    public BubbleParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, double age, double vel) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.textureIndex = 32;
        this.setSize(0.02F, 0.02F);
        this.scale = this.scale * (this.rand.nextFloat() * 0.6F + 0.2F);
        this.motionX = motionX * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02;
        this.motionY = motionY * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02;
        this.motionZ = motionZ * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02;
        this.age = (int)(age / (Math.random() * 0.8 + 0.2));
        this.maxAge = 2;
        this.raiseSpd = vel;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.motionY = this.motionY + this.raiseSpd;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.85;
        this.motionY *= 0.85;
        this.motionZ *= 0.85;
        if (this.world.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                != MaterialList.water
            && this.age > 2) {
            this.textureIndex = 35;
            this.age = this.maxAge;
            this.raiseSpd = this.motionY = 0.0;
        }

        if (this.age-- <= 0) {
            this.textureIndex = 35;
            if (this.maxAge-- <= 0) {
                this.setDead();
            }
        }
    }
}
