package net.minecraft.game.item.weapons;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.ScytheHead;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Scythe extends Item {
    public Scythe(int id) {
        super(id);
        this.maxDamage = 256;
        this.maxStackSize = 1;
        this.attackDelay = 20;
        this.isWeapon = true;
        this.unbreakable = true;
        this.priorityItem = true;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add("7item.desc.attack");
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.itemUseCooldown == 0 && stack.isNotBroken()) {
            Projectile head = new ScytheHead(world, player, 7);
            head.setSize(3.0F, 3.0F);
            world.spawnEntity(head);
            world.playSoundEffect(player.posX, player.posY, player.posZ, "mob.ghast.shoot", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            player.itemUseCooldown = player.maxDelay = 20;
            stack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return 7;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.amethyst.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 1 || stack2.getItemDamage() == 3);
    }

    @Override
    public int repairCost() {
        return 3000;
    }
}
