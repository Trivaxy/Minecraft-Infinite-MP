package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class PrimedTNT extends Entity {
    public int fuse = 0;

    public PrimedTNT(World world) {
        super(world);
        this.preventSpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
    }

    public PrimedTNT(World world, double xCoord, double yCoord, double zCoord) {
        this(world);
        this.setPosition(xCoord, yCoord, zCoord);
        float f5 = (float)(Math.random() * (float) Math.PI * 2.0);
        this.motionX = -MathHelper.sin(f5 * (float) Math.PI / 180.0F) * 0.02F;
        this.motionY = 0.2F;
        this.motionZ = -MathHelper.cos(f5 * (float) Math.PI / 180.0F) * 0.02F;
        this.produceFootsteps = false;
        this.fuse = 80;
        this.prevX = xCoord;
        this.prevY = yCoord;
        this.prevZ = zCoord;
    }

    @Override
    protected void init() {
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.motionY -= 0.04F;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
            this.motionY *= -0.5;
        }

        if (this.fuse-- <= 0) {
            if (!this.world.isOnline) {
                this.setDead();
                this.world.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 4.0F);
            } else {
                this.setDead();
            }
        } else {
            this.world.spawnParticle(ParticleType.SMOKE, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (heldItem != null && heldItem.getItem() == ItemList.shears) {
            if (!this.world.isOnline) {
                this.setDead();
                this.setAttacked();
                this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(BlockList.tnt, 1)));
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            heldItem.damageItem(1, player);
            this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            player.addStat(StatList.tntUse, 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        tag.setByte("Fuse", (byte)this.fuse);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        this.fuse = tag.getByte("Fuse");
    }
}
