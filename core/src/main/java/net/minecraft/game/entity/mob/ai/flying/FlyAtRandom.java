package net.minecraft.game.entity.mob.ai.flying;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.util.MoveControl;

public class FlyAtRandom extends FlyAround {
    private MoveControl moveHelper = new MoveControl(this.mob);

    public FlyAtRandom(Mob parent, float dist, float spd) {
        super(parent, dist, spd);
    }

    @Override
    public boolean canExecute() {
        return this.mob.flying && super.canExecute();
    }

    @Override
    public void start() {
        if (!this.mob.navigator.noPath()) {
            this.mob.navigator.clearPath();
        }

        if ((this.mob.rand.nextInt(10) == 0 || this.mob.isInWater()) && this.mob.attackTarget == null) {
            this.mob.flying = false;
            this.mob.moveControl = this.moveHelper;
        } else {
            super.start();
        }
    }
}
