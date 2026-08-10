package net.minecraft.game.item.crafting.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Bricks;
import net.minecraft.game.block.building.Cloth;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;

public class DyeRecipes {
    public void addRecipes(CraftingManager cm) {
        for (int i = 0; i < 16; i++) {
            cm.addShapelessRecipe(new ItemStack(BlockList.cloth, 1, Cloth.getClothColor(i)), new ItemStack(ItemList.dyePowder, 1, i), BlockList.cloth);
            cm.addShapelessRecipe(new ItemStack(BlockList.carpet, 1, Cloth.getClothColor(i)), new ItemStack(ItemList.dyePowder, 1, i), BlockList.carpet);
            cm.addShapelessRecipe(new ItemStack(BlockList.glassStained, 1, Cloth.getClothColor(i)), new ItemStack(ItemList.dyePowder, 1, i), BlockList.glass);
            cm.addShapelessRecipe(new ItemStack(BlockList.hardenedClay, 1, Bricks.getColor(i)), new ItemStack(ItemList.dyePowder, 1, i), BlockList.hardenedClay);
            cm.addRecipe(
                new ItemStack(BlockList.coloredPane, 16, Cloth.getClothColor(i)),
                "   ",
                "###",
                "###",
                '#',
                new ItemStack(ItemList.items[BlockList.glassStained.id], 1, Cloth.getClothColor(i))
            );
            cm.addShapelessRecipe(new ItemStack(BlockList.brick, 1, Bricks.getColor(i)), new ItemStack(ItemList.dyePowder, 1, i), BlockList.brick);
            cm.addShapelessRecipe(
                new ItemStack(BlockList.cloth, 1, 0), new ItemStack(ItemList.dyePowder, 1, 15), new ItemStack(ItemList.items[BlockList.cloth.id], 1, i)
            );
            cm.addShapelessRecipe(
                new ItemStack(BlockList.carpet, 1, 0), new ItemStack(ItemList.dyePowder, 1, 15), new ItemStack(ItemList.items[BlockList.carpet.id], 1, i)
            );
        }

        cm.addRecipe(new ItemStack(ItemList.coal, 2, 0), "#", '#', BlockList.witherRose);
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 0), "#", '#', BlockList.rose);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 1), new ItemStack(ItemList.dyePowder, 1, 0), new ItemStack(ItemList.dyePowder, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 1), "#", '#', BlockList.fireFlower);
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 1), "#", '#', BlockList.tulip);
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 2), "#", '#', BlockList.dandelion);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 3), new ItemStack(ItemList.dyePowder, 1, 4), new ItemStack(ItemList.dyePowder, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 4), "#", '#', ItemList.reeds);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 5), new ItemStack(ItemList.dyePowder, 1, 4), new ItemStack(ItemList.dyePowder, 1, 6));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 6), "#", '#', BlockList.paeonia);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 6), new ItemStack(ItemList.dyePowder, 1, 4), new ItemStack(ItemList.dyePowder, 1, 7));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 7), "#", '#', BlockList.lilac);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 8), new ItemStack(ItemList.dyePowder, 1, 9), new ItemStack(ItemList.dyePowder, 1, 7));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 9), "#", '#', BlockList.petunia);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 10), new ItemStack(ItemList.dyePowder, 1, 7), new ItemStack(ItemList.dyePowder, 1, 0));
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 11), new ItemStack(ItemList.dyePowder, 1, 10), new ItemStack(ItemList.dyePowder, 1, 15));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 11), "#", '#', BlockList.pinkTulip);
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 12), new ItemStack(ItemList.dyePowder, 1, 11), new ItemStack(ItemList.dyePowder, 1, 0));
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 13), new ItemStack(ItemList.coal), new ItemStack(ItemList.dyePowder, 1, 15));
        cm.addShapelessRecipe(new ItemStack(ItemList.dyePowder, 2, 14), new ItemStack(ItemList.dyePowder, 1, 13), new ItemStack(ItemList.dyePowder, 1, 15));
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 14), "#", '#', BlockList.daisy);
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 2, 14), "#", '#', BlockList.whiteTulip);
        cm.addRecipe(new ItemStack(ItemList.dyePowder, 3, 15), "#", '#', ItemList.bone);
        cm.addShapelessRecipe(
            new ItemStack(BlockList.cloth, 1, 16), new ItemStack(ItemList.coal, 1, 0), new ItemStack(ItemList.items[BlockList.cloth.id], 1, 0)
        );
        cm.addShapelessRecipe(
            new ItemStack(BlockList.cloth, 1, 17), new ItemStack(ItemList.coal, 1, 1), new ItemStack(ItemList.items[BlockList.cloth.id], 1, 0)
        );
        cm.addShapelessRecipe(
            new ItemStack(BlockList.glassStained, 1, 16), new ItemStack(ItemList.coal, 1, 0), new ItemStack(ItemList.items[BlockList.glass.id], 1, 0)
        );
        cm.addShapelessRecipe(
            new ItemStack(BlockList.glassStained, 1, 17), new ItemStack(ItemList.coal, 1, 1), new ItemStack(ItemList.items[BlockList.glass.id], 1, 0)
        );
        cm.addShapelessRecipe(
            new ItemStack(BlockList.hardenedClay, 1, 17), new ItemStack(ItemList.coal, 1, 0), new ItemStack(ItemList.items[BlockList.hardenedClay.id], 1, 0)
        );
        cm.addShapelessRecipe(
            new ItemStack(BlockList.hardenedClay, 1, 18), new ItemStack(ItemList.coal, 1, 1), new ItemStack(ItemList.items[BlockList.hardenedClay.id], 1, 0)
        );
        cm.addRecipe(new ItemStack(BlockList.coloredPane, 16, 16), "   ", "###", "###", '#', new ItemStack(ItemList.items[BlockList.glassStained.id], 1, 16));
        cm.addRecipe(new ItemStack(BlockList.coloredPane, 16, 17), "   ", "###", "###", '#', new ItemStack(ItemList.items[BlockList.glassStained.id], 1, 17));

        for (int i = 0; i <= BlockList.cloth.getMaxMeta(); i++) {
            cm.addRecipe(new ItemStack(ItemList.flag, 2, i), "X##", "X##", "X  ", '#', new ItemStack(BlockList.cloth, 1, i), 'X', ItemList.stick);
        }

        for (int i = 0; i < ItemList.cape.getMaxDamage(); i++) {
            cm.addRecipe(new ItemStack(ItemList.cape, 1, i), "## ", "## ", "## ", '#', new ItemStack(BlockList.cloth, 1, i));
        }

        cm.addRecipe(new ItemStack(ItemList.bedWhite, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 0), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedLightGray, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 1), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedDarkGray, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 2), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedRose, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 3), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedPink, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 4), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedMagenta, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 5), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedPurple, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 6), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedAqua, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 7), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedBlue, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 8), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedCyan, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 9), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedSpringGreen, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 10), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedGreen, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 11), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedChartreuse, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 12), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedYellow, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 13), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedOrange, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 14), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedRed, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 15), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedBlack, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 16), 'X', BlockList.planks);
        cm.addRecipe(new ItemStack(ItemList.bedBrown, 1), "###", "XXX", '#', new ItemStack(BlockList.cloth, 1, 17), 'X', BlockList.planks);
    }
}
