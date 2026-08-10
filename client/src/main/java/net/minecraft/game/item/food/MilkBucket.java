package net.minecraft.game.item.food;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class MilkBucket extends Item {
    public MilkBucket(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.priorityItem = true;
        this.canSwing = false;
    }

    @Override
    public ItemStack onFinishedUsing(ItemStack stack, World world, Player player) {
        if (!world.isOnline) {
            player.clearEffects();
            player.addStat(StatList.useStats[this.id], 1);
        }

        return new ItemStack(ItemList.bucketEmpty);
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
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.setItemInUse(stack, this.getMaxUseDuration(stack));
        return stack;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("bucket/" + this.internalName);
    }
}
