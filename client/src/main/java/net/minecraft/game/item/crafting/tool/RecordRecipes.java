package net.minecraft.game.item.crafting.tool;

import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;

public class RecordRecipes {
    private Object[][] recipes = new Object[][]{
        {
                ItemList.ingotCopper,
                ItemList.ingotIron,
                ItemList.ingotGold,
                ItemList.ruby,
                ItemList.sapphire,
                ItemList.emerald,
                ItemList.diamond,
                ItemList.coal,
                ItemList.amethyst
        },
        {
                ItemList.recordOrange,
                ItemList.recordWhite,
                ItemList.recordGold,
                ItemList.recordRed,
                ItemList.recordBlue,
                ItemList.recordGreen,
                ItemList.recordTeal,
                ItemList.recordBlack,
                ItemList.recordPurple
        }
    };

    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < this.recipes[0].length; i++) {
            Object recipeItems = this.recipes[0][i];

            for (int j = 0; j < this.recipes.length - 1; j++) {
                Item item = (Item)this.recipes[j + 1][i];
                cm.addRecipe(new ItemStack(item), " # ", "#X#", " # ", '#', new ItemStack(ItemList.scrap, 1, 0), 'X', recipeItems);
            }
        }

        cm.addRecipe(new ItemStack(ItemList.recordCactus), " # ", "#X#", " # ", '#', new ItemStack(ItemList.scrap, 1, 2), 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(ItemList.recordHell), " # ", "#X#", " # ", '#', new ItemStack(ItemList.scrap, 1, 1), 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(ItemList.recordSky), " # ", "#X#", " # ", '#', new ItemStack(ItemList.scrap, 1, 3), 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(ItemList.recordVoid), "###", "###", "###", '#', ItemList.discFragment);
    }
}
