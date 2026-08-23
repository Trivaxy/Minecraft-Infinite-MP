package net.minecraft.game.container.inventory;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.ArrowItem;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.AchievementList;

public class PlayerInventory implements Inventory {
    public ItemStack[] mainInventory = new ItemStack[36];
    public ItemStack[] armorSlots = new ItemStack[4];
    public ItemStack[] accessorySlots = new ItemStack[4];
    public ItemStack[] offhandSlot = new ItemStack[1];
    public Player player;
    public ItemStack activeStack;
    public int currentSlot = 0;
    public int prevActiveSlot = 0;

    public PlayerInventory(Player player) {
        this.player = player;
    }

    public ItemStack getCurrentItem() {
        return this.currentSlot < 9 && this.currentSlot >= 0 ? this.mainInventory[this.currentSlot] : null;
    }

    public ItemStack getOffHandItem() {
        return this.offhandSlot[0];
    }

    public ItemStack getItemStack() {
        return this.activeStack;
    }

    public void setItemStack(ItemStack stack) {
        this.activeStack = stack;
    }

    public boolean canUseItem() {
        return this.getOffHandItem() != null
            && (this.getCurrentItem() == null || this.getCurrentItem() != null && !this.getCurrentItem().getItem().priorityItem);
    }

    private int getSlotWithItem(int itemID) {
        for (int slot = 0; slot < this.mainInventory.length; slot++) {
            if (this.mainInventory[slot] != null && this.mainInventory[slot].itemID == itemID) {
                return slot;
            }
        }

        return -1;
    }

    public boolean hasItem(int itemId) {
        return this.getSlotWithItem(itemId) >= 0;
    }

    public boolean hasItemStack(ItemStack stack) {
        for (int slot = 0; slot < this.armorSlots.length; slot++) {
            if (this.armorSlots[slot] != null && this.armorSlots[slot].isItemEqual(stack)) {
                return true;
            }
        }

        for (int var3 = 0; var3 < this.accessorySlots.length; var3++) {
            if (this.accessorySlots[var3] != null && this.accessorySlots[var3].isItemEqual(stack)) {
                return true;
            }
        }

        for (int var4 = 0; var4 < this.mainInventory.length; var4++) {
            if (this.mainInventory[var4] != null && this.mainInventory[var4].isItemEqual(stack)) {
                return true;
            }
        }

        return this.offhandSlot[0] != null && this.offhandSlot[0].isItemEqual(stack);
    }

    public void changeCurrentItem(int index) {
        if (index > 0) {
            index = 1;
        }

        if (index < 0) {
            index = -1;
        }

        this.currentSlot -= index;

        while (this.currentSlot < 0) {
            this.currentSlot += 9;
        }

        while (this.currentSlot >= 9) {
            this.currentSlot -= 9;
        }

        if (this.mainInventory[this.currentSlot] != null) {
            this.mainInventory[this.currentSlot].textTime = 30.0F;
        }
    }

    public final void swapHands(ItemStack main, ItemStack offhand) {
        this.offhandSlot[0] = main;
        this.mainInventory[this.currentSlot] = offhand;
    }

