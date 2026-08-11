package net.minecraft.game.container.slot;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;

public class CreativeSlot extends Slot {
    private final Slot theSlot;

    public CreativeSlot(Slot slot, int index) {
        super(slot.inventory, index, 0, 0);
        this.theSlot = slot;
    }

    public void onPickupFromSlot(Player player, ItemStack stack) {
        this.theSlot.onPickup(stack);
    }

    @Override
    public boolean validItem(ItemStack stack) {
        return this.theSlot.validItem(stack);
    }

    @Override
    public ItemStack getStack() {
        return this.theSlot.getStack();
    }

    @Override
    public boolean hasStack() {
        return this.theSlot.hasStack();
    }

    @Override
    public void putStack(ItemStack stack) {
        this.theSlot.putStack(stack);
    }

    @Override
    public void onChanged() {
        this.theSlot.onChanged();
    }

    @Override
    public int stackLimit() {
        return this.theSlot.stackLimit();
    }

    @Override
    public Icon backgroundIcon() {
        return this.theSlot.backgroundIcon();
    }

    @Override
    public ItemStack decrStackSize(int count) {
        return this.theSlot.decrStackSize(count);
    }

    public static Slot getSlot(CreativeSlot slot) {
        return slot.theSlot;
    }
}
