package net.minecraft.game.item.enchantment.modifier;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.item.enchantment.Enchantment;

public final class DamageModifier implements Modifier {
    public int damage;
    public DamageSource source;

    @Override
    public void calculateModifier(Enchantment ench, int lvl) {
        this.damage = this.damage + ench.damageModifier(lvl, this.source);
    }
}
