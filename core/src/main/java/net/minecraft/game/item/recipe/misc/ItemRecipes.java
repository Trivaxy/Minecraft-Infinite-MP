package net.minecraft.game.item.recipe.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class ItemRecipes {
    public void addRecipes(CraftingManager cm) {
        cm.addShapelessRecipe(new ItemStack(ItemList.fertilizer, 1), ItemList.ash, new ItemStack(ItemList.dyePowder, 1, 15));
        cm.addShapelessRecipe(new ItemStack(ItemList.nuggetGold, 9), ItemList.ingotGold);
        cm.addShapelessRecipe(new ItemStack(ItemList.nuggetIron, 9), ItemList.ingotIron);
        cm.addShapelessRecipe(new ItemStack(ItemList.ingotDamascus, 1), ItemList.rawDamascus, ItemList.ingotGold);
        cm.addShapelessRecipe(new ItemStack(ItemList.itemFrameGlowing, 1), ItemList.itemFrame, ItemList.glodust);
        cm.addRecipe(new ItemStack(ItemList.stick, 4), "#", "#", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.stick, 4), "#", "#", '#', ItemList.bamboo);
        cm.addRecipe(new ItemStack(ItemList.silk, 4), "#", "#", '#', ItemList.cotton);
        cm.addRecipe(new ItemStack(ItemList.paper, 3), "###", '#', ItemList.reeds);
        cm.addRecipe(new ItemStack(ItemList.paper, 3), "###", '#', ItemList.bark);
        cm.addRecipe(new ItemStack(ItemList.book, 3), "#", "#", "#", '#', ItemList.paper);
        cm.addRecipe(new ItemStack(ItemList.bowlEmpty, 4), "# #", " # ", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.seedsMelon, 1), "X", 'X', ItemList.melonSlice);
        cm.addRecipe(new ItemStack(ItemList.seedsPumpkin, 2), "X", 'X', new ItemStack(BlockList.pumpkin.id, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.glodust, 1), "#", '#', BlockList.mushroomGlowing);
        cm.addRecipe(new ItemStack(ItemList.ingotGold, 1), "###", "###", "###", '#', ItemList.nuggetGold);
        cm.addRecipe(new ItemStack(ItemList.ingotIron, 1), "###", "###", "###", '#', ItemList.nuggetIron);
    }
}
