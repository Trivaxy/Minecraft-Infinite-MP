package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.container.slot.CraftingSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.CraftingManager;
import net.minecraft.game.world.World;

public class WorkbenchContainer extends Container {
    public CraftingInventory craftMatrix = new CraftingInventory(this, 3, 3);
    public Inventory craftResult = new WorkbenchInventory();
    private World world;
    private int xPos;
    private int yPos;
    private int zPos;
    private boolean portable;

    public WorkbenchContainer(PlayerInventory playerInv, World world, int x, int y, int z, boolean portable) {
        this.world = world;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.portable = portable;
        this.addSlot(new CraftingSlot(playerInv.player, this.craftMatrix, this.craftResult, 0, 120, 36));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                this.addSlot(new Slot(this.craftMatrix, row + column * 3, 26 + row * 18, 18 + column * 18));
            }
        }

        for (int var9 = 0; var9 < 3; var9++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + var9 * 9 + 9, 8 + row * 18, 101 + var9 * 18));
            }
        }

        for (int var10 = 0; var10 < 9; var10++) {
            this.addSlot(new Slot(playerInv, var10, 8 + var10 * 18, 159));
        }

        this.updateInventory(this.craftMatrix);
    }

    @Override
    public void updateInventory(Inventory inventory) {
        this.craftResult.setSlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (!this.world.isOnline) {
            for (int slot = 0; slot < 9; slot++) {
                ItemStack stack = this.craftMatrix.getStackInSlot(slot);
                if (stack != null && !player.inventory.addItemToInventory(stack)) {
                    player.dropPlayerItem(stack);
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        if (this.portable) {
            return true;
        } else {
            return this.world.getBlockId(this.xPos, this.yPos, this.zPos) != BlockList.workbench.id
                ? false
                : player.getDistanceSq(this.xPos + 0.5, this.yPos + 0.5, this.zPos + 0.5) <= 64.0;
        }
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex == 0) {
                if (!this.isSlotFree(slotStack, 10, 46, true)) {
                    return null;
                }
            } else if (slotIndex >= 10 && slotIndex < 37) {
                if (!this.isSlotFree(slotStack, 37, 46, false)) {
                    return null;
                }
            } else if (slotIndex >= 37 && slotIndex < 46) {
                if (!this.isSlotFree(slotStack, 10, 37, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 10, 46, false)) {
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
}
