package net.minecraft.game.item.staff;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.follower.Snowman;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Snowball;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class SnowmanStaff extends Staff {
    public SnowmanStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (stack.itemDmg < stack.getItem().getMaxDamage() - 16) {
            Snowman snowman = new Snowman(world);
            snowman.setPosAndRot(player.posX + 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            world.spawnEntity(snowman);
            world.playSoundEffect(player, player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    snowman.posX + world.rand.nextFloat() * snowman.width * 2.0F - snowman.width,
                    snowman.posY + world.rand.nextFloat() * snowman.height,
                    snowman.posZ + world.rand.nextFloat() * snowman.width * 2.0F - snowman.width,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
            }

            stack.damageItem(16, player);
        } else {
            Entity throwable = new Snowball(world, player, 0);
            if (!world.isOnline) {
                world.spawnEntity(throwable);
            }

            world.playEntitySound(player, "random.throw", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public int repairCost() {
        return 1000;
    }
}
