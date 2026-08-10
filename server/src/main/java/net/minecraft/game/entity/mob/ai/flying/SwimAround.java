package net.minecraft.game.entity.mob.ai.flying;

import net.minecraft.game.entity.mob.Mob;

public class SwimAround extends FlyAround {
    public SwimAround(Mob parent, float dist, float spd) {
        super(parent, dist, spd);
    }

    @Override
    public boolean canExecute() {
        return this.mob.isInWater() && super.canExecute();
    }
}
