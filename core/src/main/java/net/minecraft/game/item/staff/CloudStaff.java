package net.minecraft.game.item.staff;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class CloudStaff extends Staff {
    public CloudStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.itemUseCooldown == 0 && stack.isNotBroken()) {
            Projectile windball = new Windball(world, player, 4);
            world.spawnEntity(windball);
            world.playSoundEffect(player, player.posX, player.posY, player.posZ, "mob.ghast.shoot", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            stack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }
}
