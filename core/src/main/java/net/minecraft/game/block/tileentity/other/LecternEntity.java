package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Random;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class LecternEntity extends TileEntity implements Inventory {
    private static Random rand = new Random();
    public ItemStack book;
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float pageFlipTimer;
    public float pageFlipDelay;
    public float bookSpread;
    public float bookSpreadPrev;
    public float currentYaw;
    public float closingAngle;
    public float prevYaw;
    public float targetYaw;

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<CompoundTag> items = tag.getTagList("Items");
        if (!items.isEmpty()) {
            this.book = new ItemStack(items.get(0));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> items = new ListTag<>();
        if (this.book != null) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.setByte("Slot", (byte)0);
            this.book.writeToNBT(itemTag);
            items.add(itemTag);
        }

        tag.setTag("Items", items);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public final ItemStack getStackInSlot(int slot) {
        return this.book;
    }

    @Override
    public void tick() {
        super.tick();
        this.bookSpreadPrev = this.bookSpread;
        this.prevYaw = this.currentYaw;
        Player closestPlayer = this.world.getClosestPlayer(this.x + 0.5F, this.y + 0.5F, this.z + 0.5F, 2.0);
        if (closestPlayer != null) {
            double angleX = closestPlayer.posX - (this.x + 0.5F);
            double angleZ = closestPlayer.posZ - (this.z + 0.5F);
            this.targetYaw = (float)Math.atan2(angleZ, angleX);
            this.bookSpread += 0.1F;
            if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
                float flipTime = this.pageFlipTimer;

                do {
                    this.pageFlipTimer = this.pageFlipTimer + (rand.nextInt(4) - rand.nextInt(4));
                } while (flipTime == this.pageFlipTimer);
            }

            this.closingAngle = 0.0F;
        } else {
            if (this.closingAngle < 90.0F) {
                this.closingAngle++;
            }

            this.bookSpread -= 0.1F;
        }

        while (this.currentYaw >= (float) Math.PI) {
            this.currentYaw -= (float) (Math.PI * 2);
        }

        while (this.currentYaw < (float) -Math.PI) {
            this.currentYaw += (float) (Math.PI * 2);
        }

        while (this.targetYaw >= (float) Math.PI) {
            this.targetYaw -= (float) (Math.PI * 2);
        }

        while (this.targetYaw < (float) -Math.PI) {
            this.targetYaw += (float) (Math.PI * 2);
        }

        float angle = this.targetYaw - this.currentYaw;

        while (angle >= (float) Math.PI) {
            angle -= (float) (Math.PI * 2);
        }

        while (angle < (float) -Math.PI) {
            angle += (float) (Math.PI * 2);
        }

        this.currentYaw += angle * 0.4F;
        if (this.bookSpread < 0.0F) {
            this.bookSpread = 0.0F;
        }

        if (this.bookSpread > 1.0F) {
            this.bookSpread = 1.0F;
        }

        this.tickCount++;
        this.pageFlipPrev = this.pageFlip;
        float delay = (this.pageFlipTimer - this.pageFlip) * 0.4F;
        delay = MathHelper.clamp(delay, -0.2F, 0.2F);
        this.pageFlipDelay = this.pageFlipDelay + (delay - this.pageFlipDelay) * 0.9F;
        this.pageFlip = this.pageFlip + this.pageFlipDelay;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.book != null) {
            if (this.book.stackSize <= amount) {
                ItemStack stack = this.book;
                this.book = null;
                this.onChanged();
                return stack;
            }

            ItemStack stack = this.book.splitStack(amount);
            if (this.book.stackSize == 0) {
                this.book = null;
            }

            this.onChanged();
            return stack;
        } else {
            return null;
        }
    }

    @Override
    public final void setSlotContents(int slot, ItemStack itemStack) {
        this.book = itemStack;
        if (itemStack != null && itemStack.stackSize > 1) {
            itemStack.stackSize = 1;
        }

        this.onChanged();
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.lectern");
    }

    @Override
    public int getStackLimit() {
        return 1;
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
