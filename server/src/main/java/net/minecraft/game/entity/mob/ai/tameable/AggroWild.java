package net.minecraft.game.entity.mob.ai.tameable;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.physics.BoundingBox;

public class AggroWild extends Aggro {
    TameableMob animal;

    public AggroWild(Mob target, boolean agro) {
        super(target, agro);
        this.animal = (TameableMob)target;
    }

    @Override
    public boolean canExecute() {
        return !this.animal.isTamed() && super.canExecute();
    }

    @Override
    public boolean continueExecuting() {
        return !this.animal.isTamed() && super.continueExecuting();
    }

    @Override
    protected void aggroAdversaries() {
        if (this.animal instanceof Wolf) {
            for (Entity candidate : this.src
                .world
                .getEntitiesOfType(
                    this.src.getClass(),
                    BoundingBox.getBoundingBox(this.src.posX, this.src.posY, this.src.posZ, this.src.posX + 1.0, this.src.posY + 1.0, this.src.posZ + 1.0)
                        .expand(this.dist, 6.0, this.dist)
                )) {
                Wolf entity = (Wolf)candidate;
                if (this.src != entity && entity.attackTarget == null && entity.calmTime <= 0 && !entity.isTamed()) {
                    entity.attackTarget = entity.lastTarget = this.src.getLastTarget();
                    entity.setAngry(true);
                }
            }
        } else {
            super.aggroAdversaries();
        }
    }
}
