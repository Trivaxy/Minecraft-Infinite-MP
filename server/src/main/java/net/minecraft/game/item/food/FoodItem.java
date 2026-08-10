package net.minecraft.game.item.food;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class FoodItem extends Item {
    public final int healAmount;
    private int effectId;
    private int effectDuration;
    private int effectAmplifier;
    public float saturationAmount;
    protected boolean alwaysEdible;

    public FoodItem(int itemid, int healAmount, float saturationAmount) {
        super(itemid);
        this.healAmount = healAmount;
        this.saturationAmount = saturationAmount;
        this.info.add(this.healAmount + " item.desc.heal");
        this.priorityItem = true;
        this.canSwing = false;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.canEat(this.alwaysEdible)) {
            player.setItemInUse(stack, this.getMaxUseDuration(stack));
        }

        return stack;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int face) {
        boolean canPlaceHere = world.getBlockId(x, y, z) == BlockList.tilledField.id;
        int placeID = 0;
        Block sndBlock = BlockList.susDirt;
        if (this.id == ItemList.potato.id) {
            placeID = BlockList.potatoes.id;
        } else if (this.id == ItemList.carrot.id) {
            placeID = BlockList.carrots.id;
        } else {
            if (this.id != ItemList.wax.id) {
                return false;
            }

            placeID = BlockList.glowRoots.id;
            canPlaceHere = world.getBlockId(x, y, z) == BlockList.ember.id || world.getBlockId(x, y, z) == BlockList.glowRoots.id;
            sndBlock = BlockList.grass;
        }

        if (canPlaceHere && world.getBlockId(x, y + 1, z) == 0) {
            world.setBlockWithNotify(x, y + 1, z, placeID);
            StepSound snd = sndBlock.getStepSound(0);
            world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack onFinishedUsing(ItemStack stack, World world, Player player) {
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        player.getFoodStats().addStats(this);
        player.addStat(StatList.useStats[this.id], 1);
        player.onFoodEaten();
        if (!player.properties.getHunger()) {
            player.queueHealing(this.healAmount);
            player.addEffect(new EffectData(EffectList.nausea.id, this.healAmount * 20, 0));
        }

        if (this.id == ItemList.appleGold.id) {
            player.heal(player.maxHealth);
            player.addHealth(10);
            player.addEffect(new EffectData(EffectList.absorption.id, 1200, 0));
            player.addEffect(new EffectData(EffectList.regeneration.id, 900, 1));
            player.addEffect(new EffectData(EffectList.resistance.id, 1800, 0));
            player.triggerAchievement(AchievementList.overpowered);
        } else if (this.id == ItemList.seaPickle.id) {
            player.air = player.maxAir;
            if (player.isInWater()) {
                player.triggerAchievement(AchievementList.seaPickle);
            }
        } else {
            player.addEffect(new EffectData(this.effectId, this.effectDuration, this.effectAmplifier));
        }

        world.playEntitySound(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        return stack;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 8 * Math.min(this.healAmount, 4);
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.eat;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return this == ItemList.appleGold;
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this.id == ItemList.fishCooked.id) {
            player.triggerAchievement(AchievementList.cookFish);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("foods/" + this.internalName);
    }

    public FoodItem setAlwaysEdible() {
        this.alwaysEdible = true;
        return this;
    }

    public FoodItem setPotionEffect(int id, int dur, int amp) {
        this.effectId = id;
        this.effectDuration = dur;
        this.effectAmplifier = amp;
        this.info.add(Translator.getInstance().translateKey(EffectList.effects[id].getPotionName()));
        return this;
    }
}
