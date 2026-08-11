package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.ai.flying.FlyAround;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.util.FlightControl;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Phantom extends Monster {
    public Phantom(World world) {
        super(world);
        this.setHealth(12);
        this.texture = "/mob/ghost/phantom.png";
        this.moveSpeed = 0.1F;
        this.attackStrength = 4;
        this.scoreValue = 130;
        this.noClip = true;
        this.moveControl = new FlightControl(this, false);
        this.behaviors.addTask(3, new FlyToTarget(this, this.moveSpeed));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, false, false));
        this.behaviors.addTask(5, new FlyAround(this, 6.0F, this.moveSpeed));
        this.behaviors.addTask(6, new LookAroundFlying(this, false));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 32.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 32.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 39;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public void mobTick() {
        if (this.world.isDaytime()) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F
                && this.world.isBlockExposed(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                this.fire = 300;
            }
        }

        for (int i = 0; i < 10; i++) {
            this.world
                .spawnParticle(
                    ParticleType.SMOKE,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.rand.nextFloat() * 0.02F,
                    this.rand.nextFloat() * 0.02F,
                    this.rand.nextFloat() * 0.02F
                );
        }

        if (this.attackTarget != null && this.ticksExisted % 150 == 0) {
            this.attackTarget.addEffect(new EffectData(EffectList.fatigue.id, 150, 0));
            this.attackTarget.addEffect(new EffectData(EffectList.slowness.id, 150, 0));
            this.attackTarget.addEffect(new EffectData(EffectList.fragility.id, 150, 0));
            this.attackTarget.addEffect(new EffectData(EffectList.weakness.id, 150, 0));
        }

        super.mobTick();
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public void moveEntityWithHeading(float x, float z) {
        if (!this.isDead && this.health > 0) {
            if (this.isInWater()) {
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
                float force = 0.91F;
                if (this.onGround) {
                    force = 0.54600006F;
                }

                float offset = 0.16277136F / (force * force * force);
                this.moveFlying(x, z, this.onGround ? 0.1F * offset : 0.02F);
                force = 0.91F;
                if (this.onGround) {
                    force = 0.54600006F;
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= force;
                this.motionY *= force;
                this.motionZ *= force;
            }

            this.prevLimbYaw = this.limbYaw;
            double moveX = this.posX - this.prevX;
            double moveZ = this.posZ - this.prevZ;
            float swing = MathHelper.sqrt_double(moveX * moveX + moveZ * moveZ) * 4.0F;
            if (swing > 1.0F) {
                swing = 1.0F;
            }

            this.limbYaw = this.limbYaw + (swing - this.limbYaw) * 0.4F;
            this.limbSwing = this.limbSwing + this.limbYaw;
        } else {
            super.moveEntityWithHeading(x, z);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInsideBlock() {
        return false;
    }

    @Override
    public void knockBack(Entity entity, double force, double knockback) {
    }

    @Override
    public final String getSound() {
        return "mob.phantom";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.phantom";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.phantom";
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return y <= 32 && this.world.getBlockBrightness(x, y, z) <= this.rand.nextInt(8);
    }

    @Override
    public boolean canSpawnNextToBed() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockBrightness(x, y, z) <= 4 && super.canSpawnHere();
    }

    @Override
    public int getMaxSpawns() {
        return 1;
    }
}
