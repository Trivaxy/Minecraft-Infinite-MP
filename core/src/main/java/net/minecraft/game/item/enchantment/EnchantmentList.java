package net.minecraft.game.item.enchantment;

import net.minecraft.game.item.enchantment.types.Efficiency;
import net.minecraft.game.item.enchantment.types.Power;
import net.minecraft.game.item.enchantment.types.Protection;
import net.minecraft.game.item.enchantment.types.Sharpness;
import net.minecraft.game.item.enchantment.types.Unbreaking;

public class EnchantmentList {
    public static final Enchantment[] enchantments = new Enchantment[256];
    public static final Enchantment sharpness = new Sharpness(1, 0);
    public static final Enchantment efficiency = new Efficiency(2);
    public static final Enchantment unbreaking = new Unbreaking(3);
    public static final Enchantment protection = new Protection(4, 0);
    public static final Enchantment power = new Power(5);
}
