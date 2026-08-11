package net.minecraft.game.container.tileentities;

import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.RecipeBook;

public class BookshelfContainer extends Container {
    private Inventory inventory;
    private int size;

    public BookshelfContainer(Inventory playerInv, Inventory bookshelfInv) {
        this.inventory = bookshelfInv;
        this.size = bookshelfInv.getSize() / 9;
        int yOffset = (this.size - 4) * 18;

        for (int column = 0; column < this.size; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new BookshelfContainer.BookSlot(bookshelfInv, row + column * 9, 8 + row * 18, 18 + column * 18));
            }
        }

        for (int var6 = 0; var6 < 3; var6++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + var6 * 9 + 9, 8 + row * 18, 118 + var6 * 18 + yOffset));
            }
        }

        for (int var7 = 0; var7 < 9; var7++) {
            this.addSlot(new Slot(playerInv, var7, 8 + var7 * 18, 176 + yOffset));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.inventory.isInteractible(player);
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < this.size * 9) {
                if (!this.isSlotFree(slotStack, this.size * 9, this.slots.size(), false)) {
                    return null;
                }
            } else {
                if (stack.getItem() != ItemList.book && stack.getItem() != ItemList.bookAndQuill && stack.getItem() != ItemList.writtenBook) {
                    return null;
                }

                if (!this.isSlotFree(slotStack, 0, this.size * 9, false)) {
                    return null;
                }
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }
        }

        return stack;
    }

    class BookSlot extends Slot {
        public BookSlot(Inventory inv, int slotIndex, int xPos, int yPos) {
            super(inv, slotIndex, xPos, yPos);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            if (stack == null) {
                return false;
            }

            Item item = stack.getItem();
            return item == ItemList.book || item == ItemList.bookAndQuill || item == ItemList.writtenBook || item instanceof RecipeBook;
        }
    }
}
