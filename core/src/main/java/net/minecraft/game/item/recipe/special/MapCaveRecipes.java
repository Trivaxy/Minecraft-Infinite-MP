package net.minecraft.game.item.recipe.special;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.Recipe;
import net.minecraft.game.world.World;

public class MapCaveRecipes implements Recipe {
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
                    if (slotStack.itemID != ItemList.compass.id) {
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
        ItemStack stack = null;

        for (int slot = 0; slot < inv.getSize() && stack == null; slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null && slotStack.itemID == ItemList.map.id) {
                stack = slotStack;
            }
        }

        stack = stack.copy();
        stack.stackSize = 1;
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().setBoolean("caves_map", true);
        stack.getTag().setBoolean("map_is_scaling", false);
        return stack;
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
