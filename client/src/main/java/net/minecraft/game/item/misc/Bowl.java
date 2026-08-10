package net.minecraft.game.item.misc;

import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.cow.Mooshroom;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class Bowl extends Item {
    public Bowl(int itemid) {
        super(itemid);
    }

    @Override
    public void useOnEntity(ItemStack stack, Mob mob, Player player) {
        if (mob instanceof Mooshroom) {
            if (stack.stackSize > 1) {
                if (!player.inventory.addItemToInventory(new ItemStack(ItemList.bowlSoup, 1))) {
                    DroppedItem item = new DroppedItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(ItemList.bowlSoup, 1));
                    player.world.spawnEntity(item);
                }

                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }
            } else if (player.getUsing()) {
                player.inventory.setSlotContents(44, new ItemStack(ItemList.bowlSoup, 1));
            } else {
                player.inventory.setSlotContents(player.inventory.currentSlot, new ItemStack(ItemList.bowlSoup, 1));
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }
        }
    }
}
