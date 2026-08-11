package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class Shears extends Item {
    public Shears(int itemid) {
        super(itemid);
        this.maxDamage = 256;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.outputId = 4105;
        this.outputCount = 2;
        this.priorityItem = true;
    }

    @Override
    public float getDigSpd(ItemStack stack, Block block) {
        return block.getTool() == ToolType.SHEARS ? 5.0F : super.getDigSpd(stack, block);
    }

    @Override
    public void onBlockDestroyed(ItemStack stack, int side, int x, int y, int z, Mob mob) {
        stack.damageItem(1, mob);
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return block.getTool() == ToolType.SHEARS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack, ItemStack input) {
        return input == null ? false : input.getItem() == ItemList.scroll && (input.getItemDamage() == 2 || input.getItemDamage() == 3);
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && (stack2.itemID == ItemList.ingotIron.id || stack2.itemID == this.id);
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
