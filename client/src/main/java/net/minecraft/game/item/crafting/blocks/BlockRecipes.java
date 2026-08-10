package net.minecraft.game.item.crafting.blocks;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;

public class BlockRecipes {
    private Object[][] blockRecipes = new Object[][]{
        {BlockList.blockGold, ItemList.ingotGold},
        {BlockList.blockCopper, ItemList.ingotCopper},
        {BlockList.blockIron, ItemList.ingotIron},
        {BlockList.blockDiamond, ItemList.diamond},
        {BlockList.blockRuby, ItemList.ruby},
        {BlockList.blockSapphire, ItemList.sapphire},
        {BlockList.blockEmerald, ItemList.emerald},
        {BlockList.blockCoal, ItemList.coal},
        {BlockList.slimeBlock, ItemList.slimeBall},
        {BlockList.hayBlock, ItemList.wheat},
        {BlockList.bone, ItemList.bone},
        {BlockList.bambooBlock, ItemList.bamboo},
        {BlockList.blockDamascus, ItemList.ingotDamascus},
        {BlockList.blockAmethyst, ItemList.amethyst},
        {BlockList.blockTopaz, ItemList.topaz}
    };

    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < this.blockRecipes.length; i++) {
            Block block = (Block)this.blockRecipes[i][0];
            Item ingredient = (Item)this.blockRecipes[i][1];
            cm.addRecipe(new ItemStack(block), "###", "###", "###", '#', ingredient);
            cm.addRecipe(new ItemStack(ingredient, 9), "#", '#', block);
        }

        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 0), "##", "##", '#', BlockList.sand);
        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 1), "##", "##", '#', new ItemStack(BlockList.sandStone, 1, 0));
        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 2), "##", "##", '#', BlockList.redSand);
        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 3), "##", "##", '#', new ItemStack(BlockList.sandStone, 1, 2));
        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 4), "##", "##", '#', BlockList.silverSand);
        cm.addRecipe(new ItemStack(BlockList.sandStone, 4, 5), "##", "##", '#', new ItemStack(BlockList.sandStone, 1, 4));
        cm.addRecipe(new ItemStack(BlockList.mudBrick, 4), "##", "##", '#', new ItemStack(BlockList.dirt, 1, 2));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 0), "##", "##", '#', new ItemStack(BlockList.stone, 1, 0));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 3), "##", "##", '#', new ItemStack(BlockList.stone, 1, 1));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 6), "##", "##", '#', new ItemStack(BlockList.stone, 1, 4));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 7), "##", "##", '#', new ItemStack(BlockList.stone, 1, 5));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 8), "##", "##", '#', new ItemStack(BlockList.stone, 1, 6));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 4), "##", "##", '#', new ItemStack(BlockList.stone, 1, 2));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 5), "##", "##", '#', new ItemStack(BlockList.stone, 1, 3));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 9), "##", "##", '#', new ItemStack(BlockList.stone, 1, 7));
        cm.addRecipe(new ItemStack(BlockList.stoneBrick, 4, 10), "##", "##", '#', new ItemStack(BlockList.stone, 1, 8));
        cm.addRecipe(new ItemStack(BlockList.obsidianBrick, 4), "##", "##", '#', BlockList.obsidian);
        cm.addRecipe(new ItemStack(BlockList.snowBlock, 4, 1), "##", "##", '#', new ItemStack(BlockList.snowBlock, 1, 0));
        cm.addRecipe(new ItemStack(BlockList.pillar, 2, 0), "#", "#", '#', new ItemStack(BlockList.slabStoneBrick, 1, 10));

        for (int i = 0; i <= BlockList.wood.getMaxMeta(); i++) {
            if (i == 18) {
                cm.addRecipe(new ItemStack(BlockList.planks, 4, 0), "#", '#', new ItemStack(BlockList.wood, 1, i));
                cm.addRecipe(new ItemStack(BlockList.planks, 4, 0), "#", '#', new ItemStack(BlockList.log, 1, i));
            } else {
                cm.addRecipe(new ItemStack(BlockList.planks, 4, i), "#", '#', new ItemStack(BlockList.wood, 1, i));
                cm.addRecipe(new ItemStack(BlockList.planks, 4, i), "#", '#', new ItemStack(BlockList.log, 1, i));
            }
        }

        cm.addRecipe(new ItemStack(BlockList.cobweb, 2), "##", "##", '#', ItemList.silk);
        cm.addRecipe(new ItemStack(BlockList.cloth, 4), "##", "##", '#', ItemList.cotton);
        cm.addRecipe(new ItemStack(BlockList.planks, 4), "##", "##", '#', ItemList.bamboo);
        cm.addRecipe(new ItemStack(BlockList.seaLantern, 2), "##", "##", '#', ItemList.seaPickle);
        cm.addRecipe(new ItemStack(BlockList.melon, 1), "##", "##", '#', ItemList.melonSlice);
        cm.addRecipe(new ItemStack(BlockList.cobblestone, 1, 0), "##", "##", '#', ItemList.rock);
        cm.addRecipe(new ItemStack(BlockList.snowBlock, 1), "##", "##", '#', ItemList.snowball);
        cm.addRecipe(new ItemStack(BlockList.sand, 1), "##", "##", '#', ItemList.sandball);
        cm.addRecipe(new ItemStack(ItemList.snowball, 4), "#", '#', BlockList.snowBlock);
        cm.addRecipe(new ItemStack(ItemList.sandball, 4), "#", '#', BlockList.sand);
        cm.addRecipe(new ItemStack(BlockList.magma, 1, 0), "##", "##", '#', ItemList.fireball);
        cm.addRecipe(new ItemStack(BlockList.cloud, 1, 0), "##", "##", '#', ItemList.windCharge);
        cm.addRecipe(new ItemStack(BlockList.clay, 1), "##", "##", '#', ItemList.clay);
        cm.addRecipe(new ItemStack(BlockList.brick, 1), "##", "##", '#', ItemList.brick);
        cm.addRecipe(new ItemStack(BlockList.packedIce, 1), "###", "###", "###", '#', BlockList.ice);
        cm.addRecipe(new ItemStack(BlockList.snowLayer, 6), "###", '#', ItemList.snowball);
        cm.addRecipe(new ItemStack(BlockList.sandLayer, 6), "###", '#', ItemList.sandball);
        cm.addRecipe(new ItemStack(BlockList.ash, 6), "###", '#', ItemList.ash);
        cm.addRecipe(new ItemStack(BlockList.tnt, 1), "X#X", "#X#", "X#X", 'X', ItemList.gunpowder, '#', BlockList.sand);
        cm.addRecipe(new ItemStack(BlockList.tnt, 1), "X#X", "#X#", "X#X", 'X', ItemList.gunpowder, '#', BlockList.redSand);
        cm.addRecipe(new ItemStack(BlockList.tnt, 1), "X#X", "#X#", "X#X", 'X', ItemList.gunpowder, '#', BlockList.silverSand);
        cm.addRecipe(new ItemStack(BlockList.bookshelf, 1), "###", "   ", "###", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(BlockList.concrete, 1), "#", "#", '#', new ItemStack(BlockList.slabStone, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.rock, 4), "#", '#', BlockList.cobblestone);
        cm.addRecipe(new ItemStack(BlockList.clusterBlock, 1, 0), "##", "##", '#', ItemList.ruby);
        cm.addRecipe(new ItemStack(BlockList.clusterBlock, 1, 1), "##", "##", '#', ItemList.sapphire);
        cm.addRecipe(new ItemStack(BlockList.clusterBlock, 1, 2), "##", "##", '#', ItemList.emerald);
        cm.addRecipe(new ItemStack(BlockList.clusterBlock, 1, 3), "##", "##", '#', ItemList.amethyst);
        cm.addRecipe(new ItemStack(BlockList.clusterBlock, 1, 4), "##", "##", '#', ItemList.topaz);
        cm.addRecipe(new ItemStack(BlockList.wood, 1, 8), "##", "##", '#', new ItemStack(ItemList.coal, 1, 1));
        cm.addRecipe(new ItemStack(BlockList.chest), "###", "# #", "###", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(BlockList.stoneOvenIdle), "###", "# #", "###", '#', BlockList.cobblestone);
        cm.addRecipe(new ItemStack(BlockList.hellforge), "###", "#X#", "###", '#', BlockList.netherrack, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(BlockList.obsidianCrying), "X#X", "#X#", "X#X", '#', BlockList.obsidian, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(BlockList.altar), "X#X", "#X#", "X#X", '#', new ItemStack(BlockList.stone, 1, 8), 'X', ItemList.topaz);
        cm.addRecipe(new ItemStack(BlockList.chestObsidian), "###", "#X#", "###", 'X', ItemList.amethyst, '#', BlockList.obsidian);
        cm.addRecipe(new ItemStack(BlockList.workbench), "##", "##", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(BlockList.barrel), "#X#", "# #", "#X#", '#', ItemList.stick, 'X', BlockList.slabWood);
        cm.addRecipe(new ItemStack(BlockList.steelOvenIdle), "###", "#X#", "###", '#', ItemList.ingotIron, 'X', BlockList.stoneOvenIdle);
        cm.addRecipe(new ItemStack(BlockList.smokerIdle), "###", "#X#", "###", '#', ItemList.ingotCopper, 'X', BlockList.stoneOvenIdle);
        cm.addRecipe(new ItemStack(BlockList.jukeBox), "###", "#X#", "###", '#', BlockList.planks, 'X', ItemList.diamond);
        cm.addRecipe(new ItemStack(BlockList.ladder, 2), "# #", "###", "# #", '#', ItemList.stick);
        cm.addRecipe(new ItemStack(BlockList.torch, 4), "X", "#", 'X', ItemList.coal, '#', ItemList.stick);
        cm.addRecipe(new ItemStack(BlockList.torch, 2), "X", "#", 'X', new ItemStack(ItemList.coal, 1, 1), '#', ItemList.stick);
        cm.addRecipe(new ItemStack(BlockList.candle, 2), "X", "#", 'X', ItemList.silk, '#', ItemList.wax);
        cm.addRecipe(new ItemStack(BlockList.tallTorch, 2), "X", "#", "#", 'X', new ItemStack(ItemList.coal, 1, 0), '#', ItemList.bamboo);
        cm.addRecipe(new ItemStack(BlockList.tallTorch, 1), "X", "#", "#", 'X', new ItemStack(ItemList.coal, 1, 1), '#', ItemList.bamboo);
        cm.addRecipe(new ItemStack(ItemList.glowStick, 4), "X", "#", "Y", 'X', ItemList.glodust, '#', ItemList.stick, 'Y', ItemList.slimeBall);
        cm.addRecipe(new ItemStack(BlockList.minecartTrack, 16), "X X", "X#X", "X X", 'X', ItemList.ingotIron, '#', ItemList.stick);
        cm.addRecipe(new ItemStack(BlockList.railBooster, 8), "YXY", "Y#Y", "YZY", '#', ItemList.stick, 'Y', ItemList.ingotGold, 'Z', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.railStopper, 8), "YXY", "Y#Y", "YZY", '#', ItemList.stick, 'Y', ItemList.ingotCopper, 'Z', BlockList.cog);
        cm.addRecipe(new ItemStack(BlockList.cog, 8), " X ", "X X", " X ", 'X', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(BlockList.glassPane, 16), "#X#", "XXX", "#X#", '#', BlockList.glass, 'X', ItemList.stick);
        cm.addRecipe(new ItemStack(BlockList.ironBars, 16), "###", "###", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(BlockList.copperGrating, 16), "###", "###", '#', ItemList.ingotCopper);
        cm.addRecipe(new ItemStack(BlockList.rope, 16), "#", "#", "#", '#', ItemList.silk);
        cm.addRecipe(new ItemStack(BlockList.chain, 8), "#", "#", "#", '#', ItemList.nuggetIron);
        cm.addRecipe(new ItemStack(BlockList.lantern, 2), "###", "#Y#", "###", '#', ItemList.nuggetIron, 'Y', BlockList.torch);
        cm.addRecipe(new ItemStack(BlockList.anvil, 1), "XXX", " X ", "XXX", 'X', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.cauldron, 1), "# #", "# #", "###", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(BlockList.pot, 1), "# #", "# #", "###", '#', ItemList.brick);
        cm.addRecipe(new ItemStack(BlockList.composter, 1), "# #", "# #", "###", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(BlockList.rod, 2), "#", "#", "#", '#', ItemList.ingotCopper);
        cm.addRecipe(new ItemStack(BlockList.lectern, 1), "DXD", " # ", "###", 'X', BlockList.carpet, '#', BlockList.planks, 'D', ItemList.ingotCopper);
        cm.addRecipe(new ItemStack(BlockList.stonecutter, 1), " B ", "D#D", "###", '#', BlockList.stone, 'D', ItemList.ingotCopper, 'B', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(BlockList.endPortalFrame, 1), "Y#Y", "#X#", "Y#Y", 'Y', BlockList.obsidian, '#', ItemList.diamond, 'X', ItemList.pearl);
        cm.addRecipe(new ItemStack(BlockList.beacon, 1), "###", "#X#", "YYY", 'Y', BlockList.obsidian, '#', ItemList.diamond, 'X', ItemList.singularity);
        cm.addRecipe(
            new ItemStack(BlockList.grindstone, 1), " X ", "#Y#", 'Y', new ItemStack(BlockList.panelStone, 1, 0), 'X', ItemList.diamond, '#', ItemList.stick
        );
        cm.addShapelessRecipe(new ItemStack(BlockList.cobblestoneMossy, 1), BlockList.moss, BlockList.cobblestone);
        cm.addShapelessRecipe(new ItemStack(BlockList.plantern, 1), new ItemStack(BlockList.pumpkin.id, 1, 1), BlockList.torch);
        cm.addShapelessRecipe(new ItemStack(BlockList.stoneBrick, 1, 1), new ItemStack(BlockList.stoneBrick, 1, 0), BlockList.moss);
    }
}
