package net.minecraft.game.entity.mob.ai.tameable;

import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;

public class ChooseWildTarget extends DetectEnemy {
    private TameableMob entity;

    public ChooseWildTarget(TameableMob mob, Class<?> class2, float f, int i, boolean b) {
        super(mob, class2, f, i, b);
        this.entity = mob;
    }

    @Override
    public boolean canExecute() {
        return this.entity.isTamed() ? false : super.canExecute();
    }
}
