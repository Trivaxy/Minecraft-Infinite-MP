package net.minecraft.game.item.enchantment.types;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentType;

public class Sharpness extends Enchantment {
    public final int damageType;

    public Sharpness(int id, int typ) {
        super(id, EnchantmentType.weapon);
        this.damageType = typ;
        this.name = "sharpness";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int attackModifier(int val, Mob mob) {
        return this.damageType == 0 ? val * 3 : 0;
    }

    @Override
    public boolean canCombine(Enchantment ench) {
        return !(ench instanceof Sharpness);
    }
}