    public final void swapItem(int itemID, int metadata) {
        int slotIndex = this.getSlotWithItem(itemID);
        if (itemID > 0 && itemID < 4096) {
            Block block = BlockList.blocks[itemID];
            if (block.hasStates) {
                slotIndex = this.getSlotWithItem(itemID, metadata);
            } else {
                metadata = 0;
            }
        }

        if (itemID == ItemList.spawnEgg.id) {
            slotIndex = this.getSlotWithItem(itemID, metadata);
        }

        if (this.getCurrentItem() != null) {
            this.mainInventory[this.currentSlot].setItemName("");
        }

        if (slotIndex >= 0 && slotIndex < 9) {
            this.currentSlot = slotIndex;
            if (this.mainInventory[this.currentSlot] != null) {
                this.mainInventory[this.currentSlot].textTime = 30.0F;
            }
        } else {
            if (this.mainInventory[this.currentSlot] == null) {
                ItemStack steck = new ItemStack(itemID, 1, metadata);
                if (steck.getItem().id > 4096 && !steck.getItem().hasSubtypes()) {
                    steck = new ItemStack(itemID, 1, 0);
                }

                this.mainInventory[this.currentSlot] = steck;
            } else {
                this.mainInventory[this.currentSlot].itemID = itemID;
                if (this.mainInventory[this.currentSlot].hasSubtypes()) {
                    this.mainInventory[this.currentSlot].itemDmg = metadata;
                } else {
                    this.mainInventory[this.currentSlot].itemDmg = 0;
                }

                this.mainInventory[this.currentSlot].stackSize = 1;
                this.mainInventory[this.currentSlot].setTag(null);
            }

            if (this.mainInventory[this.currentSlot] != null) {
                this.mainInventory[this.currentSlot].textTime = 30.0F;
            }
        }
    }

    public final void swapFromInventory(int itemID, int metadata) {
        if (!this.player.world.isOnline) {
            int slotIndex = this.getSlotWithItem(itemID);
            Item item = ItemList.items[itemID];
            if (item.hasSubtypes()) {
                slotIndex = this.getSlotWithItem(itemID, metadata);
            } else {
                metadata = 0;
            }

            if (slotIndex >= 0 && slotIndex < 9) {
                this.currentSlot = slotIndex;
                if (this.mainInventory[this.currentSlot] != null) {
                    this.mainInventory[this.currentSlot].textTime = 30.0F;
                }
            } else if (slotIndex >= 9) {
                if (ItemList.items[itemID].hasSubtypes()) {
                    this.replaceItem(itemID, metadata);
                } else {
                    this.replaceItem(itemID, 0);
                }

                if (this.mainInventory[this.currentSlot] != null) {
                    this.mainInventory[this.currentSlot].textTime = 30.0F;
                }
            }
        }
    }

    public void replaceItem(int itemID, int metadata) {
        int slotIndex = this.getSlotWithItem(itemID);
        Item item = ItemList.items[itemID];
        if (item.hasSubtypes()) {
            slotIndex = this.getSlotWithItem(itemID, metadata);
        } else {
            metadata = 0;
        }

        int count = this.mainInventory[slotIndex].stackSize;
        if (slotIndex >= 0 && this.mainInventory[this.currentSlot] != null) {
            this.mainInventory[slotIndex].itemID = this.mainInventory[this.currentSlot].itemID;
            this.mainInventory[slotIndex].stackSize = this.mainInventory[this.currentSlot].stackSize;
            if (this.mainInventory[this.currentSlot].hasDisplayName()) {
                this.mainInventory[slotIndex].setItemName(this.mainInventory[this.currentSlot].customName);
            }

            if (this.mainInventory[this.currentSlot].hasTag()) {
                this.mainInventory[slotIndex].setTag(this.mainInventory[this.currentSlot].customTag);
            }

            if (!this.mainInventory[slotIndex].hasSubtypes() && this.mainInventory[slotIndex].getMaxDamage() <= 0) {
                this.mainInventory[slotIndex].itemDmg = 0;
            } else {
                this.mainInventory[slotIndex].itemDmg = this.mainInventory[this.currentSlot].itemDmg;
            }
        } else if (slotIndex >= 0 && this.mainInventory[this.currentSlot] == null) {
            this.mainInventory[slotIndex] = null;
        }

        if (this.mainInventory[this.currentSlot] != null) {
            this.mainInventory[this.currentSlot].itemID = itemID;
            this.mainInventory[this.currentSlot].stackSize = count;
            this.mainInventory[this.currentSlot].setItemName("");
            this.mainInventory[this.currentSlot].setTag(null);
            if (this.mainInventory[this.currentSlot].hasSubtypes()) {
                this.mainInventory[this.currentSlot].itemDmg = metadata;
            } else {
                this.mainInventory[this.currentSlot].itemDmg = 0;
            }

            this.mainInventory[this.currentSlot].textTime = 30.0F;
        } else if (item.hasSubtypes()) {
            this.mainInventory[this.currentSlot] = new ItemStack(itemID, count, metadata);
        } else {
            this.mainInventory[this.currentSlot] = new ItemStack(itemID, count, 0);
        }
    }

