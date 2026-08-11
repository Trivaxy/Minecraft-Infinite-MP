package net.minecraft.game.item.recipe.special;

import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.Recipe;
import net.minecraft.game.world.World;

public class MapCloningRecipes implements Recipe {
    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int index = 0;
        ItemStack stack = null;

        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null) {
                if (slotStack.itemID == ItemList.map.id) {
                    if (stack != null) {
                        return false;
                    }

                    stack = slotStack;
                } else {
                    if (slotStack.itemID != ItemList.mapEmpty.id) {
                        return false;
                    }

                    index++;
                }
            }
        }

        return stack != null && index > 0;
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inv) {
        int index = 0;
        ItemStack stack = null;

        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null) {
                if (slotStack.itemID == ItemList.map.id) {
                    if (stack != null) {
                        return null;
                    }

                    stack = slotStack;
                } else {
                    if (slotStack.itemID != ItemList.mapEmpty.id) {
                        return null;
                    }

                    index++;
                }
            }
        }

        return stack != null && index >= 1 ? new ItemStack(ItemList.map, index + 1, stack.getItemDamage()) : null;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getIngredients() {
        return null;
    }
}
