package net.minecraft.game.container.inventory;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public interface Inventory {
    int getSize();

    ItemStack getStackInSlot(int var1);

    ItemStack decrStackSize(int var1, int var2);

    void setSlotContents(int var1, ItemStack var2);

    String getName();

    int getStackLimit();

    void onChanged();

    boolean isInteractible(Player var1);

    void open();

    void close();
}