    private int getSlotWithItem(int id, int meta) {
        for (int slotIndex = 0; slotIndex < this.mainInventory.length; slotIndex++) {
            if (this.mainInventory[slotIndex] != null && this.mainInventory[slotIndex].itemID == id && this.mainInventory[slotIndex].itemDmg == meta) {
                return slotIndex;
            }
        }

        return -1;
    }

    public void tick() {
        for (int slotIndex = 0; slotIndex < this.mainInventory.length; slotIndex++) {
            ItemStack slot = this.mainInventory[slotIndex];
            if (slot != null) {
                if (slot.animationsToGo > 0) {
                    slot.animationsToGo--;
                }

                if (slot.textTime > 0.0F) {
                    slot.textTime--;
                }

                if (slot.popupTime > 0.0F) {
                    slot.popupTime--;
                }

                slot.updateItem(this.player.world, this.player, slotIndex, this.currentSlot == slotIndex);
                if (slot.itemID == BlockList.battery.id && slot.itemDmg >= 192) {
                    this.player.triggerAchievement(AchievementList.charge);
                }

                if (slot.itemID == ItemList.coal.id && slot.itemDmg == 1) {
                    this.player.triggerAchievement(AchievementList.coal);
                }

                slot.getItem().triggerAchievement(this.player);
            }
        }

        if (this.offhandSlot[0] != null) {
            if (this.offhandSlot[0].animationsToGo > 0) {
                this.offhandSlot[0].animationsToGo--;
            }

            this.offhandSlot[0].updateItem(this.player.world, this.player, 0, this.offhandSlot[0] != null);
        }
    }

    public boolean consumeInventoryItem(int id) {
        int slot = this.getSlotWithItem(id);
        if (slot < 0) {
            return false;
        }

        if (slot == 44) {
            if (--this.offhandSlot[0].stackSize <= 0) {
                this.offhandSlot[0] = null;
            }
        } else if (--this.mainInventory[slot].stackSize <= 0) {
            this.mainInventory[slot] = null;
        }

        return true;
    }

    public boolean consumeArrow(boolean bolt) {
        for (int slotIndex = 0; slotIndex < this.mainInventory.length; slotIndex++) {
            if (this.mainInventory[slotIndex] != null
                && this.mainInventory[slotIndex].getItem() instanceof ArrowItem
                && ((ArrowItem)this.mainInventory[slotIndex].getItem()).bolt == bolt) {
                if (--this.mainInventory[slotIndex].stackSize <= 0) {
                    this.mainInventory[slotIndex] = null;
                }

                return true;
            }
        }

        return false;
    }

    public int getArrow(boolean bolt) {
        for (int slotIndex = 0; slotIndex < this.mainInventory.length; slotIndex++) {
            if (this.mainInventory[slotIndex] != null
                && this.mainInventory[slotIndex].getItem() instanceof ArrowItem
                && ((ArrowItem)this.mainInventory[slotIndex].getItem()).bolt == bolt) {
                return this.mainInventory[slotIndex].itemID;
            }
        }

        return -1;
    }

    public boolean hasSetEquipped(ArmorMaterial material) {
        int equips = 0;

        for (int i = 0; i < this.armorSlots.length; i++) {
            if (this.armorSlots[i] != null && ((Armor)this.armorSlots[i].getItem()).tier == material) {
                equips++;
            }
        }

        return equips >= this.armorSlots.length;
    }

