package net.minecraft.game.item.crafting.tool;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;

public class WeaponRecipes {
    private Object[][] swords = new Object[][]{
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
                ItemList.swordStone,
                ItemList.swordSteel,
                ItemList.swordDiamond,
                ItemList.swordGold,
                ItemList.swordDamascus,
                ItemList.swordCopper,
                ItemList.swordAdamantine
        }
    };
    private Object[][] spears = new Object[][]{
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
                ItemList.spearStone,
                ItemList.spearSteel,
                ItemList.spearDiamond,
                ItemList.spearGold,
                ItemList.spearDamascus,
                ItemList.spearCopper,
                ItemList.spearAdamantine
        }
    };
    private Object[][] arrows = new Object[][]{
        {
                ItemList.ingotIron,
                ItemList.flint,
                ItemList.ruby,
                ItemList.sapphire,
                ItemList.emerald,
                ItemList.amethyst,
                ItemList.topaz,
                ItemList.ingotGold,
                ItemList.gunpowder
        },
        {
                ItemList.arrow,
                ItemList.arrowFlint,
                ItemList.arrowFlame,
                ItemList.arrowFrost,
                ItemList.arrowPoison,
                ItemList.arrowEffect,
                ItemList.arrowLightning,
                ItemList.arrowPiercing,
                ItemList.arrowExplosive
        }
    };

    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < this.swords[0].length; i++) {
            Object object = this.swords[0][i];

            for (int j = 0; j < this.swords.length - 1; j++) {
                Item item = (Item)this.swords[j + 1][i];
                cm.addRecipe(new ItemStack(item), "X", "X", "#", '#', ItemList.stick, 'X', object);
            }
        }

        for (int i = 0; i < this.spears[0].length; i++) {
            Object object = this.spears[0][i];

            for (int j = 0; j < this.spears.length - 1; j++) {
                Item item = (Item)this.spears[j + 1][i];
                cm.addRecipe(new ItemStack(item), "  X", " # ", "#  ", '#', ItemList.stick, 'X', object);
            }
        }

        for (int i = 0; i < this.arrows[0].length; i++) {
            Object object = this.arrows[0][i];

            for (int j = 0; j < this.arrows.length - 1; j++) {
                Item item = (Item)this.arrows[j + 1][i];
                cm.addRecipe(new ItemStack(item, 4), "X", "#", "Y", '#', ItemList.stick, 'X', object, 'Y', ItemList.feather);
            }
        }

        cm.addRecipe(new ItemStack(ItemList.bow, 1), " #X", "# X", " #X", 'X', ItemList.silk, '#', ItemList.stick);
        cm.addRecipe(new ItemStack(ItemList.crossbow, 1), "X#X", "YYY", " X ", 'Y', ItemList.silk, 'X', ItemList.stick, '#', ItemList.ingotIron);
        cm.addRecipe(new ItemStack(ItemList.arrowRope, 1), "YYY", "YXY", "YYY", 'Y', BlockList.rope, 'X', ItemList.arrowFlint);
        cm.addRecipe(new ItemStack(ItemList.arrowGlowing, 2), " Y ", "YXY", " Y ", 'Y', ItemList.glodust, 'X', ItemList.arrowFlint);
    }
}
