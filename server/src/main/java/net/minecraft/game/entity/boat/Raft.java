package net.minecraft.game.entity.boat;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class Raft extends Boat implements Inventory {
    private ItemStack[] contents = new ItemStack[36];

    public Raft(World world) {
        super(world);
    }

    public Raft(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void onDead() {
        this.dropItem(ItemList.raft.id, 1, 0.0F);

        for (int slot = 0; slot < this.getSize(); slot++) {
            ItemStack stack = this.getStackInSlot(slot);
            if (stack != null) {
                float offsetX = this.rand.nextFloat() * 0.8F + 0.1F;
                float offsetY = this.rand.nextFloat() * 0.8F + 0.1F;
                float offsetZ = this.rand.nextFloat() * 0.8F + 0.1F;

                while (stack.stackSize > 0) {
                    int stackSize = this.rand.nextInt(21) + 10;
                    if (stackSize > stack.stackSize) {
                        stackSize = stack.stackSize;
                    }

                    stack.stackSize -= stackSize;
                    DroppedItem drop = new DroppedItem(
                        this.world, this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ, new ItemStack(stack.itemID, stackSize, stack.itemDmg)
                    );
                    drop.motionX = (float)this.rand.nextGaussian() * 0.05F;
                    drop.motionY = (float)this.rand.nextGaussian() * 0.05F + 0.2F;
                    drop.motionZ = (float)this.rand.nextGaussian() * 0.05F;
                    this.world.spawnEntity(drop);
                }
            }
        }

        this.setDead();
    }

    @Override
    public boolean interact(Player player) {
        if (!this.world.isOnline && player.sneaking) {
            player.displayGUIChest(this, false);
            return true;
        } else {
            return super.interact(player);
        }
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        ListTag<? extends Tag> inventoryTag = tag.getTagList("Items");
        this.contents = new ItemStack[27];

        for (int index = 0; index < inventoryTag.size(); index++) {
            CompoundTag itemTag = (CompoundTag)inventoryTag.get(index);
            int slotID = itemTag.getByte("Slot") & 255;
            if (slotID >= 0 && slotID < this.contents.length) {
                this.contents[slotID] = new ItemStack(itemTag);
            }
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        ListTag<CompoundTag> inventoryTag = new ListTag<>();

        for (int index = 0; index < this.contents.length; index++) {
            if (this.contents[index] != null) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.setByte("Slot", (byte)index);
                this.contents[index].writeToNBT(itemTag);
                inventoryTag.add(itemTag);
            }
        }

        tag.setTag("Items", inventoryTag);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if (this.contents[slot] != null) {
            if (this.contents[slot].stackSize <= size) {
                ItemStack stack = this.contents[slot];
                this.contents[slot] = null;
                return stack;
            }

            ItemStack stack = this.contents[slot].splitStack(size);
            if (this.contents[slot].stackSize == 0) {
                this.contents[slot] = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        this.contents[slot] = stack;
        if (stack != null && stack.stackSize > 64) {
            stack.stackSize = 64;
        }
    }

    @Override
    public boolean isInteractible(Player player) {
        return this.isDead ? false : player.getDistanceSqTo(this) <= 64.0;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public String getName() {
        return "Raft";
    }

    @Override
    public int getStackLimit() {
        return 64;
    }

    @Override
    public void onChanged() {
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
