package net.minecraft.game.item.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class ShapelessRecipe implements Recipe {
    private final ItemStack output;
    private final List<ItemStack> ingredients;
    private final ItemStack[] ingredientsBackup;

    public ShapelessRecipe(ItemStack out, List<ItemStack> in) {
        this.output = out;
        this.ingredients = in;
        this.ingredientsBackup = new ItemStack[in.size()];
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ArrayList<ItemStack> ingredients = new ArrayList<>(this.ingredients);

        for (int w = 0; w < 3; w++) {
            for (int h = 0; h < 3; h++) {
                ItemStack stack = inv.getStackInCoords(h, w);
                if (stack != null) {
                    boolean foundOutput = false;

                    for (ItemStack ingredient : ingredients) {
                        if (stack.itemID == ingredient.itemID && (ingredient.getItemDamage() == -1 || stack.getItemDamage() == ingredient.getItemDamage())) {
                            foundOutput = true;
                            ingredients.remove(ingredient);
                            break;
                        }
                    }

                    if (!foundOutput) {
                        return false;
                    }
                }
            }
        }

        return ingredients.isEmpty();
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inv) {
        return this.output.copy();
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }

    @Override
    public ItemStack[] getIngredients() {
        return this.ingredients.toArray(this.ingredientsBackup);
    }
}
