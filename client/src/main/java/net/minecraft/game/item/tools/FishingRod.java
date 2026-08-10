package net.minecraft.game.item.tools;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class FishingRod extends Item {
    private Icon altIcon;

    public FishingRod(int id) {
        super(id);
        this.maxDamage = 64;
        this.maxStackSize = 1;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.priorityItem = true;
    }

    public boolean isDamagable() {
        return true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.bobber != null) {
            stack.damageItem(player.bobber.reelIn(), player);
            if (player.inventory.getOffHandItem() != stack) {
                player.swingItem();
            } else {
                player.swingOffhandItem();
            }
        } else {
            world.playEntitySound(player, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
            if (!world.isOnline) {
                if (player.inventory.consumeInventoryItem(ItemList.rottenFlesh.id)) {
                    world.spawnEntity(new Bobber(world, player, true));
                    player.triggerAchievement(AchievementList.bait);
                } else {
                    world.spawnEntity(new Bobber(world, player, false));
                }
            }

            if (player.inventory.getOffHandItem() != stack) {
                player.swingItem();
            } else {
                player.swingOffhandItem();
            }
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon(this.internalName);
        this.altIcon = ir.registerIcon("reel");
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return meta == -1 ? this.altIcon : this.itemIcon;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == ItemList.silk.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return 100;
    }

    @Override
    public void triggerAchievement(Player player) {
        player.triggerAchievement(AchievementList.catchFish);
    }
}
