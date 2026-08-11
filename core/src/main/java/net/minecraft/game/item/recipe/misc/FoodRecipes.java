package net.minecraft.game.item.recipe.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class FoodRecipes {
    public void addRecipes(CraftingManager cm) {
        cm.addRecipe(new ItemStack(ItemList.bread, 1), "###", '#', ItemList.wheat);
        cm.addRecipe(new ItemStack(ItemList.cake, 1), "###", "XXX", "###", 'X', ItemList.appleRed, '#', ItemList.wheat);
        cm.addRecipe(new ItemStack(ItemList.pumpkinPie, 1), " X ", "XYX", " X ", 'X', ItemList.wheat, 'Y', new ItemStack(BlockList.pumpkin.id, 1, 0));
        cm.addRecipe(
            new ItemStack(ItemList.rabbitStew, 1),
            " A ",
            "YXZ",
            " # ",
            'A',
            ItemList.rabbitCooked,
            'X',
            ItemList.bakedPotato,
            'Y',
            ItemList.carrot,
            'Z',
            BlockList.mushroomBrown,
            '#',
            ItemList.bowlEmpty
        );
        cm.addRecipe(new ItemStack(ItemList.carrotGold, 1), "###", "#X#", "###", '#', ItemList.nuggetGold, 'X', ItemList.carrot);
        cm.addRecipe(new ItemStack(ItemList.appleGold, 1), "###", "#X#", "###", '#', BlockList.blockGold, 'X', ItemList.appleRed);
        cm.addShapelessRecipe(new ItemStack(ItemList.bowlSoup), BlockList.mushroomBrown, BlockList.mushroomRed, ItemList.bowlEmpty);
        cm.addShapelessRecipe(new ItemStack(ItemList.bowlBeetroot), ItemList.beetroot, ItemList.beetroot, ItemList.bowlEmpty);
        cm.addShapelessRecipe(new ItemStack(ItemList.bowlSussy), BlockList.mushroomCrimson, BlockList.mushroomNylium, ItemList.bowlEmpty);
    }
}
