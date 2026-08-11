package net.minecraft.game.item.misc;

import java.util.List;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.animal.mounts.RideableMob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Horn extends Item {
    public Horn(int itemid) {
        super(itemid);
        this.priorityItem = true;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        if (stack.getMaxItemUseDuration() - player.itemAnimFrames >= 18) {
            world.playEntitySound(player, "random.horn", 1.0F, 1.0F);
            List<Entity> entities = world.getEntityList();
            if (entities != null) {
                for (int entity = 0; entity < entities.size(); entity++) {
                    if (entities.get(entity) != null
                        && entities.get(entity) instanceof RideableMob
                        && ((RideableMob)entities.get(entity)).isTamed()
                        && ((RideableMob)entities.get(entity)).getOwner() != null
                        && ((RideableMob)entities.get(entity)).getOwner().equals(player)) {
                        entities.get(entity).setPosition(player.posX, player.posY, player.posZ);
                        player.triggerAchievement(AchievementList.horn);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.bow;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.setItemInUse(stack, this.getMaxUseDuration(stack));
        return stack;
    }
}
