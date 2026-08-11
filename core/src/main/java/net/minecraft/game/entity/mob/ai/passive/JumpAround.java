package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;

public class JumpAround extends Action {
    private Mob mob;

    public JumpAround(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canExecute() {
        return this.mob.onGround;
    }

    @Override
    public void tick() {
        if (!this.mob.navigator.noPath() && !this.mob.isInWater()) {
            this.mob.jumpControl.setJumping();
        }
    }
}
