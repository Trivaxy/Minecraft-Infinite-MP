package net.minecraft.game.entity.mob.monster.elemental;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.animal.aquatic.AquaticMob;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Flood extends AquaticMob {
    private int attackTime;

    public Flood(World world) {
        super(world);
        this.texture = "/mob/elemental/water_elemental.png";
        this.setHealth(20);
        this.moveSpeed = 1.2F;
        this.attackStrength = 2;
        this.setSize(1.0F, 1.8F);
        this.produceFootsteps = false;
        this.scoreValue = 100;
        this.behaviors.addTask(3, new Flood.KineticAttack(this, 32.0F, this.attackStrength));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void mobTick() {
        super.mobTick();
        Player nearestPlayer = this.world.getClosestPlayer(this, 32.0);
        if (nearestPlayer != null && this.boss) {
            nearestPlayer.addEffect(new EffectData(EffectList.miningFatigue.id, 500, 0));
        }

        if (this.isInWater()) {
            this.world
                .spawnParticle(
                    ParticleType.BUBBLE,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.rand.nextFloat() * 0.02F,
                    this.rand.nextFloat() * 0.02F,
                    this.rand.nextFloat() * 0.02F
                );
        }
    }

    @Override
    public int getSpawnID() {
        return 51;
    }

    @Override
    protected String getHurtSound() {
        return "mob.water";
    }

    @Override
    protected String getDeathSound() {
        return "mob.waterdeath";
    }

    @Override
    public float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public int getDrop() {
        return ItemList.seaPickle.id;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Boss", this.boss);
        tag.setInteger("AttackTimer", this.attackTime);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.boss = tag.getBoolean("Boss");
        this.attackTime = tag.getInteger("AttackTimer");
    }

    private class KineticAttack extends Action {
        private Flood flood;
        float dist;
        int dmg;

        private KineticAttack(Flood flood, float dist, int attack) {
            this.flood = flood;
            this.dmg = attack;
            this.dist = dist;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            return this.flood.attackTarget != null && this.flood.getDistanceTo(this.flood.attackTarget) < this.dist;
        }

        @Override
        public void tick() {
            Entity target = this.flood.attackTarget;
            if (this.flood.isVisible(target) && this.flood.attackTime <= 0) {
                if (this.flood.boss) {
                    this.flood.attackTime = 200;
                    this.dmg *= 2;
                } else {
                    this.flood.attackTime = 400;
                }

                target.damageEntity(DamageSource.mobDamage(this.flood), this.dmg);
            }

            if (this.flood.attackTime > 0) {
                --this.flood.attackTime;
            }
        }
    }
}
