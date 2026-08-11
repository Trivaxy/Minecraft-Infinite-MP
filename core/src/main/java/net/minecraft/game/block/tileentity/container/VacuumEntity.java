package net.minecraft.game.block.tileentity.container;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class VacuumEntity extends ContainerEntity implements Inventory {
    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.vacuum");
    }

    public final boolean storeStack(ItemStack stack) {
        int size = stack.stackSize;
        int id = stack.itemID;
        int meta = stack.itemDmg;
        int slot = 0;
        if (size <= 0) {
            return false;
        }

        int outOfBounds;
        while (true) {
            if (slot >= this.getSize()) {
                outOfBounds = -1;
                break;
            }

            if (this.inventory[slot] != null
                && this.inventory[slot].itemID == id
                && this.inventory[slot].stackSize < this.inventory[slot].getItem().getStackLimit()
                && this.inventory[slot].stackSize < 64
                && (!this.inventory[slot].hasSubtypes() || this.inventory[slot].getItemDamage() == stack.getItemDamage())
                && ItemStack.areItemStackTagsEqual(this.inventory[slot], stack)
                && !this.inventory[slot].hasTag()) {
                outOfBounds = slot;
                break;
            }

            slot++;
        }

        int selectedSlot = outOfBounds;
        if (outOfBounds < 0) {
            selectedSlot = this.getFirstEmptySlot();
        }

        if (selectedSlot < 0) {
            outOfBounds = size;
        } else {
            if (this.inventory[selectedSlot] == null) {
                this.inventory[selectedSlot] = new ItemStack(id, 0, meta);
                if (stack.hasTag()) {
                    this.inventory[selectedSlot].setTag((CompoundTag)stack.getTag().copy());
                }
            }

            id = size;
            if (size > this.inventory[selectedSlot].getItem().getStackLimit() - this.inventory[selectedSlot].stackSize) {
                id = this.inventory[selectedSlot].getItem().getStackLimit() - this.inventory[selectedSlot].stackSize;
            }

            if (id > 64 - this.inventory[selectedSlot].stackSize) {
                id = 64 - this.inventory[selectedSlot].stackSize;
            }

            if (id == 0) {
                outOfBounds = size;
            } else {
                size -= id;
                this.inventory[selectedSlot].stackSize += id;
                this.inventory[selectedSlot].animationsToGo = 5;
                outOfBounds = size;
            }
        }

        stack.stackSize = outOfBounds;
        if (stack.stackSize == 0) {
            return true;
        } else {
            int finalSlot = this.getFirstEmptySlot();
            if (finalSlot >= 0) {
                this.inventory[finalSlot] = stack;
                this.inventory[finalSlot].animationsToGo = 5;
                return true;
            } else {
                return false;
            }
        }
    }

    private int getFirstEmptySlot() {
        for (int slot = 0; slot < this.getSize(); slot++) {
            if (this.inventory[slot] == null) {
                return slot;
            }
        }

        return -1;
    }
}