    private int addToSlotArray(ItemStack stack, ItemStack[] slots, int maxStackLimit) {
        int remaining = stack.stackSize;
        int itemID = stack.itemID;
        int dmg = stack.itemDmg;

        for (int i = 0; i < slots.length; i++) {
            ItemStack slot = slots[i];
            if (slot != null
                && slot.itemID == itemID
                && (!slot.hasSubtypes() || slot.getItemDamage() == dmg)
                && ItemStack.areItemStackTagsEqual(slot, stack)
                && !slot.hasTag()
                && slot.stackSize < Math.min(slot.getItem().getStackLimit(), maxStackLimit)) {
                int canAdd = Math.min(remaining, Math.min(slot.getItem().getStackLimit(), maxStackLimit) - slot.stackSize);
                if (canAdd > 0) {
                    slot.stackSize += canAdd;
                    slot.animationsToGo = 5;
                    slot.popupTime = 75.0F;
                    remaining -= canAdd;
                    if (remaining <= 0) {
                        return 0;
                    }
                }
            }
        }

        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) {
                ItemStack newStack = stack.copy();
                newStack.stackSize = remaining;
                newStack.animationsToGo = 5;
                newStack.popupTime = 75.0F;
                slots[i] = newStack;
                return 0;
            }
        }

        return remaining;
    }

    public boolean addItemToOffhand(ItemStack stack) {
        int remaining = this.addToSlotArray(stack, this.offhandSlot, this.getStackLimit());
        stack.stackSize = remaining;
        return remaining == 0;
    }

    public boolean addItemToInventory(ItemStack stack) {
        if (this.offhandSlot[0] != null
            && this.offhandSlot[0].itemID == stack.itemID
            && this.offhandSlot[0].itemDmg == stack.itemDmg
            && this.offhandSlot[0].stackSize < this.offhandSlot[0].getItem().getStackLimit()) {
            return this.addItemToOffhand(stack);
        }

        int oldStack = stack.stackSize;
        int remaining = this.addToSlotArray(stack, this.mainInventory, this.getStackLimit());
        if (remaining < oldStack) {
            for (ItemStack slot : this.mainInventory) {
                if (slot != null && slot.itemID == stack.itemID && slot == this.mainInventory[this.currentSlot] && slot.stackSize == 1) {
                    slot.textTime = 30.0F;
                }
            }
        }

        stack.stackSize = remaining;
        return remaining == 0;
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int amount) {
        ItemStack[] stacks = this.mainInventory;
        if (slotIndex >= this.mainInventory.length) {
            stacks = this.armorSlots;
            slotIndex -= this.mainInventory.length;
            if (slotIndex >= this.armorSlots.length) {
                stacks = this.accessorySlots;
                slotIndex -= this.armorSlots.length;
                if (slotIndex >= this.accessorySlots.length) {
                    stacks = this.offhandSlot;
                    slotIndex -= this.accessorySlots.length;
                }
            }
        }

        if (stacks[slotIndex] != null) {
            if (stacks[slotIndex].stackSize <= amount) {
                ItemStack newStack = stacks[slotIndex];
                stacks[slotIndex] = null;
                return newStack;
            }

            ItemStack newStack = stacks[slotIndex].splitStack(amount);
            if (stacks[slotIndex].stackSize == 0) {
                stacks[slotIndex] = null;
            }

            return newStack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slotIndex, ItemStack stack) {
        ItemStack[] stacks = this.mainInventory;
        if (slotIndex >= this.mainInventory.length) {
            stacks = this.armorSlots;
            slotIndex -= this.mainInventory.length;
            if (slotIndex >= this.armorSlots.length) {
                stacks = this.accessorySlots;
                slotIndex -= this.armorSlots.length;
                if (slotIndex >= this.accessorySlots.length) {
                    stacks = this.offhandSlot;
                    slotIndex -= this.accessorySlots.length;
                }
            }
        }

        stacks[slotIndex] = stack;
    }

    public ListTag<CompoundTag> saveContents(ListTag<CompoundTag> tagList) {
        for (int slot = 0; slot < this.mainInventory.length; slot++) {
            if (this.mainInventory[slot] != null) {
                CompoundTag tag = new CompoundTag();
                tag.setByte("Slot", (byte)slot);
                this.mainInventory[slot].writeToNBT(tag);
                tagList.add(tag);
            }
        }

        for (int var4 = 0; var4 < this.armorSlots.length; var4++) {
            if (this.armorSlots[var4] != null) {
                CompoundTag tag = new CompoundTag();
                tag.setByte("Slot", (byte)(var4 + 100));
                this.armorSlots[var4].writeToNBT(tag);
                tagList.add(tag);
            }
        }

        for (int var5 = 0; var5 < this.accessorySlots.length; var5++) {
            if (this.accessorySlots[var5] != null) {
                CompoundTag tag = new CompoundTag();
                tag.setByte("Slot", (byte)(var5 + 200));
                this.accessorySlots[var5].writeToNBT(tag);
                tagList.add(tag);
            }
        }

        if (this.offhandSlot[0] != null) {
            CompoundTag tag = new CompoundTag();
            tag.setByte("Slot", (byte)-51);
            this.offhandSlot[0].writeToNBT(tag);
            tagList.add(tag);
        }

        return tagList;
    }

    public void loadContents(ListTag<? extends Tag> tagList) {
        this.mainInventory = new ItemStack[36];
        this.armorSlots = new ItemStack[4];
        this.accessorySlots = new ItemStack[4];
        this.offhandSlot = new ItemStack[1];

        for (int index = 0; index < tagList.size(); index++) {
            CompoundTag tag = (CompoundTag)tagList.get(index);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.mainInventory.length) {
                this.mainInventory[slot] = new ItemStack(tag);
            }

            if (slot >= 100 && slot < this.armorSlots.length + 100) {
                this.armorSlots[slot - 100] = new ItemStack(tag);
            }

            if (slot >= 200 && slot < this.accessorySlots.length + 200) {
                this.accessorySlots[slot - 200] = new ItemStack(tag);
            }

            if (slot == 205) {
                this.offhandSlot[0] = new ItemStack(tag);
            }
        }
    }

    public void clearContents(boolean dropItems) {
        for (int slot = 0; slot < this.mainInventory.length; slot++) {
            if (this.mainInventory[slot] != null) {
                if (dropItems) {
                    this.player.dropPlayerItem(this.mainInventory[slot], true);
                }

                this.mainInventory[slot] = null;
            }
        }

        for (int var3 = 0; var3 < this.armorSlots.length; var3++) {
            if (this.armorSlots[var3] != null) {
                if (dropItems) {
                    this.player.dropPlayerItem(this.armorSlots[var3], true);
                }

                this.armorSlots[var3] = null;
            }
        }

        for (int var4 = 0; var4 < this.accessorySlots.length; var4++) {
            if (this.accessorySlots[var4] != null) {
                if (dropItems) {
                    this.player.dropPlayerItem(this.accessorySlots[var4], true);
                }

                this.accessorySlots[var4] = null;
            }
        }

        if (this.offhandSlot[0] != null) {
            if (dropItems) {
                this.player.dropPlayerItem(this.offhandSlot[0], true);
            }

            this.offhandSlot[0] = null;
        }
    }

    @Override
    public int getSize() {
        return this.mainInventory.length + 8;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        ItemStack[] stacks = this.mainInventory;
        if (index >= this.mainInventory.length) {
            stacks = this.armorSlots;
            index -= this.mainInventory.length;
            if (index >= this.armorSlots.length) {
                stacks = this.accessorySlots;
                index -= this.armorSlots.length;
                if (index >= this.accessorySlots.length) {
                    stacks = this.offhandSlot;
                    index -= this.accessorySlots.length;
                }
            }
        }

        return stacks[index];
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.inventory");
    }

    @Override
    public int getStackLimit() {
        return 64;
    }

    @Override
    public void onChanged() {
    }

    @Override
    public boolean isInteractible(Player player) {
        if (this.player.isDead) {
            return false;
        } else {
            return player.op ? true : player.getDistanceSqTo(this.player) <= 64.0;
        }
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
