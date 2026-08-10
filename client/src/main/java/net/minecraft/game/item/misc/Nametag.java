package net.minecraft.game.item.misc;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;

public class Nametag extends Item {
    public Nametag(int itemid) {
        super(itemid);
    }

    @Override
    public void useOnEntity(ItemStack stack, Mob mob, Player player) {
        if (stack.hasDisplayName()) {
            mob.customName = stack.getDisplayName();
            if (player.getUsing()) {
                player.inventory.setSlotContents(44, null);
            } else {
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return true;
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
