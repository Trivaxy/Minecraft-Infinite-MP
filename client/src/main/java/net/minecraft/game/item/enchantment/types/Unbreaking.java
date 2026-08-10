package net.minecraft.game.item.enchantment.types;

import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentType;

public class Unbreaking extends Enchantment {
    public Unbreaking(int id) {
        super(id, EnchantmentType.digger);
        this.setName("unbreaking");
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
