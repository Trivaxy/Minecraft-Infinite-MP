package net.minecraft.game.entity.mob.animal.aquatic;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.flying.FlyAround;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public abstract class AquaticMob extends Animal {
    public byte airTimer = 100;

    public AquaticMob(World world) {
        super(world);
        this.produceFootsteps = false;
        this.moveControl = new FlightControl(this);
        this.behaviors.addTask(5, new FlyAround(this, 6.0F, 0.1F));
        this.behaviors.addTask(6, new LookAroundFlying(this, true));
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.AQUATIC;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.isClear(this.boundingBox) && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0 && this.isInWater();
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.isInWater() && this.canBreatheUnderwater()) {
            if (!this.hasEffect(EffectList.waterBreathing) && this.airTimer-- <= 0) {
                this.damageEntity(DamageSource.drown, 1);
                this.airTimer = 0;
            }

            if (this.onGround) {
                double speed = 0.1;
                double angle = this.rand.nextDouble() * Math.PI * 2.0;
                this.motionX = this.motionX + Math.cos(angle) * speed;
                this.motionZ = this.motionZ + Math.sin(angle) * speed;
                this.jump();
            }
        } else {
            this.airTimer = 100;
        }
    }

    @Override
    public void knockBack(Entity entity, double forceX, double forceZ) {
        if (!this.isInWater()) {
            super.knockBack(entity, forceX, forceZ);
        }
    }

    @Override
    public String getSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setByte("AirTime", this.airTimer);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.airTimer = tag.getByte("AirTime");
    }

    private void moveSwimming(float x, float z) {
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

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (this.isDead || this.health <= 0) {
            super.moveEntityWithHeading(x, z);
        } else if (this.isInWater()) {
            this.moveSwimming(x, z);
        } else if (this.isInLava()) {
            this.moveFlying(x, z, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            this.moveSwimming(x, z);
            this.motionY = (float)(this.motionY - 0.08);
        }
    }
}
