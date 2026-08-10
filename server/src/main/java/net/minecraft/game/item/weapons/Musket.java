package net.minecraft.game.item.weapons;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Bullet;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Musket extends Item {
    public Musket(int id) {
        super(id);
        this.maxStackSize = 1;
        this.maxDamage = 256;
        this.priorityItem = true;
        this.canSwing = false;
        this.outputId = 4326;
        this.outputCount = 3;
        this.info.add(this.maxDamage + " item.desc.durability");
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        if ((player.inventory.hasItem(ItemList.musketRound.id) || player.properties.gamemode == 1)
            && stack.getMaxItemUseDuration() - player.itemAnimFrames >= 18) {
            float force = (this.getMaxUseDuration(stack) - itemInUseCount) / 20.0F;
            force = (force * force + force * 2.0F) / 3.0F;
            if (force < 0.1) {
                return;
            }

            if (force > 1.0F) {
                force = 1.0F;
            }

            if (player.inventory.consumeInventoryItem(ItemList.musketRound.id) || player.properties.gamemode == 1) {
                if (player.inventory.getOffHandItem() != null
                    && player.itemInUse == player.inventory.getOffHandItem()
                    && player.inventory.getOffHandItem().itemID == ItemList.musket.id) {
                    player.inventory.setSlotContents(44, new ItemStack(ItemList.musketLoaded.id, 1, stack.itemDmg, stack.customName, stack.customTag));
                    world.playEntitySound(player, "random.click", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
                    player.swingOffhandItem();
                } else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == ItemList.musket.id) {
                    player.inventory
                        .setSlotContents(
                            player.inventory.currentSlot, new ItemStack(ItemList.musketLoaded.id, 1, stack.itemDmg, stack.customName, stack.customTag)
                        );
                    world.playEntitySound(player, "random.click", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
                    player.swingItem();
                }
            }
        }
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.bow;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (this.id == ItemList.musketLoaded.id) {
            Projectile bullet = new Bullet(world, player, 20);
            world.playEntitySound(player, "random.explode", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + 1.0F);
            world.spawnParticle(
                ParticleType.EXPLODE,
                player.posX,
                player.posY,
                player.posZ,
                world.rand.nextGaussian() * 0.02,
                world.rand.nextGaussian() * 0.02,
                world.rand.nextGaussian() * 0.02
            );
            if (!world.isOnline) {
                world.spawnEntity(bullet);
                stack.damageItem(1, player);
                return new ItemStack(ItemList.musket.id, stack.stackSize, stack.itemDmg, stack.customName, stack.customTag);
            }
        } else if (player.inventory.hasItem(ItemList.musketRound.id) || player.properties.gamemode == 1) {
            player.setItemInUse(stack, this.getMaxUseDuration(stack));
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == ItemList.ingotDamascus.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && stack2.getItemDamage() == 3;
    }

    @Override
    public int repairCost() {
        return 1000;
    }
}
