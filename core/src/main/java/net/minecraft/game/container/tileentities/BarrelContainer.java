package net.minecraft.game.container.tileentities;

import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class BarrelContainer extends Container {
    private Inventory inventory;

    public BarrelContainer(Inventory playerInv, Inventory barrelInv) {
        this.inventory = barrelInv;

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                this.addSlot(new Slot(barrelInv, row + column * 3, 62 + row * 18, 17 + column * 18));
            }
        }

        for (int var5 = 0; var5 < 3; var5++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + var5 * 9 + 9, 8 + row * 18, 100 + var5 * 18));
            }
        }

        for (int var6 = 0; var6 < 9; var6++) {
            this.addSlot(new Slot(playerInv, var6, 8 + var6 * 18, 158));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.inventory.isInteractible(player);
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < 9) {
                if (!this.isSlotFree(slotStack, 9, this.slots.size(), false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 0, 9, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }
        }

        return stack;
    }
}
