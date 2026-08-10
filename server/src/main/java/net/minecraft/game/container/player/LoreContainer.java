package net.minecraft.game.container.player;

import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.RecipeSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class LoreContainer extends Container {
    public Inventory loreSlot = new BasicInventory("Lore", 1);

    public LoreContainer(PlayerInventory inventoryplayer) {
        this.addSlot(new RecipeSlot(this.loreSlot, 0, 82, 66));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 48 + k * 18, 129 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventoryplayer, j, 48 + j * 18, 187));
        }
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        ItemStack itemstack = this.loreSlot.getStackInSlot(0);
        if (itemstack != null && !player.inventory.addItemToInventory(itemstack)) {
            player.dropPlayerItem(itemstack);
        }
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        ItemStack itemstack = null;
        Slot slot = this.slots.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();
            if (i == 0) {
                if (!this.isSlotFree(slotStack, 1, 36, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 0, 1, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }

            if (slotStack.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickup(slotStack);
        }

        return itemstack;
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
