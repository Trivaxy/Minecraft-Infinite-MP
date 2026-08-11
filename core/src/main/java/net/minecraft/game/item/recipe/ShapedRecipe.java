package net.minecraft.game.item.recipe;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class ShapedRecipe implements Recipe {
    protected ItemStack[] ingredients;
    protected ItemStack output;
    protected int width;
    protected int height;
    private boolean hasNBT = false;

    public ShapedRecipe(int w, int h, ItemStack[] items, ItemStack output) {
        this.width = w;
        this.height = h;
        this.ingredients = items;
        this.output = output;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        for (int w = 0; w <= 3 - this.width; w++) {
            for (int h = 0; h <= 3 - this.height; h++) {
                if (this.doesItemMatch(inv, w, h, true)) {
                    return true;
                }

                if (this.doesItemMatch(inv, w, h, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean doesItemMatch(CraftingInventory inv, int offx, int offy, boolean shapeless) {
        for (int w = 0; w < 3; w++) {
            for (int h = 0; h < 3; h++) {
                int x = w - offx;
                int y = h - offy;
                ItemStack stack = null;
                if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
                    if (shapeless) {
                        stack = this.ingredients[this.width - x - 1 + y * this.width];
                    } else {
                        stack = this.ingredients[x + y * this.width];
                    }
                }

                ItemStack other = inv.getStackInCoords(w, h);
                if (other != null || stack != null) {
                    if (other == null && stack != null || other != null && stack == null) {
                        return false;
                    }

                    if (stack.itemID != other.itemID) {
                        return false;
                    }

                    if (stack.getItemDamage() != -1 && stack.getItemDamage() != 32767 && stack.getItemDamage() != other.getItemDamage()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inv) {
        ItemStack result = new ItemStack(this.output.itemID, this.output.stackSize, this.output.getItemDamage());
        if (this.hasNBT) {
            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack != null && stack.hasTag()) {
                    result.setTag((CompoundTag)stack.customTag.copy());
                }
            }
        }

        return result;
    }

    @Override
    public int getRecipeSize() {
        return this.width * this.height;
    }

    public int getRecipeWidth() {
        return this.width;
    }

    public int getRecipeHeight() {
        return this.height;
    }

    @Override
    public ItemStack[] getIngredients() {
        return this.ingredients;
    }
}
