package net.minecraft.game.item.staff;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.monster.arachnid.Termite;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class PharaohStaff extends Staff {
    public PharaohStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (stack.itemDmg < stack.getItem().getMaxDamage() - 16) {
            Termite human1 = new Termite(world, true);
            Termite human2 = new Termite(world, true);
            Termite human3 = new Termite(world, true);
            Termite human4 = new Termite(world, true);
            human1.setPosAndRot(player.posX + 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            human2.setPosAndRot(player.posX - 1.0, player.posY, player.posZ, player.pitch, player.yaw);
            human3.setPosAndRot(player.posX, player.posY, player.posZ + 1.0, player.pitch, player.yaw);
            human4.setPosAndRot(player.posX, player.posY, player.posZ - 1.0, player.pitch, player.yaw);
            world.spawnEntity(human1);
            world.spawnEntity(human2);
            world.spawnEntity(human3);
            world.spawnEntity(human4);
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                double offsetX = world.rand.nextGaussian() * 0.02;
                double offsetY = world.rand.nextGaussian() * 0.02;
                double offsetZ = world.rand.nextGaussian() * 0.02;
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human1.posX + world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    human1.posY + world.rand.nextFloat() * human1.height,
                    human1.posZ + world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    offsetX,
                    offsetY,
                    offsetZ
                );
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human2.posX + world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    human2.posY + world.rand.nextFloat() * human2.height,
                    human2.posZ + world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    offsetX,
                    offsetY,
                    offsetZ
                );
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human3.posX + world.rand.nextFloat() * human3.width * 2.0F - human3.width,
                    human3.posY + world.rand.nextFloat() * human3.height,
                    human3.posZ + world.rand.nextFloat() * human3.width * 2.0F - human3.width,
                    offsetX,
                    offsetY,
                    offsetZ
                );
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    human4.posX + world.rand.nextFloat() * human4.width * 2.0F - human4.width,
                    human4.posY + world.rand.nextFloat() * human4.height,
                    human4.posZ + world.rand.nextFloat() * human4.width * 2.0F - human4.width,
                    offsetX,
                    offsetY,
                    offsetZ
                );
            }

            stack.damageItem(16, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.ruby.id || stack2.itemID == ItemList.ingotGold.id || stack2.itemID == this.id;
    }
}
