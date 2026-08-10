package net.minecraft.game.container;

import java.util.List;
import net.minecraft.game.item.ItemStack;

public interface ContainerListener {
    void updateCraftingInventory(Container var1, List<ItemStack> var2);

    void setStack(Container var1, int var2, ItemStack var3);

    void setId(Container var1, int var2, int var3);
}
