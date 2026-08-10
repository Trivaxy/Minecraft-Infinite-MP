package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;

public class ChargeAt extends Attack {
    public ChargeAt(Mob src, float spd, boolean priority, boolean sprint) {
        super(src, spd, priority, sprint);
    }

    @Override
    public boolean continueExecuting() {
        return !this.attacker.collidedHorizontally && super.continueExecuting();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attacker.collidedHorizontally) {
            this.clearTarget();
            this.attacker.damageEntity(DamageSource.fall, 2);
        }
    }

    @Override
    protected void attack() {
        this.attackTimer = 0;
        super.attack();
        this.clearTarget();
    }

    private void clearTarget() {
        this.attacker.attackTarget = this.attacker.lastTarget = null;
        this.attacker.navigator.clearPath();
    }
}
