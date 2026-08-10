package net.minecraft.client.particle.environment;

import net.minecraft.client.particle.Particle;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class DropletParticle extends Particle {
    private Material material;

    public DropletParticle(World world, double x, double y, double z, Material mat) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.motionX = this.motionY = this.motionZ = 0.0;
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.material = mat;
        if (this.material == MaterialList.lava) {
            this.glows = true;
        }

        this.age = 40;
        this.maxAge = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        this.motionX = this.motionY = this.motionZ = 0.0;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.material == MaterialList.water) {
            this.red = 0.2F;
            this.green = 0.3F;
            this.blue = 1.0F;
        } else {
            this.red = 1.0F;
            this.green = 16.0F / (40 - this.age + 16);
            this.blue = 4.0F / (40 - this.age + 8);
        }

        this.motionY = this.motionY - this.gravity;
        if (this.age-- > 0) {
            this.motionX *= 0.02;
            this.motionY *= 0.02;
            this.motionZ *= 0.02;
            this.textureIndex = 113;
        } else {
            this.textureIndex = 112;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        if (this.maxAge-- <= 0) {
            this.setDead();
        }

        if (this.onGround) {
            if (this.material == MaterialList.water) {
                this.world.spawnParticle(ParticleType.RAINDROP, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
                this.setDead();
            } else if (this.material == MaterialList.lava) {
                this.textureIndex = 114;
            }

            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
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
