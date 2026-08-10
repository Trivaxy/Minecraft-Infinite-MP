package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.statistics.achievement.AchievementList;

public class DarkMatter extends Item {
    public DarkMatter(int itemid) {
        super(itemid);
    }

    @Override
    public void triggerAchievement(Player player) {
        player.triggerAchievement(AchievementList.voidBeing);
    }
}
