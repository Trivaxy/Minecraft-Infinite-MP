package net.minecraft.game.block.tileentity.smelters;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.utility.BlastFurnace;
import net.minecraft.game.block.utility.Furnace;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.DoorItem;
import net.minecraft.game.item.block.SignItem;
import net.minecraft.game.item.crafting.special.FurnaceRecipes;
import net.minecraft.localization.Translator;

public class FurnaceEntity extends TileEntity implements Inventory {
    protected ItemStack[] slots = new ItemStack[3];
    public int burnTime = 0;
    public int fuel = 0;
    public int cookTime = 0;
    protected int smeltSpeed = 200;
    protected int outputSlot = 2;

    @Override
    public int getSize() {
        return this.slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.slots[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.slots[slot] != null) {
            if (this.slots[slot].stackSize <= amount) {
                ItemStack stack = this.slots[slot];
                this.slots[slot] = null;
                return stack;
            }

            ItemStack stack = this.slots[slot].splitStack(amount);
            if (this.slots[slot].stackSize == 0) {
                this.slots[slot] = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        this.slots[slot] = stack;
        if (stack != null && stack.stackSize > this.getStackLimit()) {
            stack.stackSize = this.getStackLimit();
        }
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.furnace");
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<? extends Tag> items = tag.getTagList("Items");
        this.slots = new ItemStack[this.getSize()];

        for (int index = 0; index < items.size(); index++) {
            CompoundTag itemTag = (CompoundTag)items.get(index);
            byte slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < this.slots.length) {
                this.slots[slot] = new ItemStack(itemTag);
            }
        }

        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.fuel = tag.getShort("FuelTime");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setShort("BurnTime", (short)this.burnTime);
        tag.setShort("CookTime", (short)this.cookTime);
        tag.setShort("FuelTime", (short)this.fuel);
        ListTag<CompoundTag> items = new ListTag<>();

        for (int slot = 0; slot < this.slots.length; slot++) {
            if (this.slots[slot] != null) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.setByte("Slot", (byte)slot);
                this.slots[slot].writeToNBT(itemTag);
                items.add(itemTag);
            }
        }

        tag.setTag("Items", items);
    }

    @Override
    public int getStackLimit() {
        return 64;
    }

    public int getCookProgress(int scale) {
        return this.cookTime * 24 / this.smeltSpeed;
    }

    public int getRemainingFuel(int scale) {
        if (this.fuel == 0) {
            this.fuel = this.smeltSpeed;
        }

        return this.burnTime * 12 / this.fuel;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void tick() {
        boolean smelting = this.burnTime > 0;
        boolean forceUpdate = false;
        if (this.burnTime > 0) {
            this.burnTime--;
            forceUpdate = true;
        }

        if (this.burnTime == 0 && this.canSmelt()) {
            this.fuel = this.burnTime = getItemFuel(this.slots[1]);
            if (this.burnTime > 0) {
                forceUpdate = true;
                if (this.slots[1] != null) {
                    if (this.slots[1].getItem() == ItemList.bucketLava) {
                        this.slots[1] = new ItemStack(ItemList.bucketEmpty, 1);
                    } else {
                        this.slots[1].stackSize--;
                        if (this.slots[1].stackSize == 0) {
                            this.slots[1] = null;
                        }
                    }
                }
            }
        }

        if (this.isBurning() && this.canSmelt()) {
            this.cookTime++;
            if (this.cookTime == this.smeltSpeed) {
                this.cookTime = 0;
                this.smeltItem();
                forceUpdate = true;
            }
        } else {
            this.cookTime = 0;
        }

        if (smelting != this.burnTime > 0) {
            forceUpdate = true;
            if (this instanceof BlastFurnaceEntity) {
                BlastFurnace.updateState(this.burnTime > 0, this.world, this.x, this.y, this.z);
            } else {
                Furnace.updateState(this.burnTime > 0, this.world, this.x, this.y, this.z);
            }
        }

        if (forceUpdate) {
            this.onChanged();
        }
    }

    protected boolean canSmelt() {
        if (this.slots[0] == null) {
            return false;
        } else {
            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(this.slots[0].getItem().id, this.slots[0].itemDmg);
            if (result == null) {
                return false;
            } else if (this.slots[this.outputSlot] == null) {
                return true;
            } else if (!this.slots[this.outputSlot].isItemEqual(result)) {
                return false;
            } else {
                return this.slots[this.outputSlot].stackSize < this.getStackLimit()
                        && this.slots[this.outputSlot].stackSize < this.slots[this.outputSlot].getStackLimit()
                    ? true
                    : this.slots[this.outputSlot].stackSize < result.getStackLimit();
            }
        }
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(this.slots[0].getItem().id, this.slots[0].itemDmg);
            if (this.slots[this.outputSlot] == null) {
                this.slots[this.outputSlot] = result.copy();
            } else if (this.slots[this.outputSlot].itemID == result.itemID) {
                this.slots[this.outputSlot].stackSize++;
            }

            this.slots[0].stackSize--;
            if (this.slots[0].stackSize <= 0) {
                this.slots[0] = null;
            }
        }
    }

    protected static int getItemFuel(ItemStack stack) {
        if (stack == null) {
            return 0;
        }

        int id = stack.getItem().id;
        if (id < 4096) {
            Material material = BlockList.blocks[id].material;
            if (material == MaterialList.wood) {
                return 300;
            }

            if (material == MaterialList.cloth) {
                return 200;
            }

            if (material == MaterialList.plants || material == MaterialList.rope) {
                return 100;
            }

            if (material == MaterialList.leaves) {
                return 150;
            }
        } else {
            Item item = ItemList.items[id];
            if (item instanceof DoorItem && item != ItemList.doorSteel) {
                return 350;
            }

            if (item instanceof SignItem) {
                return 200;
            }
        }

        if (id == BlockList.seaweed.id) {
            return 400;
        } else if (id == ItemList.stick.id) {
            return 100;
        } else if (id == ItemList.coal.id) {
            return stack.getItemDamage() == 1 ? 800 : 1600;
        } else if (id == BlockList.blockCoal.id) {
            return 14400;
        } else {
            return id == ItemList.bucketLava.id ? 20000 : 0;
        }
    }

    @Override
    public void onChanged() {
        if (this.world != null) {
            this.world.setChunkModified(this.x, this.y, this.z);
        }
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : player.getDistanceSq(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }

    public static boolean isFuel(ItemStack stack) {
        return getItemFuel(stack) > 0;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
