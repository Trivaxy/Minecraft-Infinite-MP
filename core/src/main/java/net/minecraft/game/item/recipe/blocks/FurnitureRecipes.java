package net.minecraft.game.item.recipe.blocks;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class FurnitureRecipes {
    public void addRecipes(CraftingManager cm) {
        cm.addRecipe(new ItemStack(BlockList.table, 1, 255), "###", "X X", "X X", '#', BlockList.glass, 'X', ItemList.ingotIron);

        for (int i = 0; i <= BlockList.planks.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.table, 1, i), "###", "X X", "X X", '#', new ItemStack(BlockList.planks, 1, i), 'X', ItemList.stick);
        }

        for (int i = 0; i <= BlockList.planks.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.chair, 1, i * 8), "#  ", "###", "X X", '#', new ItemStack(BlockList.planks, 1, i), 'X', ItemList.stick);
        }

        cm.addRecipe(new ItemStack(ItemList.doorGlass, 2), "#X", "#X", "#X", '#', BlockList.planks, 'X', BlockList.glass);
        cm.addRecipe(new ItemStack(ItemList.doorWood, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.doorBirch, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.doorSpruce, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.doorPalm, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.doorAcacia, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.doorJungle, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.doorMangrove, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 6));
        cm.addRecipe(new ItemStack(ItemList.doorBaobab, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 7));
        cm.addRecipe(new ItemStack(ItemList.doorScorched, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 8));
        cm.addRecipe(new ItemStack(ItemList.doorMaple, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 9));
        cm.addRecipe(new ItemStack(ItemList.doorCherry, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 10));
        cm.addRecipe(new ItemStack(ItemList.doorMahogany, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 11));
        cm.addRecipe(new ItemStack(ItemList.doorOldenOak, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 12));
        cm.addRecipe(new ItemStack(ItemList.doorCrimson, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 13));
        cm.addRecipe(new ItemStack(ItemList.doorNylium, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 14));
        cm.addRecipe(new ItemStack(ItemList.doorPurpur, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 15));
        cm.addRecipe(new ItemStack(ItemList.doorWillow, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 16));
        cm.addRecipe(new ItemStack(ItemList.doorLime, 2), "##", "##", "##", '#', new ItemStack(BlockList.planks, 1, 17));
        cm.addRecipe(new ItemStack(ItemList.doorSteel, 2), "##", "##", "##", '#', ItemList.ingotIron);

        for (int i = 0; i <= 15; i++) {
            cm.addRecipe(new ItemStack(BlockList.trapdoorWood, 2, i * 8), "###", "###", '#', new ItemStack(BlockList.planks, 1, i));
        }

        cm.addRecipe(new ItemStack(BlockList.trapdoorSteel, 2), "##", "##", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(BlockList.trapdoorGlass, 2, 0), "#X#", "#X#", '#', BlockList.planks, 'X', BlockList.glass);
        cm.addRecipe(new ItemStack(BlockList.trapdoorGlass, 2, 8), "###", "###", '#', new ItemStack(BlockList.planks, 1, 16));
        cm.addRecipe(new ItemStack(BlockList.trapdoorGlass, 2, 16), "###", "###", '#', new ItemStack(BlockList.planks, 1, 17));

        for (int i = 0; i <= BlockList.planks.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(ItemList.sign, 3, i), "###", "###", " X ", '#', new ItemStack(BlockList.planks, 1, i), 'X', ItemList.stick);
        }

        cm.addRecipe(new ItemStack(ItemList.flowerPot, 2), "# #", " # ", '#', ItemList.brick);
        cm.addRecipe(new ItemStack(ItemList.painting, 1), "###", "#X#", "###", '#', ItemList.stick, 'X', BlockList.cloth);
        cm.addRecipe(new ItemStack(ItemList.itemFrame, 1), "###", "#X#", "###", '#', ItemList.stick, 'X', ItemList.leather);
    }
}
