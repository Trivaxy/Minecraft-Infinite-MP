package net.minecraft.game.item.staff;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class HealingStaff extends Staff {
    public HealingStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (stack.itemDmg < stack.getItem().getMaxDamage() - 16 && player.itemUseCooldown <= 0) {
            player.heal(10);
            player.itemUseCooldown = player.maxDelay = 20;
            world.playSoundEffect(player, player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
            stack.damageItem(16, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.ruby.id || stack2.itemID == ItemList.ingotGold.id || stack2.itemID == this.id;
    }
}
