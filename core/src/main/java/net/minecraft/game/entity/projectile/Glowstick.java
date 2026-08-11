package net.minecraft.game.entity.projectile;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Glowstick extends Projectile {
    public Mob thrower;
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    public int inTile = 0;
    private int ticksInGround;
    public boolean inBlock = false;

    public Glowstick(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.itemAssigned = ItemList.glowStick;
    }

    public Glowstick(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
        this.yOffset = 0.0F;
    }

    public Glowstick(World world, Mob thrower, float tickDelta) {
        super(world);
        this.thrower = thrower;
        this.setSize(0.5F, 0.5F);
        this.teleportTo(thrower.posX, thrower.posY + thrower.getEyeHeight(), thrower.posZ, thrower.yaw, thrower.pitch);
        float multiplier = 0.16F;
        if (((Player)thrower).inventory.canUseItem()) {
            multiplier = -0.16F;
        }

        this.posX = this.posX - MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * multiplier;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * multiplier;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
        this.motionZ = MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI);
        this.motionY = -MathHelper.sin(this.pitch / 180.0F * (float) Math.PI);
        this.setHeading(this.motionX, this.motionY, this.motionZ, tickDelta * 1.5F, 1.0F);
        this.itemAssigned = ItemList.glowStick;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float angle = MathHelper.sqrt_double(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0 / (float) Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, angle) * 180.0 / (float) Math.PI);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPosAndRot(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    protected void init() {
    }

    @Override
    public int isLit() {
        return 15;
    }

    @Override
    public void onTick() {
        if (this.inBlock) {
            int tile = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
            if (tile == this.inTile) {
                this.ticksInGround++;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }

                return;
            }

            this.inBlock = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
        }

        super.onTick();
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop != null && mop.entityHit == null) {
            this.xTile = mop.blockX;
            this.yTile = mop.blockY;
            this.zTile = mop.blockZ;
            this.inTile = this.world.getBlockId(this.xTile, this.yTile, this.zTile);
            this.motionX = (float)(mop.hitVec.xCoord - this.posX);
            this.motionY = (float)(mop.hitVec.yCoord - this.posY);
            this.motionZ = (float)(mop.hitVec.zCoord - this.posZ);
            float multiplier = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX = this.posX - this.motionX / multiplier * 0.05F;
            this.posY = this.posY - this.motionY / multiplier * 0.05F;
            this.posZ = this.posZ - this.motionZ / multiplier * 0.05F;
            this.world.playEntitySound(this, "mob.slime", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inBlock = true;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setShort("xTile", (short)this.xTile);
        tag.setShort("yTile", (short)this.yTile);
        tag.setShort("zTile", (short)this.zTile);
        tag.setShort("inTile", (byte)this.inTile);
        tag.setByte("inGround", (byte)(this.inBlock ? 1 : 0));
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.xTile = tag.getShort("xTile");
        this.yTile = tag.getShort("yTile");
        this.zTile = tag.getShort("zTile");
        this.inTile = tag.getShort("inTile");
        this.inBlock = tag.getByte("inGround") == 1;
    }

    @Override
    public void onCollideWith(Entity player) {
        if (!this.world.isOnline && this.inBlock && player instanceof Player) {
            Player newPlayer = (Player)player;
            if (newPlayer.inventory.addItemToInventory(new ItemStack(ItemList.glowStick.id, 1))) {
                this.world.playEntitySound(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                newPlayer.onItemPickup(this);
                this.setDead();
            }
        }
    }

    public int getFacing() {
        int bx = MathHelper.floor_double(this.posX);
        int by = MathHelper.floor_double(this.posY);
        int bz = MathHelper.floor_double(this.posZ);
        double dx = this.posX - bx;
        double dy = this.posY - by;
        double dz = this.posZ - bz;
        double min = 1.0;
        int facing = -1;
        if (dy < min) {
            min = dy;
            facing = 0;
        }

        if (1.0 - dy < min) {
            min = 1.0 - dy;
            facing = 1;
        }

        if (dz < min) {
            min = dz;
            facing = 2;
        }

        if (1.0 - dz < min) {
            min = 1.0 - dz;
            facing = 3;
        }

        if (dx < min) {
            min = dx;
            facing = 4;
        }

        if (1.0 - dx < min) {
            min = 1.0 - dx;
            facing = 5;
        }

        return facing;
    }
}
