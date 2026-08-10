package net.minecraft.game.item.tools;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;

public class MagicMirror extends Item {
    public MagicMirror(int id) {
        super(id);
        this.maxDamage = 256;
        this.maxStackSize = 1;
        this.unbreakable = true;
        this.priorityItem = true;
        this.canSwing = false;
        this.info.add(this.maxDamage + " item.desc.durability");
    }

    @Override
    public final ItemStack use(ItemStack itemStack, World world, Player entityPlayer) {
        if (itemStack.itemDmg < itemStack.getItem().getMaxDamage() - 25) {
            if (entityPlayer.getSpawn(entityPlayer.dimension) != null) {
                if (entityPlayer.hook != null) {
                    entityPlayer.hook.setDead();
                    entityPlayer.hook = null;
                }

                entityPlayer.setPosAndRot(
                    entityPlayer.getSpawn(entityPlayer.dimension).x + 0.5,
                    entityPlayer.getSpawn(entityPlayer.dimension).y + 1.0,
                    entityPlayer.getSpawn(entityPlayer.dimension).z + 0.5,
                    0.0F,
                    0.0F
                );
                world.playSoundEffect(
                    entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F)
                );

                for (int i = 0; i < 20; i++) {
                    world.spawnParticle(
                        ParticleType.EXPLODE,
                        entityPlayer.posX + world.rand.nextFloat() * entityPlayer.width * 2.0F - entityPlayer.width,
                        entityPlayer.posY + world.rand.nextFloat() * entityPlayer.height,
                        entityPlayer.posZ + world.rand.nextFloat() * entityPlayer.width * 2.0F - entityPlayer.width,
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02
                    );
                }

                itemStack.damageItem(25, entityPlayer);
                entityPlayer.addStat(StatList.useStats[this.id], 1);
            } else {
                entityPlayer.addChatMessage("§c" + Translator.getInstance().translateKey("respawn.no"));
            }
        }

        return itemStack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == ItemList.sapphire.id || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 3000;
    }
}
