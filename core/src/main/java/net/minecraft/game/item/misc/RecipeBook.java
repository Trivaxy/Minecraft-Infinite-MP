package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class RecipeBook extends Item {
    public RecipeBook(int itemid) {
        super(itemid);
        this.maxStackSize = 1;
        this.priorityItem = true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.displayGUIRecipe();
        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }
}
