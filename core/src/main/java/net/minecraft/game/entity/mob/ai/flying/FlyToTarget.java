package net.minecraft.game.entity.mob.ai.flying;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.item.potion.EffectList;

public class FlyToTarget extends Action {
    protected Mob mob;
    protected float spd;
    protected int attackTimer = 0;

    public FlyToTarget(Mob parent, float dist) {
        this.mob = parent;
        this.spd = dist;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        return this.mob.attackTarget != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.mob.attackTarget != null;
    }

    @Override
    public void stop() {
        this.mob.attackFrames = 0;
    }

    @Override
    public void tick() {
        this.mob.moveControl.moveTo(this.mob.attackTarget.posX, this.mob.attackTarget.posY, this.mob.attackTarget.posZ, this.spd);
        this.attackTimer = Math.max(this.attackTimer - 1, 0);
        double dist = this.mob.width * 2.0F * this.mob.width * 2.0F;
        if (this.mob.getDistanceSq(this.mob.attackTarget.posX, this.mob.attackTarget.posY, this.mob.attackTarget.posZ) <= dist) {
            this.attack();
        }
    }

    protected void attack() {
        if (this.attackTimer <= 0) {
            this.attackTimer = 20;
            int dmg = this.mob.attackStrength;
            if (this.mob.hasEffect(EffectList.strength)) {
                dmg += 3 << this.mob.getEffect(EffectList.strength).getPotionAmplifier();
            }

            if (this.mob.hasEffect(EffectList.weakness)) {
                dmg -= 2 << this.mob.getEffect(EffectList.weakness).getPotionAmplifier();
            }

            this.mob.attackTarget.damageEntity(DamageSource.mobDamage(this.mob), dmg);
            this.mob.attackFrames = 10;
        }
    }
}
