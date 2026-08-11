package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class EnderEye extends Projectile {
    private int popTime;
    private double newX;
    private double newY;
    private double newZ;

    public EnderEye(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.itemAssigned = ItemList.enderEye;
        this.canGlow = true;
        this.gravityForce = 0.0;
    }

    public EnderEye(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.popTime = 0;
        this.yOffset = 0.0F;
        this.itemAssigned = ItemList.enderEye;
        this.canGlow = true;
        this.gravityForce = 0.0;
    }

    public void setFacing(double x, double y, double z) {
        double distX = x - this.posX;
        double distZ = z - this.posZ;
        float distTotal = MathHelper.sqrt_double(distX * distX + distZ * distZ);
        if (distTotal > 12.0F) {
            this.newX = this.posX + distX / distTotal * 12.0;
            this.newZ = this.posZ + distZ / distTotal * 12.0;
            this.newY = this.posY + 8.0;
        } else {
            this.newX = x;
            this.newY = y;
            this.newZ = z;
        }

        this.popTime = 0;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float angle = MathHelper.sqrt_double(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0 / (float) Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, angle) * 180.0 / (float) Math.PI);
        }
    }

    @Override
    public void onTick() {
        this.tick();
        if (!this.world.isOnline) {
            double distX = this.newX - this.posX;
            double distZ = this.newZ - this.posZ;
            float facing = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            float distMax = (float)Math.sqrt(distX * distX + distZ * distZ);
            float throwAngle = (float)Math.atan2(distZ, distX);
            double velocityMultiplier = facing + (distMax - facing) * 0.0025;
            if (distMax < 1.0F) {
                velocityMultiplier *= 0.8;
                this.motionY *= 0.8;
            }

            this.motionX = Math.cos(throwAngle) * velocityMultiplier;
            this.motionZ = Math.sin(throwAngle) * velocityMultiplier;
            if (this.posY < this.newY) {
                this.motionY = this.motionY + (1.0 - this.motionY) * 0.015F;
            } else {
                this.motionY = this.motionY + (-1.0 - this.motionY) * 0.015F;
            }
        }

        float offset = 0.25F;
        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
                this.world
                    .spawnParticle(
                        ParticleType.BUBBLE,
                        this.posX - this.motionX * offset,
                        this.posY - this.motionY * offset,
                        this.posZ - this.motionZ * offset,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    );
            }
        } else {
            this.world
                .spawnParticle(
                    ParticleType.SMOKE,
                    this.posX - this.motionX * offset + this.rand.nextDouble() * 0.6 - 0.3,
                    this.posY - this.motionY * offset - 0.5,
                    this.posZ - this.motionZ * offset + this.rand.nextDouble() * 0.6 - 0.3,
                    this.motionX,
                    this.motionY,
                    this.motionZ
                );
        }

        if (!this.world.isOnline) {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.popTime++;
            if (this.popTime > 80 && !this.world.isOnline) {
                this.setDead();
                this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(ItemList.enderEye)));
            }
        }
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop != null && mop.entityHit == null) {
            this.motionX = this.motionZ = this.motionY = 0.0;
        }
    }

    @Override
    public float getBrightness(float brightness) {
        return 1.0F;
    }

    @Override
    public int getBrightnessInt(float brightness) {
        return 15728880;
    }

    @Override
    public boolean inRange(double dist) {
        double maxDist = this.boundingBox.getLength() * 4.0;
        maxDist *= 64.0;
        return dist < maxDist * maxDist;
    }
}
