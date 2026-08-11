package net.minecraft.game.item.recipe.tool;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class ToolRecipes {
    private String[][] recipePatterns = new String[][]{{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}};
    private Object[][] recipeItems = new Object[][]{
        {
                BlockList.cobblestone,
                ItemList.ingotIron,
                ItemList.diamond,
                ItemList.ingotGold,
                ItemList.ingotDamascus,
                ItemList.ingotCopper,
                BlockList.blockAdamantine
        },
        {
                ItemList.pickaxeStone,
                ItemList.pickaxeSteel,
                ItemList.pickaxeDiamond,
                ItemList.pickaxeGold,
                ItemList.pickaxeDamascus,
                ItemList.pickaxeCopper,
                ItemList.pickaxeAdamantine
        },
        {
                ItemList.shovelStone,
                ItemList.shovelSteel,
                ItemList.shovelDiamond,
                ItemList.shovelGold,
                ItemList.shovelDamascus,
                ItemList.shovelCopper,
                ItemList.shovelAdamantine
        },
        {ItemList.axeStone, ItemList.axeSteel, ItemList.axeDiamond, ItemList.axeGold, ItemList.axeDamascus, ItemList.axeCopper, ItemList.axeAdamantine},
        {ItemList.hoeStone, ItemList.hoeSteel, ItemList.hoeDiamond, ItemList.hoeGold, ItemList.hoeDamascus, ItemList.hoeCopper, ItemList.hoeAdamantine}
    };

    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < this.recipeItems[0].length; i++) {
            Object object = this.recipeItems[0][i];

            for (int j = 0; j < this.recipeItems.length - 1; j++) {
                Item item = (Item)this.recipeItems[j + 1][i];
                cm.addRecipe(new ItemStack(item), this.recipePatterns[j], '#', ItemList.stick, 'X', object);
            }
        }

        cm.addRecipe(
            new ItemStack(ItemList.paxel), "#X#", " Y ", " Y ", '#', ItemList.pearl, 'X', ItemList.endCrystal, 'Y', new ItemStack(ItemList.scrap, 1, 0)
        );
        cm.addRecipe(new ItemStack(ItemList.fishingRod, 1), "  #", " #X", "# X", '#', ItemList.stick, 'X', ItemList.silk);
        cm.addRecipe(new ItemStack(ItemList.compass, 1), " # ", "#X#", " # ", '#', ItemList.ingotIron, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(ItemList.compassAmethyst, 1), " # ", "#X#", " # ", '#', BlockList.obsidian, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(ItemList.clock, 1), " # ", "#X#", " # ", '#', ItemList.ingotGold, 'X', BlockList.cog);
        cm.addRecipe(new ItemStack(ItemList.lightGem, 1), " # ", "#X#", " # ", 'X', ItemList.topaz, '#', BlockList.glassTinted);
        cm.addRecipe(new ItemStack(ItemList.hornCopper, 1), "#X#", " # ", '#', ItemList.ingotCopper, 'X', ItemList.horn);
        cm.addRecipe(new ItemStack(ItemList.mapEmpty, 1), "###", "#X#", "###", '#', ItemList.paper, 'X', ItemList.compass);
        cm.addRecipe(new ItemStack(ItemList.bucketEmpty, 1), "# #", " # ", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.glassBottle, 3), "# #", " # ", '#', BlockList.glass);
        cm.addRecipe(new ItemStack(ItemList.spyGlass, 1), "  X", " # ", "Y  ", '#', ItemList.ingotIron, 'X', ItemList.sapphire, 'Y', ItemList.ingotGold);
        cm.addRecipe(new ItemStack(ItemList.screwdriver, 1), "#", "X", 'X', ItemList.ingotCopper, '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.shears, 1), " #", "# ", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.brush, 1), " X", "# ", '#', ItemList.ingotCopper, 'X', ItemList.feather);
        cm.addRecipe(new ItemStack(ItemList.quiver, 1), "#X ", "# X", "#  ", '#', ItemList.leather, 'X', ItemList.silk);
        cm.addRecipe(new ItemStack(ItemList.shieldDamascus, 1), "X X", "XYX", "XXX", 'Y', ItemList.amethyst, 'X', ItemList.ingotDamascus);
        cm.addRecipe(new ItemStack(ItemList.singularity), "YXY", "X#X", "YXY", 'X', ItemList.fireball, '#', ItemList.pearl, 'Y', ItemList.windCharge);
        cm.addRecipe(new ItemStack(ItemList.enderEye), " # ", "#X#", " # ", '#', ItemList.ruby, 'X', ItemList.eye);
        cm.addRecipe(new ItemStack(ItemList.dreamCatcher), " X ", "X#X", " X ", '#', ItemList.tear, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(ItemList.endCrystal, 2), "###", "#X#", "#Y#", '#', ItemList.diamond, 'X', ItemList.endCrystal, 'Y', BlockList.obsidian);
        cm.addRecipe(new ItemStack(ItemList.wolfArmor), "#  ", "###", "# #", '#', ItemList.leather);
        cm.addRecipe(new ItemStack(ItemList.horseArmor), "# #", "YXY", "# #", 'X', ItemList.leather, '#', ItemList.chainmail, 'Y', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.scroll, 1, 1), "###", "#X#", "###", '#', ItemList.ruby, 'X', new ItemStack(ItemList.scroll, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.scroll, 1, 2), "###", "#X#", "###", '#', ItemList.sapphire, 'X', new ItemStack(ItemList.scroll, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.scroll, 1, 3), "###", "#X#", "###", '#', ItemList.emerald, 'X', new ItemStack(ItemList.scroll, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.scroll, 1, 4), "###", "#X#", "###", '#', ItemList.topaz, 'X', new ItemStack(ItemList.scroll, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.scroll, 1, 5), "###", "#X#", "###", '#', ItemList.amethyst, 'X', new ItemStack(ItemList.scroll, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.saddle, 1), "###", "X X", '#', ItemList.leather, 'X', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.minecart, 1), "# #", "###", '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.boat, 1), "# #", "###", '#', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.boatObsidian, 1), "#X#", "###", '#', BlockList.obsidian, 'X', ItemList.amethyst);
        cm.addRecipe(new ItemStack(ItemList.raft, 1), "#X#", "###", '#', BlockList.bambooBlock, 'X', BlockList.chest);
        cm.addShapelessRecipe(new ItemStack(ItemList.striker, 1), ItemList.ingotIron, ItemList.flint);
        cm.addShapelessRecipe(new ItemStack(ItemList.stickCarrot, 1), ItemList.carrot, ItemList.fishingRod);
        cm.addShapelessRecipe(new ItemStack(ItemList.minecartChest, 1), ItemList.minecart, BlockList.chest);
        cm.addShapelessRecipe(new ItemStack(ItemList.minecartFurnace, 1), ItemList.minecart, BlockList.stoneOvenIdle);
        cm.addShapelessRecipe(new ItemStack(ItemList.minecartExplosive, 1), ItemList.minecart, BlockList.tnt);
        cm.addShapelessRecipe(new ItemStack(ItemList.bookAndQuill, 1), ItemList.feather, ItemList.book);
        cm.addShapelessRecipe(new ItemStack(ItemList.musketRound, 4), ItemList.ingotIron, ItemList.gunpowder);

        for (int i = 0; i < 32768; i++) {
            cm.addShapelessRecipe(new ItemStack(ItemList.splashPotion, 1, i), new ItemStack(ItemList.potion, 1, i), ItemList.gunpowder);
        }
    }
}
