package net.minecraft.game.item.enchantment;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Random;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchantment.modifier.AttackModifier;
import net.minecraft.game.item.enchantment.modifier.DamageModifier;
import net.minecraft.game.item.enchantment.modifier.Modifier;

public class EnchantmentHelper {
    private static final Random rand = new Random();
    private static final DamageModifier dmgModifier = new DamageModifier();
    private static final AttackModifier atkModifier = new AttackModifier();

    public static int getLevel(int id, ItemStack stack) {
        if (stack == null) {
            return 0;
        }

        ListTag<CompoundTag> enchantTag = stack.getEnchants();
        if (enchantTag == null) {
            return 0;
        }

        for (int tagIndex = 0; tagIndex < enchantTag.size(); tagIndex++) {
            short eyed = enchantTag.get(tagIndex).getShort("id");
            short lvl = enchantTag.get(tagIndex).getShort("lvl");
            if (eyed == id) {
                return lvl;
            }
        }

        return 0;
    }

    public static int getMaxLevel(int id, ItemStack[] stacks) {
        int lvl = 0;

        for (int stackIndex = 0; stackIndex < stacks.length; stackIndex++) {
            ItemStack stack = stacks[stackIndex];
            int maxLvl = getLevel(id, stack);
            if (maxLvl > lvl) {
                lvl = maxLvl;
            }
        }

        return lvl;
    }

    private static void applyModifier(Modifier mod, ItemStack stack) {
        if (stack != null) {
            ListTag<CompoundTag> enchantTag = stack.getEnchants();
            if (enchantTag != null) {
                for (int tagIndex = 0; tagIndex < enchantTag.size(); tagIndex++) {
                    short id = enchantTag.get(tagIndex).getShort("id");
                    short lvl = enchantTag.get(tagIndex).getShort("lvl");
                    if (EnchantmentList.enchantments[id] != null) {
                        mod.calculateModifier(EnchantmentList.enchantments[id], lvl);
                    }
                }
            }
        }
    }

    private static void applyModifierArray(Modifier mod, ItemStack[] stacks) {
        for (int index = 0; index < stacks.length; index++) {
            ItemStack stack = stacks[index];
            applyModifier(mod, stack);
        }
    }

    public static int getDmgModifier(PlayerInventory inv, DamageSource src) {
        dmgModifier.damage = 0;
        dmgModifier.source = src;
        applyModifierArray(dmgModifier, inv.armorSlots);
        if (dmgModifier.damage > 25) {
            dmgModifier.damage = 25;
        }

        return (dmgModifier.damage + 1 >> 1) + rand.nextInt((dmgModifier.damage >> 1) + 1);
    }

    public static int getAtkModifier(PlayerInventory inv, Mob mob) {
        atkModifier.modifierValue = 0;
        atkModifier.mob = mob;
        applyModifier(atkModifier, inv.getCurrentItem());
        return atkModifier.modifierValue > 0 ? 1 + rand.nextInt(atkModifier.modifierValue) : 0;
    }

    public static int getEfficiencyModifier(PlayerInventory inv) {
        return getLevel(EnchantmentList.efficiency.id, inv.getCurrentItem());
    }

    public static int getUnbreakingModifier(PlayerInventory inv) {
        return getLevel(EnchantmentList.unbreaking.id, inv.getCurrentItem());
    }
}
