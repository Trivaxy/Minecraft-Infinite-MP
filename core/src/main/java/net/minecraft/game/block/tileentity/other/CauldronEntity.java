package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.BrewingSystem;

public class CauldronEntity extends TileEntity {
    private int liquidLevel;
    private int potion;

    public boolean hasLiquid() {
        return this.liquidLevel <= 0;
    }

    public boolean justWater() {
        return this.potion == 0;
    }

    public boolean setPotion() {
        if (this.liquidLevel == 0) {
            this.liquidLevel = 3;
            this.potion = 0;
            return true;
        } else if (this.liquidLevel < 3) {
            this.potion = 0;
            this.liquidLevel = 3;
            return true;
        } else {
            return false;
        }
    }

    public boolean activateCauldron(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.itemID == ItemList.potion.id) {
            if (this.hasLiquid()) {
                this.liquidLevel = 1;
                this.potion = itemStack.getItemDamage();
                return true;
            } else if (this.potion == itemStack.getItemDamage() && this.liquidLevel < 3) {
                this.liquidLevel++;
                return true;
            } else {
                return false;
            }
        } else {
            int potion = 0;
            if (!this.hasLiquid() && ItemList.items[itemStack.itemID].getPotionPrefix() >= 0) {
                if (this.potion == 0) {
                    potion = ItemList.items[itemStack.itemID].getPotionPrefix();
                } else {
                    potion = BrewingSystem.applyIngredient(this.potion, ItemList.items[itemStack.itemID].getPotionEffect());
                }

                if (potion != this.potion) {
                    this.potion = potion;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public void decreaseLiquidLevel() {
        this.liquidLevel--;
        if (this.liquidLevel <= 0) {
            this.potion = 0;
        }
    }

    public void increaseLiquidLevel() {
        if (this.liquidLevel < 3) {
            this.liquidLevel++;
        }
    }

    public int getLiquidLevel() {
        return this.liquidLevel;
    }

    public int getPotion() {
        return this.potion;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.liquidLevel = tag.getInteger("liquidLevel");
        this.potion = tag.getInteger("liquidData");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("liquidLevel", this.liquidLevel);
        tag.setInteger("liquidData", this.potion);
    }
}
