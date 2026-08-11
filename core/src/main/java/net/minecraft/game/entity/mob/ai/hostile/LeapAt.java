package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.util.math.MathHelper;

public class LeapAt extends Action {
    Mob leaper;
    Mob target;
    float force;
    int attackTimer;

    public LeapAt(Mob mob, float f) {
        this.leaper = mob;
        this.force = f;
        this.setMutexBits(5);
    }

    @Override
    public boolean canExecute() {
        this.target = this.leaper.attackTarget;
        if (this.target == null) {
            return false;
        }

        double dist = this.leaper.getDistanceSqTo(this.target);
        return dist >= 4.0 && dist <= 16.0 ? (!this.leaper.onGround ? false : this.leaper.rand.nextInt(5) == 0) : false;
    }

    @Override
    public boolean continueExecuting() {
        return !this.leaper.onGround;
    }

    @Override
    public void start() {
        double distX = this.target.posX - this.leaper.posX;
        double distZ = this.target.posZ - this.leaper.posZ;
        double dist = MathHelper.sqrt_double(distX * distX + distZ * distZ);
        this.leaper.motionX = this.leaper.motionX + (distX / dist * 0.5 * 0.8 + this.leaper.motionX * 0.2);
        this.leaper.motionZ = this.leaper.motionZ + (distZ / dist * 0.5 * 0.8 + this.leaper.motionZ * 0.2);
        this.leaper.motionY = this.force;
    }

    @Override
    public void tick() {
        this.leaper.lookControl.faceEntity(this.target, 30.0F, 30.0F);
        this.attackTimer = Math.max(this.attackTimer - 1, 0);
        double dist = this.leaper.width * 2.0F * this.leaper.width * 2.0F;
        if (this.leaper.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ) <= dist
            && this.leaper.senses.canSee(this.target)
            && this.attackTimer <= 0) {
            this.attackTimer = 20;
            int dmg = this.leaper.attackStrength;
            if (this.leaper.hasEffect(EffectList.strength)) {
                dmg += 3 << this.leaper.getEffect(EffectList.strength).getPotionAmplifier();
            }

            if (this.leaper.hasEffect(EffectList.weakness)) {
                dmg -= 2 << this.leaper.getEffect(EffectList.weakness).getPotionAmplifier();
            }

            this.target.damageEntity(DamageSource.mobDamage(this.leaper), dmg);
        }
    }
}
