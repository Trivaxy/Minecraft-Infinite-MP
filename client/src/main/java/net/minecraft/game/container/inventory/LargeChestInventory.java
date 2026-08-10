package net.minecraft.game.container.inventory;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class LargeChestInventory implements Inventory {
    public Inventory upperChest;
    public Inventory lowerChest;
    private String name;

    public LargeChestInventory(String name, Inventory upper, Inventory lower) {
        this.name = name;
        this.upperChest = upper;
        this.lowerChest = lower;
    }

    @Override
    public int getSize() {
        return this.upperChest.getSize() + this.lowerChest.getSize();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return slotIndex >= this.upperChest.getSize()
            ? this.lowerChest.getStackInSlot(slotIndex - this.upperChest.getSize())
            : this.upperChest.getStackInSlot(slotIndex);
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int count) {
        return slotIndex >= this.upperChest.getSize()
            ? this.lowerChest.decrStackSize(slotIndex - this.upperChest.getSize(), count)
            : this.upperChest.decrStackSize(slotIndex, count);
    }

    @Override
    public void setSlotContents(int slotIndex, ItemStack stack) {
        if (slotIndex >= this.upperChest.getSize()) {
            this.lowerChest.setSlotContents(slotIndex - this.upperChest.getSize(), stack);
        } else {
            this.upperChest.setSlotContents(slotIndex, stack);
        }
    }

    @Override
    public int getStackLimit() {
        return this.upperChest.getStackLimit();
    }

    @Override
    public void onChanged() {
        this.upperChest.onChanged();
        this.lowerChest.onChanged();
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.upperChest.isInteractible(player) && this.lowerChest.isInteractible(player);
    }

    @Override
    public void open() {
        this.upperChest.open();
        this.lowerChest.open();
    }

    @Override
    public void close() {
        this.upperChest.close();
        this.lowerChest.close();
    }
}
