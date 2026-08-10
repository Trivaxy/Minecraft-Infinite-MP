package net.minecraft.game.item.recipe.blocks;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class StairRecipes {
    public final void addRecipes(CraftingManager cm) {
        for (int i = 0; i <= BlockList.wood.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.stairWood, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.planks, 1, i));
        }

        for (int i = 0; i <= BlockList.cobblestone.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.stairStone, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.cobblestone, 1, i));
        }

        for (int i = 0; i <= BlockList.stoneBrick.getMaxMeta() - 1; i++) {
            cm.addRecipe(new ItemStack(BlockList.stairBrick, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.stoneBrick, 1, i));
        }

        for (int i = 0; i <= BlockList.brick.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.stairRedBrick, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.brick, 1, i));
        }

        for (int i = 0; i <= BlockList.sandStone.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.stairSandstone, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.sandStone, 1, i));
        }

        for (int i = 0; i <= BlockList.cloth.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.stairCloth, 6, i * 8), "#  ", "## ", "###", '#', new ItemStack(BlockList.cloth, 1, i));
        }

        cm.addRecipe(new ItemStack(BlockList.stairMoss, 6, 0), "#  ", "## ", "###", '#', BlockList.cobblestoneMossy);
        cm.addRecipe(new ItemStack(BlockList.stairObsidianBrick, 6, 0), "#  ", "## ", "###", '#', BlockList.obsidianBrick);
    }
}
