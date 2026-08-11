package net.minecraft.game.block.tileentity.smelters;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.machines.GeneratorBlock;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class GeneratorEntity extends TileEntity implements Inventory {
    private ItemStack fuel;
    public int activeTime = 0;
    public int remainingFuel = 0;
    public int offTimer = 0;
    public int onTimer = 0;
    public boolean active = false;

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.fuel;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.fuel != null) {
            if (this.fuel.stackSize <= amount) {
                ItemStack stack = this.fuel;
                this.fuel = null;
                return stack;
            }

            ItemStack stack = this.fuel.splitStack(amount);
            if (this.fuel.stackSize == 0) {
                this.fuel = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack itemStack) {
        this.fuel = itemStack;
        if (itemStack != null && itemStack.stackSize > 64) {
            itemStack.stackSize = 64;
        }
    }

    @Override
    public String getName() {
        return "container.generator";
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Fuel")) {
            this.fuel = new ItemStack(tag.getCompoundTag("Fuel"));
        }

        this.activeTime = tag.getShort("BurnTime");
        this.active = tag.getBoolean("State");
        this.offTimer = tag.getShort("OffTimer");
        this.onTimer = tag.getShort("OnTimer");
        this.remainingFuel = getItemBurnTime(this.fuel);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setShort("BurnTime", (short)this.activeTime);
        tag.setShort("OnTimer", (short)this.onTimer);
        tag.setShort("OffTimer", (short)this.offTimer);
        tag.setString("id", "Generator");
        tag.setBoolean("State", this.active);
        if (this.fuel != null) {
            CompoundTag fuelTag = new CompoundTag();
            this.fuel.writeToNBT(fuelTag);
            tag.setTag("Fuel", fuelTag);
        }
    }

    @Override
    public int getStackLimit() {
        return 64;
    }

    public int getRemainingFuel(int time) {
        if (this.remainingFuel == 0) {
            this.remainingFuel = 200;
        }

        return this.activeTime * 12 / this.remainingFuel;
    }

    public boolean isBurning() {
        return this.activeTime > 0;
    }

    @Override
    public void tick() {
        boolean burning = this.offTimer > 0;
        boolean forceUpdate = false;
        if (this.offTimer > 0) {
            this.offTimer--;
        }

        if (this.onTimer > 0) {
            this.onTimer--;
        }

        if (this.shouldChangeState(this.x, this.y, this.z, BlockList.regulator.id)) {
            int delay = 20;
            if (this.checkForModifiers(this.x, this.y, this.z, 5, BlockList.regulator.id)) {
                delay = 50;
            } else if (this.checkForModifiers(this.x, this.y, this.z, 6, BlockList.regulator.id)) {
                delay = 75;
            } else if (this.checkForModifiers(this.x, this.y, this.z, 7, BlockList.regulator.id)) {
                delay = 100;
            }

            if (this.onTimer <= 0) {
                this.onTimer = delay;
            }

            if (this.onTimer == 1) {
                this.active = true;
            }
        }

        if (this.shouldChangeState(this.x, this.y, this.z, BlockList.detector.id) || this.shouldChangeState(this.x, this.y, this.z, BlockList.observer.id)) {
            this.active = true;
        }

        if (this.active) {
            if (this.activeTime > 0) {
                this.activeTime--;
            }

            if (this.shouldChangeState(this.x, this.y, this.z, BlockList.transformer.id)) {
                int delay = 20;
                if (this.checkForModifiers(this.x, this.y, this.z, 5, BlockList.transformer.id)) {
                    delay = 50;
                } else if (this.checkForModifiers(this.x, this.y, this.z, 6, BlockList.transformer.id)) {
                    delay = 75;
                } else if (this.checkForModifiers(this.x, this.y, this.z, 7, BlockList.transformer.id)) {
                    delay = 100;
                }

                if (this.offTimer > delay) {
                    this.offTimer = delay;
                }

                if (this.offTimer == 0) {
                    this.active = false;
                }
            } else {
                this.offTimer = this.activeTime;
            }

            if (!this.world.isOnline && this.activeTime == 0 && this.canSmelt()) {
                this.remainingFuel = this.activeTime = getItemBurnTime(this.fuel);
                if (this.activeTime > 0) {
                    forceUpdate = true;
                    if (this.fuel != null) {
                        if (this.fuel.getItem() == ItemList.bucketLava) {
                            this.fuel = new ItemStack(ItemList.bucketEmpty, 1);
                        } else {
                            this.fuel.stackSize--;
                            if (this.fuel.stackSize == 0) {
                                this.fuel = null;
                            }
                        }

                        this.offTimer++;
                    }
                }
            }
        } else {
            this.offTimer = 0;
        }

        if (burning != this.offTimer > 0 && !this.world.isOnline) {
            forceUpdate = true;
            GeneratorBlock.updateState(this.offTimer > 0, this.world, this.x, this.y, this.z);
        }

        if (forceUpdate) {
            this.onChanged();
        }
    }

    private boolean checkForModifiers(int posX, int posY, int posZ, int metadata, int blockID) {
        boolean returnClause = false;

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 2; y++) {
                    if (this.world.getBlockId(posX + x, posY + y, posZ + z) == blockID && this.world.getBlockMetadata(posX + x, posY + y, posZ + z) == metadata
                        )
                     {
                        returnClause = true;
                        break;
                    }
                }
            }
        }

        return returnClause;
    }

    private boolean shouldChangeState(int posX, int posY, int posZ, int blockID) {
        boolean returnClause = false;

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                for (int y = -1; y < 2; y++) {
                    if (this.world.getBlockId(posX + x, posY + y, posZ + z) == blockID && this.world.getBlockMetadata(posX + x, posY + y, posZ + z) >= 4) {
                        returnClause = true;
                        break;
                    }
                }
            }
        }

        return returnClause;
    }

    private boolean canSmelt() {
        return this.fuel == null ? false : isFuel(this.fuel);
    }

    private static int getItemBurnTime(ItemStack stack) {
        if (stack == null) {
            return 0;
        } else {
            int id = stack.getItem().id;
            if (id == ItemList.coal.id) {
                return stack.getItemDamage() == 1 ? 800 : 1600;
            } else if (id == BlockList.blockCoal.id) {
                return 14400;
            } else {
                return id == ItemList.bucketLava.id ? 20000 : 0;
            }
        }
    }

    public static boolean isFuel(ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : player.getDistanceSq(this.x + 0.5F, this.y + 0.5F, this.z + 0.5F) <= 64.0;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
