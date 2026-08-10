package net.minecraft.game.block.tileentity.smelters;

import net.minecraft.game.block.utility.Smoker;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.special.FurnaceRecipes;
import net.minecraft.localization.Translator;

public class SmokerEntity extends FurnaceEntity implements Inventory {
    public SmokerEntity() {
        this.slots = new ItemStack[22];
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.smoker");
    }

    @Override
    public void tick() {
        boolean burning = this.burnTime > 0;
        boolean forceUpdate = false;
        if (this.burnTime > 0) {
            this.burnTime--;
            forceUpdate = true;
        }

        if (this.burnTime == 0 && this.canSmelt()) {
            this.fuel = this.burnTime = getItemFuel(this.slots[21]);
            if (this.burnTime > 0) {
                forceUpdate = true;
                if (this.slots[21] != null) {
                    this.slots[21].stackSize--;
                    if (this.slots[21].stackSize == 0) {
                        this.slots[21] = null;
                    }
                }
            }
        }

        if (this.isBurning() && this.canSmelt()) {
            int totalCookTime = 200;

            for (int i = 0; i < 21; i++) {
                if (this.slots[i] != null) {
                    totalCookTime += this.slots[i].stackSize;
                }
            }

            this.cookTime++;
            if (this.cookTime == totalCookTime) {
                this.cookTime = 0;
                this.smeltItem();
                forceUpdate = true;
            }
        } else {
            this.cookTime = 0;
        }

        if (burning != this.burnTime > 0) {
            forceUpdate = true;
            Smoker.updateFurnaceBlockState(this.burnTime > 0, this.world, this.x, this.y, this.z);
        }

        if (forceUpdate) {
            this.onChanged();
        }
    }

    @Override
    protected boolean canSmelt() {
        boolean canSmelt = false;

        for (int i = 0; i < 21; i++) {
            if (this.slots[i] != null && FurnaceRecipes.smelting().getSmeltingResult(this.slots[i].getItem().id, this.slots[i].itemDmg) != null) {
                canSmelt = true;
            }
        }

        return canSmelt && (this.slots[21] != null && getItemFuel(this.slots[21]) > 0 || this.isBurning());
    }

    private boolean canSmelt(int i) {
        boolean canSmelt = false;
        if (this.slots[i] != null && FurnaceRecipes.smelting().getSmeltingResult(this.slots[i].getItem().id, this.slots[i].itemDmg) != null) {
            canSmelt = true;
        }

        return canSmelt && (this.slots[21] != null && getItemFuel(this.slots[21]) > 0 || this.isBurning());
    }

    @Override
    public void smeltItem() {
        for (int i = 0; i < 21; i++) {
            if (this.canSmelt(i)) {
                ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(this.slots[i].getItem().id, this.slots[i].itemDmg);
                int stackSize = this.slots[i].stackSize;
                this.slots[i] = result.copy();
                this.slots[i].stackSize = stackSize;
                break;
            }
        }
    }
}
