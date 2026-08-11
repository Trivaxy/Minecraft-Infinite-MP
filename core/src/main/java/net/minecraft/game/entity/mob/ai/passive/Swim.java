package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;

public class Swim extends Action {
    private Mob host;

    public Swim(Mob entity) {
        this.host = entity;
        this.setMutexBits(4);
        entity.navigator.setCanSwim(true);
    }

    @Override
    public boolean canExecute() {
        return this.host.isInWater() || this.host.isInLava();
    }

    @Override
    public void tick() {
        if (this.host.rand.nextFloat() < 0.8F) {
            this.host.jumpControl.setJumping();
        }
    }
}
