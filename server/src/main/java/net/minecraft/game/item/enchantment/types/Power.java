package net.minecraft.game.item.enchantment.types;

import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentType;

public class Power extends Enchantment {
    public Power(int id) {
        super(id, EnchantmentType.bow);
        this.setName("power");
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
