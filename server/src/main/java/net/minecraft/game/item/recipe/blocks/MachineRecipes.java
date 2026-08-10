package net.minecraft.game.item.recipe.blocks;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class MachineRecipes {
    public void addRecipes(CraftingManager cm) {
        cm.addRecipe(new ItemStack(BlockList.generator), "###", "#X#", "###", '#', ItemList.ingotIron, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.transformer), "###", "X X", "###", '#', ItemList.ingotIron, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.regulator), "###", "X X", "###", '#', ItemList.ingotGold, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.detector), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'X', BlockList.glass, 'Y', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.observer), "###", "XYX", "###", '#', ItemList.ingotGold, 'X', ItemList.ruby, 'Y', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.pulleyBase), " # ", "#X#", "###", '#', ItemList.ingotIron, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.pulleyStickyBase), " # ", "#X#", "###", '#', ItemList.ingotGold, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.vacuum), "###", "#X#", "#Y#", '#', ItemList.ingotGold, 'X', BlockList.cog, 'Y', BlockList.chest);
        cm.addRecipe(new ItemStack(BlockList.crafter), "###", "#X#", "#Y#", '#', ItemList.ingotCopper, 'X', BlockList.cog, 'Y', BlockList.workbench);
        cm.addRecipe(new ItemStack(BlockList.dispenser), "###", "#X#", "#Y#", '#', ItemList.ingotIron, 'X', BlockList.cog, 'Y', ItemList.bow);
        cm.addRecipe(new ItemStack(BlockList.noteBlock), "###", "#X#", "###", '#', BlockList.planks, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.daylightSensor), "YYY", "XXX", "###", '#', ItemList.ingotIron, 'X', ItemList.sapphire, 'Y', BlockList.glass);
        cm.addRecipe(
            new ItemStack(BlockList.lamp, 2),
            "Z#Z",
            "YXY",
            "Z#Z",
            '#',
            BlockList.cog,
            'X',
            BlockList.glowStone,
            'Y',
            BlockList.glass,
            'Z',
            ItemList.ingotCopper
        );
        cm.addRecipe(new ItemStack(BlockList.battery), "#X#", "#X#", "#X#", 'X', ItemList.emerald, '#', ItemList.ingotCopper);
        cm.addRecipe(new ItemStack(BlockList.fan), "###", "#X#", "# #", '#', ItemList.ingotIron, 'X', BlockList.cog);
        cm.addRecipe(
            new ItemStack(BlockList.freezer), "Y#Y", "#X#", "Y#Y", '#', new ItemStack(BlockList.stone.id, 1, 8), 'X', BlockList.cog, 'Y', ItemList.topaz
        );
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 0), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.ruby);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 6), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.ruby);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 1), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.sapphire);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 7), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.sapphire);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 2), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.emerald);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 8), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.emerald);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 3), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 9), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 4), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 10), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 5), "#X#", "XYX", "#X#", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.diamond);
        cm.addRecipe(new ItemStack(BlockList.teleporter, 1, 11), "X#X", "#Y#", "X#X", '#', ItemList.ingotIron, 'Y', BlockList.cog, 'X', ItemList.diamond);
    }
}
