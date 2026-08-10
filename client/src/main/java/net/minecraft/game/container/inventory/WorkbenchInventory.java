package net.minecraft.game.container.inventory;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class WorkbenchInventory implements Inventory {
    private ItemStack[] outputSlot = new ItemStack[1];

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.outputSlot[0];
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.result");
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.outputSlot[0] != null) {
            ItemStack stack = this.outputSlot[0];
            this.outputSlot[0] = null;
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        this.outputSlot[0] = stack;
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
