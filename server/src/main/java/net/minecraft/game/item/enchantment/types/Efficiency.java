package net.minecraft.game.item.enchantment.types;

import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentType;

public class Efficiency extends Enchantment {
    public Efficiency(int id) {
        super(id, EnchantmentType.digger);
        this.setName("efficiency");
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
