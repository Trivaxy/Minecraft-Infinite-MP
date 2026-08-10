package net.minecraft.game.item.enchantment.modifier;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.enchantment.Enchantment;

public final class AttackModifier implements Modifier {
    public int modifierValue;
    public Mob mob;

    @Override
    public void calculateModifier(Enchantment ench, int lvl) {
        this.modifierValue = this.modifierValue + ench.attackModifier(lvl, this.mob);
    }
}
