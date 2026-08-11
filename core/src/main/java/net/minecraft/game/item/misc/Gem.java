package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.statistics.achievement.AchievementList;

public class Gem extends Item {
    public Gem(int itemid) {
        super(itemid);
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this == ItemList.diamond) {
            player.triggerAchievement(AchievementList.diamantes);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("gems/" + this.internalName);
    }
}
