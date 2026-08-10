package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.ContainerListener;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.special.FurnaceRecipes;
import net.minecraft.statistics.StatList;

public class FurnaceContainer extends Container {
    private FurnaceEntity furnace;
    private int cookTime = 0;
    private int burnTime = 0;
    private int itemBurnTime = 0;

    public FurnaceContainer(PlayerInventory playerInv, FurnaceEntity furnass) {
        this.furnace = furnass;
        this.addSlot(new Slot(furnass, 0, 48, 22));
        this.addSlot(new Slot(furnass, 1, 48, 58));
        this.addSlot(new FurnaceContainer.FurnaceSlot(furnass, 2, 108, 40, playerInv.player));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + column * 9 + 9, 8 + row * 18, 100 + column * 18));
            }
        }

        for (int var5 = 0; var5 < 9; var5++) {
            this.addSlot(new Slot(playerInv, var5, 8 + var5 * 18, 158));
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
    public ItemStack getStackInSlot(int index) {
        ItemStack stack = null;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index == 2) {
                if (!this.isSlotFree(slotStack, 3, 39, false)) {
                    return null;
                }
            } else if (index != 1 && index != 0) {
                if (FurnaceRecipes.smelting().getSmeltingResult(slotStack.getItem().id, slotStack.itemDmg) != null) {
                    if (!this.isSlotFree(slotStack, 0, 1, false)) {
                        return null;
                    }
                } else if (FurnaceEntity.isFuel(slotStack)) {
                    if (!this.isSlotFree(slotStack, 1, 2, false)) {
                        return null;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.isSlotFree(slotStack, 30, 39, false)) {
                        return null;
                    }
                } else if (index >= 30 && index < 39 && !this.isSlotFree(slotStack, 3, 30, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 3, 39, false)) {
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

    class FurnaceSlot extends Slot {
        public Player player;

        public FurnaceSlot(Inventory inv, int slotIndex, int xPos, int yPos, Player player) {
            super(inv, slotIndex, xPos, yPos);
            this.player = player;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return false;
        }

        @Override
        public void onPickup(ItemStack stack) {
            super.onPickup(stack);
            this.player.addStat(StatList.craftStats[stack.itemID], 1);
        }
    }
}
