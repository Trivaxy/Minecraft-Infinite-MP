package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class LifeShard extends Item {
    public LifeShard(int itemid) {
        super(itemid);
        this.priorityItem = true;
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.maxHealth < 40) {
            player.maxHealth += 2;
            player.addHealth(2);
            if (player.maxHealth >= 40) {
                player.triggerAchievement(AchievementList.maxHP);
            }
        }

        player.heal(player.maxHealth);
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }
}
