package net.minecraft.game.entity.mob.animal.mounts;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public abstract class RideableMob extends TameableMob {
    protected double rideSpeed = 1.0;

    public RideableMob(World world) {
        super(world);
        this.stepHeight = 1.0F;
    }

    @Override
    public void updateAI() {
        if (this.rider != null) {
            this.ride();
        } else {
            if (this.isRideable()) {
                this.motionX /= 8.0;
                this.motionZ /= 8.0;
            }

            super.updateAI();
        }
    }

    protected void ride() {
        this.moveForward = 0.0F;
        this.moveStrafing = 0.0F;
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.motionX = this.motionX + this.rider.motionX * this.rideSpeed;
        this.motionZ = this.motionZ + this.rider.motionZ * this.rideSpeed;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.setRotation(this.rider.yaw, this.rider.pitch);
        this.headFacing = this.yaw;
        this.jumping = this.rider.jumping;
        if (this.isInWater() && this.collidedHorizontally) {
            this.motionY = 0.3F;
        }

        if (this.rider.isDead) {
            this.rider = null;
        }
    }

    @Override
    public void rotateBody() {
        if (this.rider != null && ((Mob)this.rider).behaviors.tasks.isEmpty()) {
            double x = this.posX - this.prevX;
            double z = this.posZ - this.prevZ;
            float dist = MathHelper.sqrt_double(x * x + z * z);
            float yaw = this.yawOffset;
            if (dist > 0.05F) {
                yaw = (float)Math.atan2(z, x) * 180.0F / (float) Math.PI - 90.0F;
            }

            if (this.swing > 0.0F) {
                yaw = this.yaw;
            }

            float newYaw = yaw - this.yawOffset;

            while (newYaw < -180.0F) {
                newYaw += 360.0F;
            }

            while (newYaw >= 180.0F) {
                newYaw -= 360.0F;
            }

            this.yawOffset += newYaw * 0.1F;
            float yaw2 = this.yaw - this.yawOffset;

            while (yaw2 < -180.0F) {
                yaw2 += 360.0F;
            }

            while (yaw2 >= 180.0F) {
                yaw2 -= 360.0F;
            }

            if (yaw2 < -75.0F) {
                yaw2 = -75.0F;
            }

            if (yaw2 >= 75.0F) {
                yaw2 = 75.0F;
            }

            this.yawOffset = this.yaw - yaw2;
            this.yawOffset += yaw2 * 0.1F;
        } else {
            super.rotateBody();
        }
    }

    protected void AIOverride() {
        super.updateAI();
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(15, (byte)0);
    }

    @Override
    public boolean isRideable() {
        return (this.dataSync.getObjectByte(15) & 1) != 0;
    }

    public void setRideable(boolean success) {
        byte state = this.dataSync.getObjectByte(15);
        if (success) {
            this.dataSync.updateObject(15, (byte)(state | 1));
        } else {
            this.dataSync.updateObject(15, (byte)(state & -2));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Saddled", this.isRideable());
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.setRideable(tag.getBoolean("Saddled"));
    }

    @Override
    public void playSound() {
        if (this.getSound() != null && this.rider == null) {
            this.world.playEntitySound(this, this.getSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }
}
