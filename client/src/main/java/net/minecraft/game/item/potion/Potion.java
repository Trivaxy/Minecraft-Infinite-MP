package net.minecraft.game.item.potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.ThrownPotion;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;

public class Potion extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static List<Icon> iconsSplash = new ArrayList<>();
    private HashMap<Integer, List<EffectData>> effectCache = new HashMap<>();
    private boolean isSplash = false;

    public Potion(int id, boolean splash) {
        super(id);
        this.setHasSubtypes(true);
        this.maxDamage = 0;
        this.isSplash = this.canSwing = splash;
        this.priorityItem = true;

        for (int i = 0; i < 6; i++) {
            this.info.add("");
        }
    }

    public List<EffectData> getEffects(int metadata) {
        List<EffectData> effectsList = this.effectCache.get(metadata);
        if (effectsList == null) {
            effectsList = BrewingSystem.getPotionEffect(metadata);
            this.effectCache.put(metadata, effectsList);
        }

        return effectsList;
    }

    @Override
    public ItemStack onFinishedUsing(ItemStack stack, World world, Player player) {
        if (!world.isOnline) {
            if (stack.itemDmg > this.getMaxDamage()) {
                List<EffectData> effects = this.getEffects(stack.itemDmg);
                if (effects != null) {
                    for (EffectData pe : effects) {
                        player.addEffect(new EffectData(pe));
                    }
                }
            } else {
                player.addEffect(new EffectData(stack.getItemDamage(), EffectList.effects[stack.getItemDamage()].defaultDuration, 0));
            }
        }

        player.addStat(StatList.useStats[this.id], 1);
        return player.properties.gamemode == 1 ? stack : new ItemStack(ItemList.glassBottle);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (!this.isSplash) {
            player.setItemInUse(stack, this.getMaxUseDuration(stack));
            return stack;
        }

        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        world.playEntitySound(player, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
        if (!world.isOnline) {
            world.spawnEntity(new ThrownPotion(world, player, stack.getItemDamage()));
        }

        if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
            player.swingOffhandItem();
        } else {
            player.swingItem();
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public int getMaxDamage() {
        return 19;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getItemDamage() > 19;
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        List<Icon> icns = icons;
        if (this.isSplash) {
            icns = iconsSplash;
        }

        return meta >= icns.size() ? icns.get(meta % this.getMaxDamage()) : icns.get(meta);
    }

    @Override
    public void registerIcons(IconRegister register) {
        for (int i = 0; i <= this.getMaxDamage(); i++) {
            icons.add(register.registerIcon("potion/potion_" + i));
            iconsSplash.add(register.registerIcon("potion/splash_" + i));
        }
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.drink;
    }

    @Override
    public String getNameFromMeta(int damage) {
        String name = BrewingSystem.potionNames(damage);
        String prefix = "";
        if (this.isSplash) {
            prefix = "splash.";
        }

        if (damage <= this.getMaxDamage()) {
            name = EffectList.effects[damage].getPotionName();
        }

        return "item." + prefix + name;
    }

    @Override
    public void addInformation(ItemStack stack) {
        for (int i = 0; i < this.info.size(); i++) {
            this.info.set(i, "");
        }

        List<EffectData> effectsList = this.getEffects(stack.itemDmg);
        if (stack.itemDmg <= this.getMaxDamage()) {
            String size = "";
            if (EffectList.effects[stack.itemDmg].defaultDuration > 20) {
                if (this.isSplash) {
                    size = Effect.getDurationString(EffectList.effects[stack.itemDmg].defaultDuration / 2);
                } else {
                    size = Effect.getDurationString(EffectList.effects[stack.itemDmg].defaultDuration);
                }
            }

            this.info.set(0, size + Translator.getInstance().translateKey(EffectList.effects[stack.itemDmg].getPotionName()));
        } else if (effectsList != null) {
            for (int i = 0; i < effectsList.size(); i++) {
                EffectData effect = effectsList.get(i);
                String str = "";
                if (effect.getPotionDuration() > 20) {
                    str = Effect.getDurationString(effect);
                }

                this.info.set(i, str + Translator.getInstance().translateKey(effect.getPotionName()));
            }
        }
    }
}
