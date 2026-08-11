package net.minecraft.game.block.tileentity.container;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.CraftingManager;

public class CrafterEntity extends TileEntity {
    public CraftingInventory inventory = new CraftingInventory(this, 3, 3);
    public WorkbenchInventory craftResult = new WorkbenchInventory();

    public void decreaseStackSizes() {
        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            if (this.inventory.getStackInSlot(slot) != null) {
                this.inventory.decrStackSize(slot, 1);
            }
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<? extends Tag> items = tag.getTagList("Items");

        for (int index = 0; index < items.size(); index++) {
            CompoundTag itemTag = (CompoundTag)items.get(index);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.inventory.getSize()) {
                this.inventory.setSlotContents(slot, new ItemStack(itemTag));
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> items = new ListTag<>();

        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            if (this.inventory.getStackInSlot(slot) != null) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.setByte("Slot", (byte)slot);
                this.inventory.getStackInSlot(slot).writeToNBT(itemTag);
                items.add(itemTag);
            }
        }

        tag.setTag("Items", items);
    }

    public final boolean storeStack(ItemStack itemStack) {
        int size = itemStack.stackSize;
        int id = itemStack.itemID;
        int slot = 0;
        if (size <= 0) {
            return false;
        }

        int bestSlot = -1;
        int lowestSize = Integer.MAX_VALUE;

        for (int var10 = 0; var10 < this.inventory.getSize(); var10++) {
            ItemStack current = this.inventory.getStackInSlot(var10);
            if (current != null
                && current.itemID == id
                && current.stackSize < current.getItem().getStackLimit()
                && current.stackSize < 64
                && (!current.hasSubtypes() || current.getItemDamage() == itemStack.getItemDamage())
                && ItemStack.areItemStackTagsEqual(current, itemStack)
                && !current.hasTag()
                && current.stackSize < lowestSize) {
                lowestSize = current.stackSize;
                bestSlot = var10;
            }
        }

        int selectedSlot = bestSlot;
        if (selectedSlot < 0) {
            selectedSlot = this.getEqualStack(itemStack);
        }

        if (selectedSlot < 0) {
            selectedSlot = size;
        } else {
            id = size;
            if (size > this.inventory.getStackInSlot(selectedSlot).getItem().getStackLimit() - this.inventory.getStackInSlot(selectedSlot).stackSize) {
                id = this.inventory.getStackInSlot(selectedSlot).getItem().getStackLimit() - this.inventory.getStackInSlot(selectedSlot).stackSize;
            }

            if (id > 64 - this.inventory.getStackInSlot(selectedSlot).stackSize) {
                id = 64 - this.inventory.getStackInSlot(selectedSlot).stackSize;
            }

            if (id == 0) {
                selectedSlot = size;
            } else {
                size -= id;
                this.inventory.getStackInSlot(selectedSlot).stackSize += id;
                this.inventory.getStackInSlot(selectedSlot).animationsToGo = 5;
                selectedSlot = size;
            }
        }

        itemStack.stackSize = selectedSlot;
        return itemStack.stackSize == 0;
    }

    private int getEqualStack(ItemStack stack) {
        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            if (this.inventory.getStackInSlot(slot) != null
                && this.inventory.getStackInSlot(slot).itemID == stack.itemID
                && this.inventory.getStackInSlot(slot).itemDmg == stack.itemDmg
                && this.inventory.getStackInSlot(slot).stackSize < this.inventory.getStackInSlot(slot).getStackLimit()) {
                return slot;
            }
        }

        return -1;
    }

    public void onCraftMatrixChanged(Inventory inv) {
        this.craftResult.setSlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.inventory, this.world));
    }
}
