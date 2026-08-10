package net.minecraft.game.item.enchantment;

import net.minecraft.game.item.Item;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.weapons.Bow;
import net.minecraft.game.item.weapons.Sword;

public enum EnchantmentType {
    all,
    armor,
    armor_feet,
    armor_legs,
    armor_torso,
    armor_head,
    weapon,
    digger,
    bow;

    public boolean canEnchantItem(Item item) {
        if (this == all) {
            return true;
        }

        if (item instanceof Armor) {
            if (this == EnchantmentType.armor) {
                return true;
            }

            Armor armor = (Armor)item;
            return armor.type == 0
                ? this == armor_head
                : (armor.type == 2 ? this == armor_legs : (armor.type == 1 ? this == armor_torso : (armor.type == 3 ? this == armor_feet : false)));
        } else {
            return item instanceof Sword ? this == weapon : (item instanceof Tool ? this == digger : (item instanceof Bow ? this == bow : false));
        }
    }
}
