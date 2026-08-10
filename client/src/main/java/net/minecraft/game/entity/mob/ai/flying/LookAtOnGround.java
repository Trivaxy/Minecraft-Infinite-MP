package net.minecraft.game.entity.mob.ai.flying;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.passive.LookAt;

public class LookAtOnGround extends LookAt {
    public LookAtOnGround(Mob host, Class<?> class2, float f) {
        super(host, class2, f);
    }

    @Override
    public boolean canExecute() {
        return !this.host.flying && super.canExecute();
    }
}
