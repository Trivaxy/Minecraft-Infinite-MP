package net.minecraft.game.block.tileentity.container;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public abstract class ContainerEntity extends TileEntity implements Inventory {
    public ItemStack[] inventory = new ItemStack[9];

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public final ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public final ItemStack decrStackSize(int slot, int amount) {
        if (this.inventory[slot] != null) {
            if (this.inventory[slot].stackSize <= amount) {
                ItemStack stack = this.inventory[slot];
                this.inventory[slot] = null;
                this.onChanged();
                return stack;
            }

            ItemStack stack = this.inventory[slot].splitStack(amount);
            if (this.inventory[slot].stackSize == 0) {
                this.inventory[slot] = null;
            }

            this.onChanged();
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public final void setSlotContents(int slotIndex, ItemStack itemStack) {
        this.inventory[slotIndex] = itemStack;
        if (itemStack != null && itemStack.stackSize > 64) {
            itemStack.stackSize = 64;
        }

        this.onChanged();
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container");
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<CompoundTag> itemsTag = tag.getTagList("Items");
        this.inventory = new ItemStack[this.getSize()];

        for (int index = 0; index < itemsTag.size(); index++) {
            CompoundTag itemTag = itemsTag.get(index);
            int slotIndex = itemTag.getByte("Slot") & 255;
            if (slotIndex >= 0 && slotIndex < this.inventory.length) {
                this.inventory[slotIndex] = new ItemStack(itemTag);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> items = new ListTag<>();

        for (int slotIndex = 0; slotIndex < this.getSize(); slotIndex++) {
            if (this.inventory[slotIndex] != null) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.setByte("Slot", (byte)slotIndex);
                this.inventory[slotIndex].writeToNBT(slotTag);
                items.add(slotTag);
            }
        }

        tag.setTag("Items", items);
    }

    @Override
    public final int getStackLimit() {
        return 64;
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : player.getDistanceSq(this.x + 0.5F, this.y + 0.5F, this.z + 0.5F) <= 64.0;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
