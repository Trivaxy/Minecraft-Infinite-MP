package net.minecraft.game.item.crafting.tool;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;

public class TreasureRecipes {
    public void addRecipes(CraftingManager cm) {
        cm.addRecipe(new ItemStack(ItemList.swordPoison, 1), " # ", " # ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.whip, 1), "  #", " ##", "X  ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(
            new ItemStack(ItemList.natureStaff, 1), " X ", " # ", " # ", 'X', new ItemStack(ItemList.dyePowder, 1, 0), '#', new ItemStack(ItemList.scrap, 1, 2)
        );
        cm.addRecipe(new ItemStack(ItemList.crown, 1), "X#X", "# #", "X#X", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.helmetThorns, 1), "###", "# #", '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.plateThorns, 1), "# #", "###", "###", '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.legsThorns, 1), "###", "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.bootsThorns, 1), "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 2));
        cm.addRecipe(new ItemStack(ItemList.swordFrost, 1), " # ", " # ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.snowmanStaff, 1), " X ", " # ", " # ", 'X', ItemList.snowball, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.cloudStaff, 1), " X ", " # ", " # ", 'X', ItemList.windCharge, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.lichStaff, 1), " X ", " # ", " # ", 'X', ItemList.ruby, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.ring, 1), " # ", "X X", " X ", 'X', ItemList.ingotIron, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.necklace, 1), "X X", " X ", " # ", 'X', ItemList.ingotIron, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.shieldRepulsion, 1), " # ", "#X#", " # ", 'X', ItemList.ingotGold, '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.helmetLich, 1), "###", "# #", '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.plateLich, 1), "# #", "###", "###", '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.legsLich, 1), "###", "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.bootsLich, 1), "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 3));
        cm.addRecipe(new ItemStack(ItemList.helmetPoseidon, 1), "#X#", "# #", '#', new ItemStack(ItemList.scrap, 1, 3), 'X', ItemList.ingotGold);
        cm.addRecipe(new ItemStack(ItemList.platePoseidon, 1), "# #", "#X#", "###", '#', new ItemStack(ItemList.scrap, 1, 3), 'X', ItemList.ingotGold);
        cm.addRecipe(new ItemStack(ItemList.legsPoseidon, 1), "#X#", "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 3), 'X', ItemList.ingotGold);
        cm.addRecipe(new ItemStack(ItemList.bootsPoseidon, 1), "X X", "# #", '#', new ItemStack(ItemList.scrap, 1, 3), 'X', ItemList.ingotGold);
        cm.addRecipe(new ItemStack(ItemList.pickaxePheonix, 1), "###", " X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.axePheonix, 1), "## ", "#X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.shovelPheonix, 1), " # ", " X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.swordPheonix, 1), " # ", " # ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.helmetPheonix, 1), "###", "# #", '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.platePheonix, 1), "# #", "###", "###", '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.legsPheonix, 1), "###", "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.bootsPheonix, 1), "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.impStaff, 1), " # ", " X ", " X ", '#', ItemList.fireball, 'X', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.swordVampire, 1), " # ", " # ", " X ", '#', ItemList.ruby, 'X', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.grapplingHook, 1), " XX", " #X", "#  ", '#', ItemList.silk, 'X', new ItemStack(ItemList.scrap, 1, 1));
        cm.addRecipe(new ItemStack(ItemList.healingStaff, 1), " # ", " X ", " X ", '#', ItemList.topaz, 'X', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.pharaohStaff, 1), " # ", " X ", " X ", '#', ItemList.ruby, 'X', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.coin, 1), " X ", "X X", " X ", 'X', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.quiverGold, 1), " X#", "X #", "  #", 'X', ItemList.silk, '#', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.trident, 1), "X X", "X#X", " X ", '#', ItemList.ruby, 'X', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(
            new ItemStack(ItemList.capeInvisibility, 1), "XX", "##", "##", 'X', new ItemStack(ItemList.scrap, 1, 5), '#', new ItemStack(BlockList.cloth, 1, 8)
        );
        cm.addRecipe(new ItemStack(ItemList.harp, 1), "B X", " X#", "X##", 'X', new ItemStack(ItemList.scrap, 1, 5), '#', ItemList.silk, 'B', ItemList.feather);
        cm.addRecipe(new ItemStack(ItemList.windAmulet, 1), "X X", " X ", " # ", 'X', ItemList.silk, '#', new ItemStack(ItemList.scrap, 1, 5));
        cm.addRecipe(new ItemStack(ItemList.pickaxeValkyrie, 1), "###", " X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.axeValkyrie, 1), "## ", "#X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.shovelValkyrie, 1), " # ", " X ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.swordValkyrie, 1), " # ", " # ", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.helmetValkyrie, 1), "###", "# #", '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.plateValkyrie, 1), "# #", "###", "###", '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.legsValkyrie, 1), "###", "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.bootsValkyrie, 1), "# #", "# #", '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(new ItemStack(ItemList.mace, 1), "###", "#X#", " X ", 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 4));
        cm.addRecipe(
            new ItemStack(ItemList.swordSpider, 1), " # ", " Y ", " X ", 'Y', ItemList.ruby, 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 0)
        );
        cm.addRecipe(
            new ItemStack(ItemList.battleAxe, 1), "#Y#", "#X#", " X ", 'Y', ItemList.ruby, 'X', ItemList.stick, '#', new ItemStack(ItemList.scrap, 1, 0)
        );
        cm.addRecipe(new ItemStack(ItemList.bracelet, 1), "#X#", " # ", 'X', ItemList.ruby, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.magnet, 1), "##X", "#  ", "##X", 'X', ItemList.ruby, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.mirror, 1), " # ", "#X#", " # ", 'X', ItemList.glodust, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.shield, 1), "###", "#X#", " # ", 'X', ItemList.ruby, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.gloves, 1), "X X", "# #", 'X', ItemList.slimeBall, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(
            new ItemStack(ItemList.capeAgility, 1), "XX", "##", "##", 'X', new ItemStack(ItemList.scrap, 1, 0), '#', new ItemStack(BlockList.cloth, 1, 15)
        );
        cm.addRecipe(new ItemStack(ItemList.spectacles, 1), "X#X", "# #", 'X', ItemList.glodust, '#', new ItemStack(ItemList.scrap, 1, 0));
        cm.addRecipe(new ItemStack(ItemList.ringRegen, 1), " # ", "X X", " X ", '#', ItemList.ruby, 'X', new ItemStack(ItemList.scrap, 1, 0));
    }
}
