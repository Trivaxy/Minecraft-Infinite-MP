package net.minecraft.game.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.container.slot.TrashSlot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;

public abstract class Container {
    public List<ItemStack> craftResult = new ArrayList<>();
    public List<Slot> slots = new ArrayList<>();
    public List<ContainerListener> listeners = new ArrayList<>();
    private Set<Player> players = new HashSet<>();
    public Slot lastClickedSlot;
    public Slot clickedSlot;
    public ItemStack movingStack;
    public int windowId = 0;
    public long currentTime;
    private short transactionID = 0;

    protected void addSlot(Slot slot) {
        slot.slotNumber = this.slots.size();
        this.slots.add(slot);
        this.craftResult.add(null);
    }

    public void addListener(ContainerListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already listening");
        }

        this.listeners.add(listener);
        listener.updateCraftingInventory(this, this.getStacks());
        this.updateCraftResults();
    }

    public void removeListener(ContainerListener istener) {
        this.listeners.remove(istener);
    }

    public void onClose(Player player) {
        PlayerInventory inventory = player.inventory;
        if (inventory.getItemStack() != null) {
            if (player.dropItems) {
                player.dropPlayerItem(inventory.getItemStack());
            }

            inventory.setItemStack(null);
        }
    }

    public List<ItemStack> getStacks() {
        ArrayList<ItemStack> stacks = new ArrayList<>();

        for (int index = 0; index < this.slots.size(); index++) {
            stacks.add(this.slots.get(index).getStack());
        }

        return stacks;
    }

    public void updateCraftResults() {
        for (int index = 0; index < this.slots.size(); index++) {
            ItemStack invItem = this.slots.get(index).getStack();
            ItemStack result = this.craftResult.get(index);
            if (!ItemStack.areItemStacksEqual(result, invItem)) {
                result = invItem == null ? null : invItem.copy();
                this.craftResult.set(index, result);

                for (int listener = 0; listener < this.listeners.size(); listener++) {
                    this.listeners.get(listener).setStack(this, index, result);
                }
            }
        }
    }

    public Slot getSlotInInventory(Inventory inventory, int id) {
        for (int index = 0; index < this.slots.size(); index++) {
            Slot slot = this.slots.get(index);
            if (slot.isHere(inventory, id)) {
                return slot;
            }
        }

        return null;
    }

    public Slot getSlot(int index) {
        return this.slots.get(index);
    }

    public ItemStack getStackInSlot(int index) {
        Slot slot = this.slots.get(index);
        return slot != null ? slot.getStack() : null;
    }

    public ItemStack windowClick(int slotIndex, int click, boolean shift, Player player) {
        ItemStack activeStack = null;
        if (click == 0 || click == 1) {
            PlayerInventory inventory = player.inventory;
            if (slotIndex == -999) {
                if (inventory.getItemStack() != null && slotIndex == -999) {
                    if (click == 0) {
                        player.dropPlayerItem(inventory.getItemStack());
                        inventory.setItemStack(null);
                    }

                    if (click == 1) {
                        player.dropPlayerItem(inventory.getItemStack().splitStack(1));
                        if (inventory.getItemStack().stackSize <= 0) {
                            inventory.setItemStack(null);
                        }
                    }
                }
            } else {
                Slot slot = this.slots.get(slotIndex);
                if (shift && click == 0) {
                    ItemStack stack = this.getStackInSlot(slotIndex);
                    if (stack != null) {
                        int id = stack.itemID;
                        int dmg = stack.itemDmg;
                        activeStack = stack.copy();
                        this.movingStack = activeStack;
                        this.clickedSlot = slot;
                        this.currentTime = System.currentTimeMillis();
                        if (slot != null && slot.getStack() != null && slot.getStack().itemID == id && slot.getStack().itemDmg == dmg) {
                            this.windowClick(slotIndex, click, shift, player);
                        }
                    }
                } else if (slot != null && slot.canTakeStack(player)) {
                    slot.onChanged();
                    ItemStack slotStack = slot.getStack();
                    ItemStack heldStack = inventory.getItemStack();
                    if (slotStack != null) {
                        activeStack = slotStack.copy();
                    }

                    if (slotStack == null) {
                        if (heldStack != null && slot.validItem(heldStack)) {
                            int stackSize = click == 0 ? heldStack.stackSize : 1;
                            if (stackSize > slot.stackLimit()) {
                                stackSize = slot.stackLimit();
                            }

                            slot.putStack(heldStack.splitStack(stackSize));
                            if (heldStack.stackSize <= 0) {
                                inventory.setItemStack(null);
                            }
                        }
                    } else if (heldStack == null) {
                        int stackSize = click == 0 ? slotStack.stackSize : (slotStack.stackSize + 1) / 2;
                        ItemStack newStack = slot.decrStackSize(stackSize);
                        inventory.setItemStack(newStack);
                        if (slotStack.stackSize <= 0) {
                            slot.putStack(null);
                        }

                        slot.onPickup(inventory.getItemStack());
                    } else if (slot.validItem(heldStack)) {
                        if (slotStack.itemID == heldStack.itemID
                            && slotStack.getItemDamage() == heldStack.getItemDamage()
                            && ItemStack.areItemStackTagsEqual(slotStack, heldStack)
                            && !slotStack.hasTag()) {
                            int stackSize = click == 0 ? heldStack.stackSize : 1;
                            if (stackSize > slot.stackLimit() - slotStack.stackSize) {
                                stackSize = slot.stackLimit() - slotStack.stackSize;
                            }

                            if (stackSize > heldStack.getStackLimit() - slotStack.stackSize) {
                                stackSize = heldStack.getStackLimit() - slotStack.stackSize;
                            }

                            heldStack.splitStack(stackSize);
                            if (heldStack.stackSize <= 0) {
                                inventory.setItemStack(null);
                            }

                            slotStack.stackSize += stackSize;
                            slot.onChanged();
                        } else if (heldStack.stackSize <= slot.stackLimit()) {
                            slot.putStack(heldStack);
                            if (slot instanceof TrashSlot && inventory.getItemStack() != null) {
                                inventory.setItemStack(null);
                            } else {
                                inventory.setItemStack(slotStack);
                            }
                        }
                    } else if (slotStack.itemID == heldStack.itemID
                        && heldStack.getStackLimit() > 1
                        && slotStack.getItemDamage() == heldStack.getItemDamage()
                        && ItemStack.areItemStackTagsEqual(slotStack, heldStack)
                        && !slotStack.hasTag()) {
                        int stackSize = slotStack.stackSize;
                        if (stackSize > 0 && stackSize + heldStack.stackSize <= heldStack.getStackLimit()) {
                            heldStack.stackSize += stackSize;
                            slotStack.splitStack(stackSize);
                            if (slotStack.stackSize <= 0) {
                                slot.putStack(null);
                            }

                            slot.onPickup(inventory.getItemStack());
                        }
                    }
                }
            }
        }

        return activeStack;
    }

    protected boolean isSlotFree(ItemStack stack, int startId, int endId, boolean reverse) {
        boolean isFree = false;
        int currId = startId;
        if (reverse) {
            currId = endId - 1;
        }

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverse && currId < endId || reverse && currId >= startId)) {
                Slot slot = this.slots.get(currId);
                ItemStack newStack = slot.getStack();
                if (newStack != null
                    && newStack.itemID == stack.itemID
                    && stack.itemDmg == newStack.itemDmg
                    && ItemStack.areItemStackTagsEqual(stack, newStack)
                    && !stack.hasTag()) {
                    int newStackSize = newStack.stackSize + stack.stackSize;
                    if (newStackSize <= stack.getStackLimit()) {
                        stack.stackSize = 0;
                        newStack.stackSize = newStackSize;
                        slot.onChanged();
                        this.lastClickedSlot = slot;
                        isFree = true;
                    } else if (newStack.stackSize < stack.getStackLimit()) {
                        stack.stackSize = stack.stackSize - (stack.getStackLimit() - newStack.stackSize);
                        newStack.stackSize = stack.getStackLimit();
                        slot.onChanged();
                        this.lastClickedSlot = slot;
                        isFree = true;
                    }
                }

                if (reverse) {
                    currId--;
                } else {
                    currId++;
                }
            }
        }

        if (stack.stackSize > 0) {
            if (reverse) {
                currId = endId - 1;
            } else {
                currId = startId;
            }

            while (!reverse && currId < endId || reverse && currId >= startId) {
                Slot slot = this.slots.get(currId);
                ItemStack newStack = slot.getStack();
                if (newStack == null) {
                    slot.putStack(stack.copy());
                    slot.onChanged();
                    this.lastClickedSlot = slot;
                    stack.stackSize = 0;
                    isFree = true;
                    break;
                }

                if (reverse) {
                    currId--;
                } else {
                    currId++;
                }
            }
        }

        return isFree;
    }

    public void updateInventory(Inventory inventory) {
        this.updateCraftResults();
    }

    public abstract boolean canUse(Player var1);

    public void putStackInSlot(int index, ItemStack stack) {
        this.getSlot(index).putStack(stack);
    }

    public void putStacksInSlots(ItemStack[] stacks) {
        for (int index = 0; index < stacks.length; index++) {
            this.getSlot(index).putStack(stacks[index]);
        }
    }

    public void setData(int packetId, int packetValue) {
    }

    public short updateTransaction(PlayerInventory inventory) {
        this.transactionID++;
        return this.transactionID;
    }

    public void deleteBackup(short transaction) {
    }

    public void rollbackChanges(short transaction) {
    }

    public boolean canUseMP(Player player) {
        return !this.players.contains(player);
    }

    public void updatePlayers(Player player, boolean removing) {
        if (removing) {
            this.players.remove(player);
        } else {
            this.players.add(player);
        }
    }
}
