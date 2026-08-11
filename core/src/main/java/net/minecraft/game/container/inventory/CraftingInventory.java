package net.minecraft.game.container.inventory;

import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class CraftingInventory implements Inventory {
    private Container container;
    private CrafterEntity theCrafter;
    private ItemStack[] stackList;
    private int invSize;

    public CraftingInventory(Container container, int rows, int columns) {
        this.stackList = new ItemStack[rows * columns];
        this.container = container;
        this.invSize = rows;
    }

    public CraftingInventory(CrafterEntity crafter, int rows, int columns) {
        this.stackList = new ItemStack[rows * columns];
        this.theCrafter = crafter;
        this.invSize = rows;
    }

    @Override
    public int getSize() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return slotIndex >= this.getSize() ? null : this.stackList[slotIndex];
    }

    public ItemStack getStackInCoords(int x, int y) {
        if (x >= 0 && x < this.invSize) {
            int slot = x + y * this.invSize;
            return this.getStackInSlot(slot);
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.craft");
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int count) {
        if (this.stackList[slotIndex] != null) {
            if (this.stackList[slotIndex].stackSize <= count) {
                ItemStack stack = this.stackList[slotIndex];
                this.stackList[slotIndex] = null;
                if (this.container != null) {
                    this.container.updateInventory(this);
                } else if (this.theCrafter != null) {
                    this.theCrafter.onCraftMatrixChanged(this);
                }

                return stack;
            } else {
                ItemStack stack = this.stackList[slotIndex].splitStack(count);
                if (this.stackList[slotIndex].stackSize == 0) {
                    this.stackList[slotIndex] = null;
                }

                if (this.container != null) {
                    this.container.updateInventory(this);
                } else if (this.theCrafter != null) {
                    this.theCrafter.onCraftMatrixChanged(this);
                }

                return stack;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slotIndex, ItemStack stack) {
        this.stackList[slotIndex] = stack;
        if (this.container != null) {
            this.container.updateInventory(this);
        } else if (this.theCrafter != null) {
            this.theCrafter.onCraftMatrixChanged(this);
        }
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
        return true;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
