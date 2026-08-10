package net.minecraft.game.item.crafting.special;

import java.util.ArrayList;
import net.minecraft.game.block.building.Cloth;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.Recipe;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import net.minecraft.game.world.World;

public class DyedArmorRecipes implements Recipe {
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
                if (slotStack.getItem() instanceof Armor) {
                    Armor var7 = (Armor)slotStack.getItem();
                    if (var7.tier != ArmorMaterial.CLOTH || stack != null) {
                        return false;
                    }

                    stack = slotStack;
                } else {
                    if (slotStack.getItem() != ItemList.dyePowder) {
                        return false;
                    }

                    ingredients.add(slotStack);
                }
            }
        }

        return stack != null && !ingredients.isEmpty();
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inventory) {
        ItemStack resultArmor = null;
        int[] rgbTotals = new int[3];
        int brightnessTotal = 0;
        int colorContributors = 0;
        Armor armorItem = null;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null) {
                if (stack.getItem() instanceof Armor) {
                    Armor armor = (Armor)stack.getItem();
                    if (armor.tier != ArmorMaterial.CLOTH || resultArmor != null) {
                        return null;
                    }

                    armorItem = armor;
                    resultArmor = stack.copy();
                    resultArmor.stackSize = 1;
                    if (armor.hasColor(stack)) {
                        int color = armor.getColor(resultArmor);
                        float r = (color >> 16 & 0xFF) / 255.0F;
                        float g = (color >> 8 & 0xFF) / 255.0F;
                        float b = (color & 0xFF) / 255.0F;
                        brightnessTotal += (int)(Math.max(r, Math.max(g, b)) * 255.0F);
                        rgbTotals[0] += (int)(r * 255.0F);
                        rgbTotals[1] += (int)(g * 255.0F);
                        rgbTotals[2] += (int)(b * 255.0F);
                        colorContributors++;
                    }
                } else {
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
                    colorContributors++;
                }
            }
        }

        if (armorItem == null) {
            return null;
        }

        int r = rgbTotals[0] / colorContributors;
        int g = rgbTotals[1] / colorContributors;
        int b = rgbTotals[2] / colorContributors;
        float averageBrightness = (float)brightnessTotal / colorContributors;
        float maxChannel = Math.max(r, Math.max(g, b));
        r = (int)(r * averageBrightness / maxChannel);
        g = (int)(g * averageBrightness / maxChannel);
        b = (int)(b * averageBrightness / maxChannel);
        int finalColor = r << 16 | g << 8 | b;
        armorItem.addColor(resultArmor, finalColor);
        return resultArmor;
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
