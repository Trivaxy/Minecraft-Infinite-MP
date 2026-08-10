package net.minecraft.game.item.staff;

import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class LichStaff extends Staff {
    public LichStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (stack.itemDmg < stack.getItem().getMaxDamage() - 16) {
            Decoy human1 = new Decoy(world);
            Decoy human2 = new Decoy(world);
            human1.setPosAndRot(player.posX + 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            human2.setPosAndRot(player.posX - 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            human1.skinUrl = human2.skinUrl = player.skinUrl;
            world.spawnEntity(human1);
            world.spawnEntity(human2);
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human1.posX + world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    human1.posY + world.rand.nextFloat() * human1.height,
                    human1.posZ + world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human2.posX + world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    human2.posY + world.rand.nextFloat() * human2.height,
                    human2.posZ + world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
            }

            double range = 16.0;
            List<Entity> entities = world.getEntities(
                player,
                BoundingBox.getBoundingBox(
                    player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range
                )
            );

            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                if (entity instanceof Monster) {
                    ((Monster)entity).attackTarget = ((Monster)entity).lastTarget = null;
                    ((Monster)entity).navigator.clearPath();
                }
            }

            stack.damageItem(16, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }
}
