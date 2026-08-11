package net.minecraft.game.item.tools;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Hook;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class GrapplingHook extends Item {
    public static Icon hook;
    public static Icon string;

    public GrapplingHook(int id) {
        super(id);
        this.maxDamage = 256;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.priorityItem = true;
        this.unbreakable = true;
    }

    @Override
    public void registerIcons(IconRegister iconRegister) {
        hook = iconRegister.registerIcon("hook");
        string = iconRegister.registerIcon("rope");
        this.itemIcon = iconRegister.registerIcon(this.internalName);
    }

    @Override
    public ItemStack use(ItemStack itemstack, World world, Player entityPlayer) {
        if (!world.isOnline && entityPlayer.hook == null && itemstack.isNotBroken()) {
            Hook hook = new Hook(world, entityPlayer);
            world.spawnEntity(hook);
            if (entityPlayer.inventory.canUseItem() && entityPlayer.inventory.getOffHandItem().getItem() == itemstack.getItem()) {
                entityPlayer.swingOffhandItem();
            } else {
                entityPlayer.swingItem();
            }

            itemstack.damageItem(1, entityPlayer);
        } else if (entityPlayer.hook != null) {
            entityPlayer.hook.setDead();
            entityPlayer.hook = null;
        }

        entityPlayer.addStat(StatList.useStats[this.id], 1);
        return itemstack;
    }

    @Override
    public boolean isRepairable(ItemStack stack, ItemStack stack2) {
        return stack2 == null
            ? super.isRepairable(stack, stack2)
            : stack2.itemID == ItemList.silk.id || stack2.itemID == ItemList.ruby.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
