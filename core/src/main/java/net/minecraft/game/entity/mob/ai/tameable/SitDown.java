package net.minecraft.game.entity.mob.ai.tameable;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;

public class SitDown extends Action {
    private TameableMob pet;
    private boolean sitState = false;

    public SitDown(TameableMob mob) {
        this.pet = mob;
        this.setMutexBits(5);
    }

    @Override
    public boolean canExecute() {
        if (this.pet.isTamed() && !this.pet.isInWater() && this.pet.onGround) {
            Mob owner = this.pet.getOwner();
            return owner == null ? true : (this.pet.getDistanceSqTo(owner) < 144.0 && owner.getLastTarget() != null ? false : this.sitState);
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.pet.navigator.clearPath();
        this.pet.setSitting(true);
    }

    @Override
    public void stop() {
        this.pet.setSitting(false);
    }

    public void setSitting(boolean sit) {
        this.sitState = sit;
    }

    public boolean getSitting() {
        return this.sitState;
    }
}
