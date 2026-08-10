package net.minecraft.game.container.tileentities;

import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.container.slot.CraftingSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;
import net.minecraft.game.world.World;

public class CrafterContainer extends Container {
    private CraftingInventory inventory;
    public Inventory craftResult;
    private World worldObj;

    public CrafterContainer(PlayerInventory playerInv, CraftingInventory crafterInv, WorkbenchInventory result, World world) {
        this.inventory = crafterInv;
        this.craftResult = result;
        this.worldObj = world;
        this.addSlot(new CraftingSlot(playerInv.player, this.inventory, this.craftResult, 0, 120, 36));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                this.addSlot(new Slot(crafterInv, row + column * 3, 26 + row * 18, 18 + column * 18));
            }
        }

        for (int var7 = 0; var7 < 3; var7++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + var7 * 9 + 9, 8 + row * 18, 101 + var7 * 18));
            }
        }

        for (int var8 = 0; var8 < 9; var8++) {
            this.addSlot(new Slot(playerInv, var8, 8 + var8 * 18, 159));
        }

        this.updateInventory(this.inventory);
    }

    @Override
    public void updateInventory(Inventory inventory) {
        this.craftResult.setSlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.inventory, this.worldObj));
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
            if (slotIndex == 0) {
                if (!this.isSlotFree(slotStack, 10, 46, true)) {
                    return null;
                }
            } else if (slotIndex >= 10 && slotIndex < 37) {
                if (!this.isSlotFree(slotStack, 37, 46, false)) {
                    return null;
                }
            } else if (slotIndex >= 37 && slotIndex < 46) {
                if (!this.isSlotFree(slotStack, 10, 37, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 10, 46, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }

            if (slotStack.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickup(slotStack);
        }

        return stack;
    }
}
