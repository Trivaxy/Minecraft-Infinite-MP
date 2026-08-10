package net.minecraft.game.container.player;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.container.slot.CraftingSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.container.slot.TrashSlot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.item.crafting.CraftingManager;
import net.minecraft.game.item.misc.ArrowItem;
import net.minecraft.game.item.misc.Cape;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.Charm;
import net.minecraft.game.texture.Icon;
import net.minecraft.statistics.achievement.AchievementList;

public class PlayerContainer extends Container {
    public CraftingInventory craftMatrix = new CraftingInventory(this, 2, 2);
    public CraftingInventory backpackContents;
    public CraftingInventory quiverContents;
    public Inventory craftResult = new WorkbenchInventory();
    public Inventory deleteSlot = new WorkbenchInventory();
    public final Player thePlayer;

    public PlayerContainer(PlayerInventory inventoryPlayer, Player player) {
        this.thePlayer = player;
        this.addSlot(new CraftingSlot(inventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 153, 30));

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                this.addSlot(new Slot(this.craftMatrix, column + row * 2, 101 + column * 18, 20 + row * 18));
            }
        }

        for (int var5 = 0; var5 < 4; var5++) {
            this.addSlot(new PlayerContainer.ArmorSlot(inventoryPlayer, inventoryPlayer.getSize() - 5 - var5, 8, 8 + var5 * 18, var5));
        }

        for (int var6 = 0; var6 < 4; var6++) {
            this.addSlot(new PlayerContainer.AccessorySlot(inventoryPlayer, inventoryPlayer.getSize() - 1 - var6, 80, 8 + var6 * 18, var6));
        }

        for (int var7 = 0; var7 < 3; var7++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventoryPlayer, column + (var7 + 1) * 9, 8 + column * 18, 84 + var7 * 18));
            }
        }

        for (int var8 = 0; var8 < 9; var8++) {
            this.addSlot(new Slot(inventoryPlayer, var8, 8 + var8 * 18, 142));
        }

        this.addSlot(new PlayerContainer.OffhandSlot(inventoryPlayer, inventoryPlayer.getSize(), 101, 62));
        this.addSlot(new TrashSlot(this.deleteSlot, 0, 126, 62));
        this.backpackContents = new CraftingInventory(this, 3, 7);

        for (int var9 = 0; var9 < 6; var9++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new PlayerContainer.BackpackSlot(this.backpackContents, column + (var9 + 1) * 3, 191 + column * 18, 34 + var9 * 18));
            }
        }

        this.quiverContents = new CraftingInventory(this, 1, 3);

        for (int column = 0; column < 3; column++) {
            this.addSlot(new PlayerContainer.ArrowSlot(this.quiverContents, column, -15, 16 + column * 18));
        }

        this.updateInventory(this.craftMatrix);
    }

    public void saveContents(ItemStack stack) {
        ListTag<CompoundTag> tagList = new ListTag<>();
        CompoundTag tag = new CompoundTag();
        int color = -1;
        Charm charm = null;
        if (stack.getItem() instanceof Charm) {
            charm = (Charm)stack.getItem();
            color = charm.getColor(stack);
        }

        for (int i = 0; i < this.backpackContents.getSize(); i++) {
            if (this.backpackContents.getStackInSlot(i) != null) {
                CompoundTag theItem = new CompoundTag();
                theItem.setByte("Slot", (byte)i);
                this.backpackContents.getStackInSlot(i).writeToNBT(theItem);
                tagList.add(theItem);
            }
        }

        for (int i = 0; i < this.quiverContents.getSize(); i++) {
            if (this.quiverContents.getStackInSlot(i) != null) {
                CompoundTag theItem = new CompoundTag();
                theItem.setByte("Slot", (byte)(i + 21));
                this.quiverContents.getStackInSlot(i).writeToNBT(theItem);
                tagList.add(theItem);
            }
        }

        tag.setTag("Items", tagList);
        stack.setTag(tag);
        if (charm != null) {
            charm.addColor(stack, color);
        }
    }

    public void loadContents(ItemStack stack) {
        ListTag<? extends Tag> tagList = stack.getTag().getTagList("Items");

        for (int index = 0; index < tagList.size(); index++) {
            CompoundTag tag = (CompoundTag)tagList.get(index);
            int slotIndex = tag.getByte("Slot") & 255;
            if (slotIndex >= 21) {
                this.quiverContents.setSlotContents(slotIndex - 21, new ItemStack(tag));
            } else {
                this.backpackContents.setSlotContents(slotIndex, new ItemStack(tag));
            }
        }
    }

    public int hasQuiverItem(int itemID) {
        for (int slot = 0; slot < this.quiverContents.getSize(); slot++) {
            if (this.quiverContents.getStackInSlot(slot) != null && this.quiverContents.getStackInSlot(slot).itemID == itemID) {
                return slot;
            }
        }

        return -1;
    }

    public boolean consumeQuiverItem(int slot) {
        if (slot >= 0 && this.quiverContents.getStackInSlot(slot) != null) {
            if (--this.quiverContents.getStackInSlot(slot).stackSize <= 0) {
                this.quiverContents.setSlotContents(slot, null);
            }

            return true;
        } else {
            return false;
        }
    }

    public int getQuiverSlot(int slot) {
        return this.quiverContents.getStackInSlot(slot).itemID;
    }

    @Override
    public void updateInventory(Inventory inventory) {
        this.craftResult.setSlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.world));
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);

        for (int slot = 0; slot < 4; slot++) {
            ItemStack stack = this.craftMatrix.getStackInSlot(slot);
            if (stack != null) {
                if (!player.inventory.addItemToInventory(stack)) {
                    player.dropPlayerItem(stack);
                }

                this.craftMatrix.setSlotContents(slot, (ItemStack)null);
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (this.thePlayer.inventory.accessorySlots[2] != null
                && (
                    this.thePlayer.inventory.accessorySlots[2].getItem() == ItemList.backpack
                        || this.thePlayer.inventory.accessorySlots[2].getItem() == ItemList.quiver
                )) {
                ((PlayerContainer)this.thePlayer.container).saveContents(this.thePlayer.inventory.accessorySlots[2]);
            }

            if (slotIndex == 0) {
                if (!this.isSlotFree(slotStack, 13, 49, true)) {
                    return null;
                }
            } else if (stack.getItem() instanceof Armor && !this.slots.get(5 + ((Armor)stack.getItem()).type).hasStack()) {
                int var7 = 5 + ((Armor)stack.getItem()).type;
                if (!this.isSlotFree(slotStack, var7, var7 + 1, false)) {
                    return null;
                }
            } else if (stack.getItem() instanceof Charm && !this.slots.get(9 + ((Charm)stack.getItem()).type).hasStack()) {
                int var6 = 9 + ((Charm)stack.getItem()).type;
                if (!this.isSlotFree(slotStack, var6, var6 + 1, false)) {
                    return null;
                }
            } else if (stack.getItem() instanceof Cape && !this.slots.get(10).hasStack()) {
                int offsetIndex = 10;
                if (!this.isSlotFree(slotStack, offsetIndex, offsetIndex + 1, false)) {
                    return null;
                }
            } else if (slotIndex >= 13 && slotIndex < 40) {
                if (!this.isSlotFree(slotStack, 40, 49, false)) {
                    return null;
                }
            } else if (slotIndex >= 39 && slotIndex < 49) {
                if (!this.isSlotFree(slotStack, 13, 40, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 13, 49, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }

            if (slotStack.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickup(slotStack);
        }

        return stack;
    }

    class AccessorySlot extends Slot {
        final int accessoryType;

        public AccessorySlot(Inventory inv, int index, int x, int y, int type) {
            super(inv, index, x, y);
            this.accessoryType = type;
        }

        @Override
        public void putStack(ItemStack stack) {
            this.inventory.setSlotContents(this.slotIndex, stack);
            this.onChanged();
            if (this.slotNumber == 10) {
                for (int i = 0; i < PlayerContainer.this.backpackContents.getSize(); i++) {
                    PlayerContainer.this.backpackContents.setSlotContents(i, null);
                }

                for (int i = 0; i < PlayerContainer.this.quiverContents.getSize(); i++) {
                    PlayerContainer.this.quiverContents.setSlotContents(i, null);
                }

                if (stack != null && (stack.getItem() == ItemList.backpack || stack.getItem() == ItemList.quiver) && stack.hasTag()) {
                    PlayerContainer.this.loadContents(stack);
                }
            }

            if (stack != null && stack.getItem().rarity != Rarity.COMMON) {
                PlayerContainer.this.thePlayer.triggerAchievement(AchievementList.charm);
            }
        }

        @Override
        public void onPickup(ItemStack stack) {
            this.onChanged();
            if (this.slotNumber == 10) {
                if (stack != null && (stack.getItem() == ItemList.backpack || stack.getItem() == ItemList.quiver)) {
                    PlayerContainer.this.saveContents(stack);
                }

                for (int i = 0; i < PlayerContainer.this.backpackContents.getSize(); i++) {
                    PlayerContainer.this.backpackContents.setSlotContents(i, null);
                }

                for (int i = 0; i < PlayerContainer.this.quiverContents.getSize(); i++) {
                    PlayerContainer.this.quiverContents.setSlotContents(i, null);
                }
            }
        }

        @Override
        public int stackLimit() {
            return 1;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            if (this.accessoryType == 1 && stack != null && stack.getItem() instanceof Cape) {
                return true;
            } else {
                return stack == null ? false : (stack.getItem() instanceof Charm ? ((Charm)stack.getItem()).type == this.accessoryType : false);
            }
        }

        @Override
        public Icon backgroundIcon() {
            return Charm.getIcon(this.accessoryType);
        }
    }

    class ArmorSlot extends Slot {
        final int armorType;

        public ArmorSlot(Inventory inv, int index, int x, int y, int type) {
            super(inv, index, x, y);
            this.armorType = type;
        }

        @Override
        public int stackLimit() {
            return 1;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack == null || !(stack.getItem() instanceof Armor) ? false : ((Armor)stack.getItem()).type == this.armorType;
        }

        @Override
        public Icon backgroundIcon() {
            return Armor.getIcon(this.armorType);
        }
    }

    class ArrowSlot extends Slot {
        public ArrowSlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ArrowItem;
        }

        @Override
        public void putStack(ItemStack stack) {
            this.inventory.setSlotContents(this.slotIndex, stack);
            this.onChanged();
            int count = 0;

            for (int i = 0; i < this.inventory.getSize(); i++) {
                if (this.inventory.getStackInSlot(i) != null) {
                    count++;
                }
            }

            if (count >= 3) {
                PlayerContainer.this.thePlayer.triggerAchievement(AchievementList.quiver);
            }
        }

        @Override
        public void onChanged() {
            this.inventory.onChanged();
            if (PlayerContainer.this.thePlayer.inventory.accessorySlots[2] != null) {
                ((PlayerContainer)PlayerContainer.this.thePlayer.container).saveContents(PlayerContainer.this.thePlayer.inventory.accessorySlots[2]);
            }
        }

        @Override
        public Icon backgroundIcon() {
            return ArrowItem.arrowSlot;
        }
    }

    class BackpackSlot extends Slot {
        public BackpackSlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack != null && stack.itemID != ItemList.backpack.id;
        }

        @Override
        public void onChanged() {
            this.inventory.onChanged();
            if (PlayerContainer.this.thePlayer.inventory.accessorySlots[2] != null) {
                ((PlayerContainer)PlayerContainer.this.thePlayer.container).saveContents(PlayerContainer.this.thePlayer.inventory.accessorySlots[2]);
            }
        }
    }

    class OffhandSlot extends Slot {
        public OffhandSlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public Icon backgroundIcon() {
            return ArrowItem.handSlot;
        }
    }
}
