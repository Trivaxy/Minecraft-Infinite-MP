package net.minecraft.game.entity.minecart;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class StorageMinecart extends Minecart implements Inventory {
    private ItemStack[] cargoItems = new ItemStack[36];

    public StorageMinecart(World world) {
        super(world);
        this.type = 1;
        this.drop = ItemList.minecartChest.id;
    }

    public StorageMinecart(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.type = 1;
        this.drop = ItemList.minecartChest.id;
    }

    @Override
    public boolean interact(Player player) {
        if (!this.world.isOnline) {
            player.displayGUIChest(this, false);
        }

        return true;
    }

    @Override
    public void setDead() {
        for (int slot = 0; slot < this.getSize(); slot++) {
            ItemStack stack = this.getStackInSlot(slot);
            if (stack != null) {
                float xOffset = this.rand.nextFloat() * 0.8F + 0.1F;
                float yOffset = this.rand.nextFloat() * 0.8F + 0.1F;
                float zOffset = this.rand.nextFloat() * 0.8F + 0.1F;

                while (stack.stackSize > 0) {
                    int stackSize = this.rand.nextInt(21) + 10;
                    if (stackSize > stack.stackSize) {
                        stackSize = stack.stackSize;
                    }

                    stack.stackSize -= stackSize;
                    ItemStack newStack = new ItemStack(stack.itemID, stackSize, stack.itemDmg);
                    if (stack.hasTag()) {
                        newStack = new ItemStack(stack.itemID, stackSize, stack.itemDmg, stack.customName, stack.customTag);
                    }

                    DroppedItem droppedItem = new DroppedItem(this.world, this.posX + xOffset, this.posY + yOffset, this.posZ + zOffset, newStack);
                    float offset = 0.05F;
                    droppedItem.motionX = (float)this.rand.nextGaussian() * offset;
                    droppedItem.motionY = (float)this.rand.nextGaussian() * offset + 0.2F;
                    droppedItem.motionZ = (float)this.rand.nextGaussian() * offset;
                    this.world.spawnEntity(droppedItem);
                }
            }
        }

        super.setDead();
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> inventoryTag = new ListTag<>();

        for (int slot = 0; slot < this.cargoItems.length; slot++) {
            if (this.cargoItems[slot] != null) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.setByte("Slot", (byte)slot);
                this.cargoItems[slot].writeToNBT(slotTag);
                inventoryTag.add(slotTag);
            }
        }

        tag.setTag("Items", inventoryTag);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<? extends Tag> itemsTag = tag.getTagList("Items");
        this.cargoItems = new ItemStack[this.getSize()];

        for (int slot = 0; slot < itemsTag.size(); slot++) {
            CompoundTag slotTag = (CompoundTag)itemsTag.get(slot);
            int slotID = slotTag.getByte("Slot") & 255;
            if (slotID >= 0 && slotID < this.cargoItems.length) {
                this.cargoItems[slotID] = new ItemStack(slotTag);
            }
        }
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.cargoItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrAmount) {
        if (this.cargoItems[slot] != null) {
            if (this.cargoItems[slot].stackSize <= decrAmount) {
                ItemStack stack = this.cargoItems[slot];
                this.cargoItems[slot] = null;
                return stack;
            }

            ItemStack stack = this.cargoItems[slot].splitStack(decrAmount);
            if (this.cargoItems[slot].stackSize == 0) {
                this.cargoItems[slot] = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        this.cargoItems[slot] = stack;
        if (stack != null && stack.stackSize > this.getStackLimit()) {
            stack.stackSize = this.getStackLimit();
        }
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.minecart");
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
        return this.isDead ? false : player.getDistanceSqTo(this) <= 64.0;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
