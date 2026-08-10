package net.minecraft.game.item.recipe;

import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public interface Recipe {
    boolean matches(CraftingInventory var1, World var2);

    ItemStack getCraftResult(CraftingInventory var1);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    ItemStack[] getIngredients();
}
