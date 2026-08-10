package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.statistics.achievement.AchievementList;

public class Ingot extends Item {
    public Ingot(int itemid) {
        super(itemid);
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this.id == ItemList.ingotIron.id) {
            player.triggerAchievement(AchievementList.acquireIron);
        } else if (this.id == ItemList.ingotDamascus.id) {
            player.triggerAchievement(AchievementList.damascus);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("ingots/" + this.internalName);
    }
}
