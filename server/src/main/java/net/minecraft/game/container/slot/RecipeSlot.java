package net.minecraft.game.container.slot;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;
import net.minecraft.game.item.recipe.Recipe;

public class RecipeSlot extends Slot {
    private List<Recipe> finalList = CraftingManager.getInstance().getRecipeList();
    public List<Recipe> recipeList = new ArrayList<>();

    public RecipeSlot(Inventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    @Override
    public void putStack(ItemStack stack) {
        this.recipeList.clear();
        this.inventory.setSlotContents(this.slotIndex, stack);
        this.onChanged();
        if (stack != null) {
            for (int index = 0; index < this.finalList.size(); index++) {
                if (this.finalList.get(index).getRecipeOutput() != null) {
                    if (this.finalList.get(index).getRecipeOutput().getItem() == ItemList.splashPotion
                        && this.finalList.get(index).getRecipeOutput().getItemDamage() > 19) {
                        continue;
                    }

                    boolean hasMeta = !this.finalList.get(index).getRecipeOutput().getItem().hasSubtypes()
                        || this.finalList.get(index).getRecipeOutput().getItemDamage() == -1
                        || this.finalList.get(index).getRecipeOutput().getItemDamage() == stack.getItemDamage();
                    if (this.finalList.get(index).getRecipeOutput().getItem() == stack.getItem() && hasMeta) {
                        this.recipeList.add(this.finalList.get(index));
                    }
                }

                ItemStack[] items = this.finalList.get(index).getIngredients();
                if (items != null) {
                    for (int slot = 0; slot < items.length; slot++) {
                        if (items[slot] != null) {
                            boolean meta = !items[slot].hasSubtypes()
                                || items[slot].getItemDamage() == -1
                                || items[slot].getItemDamage() == stack.getItemDamage();
                            if (items[slot].getItem() == stack.getItem() && meta) {
                                this.recipeList.add(this.finalList.get(index));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPickup(ItemStack stack) {
        this.onChanged();
        if (this.inventory.getStackInSlot(this.slotIndex) == null) {
            this.recipeList.clear();
        }
    }

    @Override
    public void onChanged() {
        this.inventory.onChanged();
    }
}
