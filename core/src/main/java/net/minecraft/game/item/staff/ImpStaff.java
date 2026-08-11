package net.minecraft.game.item.staff;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.monster.Imp;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class ImpStaff extends Staff {
    public ImpStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (stack.itemDmg < stack.getItem().getMaxDamage() - 16) {
            for (Entity entity : world.entities) {
                if (entity != null && entity instanceof Imp && ((Imp)entity).tamed) {
                    entity.setDead();

                    for (int i = 0; i < 20; i++) {
                        world.spawnParticle(
                            ParticleType.EXPLODE,
                            entity.posX + world.rand.nextFloat() * entity.width * 2.0F - entity.width,
                            entity.posY + world.rand.nextFloat() * entity.height,
                            entity.posZ + world.rand.nextFloat() * entity.width * 2.0F - entity.width,
                            world.rand.nextGaussian() * 0.02,
                            world.rand.nextGaussian() * 0.02,
                            world.rand.nextGaussian() * 0.02
                        );
                    }
                }
            }

            Imp imp = new Imp(world, true);
            imp.setPosAndRot(player.posX + 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            imp.tamed = true;
            world.spawnEntity(imp);
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    imp.posX + world.rand.nextFloat() * imp.width * 2.0F - imp.width,
                    imp.posY + world.rand.nextFloat() * imp.height,
                    imp.posZ + world.rand.nextFloat() * imp.width * 2.0F - imp.width,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
            }

            stack.damageItem(16, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.ruby.id || stack2.itemID == this.id;
    }
}
