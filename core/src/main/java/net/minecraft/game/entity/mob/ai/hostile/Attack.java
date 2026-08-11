package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.util.math.MathHelper;

public class Attack extends Action {
    protected Path path;
    protected Mob attacker;
    protected Mob target;
    protected int attackTimer;
    float friction;
    boolean prioritize;
    private int probability;
    private int sprintTimer;
    private float sprint;
    private int effect;
    private int duration;
    private int level;
    private boolean canSprint = false;

    public Attack(Mob target, float range, boolean chase, int effect, int duration, int level, boolean sprint) {
        this(target, range, chase, sprint);
        this.effect = effect;
        this.duration = duration;
        this.level = level;
    }

    public Attack(Mob src, float spd, boolean priority, boolean sprint) {
        this.attackTimer = 0;
        this.attacker = src;
        this.friction = spd;
        this.prioritize = priority;
        this.canSprint = sprint;
        this.setMutexBits(3);
    }

    @Override
    public boolean canExecute() {
        Mob tgt = this.attacker.attackTarget;
        if (tgt == null) {
            return false;
        }

        this.target = tgt;
        this.path = this.attacker.navigator.pathToTarget(this.target);
        return this.path != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.attacker.attackTarget == null || this.target == null
            ? false
            : (
                !this.target.isAlive()
                    ? false
                    : (
                        !this.prioritize
                            ? !this.attacker.navigator.noPath()
                            : this.attacker
                                .withinTrackingDist(
                                    MathHelper.floor_double(this.target.posX),
                                    MathHelper.floor_double(this.target.posY),
                                    MathHelper.floor_double(this.target.posZ)
                                )
                    )
            );
    }

    @Override
    public void start() {
        this.attacker.navigator.setPath(this.path, this.friction);
        this.probability = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        this.attacker.navigator.clearPath();
    }

    @Override
    public void tick() {
        if (this.target != null && this.attacker.world.settings.getDifficulty() > 1 && this.attacker.rand.nextFloat() < 0.01F && this.canSprint) {
            this.sprintTimer = 100;
        }

        if (this.sprintTimer > 0) {
            this.sprint = this.friction + this.friction / 2.0F;
            this.sprintTimer--;
        } else {
            this.sprint = this.friction;
        }

        this.attacker.lookControl.faceEntity(this.target, 30.0F, 30.0F);
        if ((this.prioritize || this.attacker.senses.canSee(this.target)) && --this.probability <= 0) {
            this.probability = 4 + this.attacker.rand.nextInt(7);
            this.attacker.navigator.moveToTarget(this.target, this.sprint);
        }

        this.attackTimer = Math.max(this.attackTimer - 1, 0);
        double dist = this.attacker.width * 2.0F * this.attacker.width * 2.0F + this.target.width;
        if (this.attacker.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ) <= dist && this.attacker.senses.canSee(this.target)) {
            this.attack();
        }
    }

    protected void attack() {
        if (this.attackTimer <= 0) {
            this.attackTimer = 20;
            int dmg = this.attacker.attackStrength;
            if (this.attacker.hasEffect(EffectList.strength)) {
                dmg += 3 << this.attacker.getEffect(EffectList.strength).getPotionAmplifier();
            }

            if (this.attacker.hasEffect(EffectList.weakness)) {
                dmg -= 2 << this.attacker.getEffect(EffectList.weakness).getPotionAmplifier();
            }

            this.target.damageEntity(DamageSource.mobDamage(this.attacker), dmg);
            if (this.attacker.fire > 0) {
                this.target.fire = 60;
            }

            if (this.effect > 0) {
                this.target.addEffect(new EffectData(this.effect, this.duration, this.level));
            }

            this.attacker.attackFrames = 10;
        }
    }
}
