package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.ContainerListener;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.statistics.achievement.AchievementList;

public class BeaconContainer extends Container {
    public PlayerInventory playerInv;
    public BeaconEntity theBeacon;
    private final BeaconContainer.BeaconSlot rangeSlot;
    private final BeaconContainer.BeaconEffectSlot effectSlot;
    private int beaconLevel;
    private int primaryEffect;

    public BeaconContainer(PlayerInventory playerInv, BeaconEntity bacon) {
        this.theBeacon = bacon;
        this.playerInv = playerInv;
        this.addSlot(this.rangeSlot = new BeaconContainer.BeaconSlot(bacon, 0, 54, 110));
        this.addSlot(this.effectSlot = new BeaconContainer.BeaconEffectSlot(bacon, 1, 162, 110));
        byte posX = 36;
        short posY = 137;

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + column * 9 + 9, posX + row * 18, posY + column * 18));
            }
        }

        for (int var7 = 0; var7 < 9; var7++) {
            this.addSlot(new Slot(playerInv, var7, posX + var7 * 18, 58 + posY));
        }

        this.beaconLevel = bacon.getRange();
        this.primaryEffect = bacon.getPrimaryEffect();
    }

    @Override
    public void addListener(ContainerListener listener) {
        super.addListener(listener);
        listener.setId(this, 0, this.beaconLevel);
        listener.setId(this, 1, this.primaryEffect);
    }

    @Override
    public void setData(int type, int value) {
        if (type == 0) {
            this.theBeacon.setRange(value);
        }

        if (type == 1) {
            this.theBeacon.setPrimaryEffect(value);
        }
    }

    @Override
    public boolean canUse(Player entityPlayer1) {
        return this.theBeacon.isInteractible(entityPlayer1);
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex == 0) {
                if (!this.isSlotFree(slotStack, 2, 38, false)) {
                    return null;
                }
            } else if (!this.rangeSlot.hasStack() && this.rangeSlot.validItem(slotStack) && slotStack.stackSize == 1) {
                if (!this.isSlotFree(slotStack, 0, 1, false)) {
                    return null;
                }
            } else if (!this.effectSlot.hasStack() && this.effectSlot.validItem(slotStack) && slotStack.stackSize == 1) {
                if (!this.isSlotFree(slotStack, 1, 2, false)) {
                    return null;
                }
            } else if (slotIndex >= 2 && slotIndex < 29) {
                if (!this.isSlotFree(slotStack, 29, 38, false)) {
                    return null;
                }
            } else if (slotIndex >= 29 && slotIndex < 38) {
                if (!this.isSlotFree(slotStack, 2, 29, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 2, 38, false)) {
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

    class BeaconEffectSlot extends Slot {
        public BeaconEffectSlot(Inventory inventory, int slotNumber, int x, int y) {
            super(inventory, slotNumber, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack == null
                ? false
                : stack.getItem().potionPrefix > 0
                    && !EffectList.effects[stack.getItem().potionPrefix].isNegative()
                    && !EffectList.effects[stack.getItem().potionPrefix].isInstant();
        }

        @Override
        public void onPickup(ItemStack stack) {
            super.onPickup(stack);
            BeaconContainer.this.theBeacon.setPrimaryEffect(0);
            BeaconContainer.this.theBeacon
                .world
                .playBlockSound(
                    BeaconContainer.this.theBeacon.x, BeaconContainer.this.theBeacon.y, BeaconContainer.this.theBeacon.z, "random.beaconDeactivate", 1.0F, 1.0F
                );
        }

        @Override
        public void putStack(ItemStack stack) {
            super.putStack(stack);
            if (BeaconContainer.this.theBeacon.effectStack != null) {
                BeaconContainer.this.theBeacon.setPrimaryEffect(BeaconContainer.this.theBeacon.effectStack.getItem().potionPrefix);
                BeaconContainer.this.theBeacon
                    .world
                    .playBlockSound(
                        BeaconContainer.this.theBeacon.x,
                        BeaconContainer.this.theBeacon.y,
                        BeaconContainer.this.theBeacon.z,
                        "random.beaconActivate",
                        1.0F,
                        1.0F
                    );
            }
        }

        @Override
        public int stackLimit() {
            return 1;
        }
    }

    class BeaconSlot extends Slot {
        public BeaconSlot(Inventory inv, int num, int x, int y) {
            super(inv, num, x, y);
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return stack == null ? false : stack.getItem() == ItemList.pearl;
        }

        @Override
        public int stackLimit() {
            return 1;
        }

        @Override
        public void putStack(ItemStack stack) {
            super.putStack(stack);
            if (BeaconContainer.this.theBeacon.range < 10) {
                BeaconContainer.this.theBeacon.range++;
                BeaconContainer.this.theBeacon.payment = null;
                BeaconContainer.this.playerInv.player.triggerAchievement(AchievementList.beacon);
                BeaconContainer.this.theBeacon
                    .world
                    .playBlockSound(
                        BeaconContainer.this.theBeacon.x, BeaconContainer.this.theBeacon.y, BeaconContainer.this.theBeacon.z, "random.beaconPower", 1.0F, 1.0F
                    );
            }
        }
    }
}
