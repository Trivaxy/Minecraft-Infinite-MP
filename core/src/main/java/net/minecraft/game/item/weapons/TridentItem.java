package net.minecraft.game.item.weapons;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Trident;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class TridentItem extends Item {
    public TridentItem(int itemid) {
        super(itemid);
        this.maxStackSize = 1;
        this.maxDamage = 256;
        this.attackDelay = 20;
        this.priorityItem = true;
        this.isWeapon = true;
        this.canSwing = false;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add("8 item.desc.attack");
    }

    @Override
    public int getDamageVsEntity(Entity stack) {
        return 8;
    }

    @Override
    public void hitEntity(ItemStack stacl, Mob target, Player player, World world) {
        stacl.damageItem(1, player);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        float force = (this.getMaxUseDuration(stack) - itemInUseCount) / 20.0F;
        force = (force * force + force * 2.0F) / 3.0F;
        if (!(force < 0.1)) {
            if (force > 1.0F) {
                force = 1.0F;
            }

            Trident trident = new Trident(world, player, stack.itemDmg, 8, this.id, this.maxDamage, force * 2.0F);
            world.playEntitySound(player, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
            if (!world.isOnline) {
                if (player.inventory.getOffHandItem() != null
                    && player.itemInUse == player.inventory.getOffHandItem()
                    && player.inventory.getOffHandItem().getItem() == ItemList.trident) {
                    player.swingOffhandItem();
                    world.spawnEntity(trident);
                    player.inventory.setSlotContents(44, null);
                } else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ItemList.trident) {
                    player.swingItem();
                    world.spawnEntity(trident);
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }
            }

            player.addStat(StatList.useStats[this.id], 1);
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
    public ItemStack use(ItemStack stack, World world, Player playa) {
        playa.setItemInUse(stack, this.getMaxUseDuration(stack));
        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null
            ? super.isRepairable(stack1, stack2)
            : stack2.itemID == ItemList.ruby.id || stack2.itemID == ItemList.ingotGold.id || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 3000;
    }
}
