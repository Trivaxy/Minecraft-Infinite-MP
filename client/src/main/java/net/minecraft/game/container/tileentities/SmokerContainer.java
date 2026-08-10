package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.ContainerListener;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.food.FoodItem;

public class SmokerContainer extends Container {
    private SmokerEntity furnace;
    private int cookTime = 0;
    private int burnTime = 0;
    private int itemBurnTime = 0;

    public SmokerContainer(PlayerInventory playerInv, SmokerEntity smoker) {
        this.furnace = smoker;
        this.addSlot(new Slot(smoker, 21, 15, 35));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 7; row++) {
                this.addSlot(new SmokerContainer.SmokerSlot(smoker, row + column * 7, 44 + row * 18, 17 + column * 18));
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
    public void updateCraftResults() {
        super.updateCraftResults();

        for (int index = 0; index < this.listeners.size(); index++) {
            ContainerListener listener = this.listeners.get(index);
            if (this.cookTime != this.furnace.cookTime) {
                listener.setId(this, 0, this.furnace.cookTime);
            }

            if (this.burnTime != this.furnace.burnTime) {
                listener.setId(this, 1, this.furnace.burnTime);
            }

            if (this.itemBurnTime != this.furnace.fuel) {
                listener.setId(this, 2, this.furnace.fuel);
            }
        }

        this.cookTime = this.furnace.cookTime;
        this.burnTime = this.furnace.burnTime;
        this.itemBurnTime = this.furnace.fuel;
    }

    @Override
    public void setData(int type, int data) {
        if (type == 0) {
            this.furnace.cookTime = data;
        }

        if (type == 1) {
            this.furnace.burnTime = data;
        }

        if (type == 2) {
            this.furnace.fuel = data;
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.furnace.isInteractible(player);
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex >= 1 && slotIndex < 23) {
                if (!this.isSlotFree(slotStack, 23, 57, false)) {
                    return null;
                }
            } else if (slotIndex >= 23 && slotIndex < 58) {
                if (!(slotStack.getItem() instanceof FoodItem) && slotStack.getItem() != ItemList.egg) {
                    if (FurnaceEntity.isFuel(slotStack) && !this.isSlotFree(slotStack, 0, 1, false)) {
                        return null;
                    }
                } else if (!this.isSlotFree(slotStack, 1, 22, false)) {
                    return null;
                }
            } else if (slotIndex == 0 && FurnaceEntity.isFuel(slotStack) && !this.isSlotFree(slotStack, 23, 57, false)) {
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

    class SmokerSlot extends Slot {
        public SmokerSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack != null && (stack.getItem() instanceof FoodItem || stack.getItem() == ItemList.egg);
        }
    }
}
