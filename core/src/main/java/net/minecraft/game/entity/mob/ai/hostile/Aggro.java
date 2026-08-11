package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.BoundingBox;

public class Aggro extends ChooseTarget {
    boolean aggroAdversaries;

    public Aggro(Mob target, boolean agro) {
        super(target, 16.0F, false);
        this.aggroAdversaries = agro;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        return this.foundValidTarget(this.src.getLastTarget(), true);
    }

    @Override
    public boolean continueExecuting() {
        return this.src.lastTarget != null && this.src.lastTarget == this.src.attackTarget;
    }

    @Override
    public void start() {
        if (this.src.calmTime <= 0) {
            this.src.attackTarget = this.src.getLastTarget();
            this.src.navigator.clearPath();
        }

        if (this.aggroAdversaries) {
            this.aggroAdversaries();
        }

        super.start();
    }

    protected void aggroAdversaries() {
        for (Entity candidate : this.src
            .world
            .getEntitiesOfType(
                this.src.getClass(),
                BoundingBox.getBoundingBox(this.src.posX, this.src.posY, this.src.posZ, this.src.posX + 1.0, this.src.posY + 1.0, this.src.posZ + 1.0)
                    .expand(this.dist, 6.0, this.dist)
            )) {
            Mob entity = (Mob)candidate;
            if (this.src != entity && entity.attackTarget == null && entity.calmTime <= 0) {
                entity.attackTarget = entity.lastTarget = this.src.getLastTarget();
            }
        }
    }
}
