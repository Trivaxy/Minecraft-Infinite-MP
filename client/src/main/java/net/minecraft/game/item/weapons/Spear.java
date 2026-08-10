package net.minecraft.game.item.weapons;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Spear extends Tool {
    private int weaponDamage;

    public Spear(int itemid, ToolMaterial toolTier) {
        super(itemid, itemid, toolTier);
        this.maxStackSize = 1;
        this.maxDamage = toolTier.getDurability();
        this.weaponDamage = 2 + toolTier.getEntityDamage();
        this.attackDelay = 10 + 10 * toolTier.getEntityDamage();
        this.isWeapon = true;
        this.canSwing = false;
        this.outputCount = 1;
        this.info.clear();
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add(this.weaponDamage + " item.desc.attack");
        this.info.add("item.desc.joust");
    }

    @Override
    public void hitEntity(ItemStack stack, Mob mob, Player player, World world) {
        stack.damageItem(1, player);
    }

    @Override
    public void onBlockDestroyed(ItemStack stack, int x, int y, int z, int side, Mob mob) {
        stack.damageItem(2, mob);
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return this.weaponDamage;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.joust;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 300 - 20 * this.toolMaterial.getEntityDamage();
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.itemUseCooldown == 0) {
            player.setItemInUse(stack, this.getMaxUseDuration(stack));
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        player.itemUseCooldown = stack.getItem().attackDelay;
        player.maxDelay = stack.getItem().attackDelay;
        player.setSprinting(false);
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == this.toolMaterial.getRepairID() || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 1 || stack2.getItemDamage() == 3);
    }

    @Override
    public int repairCost() {
        return 100 * this.toolMaterial.getEntityDamage();
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("spears/" + this.internalName);
    }
}
