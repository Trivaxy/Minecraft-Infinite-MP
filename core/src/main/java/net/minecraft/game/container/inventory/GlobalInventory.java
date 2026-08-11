package net.minecraft.game.container.inventory;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public final class GlobalInventory implements Inventory {
    private final int slotCount = 27;
    public ItemStack[] stacks = new ItemStack[27];
    public Player player;

    public GlobalInventory(Player entityPlayer) {
        this.player = entityPlayer;
    }

    @Override
    public final ItemStack decrStackSize(int slotIndex, int count) {
        ItemStack[] stacks = this.stacks;
        if (slotIndex >= this.stacks.length) {
            slotIndex -= this.stacks.length;
        }

        if (stacks[slotIndex] != null) {
            if (stacks[slotIndex].stackSize <= count) {
                ItemStack stack = stacks[slotIndex];
                stacks[slotIndex] = null;
                return stack;
            }

            ItemStack stack = stacks[slotIndex].splitStack(count);
            if (stacks[slotIndex].stackSize == 0) {
                stacks[slotIndex] = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public final void setSlotContents(int slotIndex, ItemStack itemStack) {
        if (slotIndex >= this.stacks.length) {
            slotIndex -= this.stacks.length;
        }

        this.stacks[slotIndex] = itemStack;
    }

    @Override
    public final int getSize() {
        return this.stacks.length;
    }

    @Override
    public final ItemStack getStackInSlot(int slot) {
        if (slot >= this.stacks.length) {
            slot -= this.stacks.length;
        }

        return this.stacks[slot];
    }

    @Override
    public final String getName() {
        return Translator.getInstance().translateKey("container.chestObsidian");
    }

    @Override
    public final int getStackLimit() {
        return 64;
    }

    @Override
    public void onChanged() {
    }

    @Override
    public boolean isInteractible(Player player) {
        return player.getDistanceSqTo(this.player) <= 64.0;
    }

    public ListTag<CompoundTag> saveContents(ListTag<CompoundTag> tagList) {
        for (int slot = 0; slot < this.stacks.length; slot++) {
            if (this.stacks[slot] != null) {
                CompoundTag tag = new CompoundTag();
                tag.setByte("Slot", (byte)slot);
                this.stacks[slot].writeToNBT(tag);
                tagList.add(tag);
            }
        }

        return tagList;
    }

    public void loadContents(ListTag<? extends Tag> tagList) {
        this.stacks = new ItemStack[27];

        for (int index = 0; index < tagList.size(); index++) {
            CompoundTag tag = (CompoundTag)tagList.get(index);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.stacks.length) {
                this.stacks[slot] = new ItemStack(tag);
            }
        }
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
