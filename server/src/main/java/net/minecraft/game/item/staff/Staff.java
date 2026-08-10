package net.minecraft.game.item.staff;

import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;

public abstract class Staff extends Item {
    public Staff(int itemid) {
        super(itemid);
        this.maxDamage = 256;
        this.maxStackSize = 1;
        this.unbreakable = true;
        this.priorityItem = true;
        this.info.add(this.maxDamage + " item.desc.durability");
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.sapphire.id || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && stack2.getItemDamage() == 3;
    }

    @Override
    public int repairCost() {
        return 3000;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("staffs/" + this.internalName);
    }
}
