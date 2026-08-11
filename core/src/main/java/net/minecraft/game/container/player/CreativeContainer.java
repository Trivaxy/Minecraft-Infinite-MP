package net.minecraft.game.container.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;

public class CreativeContainer extends Container {
    public List<ItemStack> itemList = new ArrayList<>();
    private BasicInventory inventory;
    public int selectedTab = CreativeTabs.tabNature.getTabIndex();

    public CreativeContainer(Player player, BasicInventory inventory) {
        this.inventory = inventory;

        for (int column = 0; column < 5; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(inventory, column * 9 + row, 9 + row * 18, 18 + column * 18));
            }
        }

        for (int var5 = 0; var5 < 9; var5++) {
            this.addSlot(new Slot(player.inventory, var5, 9 + var5 * 18, 112));
        }

        this.scrollTo(0.0F);
    }

    public ItemStack clickSlot(int slotIndex, int click, boolean shift, Player player) {
        ItemStack stack = null;
        if (click == 0 || click == 1) {
            PlayerInventory playerInv = player.inventory;
            if (slotIndex == -999) {
                if (playerInv.getItemStack() != null) {
                    if (click == 0) {
                        player.dropPlayerItem(playerInv.getItemStack());
                        playerInv.setItemStack(null);
                    }

                    if (click == 1) {
                        player.dropPlayerItem(playerInv.getItemStack().splitStack(1));
                        if (playerInv.getItemStack().stackSize <= 0) {
                            playerInv.setItemStack(null);
                        }
                    }
                }
            } else {
                Slot slot = this.slots.get(slotIndex);
                if (slot != null) {
                    if (shift) {
                        if (slotIndex < 45) {
                            playerInv.setItemStack(ItemStack.copyStack(this.getStackInSlot(slotIndex)));
                            if (playerInv.getItemStack() != null) {
                                playerInv.getItemStack().stackSize = playerInv.getItemStack().getStackLimit();
                            }
                        } else {
                            this.putStackInSlot(slotIndex, null);
                        }
                    } else {
                        slot.onChanged();
                        ItemStack slotStack = slot.getStack();
                        ItemStack hotbarStack = playerInv.getItemStack();
                        if (slotStack != null) {
                            stack = slotStack.copy();
                        }

                        if (slotStack == null) {
                            playerInv.setItemStack(null);
                        } else if (hotbarStack == null) {
                            playerInv.setItemStack(ItemStack.copyStack(this.getStackInSlot(slotIndex)));
                            slot.onPickup(playerInv.getItemStack());
                        } else if (slotStack.itemID == hotbarStack.itemID
                            && hotbarStack.stackSize < hotbarStack.getStackLimit()
                            && slotStack.getItemDamage() == hotbarStack.getItemDamage()) {
                            hotbarStack.stackSize++;
                        } else if (hotbarStack.stackSize <= slot.stackLimit()) {
                            playerInv.setItemStack(null);
                        }
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack windowClick(int slotIndex, int click, boolean shift, Player player) {
        if (this.selectedTab == CreativeTabs.tabInventory.getTabIndex()) {
            this.movingStack = player.container.movingStack;
            this.clickedSlot = player.container.clickedSlot;
            this.lastClickedSlot = player.container.lastClickedSlot;
            this.currentTime = player.container.currentTime;
            return player.container.windowClick(slotIndex, click, shift, player);
        }

        if (slotIndex != -999) {
            if (slotIndex >= 45) {
                return !shift ? super.windowClick(slotIndex, click, shift, player) : this.clickSlot(slotIndex, click, shift, player);
            } else {
                return this.clickSlot(slotIndex, click, shift, player);
            }
        } else {
            return this.clickSlot(slotIndex, click, shift, player);
        }
    }

    public void scrollTo(float scrollVal) {
        int margin = this.itemList.size() / 9 - 5 + 1;
        int amountScrolled = (int)(scrollVal * margin + 0.5);
        if (amountScrolled < 0) {
            amountScrolled = 0;
        }

        for (int row = 0; row < 5; row++) {
            for (int column = 0; column < 9; column++) {
                int scrollIndex = column + (row + amountScrolled) * 9;
                if (scrollIndex >= 0 && scrollIndex < this.itemList.size()) {
                    this.inventory.setSlotContents(column + row * 9, this.itemList.get(scrollIndex));
                } else {
                    this.inventory.setSlotContents(column + row * 9, null);
                }
            }
        }
    }

    public boolean scrollable() {
        return this.itemList.size() > 45;
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
