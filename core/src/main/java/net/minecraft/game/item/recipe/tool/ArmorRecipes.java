package net.minecraft.game.item.recipe.tool;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class ArmorRecipes {
    private String[][] patterns = new String[][]{{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
    private Object[][] resources = new Object[][]{
        {
                ItemList.leather,
                ItemList.chainmail,
                ItemList.ingotIron,
                ItemList.diamond,
                ItemList.ingotGold,
                ItemList.ingotDamascus,
                BlockList.blockAdamantine,
                ItemList.ingotCopper
        },
        {
                ItemList.helmetLeather,
                ItemList.helmetChain,
                ItemList.helmetSteel,
                ItemList.helmetDiamond,
                ItemList.helmetGold,
                ItemList.helmetDamascus,
                ItemList.helmetAdamantine,
                ItemList.helmetCopper
        },
        {
                ItemList.plateLeather,
                ItemList.plateChain,
                ItemList.plateSteel,
                ItemList.plateDiamond,
                ItemList.plateGold,
                ItemList.plateDamascus,
                ItemList.plateAdamantine,
                ItemList.plateCopper
        },
        {
                ItemList.legsLeather,
                ItemList.legsChain,
                ItemList.legsSteel,
                ItemList.legsDiamond,
                ItemList.legsGold,
                ItemList.legsDamascus,
                ItemList.legsAdamantine,
                ItemList.legsCopper
        },
        {
                ItemList.bootsLeather,
                ItemList.bootsChain,
                ItemList.bootsSteel,
                ItemList.bootsDiamond,
                ItemList.bootsGold,
                ItemList.bootsDamascus,
                ItemList.bootsAdamantine,
                ItemList.bootsCopper
        }
    };

    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < this.resources[0].length; i++) {
            Object object = this.resources[0][i];

            for (int j = 0; j < this.resources.length - 1; j++) {
                Item item = (Item)this.resources[j + 1][i];
                cm.addRecipe(new ItemStack(item), this.patterns[j], 'X', object);
            }
        }
    }
}
