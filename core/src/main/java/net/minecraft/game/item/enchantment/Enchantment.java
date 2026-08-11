package net.minecraft.game.item.enchantment;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.localization.Translator;

public abstract class Enchantment {
    public EnchantmentType type;
    public String name;
    public final int id;

    protected Enchantment(int id, EnchantmentType typ) {
        this.id = id;
        this.type = typ;
        if (EnchantmentList.enchantments[id] != null) {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        }

        EnchantmentList.enchantments[id] = this;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int damageModifier(int val, DamageSource src) {
        return 0;
    }

    public int attackModifier(int val, Mob mob) {
        return 0;
    }

    public boolean canCombine(Enchantment enchant) {
        return this != enchant;
    }

    public Enchantment setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return "enchantment." + this.name;
    }

    public String getTranslatedName(int lvl) {
        String name = Translator.getInstance().translateKey(this.getName());
        String level = Translator.getInstance().translateKey("enchantment.level." + lvl);
        if (lvl > 5) {
            level = "" + lvl;
        }

        return name + " " + level;
    }
}
