package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public class GeneratorContainer extends Container {
    private GeneratorEntity generatorBlock;

    public GeneratorContainer(PlayerInventory playerInv, GeneratorEntity generator) {
        this.generatorBlock = generator;
        this.addSlot(new Slot(generator, 0, 80, 35));

        for (int columns = 0; columns < 3; columns++) {
            for (int rows = 0; rows < 9; rows++) {
                this.addSlot(new Slot(playerInv, rows + columns * 9 + 9, 8 + rows * 18, 84 + columns * 18));
            }
        }

        for (int var5 = 0; var5 < 9; var5++) {
            this.addSlot(new Slot(playerInv, var5, 8 + var5 * 18, 142));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.generatorBlock.isInteractible(player);
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex != 0) {
                if (GeneratorEntity.isFuel(slotStack)) {
                    if (!this.isSlotFree(slotStack, 0, 1, false)) {
                        return null;
                    }
                } else if (slotIndex >= 1 && slotIndex < 30) {
                    if (!this.isSlotFree(slotStack, 30, 37, false)) {
                        return null;
                    }
                } else if (slotIndex >= 30 && slotIndex < 37 && !this.isSlotFree(slotStack, 1, 30, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 1, 37, false)) {
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
