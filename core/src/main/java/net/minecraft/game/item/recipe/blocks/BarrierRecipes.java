package net.minecraft.game.item.recipe.blocks;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class BarrierRecipes {
    public final void addRecipes(CraftingManager craftingManager) {
        for (int i = 0; i < BlockList.wall.getMaxMeta(); i++) {
            craftingManager.addRecipe(new ItemStack(BlockList.wall, 6, i), "###", "###", '#', new ItemStack(BlockList.cobblestone, 1, i));
        }

        for (int i = 0; i < BlockList.wallStoneBrick.getMaxMeta(); i++) {
            craftingManager.addRecipe(new ItemStack(BlockList.wallStoneBrick, 6, i), "###", "###", '#', new ItemStack(BlockList.stoneBrick, 1, i));
        }

        for (int i = 0; i < BlockList.wallSandstone.getMaxMeta(); i++) {
            craftingManager.addRecipe(new ItemStack(BlockList.wallSandstone, 6, i), "###", "###", '#', new ItemStack(BlockList.sandStone, 1, i));
        }

        for (int i = 0; i < BlockList.wallBrick.getMaxMeta() + 1; i++) {
            craftingManager.addRecipe(new ItemStack(BlockList.wallBrick, 6, i), "###", "###", '#', new ItemStack(BlockList.brick, 1, i));
        }

        craftingManager.addRecipe(new ItemStack(BlockList.wallMoss, 6), "###", "###", '#', BlockList.cobblestoneMossy);
        craftingManager.addRecipe(new ItemStack(BlockList.wallObsidian, 6), "###", "###", '#', BlockList.obsidianBrick);

        for (int i = 0; i <= BlockList.fence.getMaxMeta(); i++) {
            craftingManager.addRecipe(new ItemStack(BlockList.fence, 4, i), "X#X", "X#X", 'X', new ItemStack(BlockList.planks, 1, i), '#', ItemList.stick);
        }

        for (int i = 0; i <= BlockList.planks.getMaxMeta(); i++) {
            craftingManager.addRecipe(
                new ItemStack(BlockList.fenceGate, 2, i * 8), "#X#", "#X#", '#', ItemList.stick, 'X', new ItemStack(BlockList.planks, 1, i)
            );
        }
    }
}
