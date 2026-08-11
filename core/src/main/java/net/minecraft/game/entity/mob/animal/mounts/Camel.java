package net.minecraft.game.entity.mob.animal.mounts;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Camel extends RideableMob implements Inventory {
    private ItemStack[] chestContents = new ItemStack[36];

    public Camel(World world) {
        super(world);
        this.setSize(2.0F, 2.0F);
        this.setHealth(30);
        this.moveSpeed = 0.2F;
        this.tamedItem = BlockList.cactus.id;
        this.texture = "/mob/camel.png";
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed * 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public int getSpawnID() {
        return 26;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.isTamed()) {
            this.tameMob(player, heldItem);
        } else {
            if (heldItem != null && ItemList.items[heldItem.itemID] == ItemList.saddle && !this.isRideable()) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }

                this.world.playEntitySound(player, "random.saddle", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                this.setRideable(true);
                return true;
            }

            if (this.isRideable() && !this.healMob(player, heldItem)) {
                if (!this.world.isOnline && player.sneaking) {
                    player.displayGUIChest(this, false);
                } else {
                    player.mountEntity(this);
                    this.navigator.clearPath();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean healMob(Player player, ItemStack heldItem) {
        if (heldItem != null && heldItem.getItem().id == BlockList.cactus.id && this.health < this.maxHealth) {
            if (player.properties.gamemode != 1) {
                heldItem.stackSize--;
            }

            this.heal(3);
            if (heldItem.stackSize <= 0) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void jump() {
        this.motionY = 0.52F;
        float force = this.yaw * 0.01745329F;
        this.motionX = this.motionX - MathHelper.sin(force) * 0.2F / 2.0F;
        this.motionZ = this.motionZ + MathHelper.cos(force) * 0.2F / 2.0F;
    }

    @Override
    public void onDeath(DamageSource parent) {
        super.onDeath(parent);
        if (this.isRideable()) {
            this.dropItem(ItemList.saddle.id, 1);
        }

        for (int slot = 0; slot < this.getSize(); slot++) {
            ItemStack stack = this.getStackInSlot(slot);
            if (stack != null) {
                double forceX = this.rand.nextFloat() * 0.8 + 0.1;
                double forceY = this.rand.nextFloat() * 0.8 + 0.1;
                double forceZ = this.rand.nextFloat() * 0.8 + 0.1;

                while (stack.stackSize > 0) {
                    int stackSize = this.rand.nextInt(21) + 10;
                    if (stackSize > stack.stackSize) {
                        stackSize = stack.stackSize;
                    }

                    stack.stackSize -= stackSize;
                    CompoundTag nbt = null;
                    if (stack.hasTag()) {
                        nbt = stack.getTag();
                    }

                    ItemStack newStack = new ItemStack(stack.itemID, stackSize, stack.itemDmg, stack.customName);
                    if (nbt != null) {
                        newStack = new ItemStack(stack.itemID, stackSize, stack.itemDmg, stack.customName, nbt);
                    }

                    DroppedItem droppedItem = new DroppedItem(this.world, this.posX + forceX, this.posY + forceY, this.posZ + forceZ, newStack);
                    droppedItem.motionX = (float)this.rand.nextGaussian() * 0.05F;
                    droppedItem.motionX = this.rand.nextGaussian() * 0.05;
                    droppedItem.motionY = this.rand.nextGaussian() * 0.05 + 0.2;
                    droppedItem.motionZ = this.rand.nextGaussian() * 0.05;
                    this.world.spawnEntity(droppedItem);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> tagList = new ListTag<>();

        for (int slot = 0; slot < this.chestContents.length; slot++) {
            if (this.chestContents[slot] != null) {
                CompoundTag invTag = new CompoundTag();
                invTag.setByte("Slot", (byte)slot);
                this.chestContents[slot].writeToNBT(invTag);
                tagList.add(invTag);
            }
        }

        tag.setTag("Items", tagList);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<? extends Tag> tagList = tag.getTagList("Items");
        this.chestContents = new ItemStack[27];

        for (int slot = 0; slot < tagList.size(); slot++) {
            CompoundTag itemTag = (CompoundTag)tagList.get(slot);
            int invSlot = itemTag.getByte("Slot") & 255;
            if (invSlot >= 0 && invSlot < this.chestContents.length) {
                this.chestContents[invSlot] = new ItemStack(itemTag);
            }
        }
    }

    @Override
    public String getSound() {
        return "mob.camel";
    }

    @Override
    protected String getHurtSound() {
        return "mob.camelhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.cameldeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.camelstep", 0.25F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    @Override
    public double getYOffset() {
        return 1.4;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.sand.id ? 10.0F : this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockId(x, y - 1, z) == BlockList.sand.id
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if (this.chestContents[slot] != null) {
            if (this.chestContents[slot].stackSize <= size) {
                ItemStack stack = this.chestContents[slot];
                this.chestContents[slot] = null;
                return stack;
            }

            ItemStack stack = this.chestContents[slot].splitStack(size);
            if (this.chestContents[slot].stackSize == 0) {
                this.chestContents[slot] = null;
            }

            return stack;
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int slot, ItemStack stack) {
        this.chestContents[slot] = stack;
        if (stack != null && stack.stackSize > 64) {
            stack.stackSize = 64;
        }
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public String getName() {
        return "Camel";
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
    public ItemStack getStackInSlot(int slot) {
        return this.chestContents[slot];
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }
}
