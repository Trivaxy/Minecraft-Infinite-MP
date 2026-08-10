package net.minecraft.game.entity.mob.animal.flying;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.world.World;

public abstract class FlyingMob extends Animal {
    protected FlightControl flyMovement = new FlightControl(this);
    public float lastGroundPos = 0.0F;
    public float destPos = 0.0F;
    public float startPos;
    public float newPos;
    public float fallDMG = 1.0F;

    public FlyingMob(World world) {
        super(world);
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.newPos = this.lastGroundPos;
        this.startPos = this.destPos;
        this.destPos = (float)(this.destPos + (this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0F) {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F) {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.fallDMG < 1.0F) {
            this.fallDMG = 1.0F;
        }

        this.fallDMG = (float)(this.fallDMG * 0.9);
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        this.lastGroundPos = this.lastGroundPos + this.fallDMG * 2.0F;
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (!this.flying || this.isDead || this.health <= 0) {
            super.moveEntityWithHeading(x, z);
        } else if (this.isInWater()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
        } else if (this.isInLava()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            float spdFactor = 0.91F;
            if (this.onGround) {
                spdFactor = 0.54600006F;
            }

            float spd = 0.16277136F / (spdFactor * spdFactor * spdFactor);
            this.moveFlying(x, z, this.onGround ? 0.1F * spd : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= spdFactor;
            this.motionY *= spdFactor;
            this.motionZ *= spdFactor;
        }
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public void onDeath() {
        super.onDeath();
        this.flying = false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Flying", this.flying);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.flying = tag.getBoolean("Flying");
        if (this.flying) {
            this.moveControl = this.flyMovement;
        }
    }
}
