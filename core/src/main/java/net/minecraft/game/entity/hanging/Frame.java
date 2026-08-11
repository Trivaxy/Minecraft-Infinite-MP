package net.minecraft.game.entity.hanging;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class Frame extends Painting {
    public Frame(World world) {
        super(world);
        this.yOffset = 1.0F;
    }

    public Frame(World world, int x, int y, int z, int dir, boolean glowing) {
        this(world);
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.yOffset = 1.0F;
        this.art = EnumArt.Kebab;
        this.canGlow = glowing;
        this.setDirection(dir);
    }

    @Override
    public int getBrightnessInt(float brightness) {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        if (this.world.blockExists(x, 0, z)) {
            double offset = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
            int y = MathHelper.floor_double(this.posY - this.yOffset + offset);
            return this.world.getBrightness(x, y - (this.direction == 5 ? 1 : 0), z, 0);
        } else {
            return 0;
        }
    }

    @Override
    protected void init() {
        this.getDataSync().addObjectData(2, 5);
        this.getDataSync().addObject(3, (byte)0);
    }

    public ItemStack getDisplayedItem() {
        return this.getDataSync().getObjectStack(2);
    }

    public void setDisplayedItem(ItemStack stack) {
        if (stack != null) {
            stack = stack.copy();
            stack.stackSize = 1;
            if (stack.getItem() == ItemList.map) {
                this.setItemRotation(0);
            }
        }

        this.getDataSync().updateObject(2, stack);
        this.getDataSync().setState(2);
    }

    @Override
    public void setDirection(int direction) {
        this.direction = direction;
        this.prevYaw = this.yaw = direction * 90;
        double offsetX = this.art.width;
        double offsetY = this.art.height;
        double offsetZ = this.art.width;
        if (direction != 0 && direction != 2) {
            offsetX = 0.5;
        } else {
            offsetZ = 0.5;
        }

        offsetX /= 42.0;
        offsetY /= 42.0;
        offsetZ /= 42.0;
        double x = this.xPosition + 0.5;
        double y = this.yPosition + 0.5;
        double z = this.zPosition + 0.5;
        if (direction == 0) {
            z -= 0.5625;
        }

        if (direction == 1) {
            x -= 0.5625;
        }

        if (direction == 2) {
            z += 0.5625;
        }

        if (direction == 3) {
            x += 0.5625;
        }

        if (direction == 4) {
            offsetY -= 0.3;
            y += 0.5;
            offsetX += 0.365;
        }

        if (direction == 5) {
            offsetY -= 0.3;
            y -= 0.5;
            offsetX += 0.365;
        }

        this.setPosition(x, y, z);
        this.boundingBox = new BoundingBox(x - offsetX, y - offsetY, z - offsetZ, x + offsetX, y + offsetY, z + offsetZ);
    }

    @Override
    public final boolean onValidSurface() {
        int width = this.art.width / 16;
        int height = this.art.height / 16;
        int x = this.xPosition;
        int z = this.zPosition;
        if (this.direction == 0 || this.direction == 2) {
            x = (int)(this.posX - this.art.width / 32.0);
        }

        if (this.direction == 1 || this.direction == 3) {
            z = (int)(this.posZ - this.art.width / 32.0);
        }

        int y = (int)(this.posY - this.art.height / 32.0);

        for (int horizontalOff = 0; horizontalOff < width; horizontalOff++) {
            for (int verticalOff = 0; verticalOff < height; verticalOff++) {
                if (this.direction != 0 && this.direction != 2) {
                    Material material = this.world.getBlockMaterial(this.xPosition, y + verticalOff, z + horizontalOff);
                }

                Material material;
                if (this.direction == 5) {
                    material = this.world.getBlockMaterial(this.xPosition, y + verticalOff + 1, z + horizontalOff);
                } else {
                    material = this.world.getBlockMaterial(x + horizontalOff, y + verticalOff, this.zPosition);
                }

                if (!material.isSolid()) {
                    return false;
                }
            }
        }

        List<Entity> entities = this.world.getEntities(this, this.boundingBox);

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof Painting) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final boolean damageEntity(DamageSource src, int amount) {
        if (this.world.isOnline) {
            return true;
        }

        Entity entity = src.getEntity();
        if (entity instanceof Arrow) {
            return false;
        }

        boolean shouldDrop = entity instanceof Player;
        if (this.getDisplayedItem() == null) {
            this.setDead();
            if (shouldDrop) {
                this.world
                    .spawnEntity(
                        new DroppedItem(
                            this.world, this.posX, this.posY, this.posZ, new ItemStack(this.canGlow ? ItemList.itemFrameGlowing : ItemList.itemFrame)
                        )
                    );
            }
        } else if (shouldDrop) {
            this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, this.getDisplayedItem()));
            this.setDisplayedItem(null);
        }

        return true;
    }

    @Override
    public final void onTick() {
        if (!this.onValidSurface() && !this.world.isOnline) {
            if (this.tickCounter++ >= 1) {
                this.setDead();
                this.world
                    .spawnEntity(
                        new DroppedItem(
                            this.world, this.posX, this.posY, this.posZ, new ItemStack(this.canGlow ? ItemList.itemFrameGlowing : ItemList.itemFrame)
                        )
                    );
                if (this.getDisplayedItem() != null) {
                    this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, this.getDisplayedItem()));
                }
            }
        } else {
            this.tickCounter = 0;
        }
    }

    public int getRotation() {
        return this.getDataSync().getObjectByte(3);
    }

    public void setItemRotation(int rot) {
        this.getDataSync().updateObject(3, (byte)(rot % 8));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        if (this.getDisplayedItem() != null) {
            tag.setCompoundTag("Item", this.getDisplayedItem().writeToNBT(new CompoundTag()));
            tag.setByte("ItemRotation", (byte)this.getRotation());
        }

        tag.setBoolean("Glowing", this.canGlow);
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        CompoundTag item = tag.getCompoundTag("Item");
        if (item != null) {
            this.setDisplayedItem(ItemStack.loadStackFromNBT(item));
            this.setItemRotation(tag.getByte("ItemRotation"));
        }

        this.canGlow = tag.getBoolean("Glowing");
        super.readFromNBT(tag);
    }

    @Override
    public boolean interact(Player player) {
        if (this.world.isOnline) {
            return true;
        }

        if (this.getDisplayedItem() == null) {
            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null) {
                this.setDisplayedItem(stack);
                if (--stack.stackSize <= 0) {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }
            }
        } else {
            this.setItemRotation(this.getRotation() + 1);
        }

        player.swingItem();
        player.addStat(StatList.frameUse, 1);
        return true;
    }
}
