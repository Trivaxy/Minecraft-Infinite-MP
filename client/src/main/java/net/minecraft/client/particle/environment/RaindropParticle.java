package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class RaindropParticle extends Particle {
    public RaindropParticle(World world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX *= 0.3;
        this.motionY = Math.random() * 0.2 + 0.1;
        this.motionZ *= 0.3;
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.textureIndex = 18 + this.rand.nextInt(4);
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
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
        if (this.maxAge-- <= 0) {
            this.setDead();
        }

        if (this.onGround) {
            if (Math.random() < 0.5) {
                this.setDead();
            }

            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }

        Material mat = this.world.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        if (mat.getIsLiquid() || mat.isSolid()) {
            double yOffset = MathHelper.floor_double(this.posY) + 1
                - LiquidBlock.getHeight(
                    this.world.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                );
            if (this.posY < yOffset) {
                this.setDead();
            }
        }
    }
}
