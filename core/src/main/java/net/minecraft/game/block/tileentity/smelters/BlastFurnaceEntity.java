package net.minecraft.game.block.tileentity.smelters;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.food.FoodItem;
import net.minecraft.localization.Translator;

public class BlastFurnaceEntity extends FurnaceEntity implements Inventory {
    public BlastFurnaceEntity() {
        this.smeltSpeed = 100;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.blstfurnace");
    }

    @Override
    protected boolean canSmelt() {
        return this.slots[0] == null || !(this.slots[0].getItem() instanceof FoodItem) && this.slots[0].getItem() != ItemList.egg ? super.canSmelt() : false;
    }
}
