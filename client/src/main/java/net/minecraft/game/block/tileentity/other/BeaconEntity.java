package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.physics.BoundingBox;

public class BeaconEntity extends TileEntity implements Inventory {
    public int range = 0;
    private int primaryEffect;
    public ItemStack payment;
    public ItemStack effectStack;
    private long activeTime;
    private float time;
    private boolean active;

    @Override
    public void tick() {
        if (this.world.getWorldTime() % 80L == 0L) {
            this.updateState();
            this.addEffectsToPlayers();
        }
    }

    private void updateState() {
        if (this.world.isBlockExposed(this.x, this.y + 1, this.z) && this.range != 0) {
            this.active = true;
        } else {
            this.active = false;
        }
    }

    private void addEffectsToPlayers() {
        if (this.active && this.range > 0 && !this.world.isOnline && this.primaryEffect > 0) {
            double range = this.range * 10 + 10;
            byte area = 0;
            if (this.range >= 5) {
                area = 1;
            }

            BoundingBox bb = new BoundingBox(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1).expand(range, range, range);
            bb.maxY = this.world.getWorldHeight();

            for (Entity candidate : this.world.getEntitiesOfType(Player.class, bb)) {
                Player player = (Player)candidate;
                player.addEffect(new EffectData(this.primaryEffect, 180, area, true));
            }
        }
    }

    public int getPrimaryEffect() {
        return this.primaryEffect;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int rng) {
        this.range = rng;
    }

    public void setPrimaryEffect(int eff) {
        this.primaryEffect = eff;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.primaryEffect = tag.getInteger("Primary");
        this.range = tag.getInteger("Levels");
        ListTag<CompoundTag> items = tag.getTagList("Items");

        for (int index = 0; index < items.size(); index++) {
            CompoundTag slot = items.get(index);
            int slotIndex = slot.getByte("Slot") & 255;
            if (slotIndex >= 0 && slotIndex < 2) {
                if (slotIndex == 0) {
                    this.effectStack = new ItemStack(slot);
                } else if (slotIndex == 1) {
                    this.payment = new ItemStack(slot);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("Primary", this.primaryEffect);
        tag.setInteger("Levels", this.range);
        ListTag<CompoundTag> items = new ListTag<>();
        if (this.effectStack != null) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.setByte("Slot", (byte)0);
            this.effectStack.writeToNBT(itemTag);
            items.add(itemTag);
        }

        if (this.payment != null) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.setByte("Slot", (byte)1);
            this.payment.writeToNBT(itemTag);
            items.add(itemTag);
        }

        tag.setTag("Items", items);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? this.payment : (slot == 1 ? this.effectStack : null);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot == 0 && this.payment != null) {
            if (amount >= this.payment.stackSize) {
                ItemStack stack = this.payment;
                this.payment = null;
                return stack;
            } else {
                this.payment.stackSize -= amount;
                return new ItemStack(this.payment.itemID, amount, this.payment.getItemDamage());
            }
        } else if (slot != 1 || this.effectStack == null) {
            return null;
        } else if (amount >= this.effectStack.stackSize) {
            ItemStack stack = this.effectStack;
            this.effectStack = null;
            return stack;
        } else {
            this.effectStack.stackSize -= amount;
            return new ItemStack(this.effectStack.itemID, amount, this.effectStack.getItemDamage());
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            this.payment = stack;
        } else if (slot == 1) {
            this.effectStack = stack;
        }
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName() {
        return "container.beacon";
    }

    @Override
    public int getStackLimit() {
        return 1;
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : player.getDistanceSq(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    public float getActiveTime() {
        if (!this.active) {
            return 0.0F;
        }

        int time = (int)(this.world.getWorldTime() - this.activeTime);
        this.activeTime = this.world.getWorldTime();
        if (time > 1) {
            this.time -= time / 40.0F;
            if (this.time < 0.0F) {
                this.time = 0.0F;
            }
        }

        this.time += 0.025F;
        if (this.time > 1.0F) {
            this.time = 1.0F;
        }

        return this.time;
    }

    @Override
    public double getMaxDistance() {
        return 65536.0;
    }

    @Override
    public boolean dontCull() {
        return true;
    }
}
