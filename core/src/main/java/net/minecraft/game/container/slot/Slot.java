package net.minecraft.game.container.slot;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;

public class Slot {
    public final Inventory inventory;
    public int slotIndex;
    public int slotNumber;
    public int x;
    public int y;

    public Slot(Inventory inv, int index, int x, int y) {
        this.inventory = inv;
        this.slotIndex = index;
        this.x = x;
        this.y = y;
    }

    public boolean validItem(ItemStack stack) {
        return true;
    }

    public ItemStack getStack() {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    public boolean hasStack() {
        return this.getStack() != null;
    }

    public boolean canTakeStack(Player player) {
        return true;
    }

    public void putStack(ItemStack stack) {
        this.inventory.setSlotContents(this.slotIndex, stack);
        this.onChanged();
    }

    public void onPickup(ItemStack stack) {
        this.onChanged();
    }

    public int stackLimit() {
        return this.inventory.getStackLimit();
    }

    public Icon backgroundIcon() {
        return null;
    }

    public void onChanged() {
        this.inventory.onChanged();
    }

    public ItemStack decrStackSize(int count) {
        return this.inventory.decrStackSize(this.slotIndex, count);
    }

    public boolean isHere(Inventory inv, int index) {
        return inv == this.inventory && index == this.slotIndex;
    }
}
