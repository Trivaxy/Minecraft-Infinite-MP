package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;

public class RestrictSun extends Action {
    private Mob entity;

    public RestrictSun(Mob mob) {
        this.entity = mob;
    }

    @Override
    public boolean canExecute() {
        return !this.entity.world.isRaining() && this.entity.world.isDaytime() && this.entity.attackTarget == null;
    }

    @Override
    public void start() {
        this.entity.navigator.setAvoidSun(true);
    }

    @Override
    public void stop() {
        this.entity.navigator.setAvoidSun(false);
    }
}
