package net.minecraft.game.container.inventory;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class BasicInventory implements Inventory {
    private ItemStack[] invStacks;
    private String title;
    private int slotCount;

    public BasicInventory(String title, int slots) {
        this.title = title;
        this.slotCount = slots;
        this.invStacks = new ItemStack[slots];
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return this.invStacks[slotIndex];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.invStacks[index] != null) {
            if (this.invStacks[index].stackSize <= count) {
                ItemStack stack = this.invStacks[index];
                this.invStacks[index] = null;
                this.onChanged();
                return stack;
            }

            ItemStack stack = this.invStacks[index].splitStack(count);
            if (this.invStacks[index].stackSize == 0) {
                this.invStacks[index] = null;
            }

            this.onChanged();
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int index, ItemStack stack) {
        this.invStacks[index] = stack;
        if (stack != null && stack.stackSize > this.getStackLimit()) {
            stack.stackSize = this.getStackLimit();
        }

        this.onChanged();
    }

    @Override
    public int getSize() {
        return this.slotCount;
    }

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public int getStackLimit() {
        return 64;
    }

    @Override
    public void onChanged() {
    }

    @Override
    public boolean isInteractible(Player player) {
        return true;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
