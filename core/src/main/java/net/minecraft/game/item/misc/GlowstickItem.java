package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Glowstick;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class GlowstickItem extends Item {
    public GlowstickItem(int itemid) {
        super(itemid);
        this.priorityItem = true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        world.playEntitySound(player, "random.throw", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
        world.spawnEntity(new Glowstick(world, player, 1.0F));
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
            player.swingOffhandItem();
        } else {
            player.swingItem();
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }
}
