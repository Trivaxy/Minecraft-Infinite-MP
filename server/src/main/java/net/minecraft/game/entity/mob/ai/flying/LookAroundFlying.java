package net.minecraft.game.entity.mob.ai.flying;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;

public class LookAroundFlying extends Action {
    private Mob mob;
    boolean carnivorous;

    public LookAroundFlying(Mob mob, boolean bool) {
        this.mob = mob;
        this.carnivorous = bool;
        this.setMutexBits(2);
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.attackTarget != null
            && (!this.carnivorous || this.mob.attackTarget.isInWater() && this.mob.attackTarget.health < this.mob.attackTarget.maxHealth / 2)) {
            Mob attackTarget = this.mob.attackTarget;
            double maxDist = 64.0;
            if (attackTarget.getDistanceSqTo(this.mob) < maxDist * maxDist) {
                double distX = attackTarget.posX - this.mob.posX;
                double distZ = attackTarget.posZ - this.mob.posZ;
                this.mob.yaw = this.mob.yawOffset = -((float)Math.atan2(distX, distZ)) * (180.0F / (float)Math.PI);
            }
        } else {
            this.mob.yaw = -((float)Math.atan2(this.mob.motionX, this.mob.motionZ)) * (180.0F / (float)Math.PI);
        }
    }
}
