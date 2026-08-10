package net.minecraft.game.item.crafting.special;

import java.util.ArrayList;
import net.minecraft.game.block.building.Cloth;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.Recipe;
import net.minecraft.game.world.World;

public class DyedItemRecipes implements Recipe {
    public static final float[][] colors = new float[][]{
        {1.0F, 1.0F, 1.0F},
        {0.6F, 0.6F, 0.6F},
        {0.3F, 0.3F, 0.3F},
        {1.0F, 0.25F, 0.625F},
        {1.0F, 0.25F, 1.0F},
        {0.75F, 0.375F, 1.0F},
        {0.643F, 0.318F, 1.0F},
        {0.5F, 0.5F, 1.0F},
        {0.0F, 0.75F, 1.0F},
        {0.25F, 1.0F, 1.0F},
        {0.25F, 1.0F, 0.625F},
        {0.25F, 1.0F, 0.25F},
        {0.625F, 1.0F, 0.25F},
        {1.0F, 1.0F, 0.25F},
        {1.0F, 0.625F, 0.25F},
        {1.0F, 0.25F, 0.25F},
        {0.1F, 0.1F, 0.1F},
        {0.3F, 0.2F, 0.1F}
    };

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack stack = null;
        ArrayList<ItemStack> ingredients = new ArrayList<>();

        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null) {
                if (slotStack.getItem() != ItemList.wolfArmor && slotStack.getItem() != ItemList.backpack && slotStack.getItem() != ItemList.quiver) {
                    if (slotStack.getItem() != ItemList.dyePowder) {
                        return false;
                    }

                    ingredients.add(slotStack);
                } else {
                    if (stack != null) {
                        return false;
                    }

                    stack = slotStack;
                }
            }
        }

        return stack != null && !ingredients.isEmpty();
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inventory) {
        ItemStack resultBackpack = null;
        int[] rgbTotals = new int[3];
        int brightnessTotal = 0;
        int colorCount = 0;
        Item charm = null;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null) {
                if (stack.getItem() != ItemList.wolfArmor && stack.getItem() != ItemList.backpack && stack.getItem() != ItemList.quiver) {
                    if (stack.getItem() != ItemList.dyePowder) {
                        return null;
                    }

                    float[] dyeColor = colors[Cloth.getClothColor(stack.getItemDamage())];
                    int r = (int)(dyeColor[0] * 255.0F);
                    int g = (int)(dyeColor[1] * 255.0F);
                    int b = (int)(dyeColor[2] * 255.0F);
                    brightnessTotal += Math.max(r, Math.max(g, b));
                    rgbTotals[0] += r;
                    rgbTotals[1] += g;
                    rgbTotals[2] += b;
                    colorCount++;
                } else {
                    charm = stack.getItem();
                    resultBackpack = stack.copy();
                    resultBackpack.stackSize = 1;
                    if (charm.hasColor(stack)) {
                        int color = charm.getColor(resultBackpack);
                        float r = (color >> 16 & 0xFF) / 255.0F;
                        float g = (color >> 8 & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        brightnessTotal += (int)(Math.max(r, Math.max(g, b)) * 255.0F);
                        rgbTotals[0] += (int)(r * 255.0F);
                        rgbTotals[1] += (int)(g * 255.0F);
                        rgbTotals[2] += (int)(b * 255.0F);
                        colorCount++;
                    }
                }
            }
        }

        if (charm == null) {
            return null;
        }

        int r = rgbTotals[0] / colorCount;
        int g = rgbTotals[1] / colorCount;
        int b = rgbTotals[2] / colorCount;
        float averageBrightness = (float)brightnessTotal / colorCount;
        float maxChannel = Math.max(r, Math.max(g, b));
        r = (int)(r * averageBrightness / maxChannel);
        g = (int)(g * averageBrightness / maxChannel);
        b = (int)(b * averageBrightness / maxChannel);
        int finalColor = r << 16 | g << 8 | b;
        charm.addColor(resultBackpack, finalColor);
        return resultBackpack;
    }

    @Override
    public int getRecipeSize() {
        return 10;
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
