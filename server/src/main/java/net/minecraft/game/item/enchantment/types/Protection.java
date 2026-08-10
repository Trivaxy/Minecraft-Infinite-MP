package net.minecraft.game.item.enchantment.types;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentType;

public class Protection extends Enchantment {
    public final int protectionType;

    public Protection(int id, int type) {
        super(id, EnchantmentType.armor);
        this.protectionType = type;
        if (type == 2) {
            this.type = EnchantmentType.armor_feet;
        }

        this.name = "protection";
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int damageModifier(int value, DamageSource src) {
        if (src.hurtCreative()) {
            return 0;
        }

        int amount = (6 + value * value) / 2;
        return this.protectionType == 0 ? amount : 0;
    }

    @Override
    public boolean canCombine(Enchantment ench) {
        if (!(ench instanceof Protection)) {
            return super.canCombine(ench);
        }

        Protection prot = (Protection)ench;
        return prot.protectionType == this.protectionType ? false : this.protectionType == 2 || prot.protectionType == 2;
    }
}
