package net.minecraft.game.item.recipe.blocks;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public final class SlabRecipes {
    public final void addRecipes(CraftingManager cm) {
        for (int i = 0; i < 18; i++) {
            cm.addRecipe(new ItemStack(BlockList.carpet, 6, i), "##", '#', new ItemStack(BlockList.cloth, 1, i));
        }

        for (int i = 0; i <= BlockList.leafPile.getMaxMeta() * 8; i += 8) {
            cm.addRecipe(new ItemStack(BlockList.leafPile, 6, i), "###", '#', new ItemStack(BlockList.leaves, 1, i >> 3));
        }

        for (int i = 0; i <= BlockList.slabWood.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.slabWood, 6, i), "###", '#', new ItemStack(BlockList.planks, 1, i));
        }

        for (int i = 0; i <= BlockList.slabCobble.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.slabCobble, 6, i), "###", '#', new ItemStack(BlockList.cobblestone, 1, i));
        }

        for (int i = 0; i <= BlockList.slabStoneBrick.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.slabStoneBrick, 6, i), "###", '#', new ItemStack(BlockList.stoneBrick, 1, i));
        }

        for (int i = 0; i <= BlockList.slabBrick.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.slabBrick, 6, i), "###", '#', new ItemStack(BlockList.brick, 1, i));
        }

        for (int i = 0; i <= BlockList.slabSandstone.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.slabSandstone, 6, i), "###", '#', new ItemStack(BlockList.sandStone, 1, i));
        }

        for (int i = 0; i < 18; i++) {
            cm.addRecipe(new ItemStack(BlockList.slabCloth, 6, i), "###", '#', new ItemStack(BlockList.cloth, 1, i));
        }

        cm.addRecipe(new ItemStack(BlockList.slabStone, 6, 0), "###", '#', new ItemStack(BlockList.stone, 1, 0));
        cm.addRecipe(new ItemStack(BlockList.slabMoss, 6, 0), "###", '#', BlockList.cobblestoneMossy);
        cm.addRecipe(new ItemStack(BlockList.slabObsidian, 6, 0), "###", '#', BlockList.obsidianBrick);

        for (int i = 0; i <= BlockList.panelWood.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelWood, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.planks, 1, i));
        }

        for (int i = 0; i <= BlockList.panelCobblestone.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelCobblestone, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.cobblestone, 1, i));
        }

        for (int i = 0; i <= BlockList.panelStoneBrick.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelStoneBrick, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.stoneBrick, 1, i));
        }

        for (int i = 0; i <= BlockList.panelBrick.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelBrick, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.brick, 1, i));
        }

        for (int i = 0; i <= BlockList.panelSandstone.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelSandstone, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.sandStone, 1, i));
        }

        for (int i = 0; i <= BlockList.panelCloth.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(BlockList.panelCloth, 6, i * 4), "#", "#", "#", '#', new ItemStack(BlockList.cloth, 1, i));
        }

        cm.addRecipe(new ItemStack(BlockList.panelStone, 6, 0), "#", "#", "#", '#', new ItemStack(BlockList.stone, 1, 0));
        cm.addRecipe(new ItemStack(BlockList.panelMoss, 6, 0), "#", "#", "#", '#', BlockList.cobblestoneMossy);
        cm.addRecipe(new ItemStack(BlockList.panelObsidian, 6, 0), "#", "#", "#", '#', BlockList.obsidianBrick);
    }
}
