package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class StonecutterContainer extends Container {
    public Inventory outputs = new BasicInventory("Stonecutter", 18);
    public Inventory inputSlot = new BasicInventory("Input", 1);
    private World world;
    private int xPos;
    private int yPos;
    private int zPos;

    public StonecutterContainer(PlayerInventory playerInv, World world, int x, int y, int z) {
        this.world = world;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.addSlot(new StonecutterContainer.InputSlot(this.inputSlot, 0, 19, 35));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 6; row++) {
                this.addSlot(new StonecutterContainer.StonecutterSlot(playerInv.player, this.outputs, row + column * 6, 52 + row * 18, 17 + column * 18));
            }
        }

        for (int var8 = 0; var8 < 3; var8++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + var8 * 9 + 9, 8 + row * 18, 100 + var8 * 18));
            }
        }

        for (int var9 = 0; var9 < 9; var9++) {
            this.addSlot(new Slot(playerInv, var9, 8 + var9 * 18, 158));
        }
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (!this.world.isOnline) {
            ItemStack inputSlot = this.inputSlot.getStackInSlot(0);
            if (inputSlot != null && !player.inventory.addItemToInventory(inputSlot)) {
                player.dropPlayerItem(inputSlot);
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.world.getBlockId(this.xPos, this.yPos, this.zPos) != BlockList.stonecutter.id
            ? false
            : player.getDistanceSq(this.xPos + 0.5, this.yPos + 0.5, this.zPos + 0.5) <= 64.0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack() && slot.getStack().itemID < 4096) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex >= 0 && slotIndex < 19) {
                if (!this.isSlotFree(slotStack, 19, 55, false)) {
                    return null;
                }
            } else if (slotIndex >= 19 && slotIndex < 55 && !this.isSlotFree(slotStack, 0, 1, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack((ItemStack)null);
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

    private void updateSlots() {
        ItemStack inputStack = this.inputSlot.getStackInSlot(0);
        if (inputStack != null) {
            Block block = BlockList.blocks[inputStack.itemID];
            if (block != null) {
                for (int slot = 0; slot < this.outputs.getSize(); slot++) {
                    this.outputs.setSlotContents(slot, block.getCarverContents(slot, this.inputSlot.getStackInSlot(0).getItemDamage()));
                }
            }
        } else {
            for (int slot = 0; slot < this.outputs.getSize(); slot++) {
                this.outputs.setSlotContents(slot, null);
            }
        }
    }

    class InputSlot extends Slot {
        public InputSlot(Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack != null && stack.itemID < 4096;
        }

        @Override
        public void putStack(ItemStack stack) {
            super.putStack(stack);
            StonecutterContainer.this.updateSlots();
        }

        @Override
        public void onPickup(ItemStack stack) {
            this.onChanged();
            StonecutterContainer.this.updateSlots();
        }
    }

    class StonecutterSlot extends Slot {
        private Player player;
        private int amountCrafted;

        public StonecutterSlot(Player player, Inventory inv, int index, int x, int y) {
            super(inv, index, x, y);
            this.player = player;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return false;
        }

        @Override
        public void onPickup(ItemStack stack) {
            this.onChanged();
            stack.onCrafting(this.player.world, this.player, this.amountCrafted);
            this.player.addStat(StatList.craftStats[stack.itemID], 1);
            ItemStack slotStack = StonecutterContainer.this.inputSlot.getStackInSlot(0);
            if (slotStack != null) {
                StonecutterContainer.this.inputSlot.decrStackSize(0, 1);
                StonecutterContainer.this.world
                    .playBlockSound(
                        StonecutterContainer.this.xPos, StonecutterContainer.this.yPos, StonecutterContainer.this.zPos, "random.stonecutter", 1.0F, 1.0F
                    );
            }

            StonecutterContainer.this.updateSlots();
        }
    }
}
