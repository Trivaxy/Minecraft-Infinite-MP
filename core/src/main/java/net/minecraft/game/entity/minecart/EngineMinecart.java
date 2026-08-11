package net.minecraft.game.entity.minecart;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class EngineMinecart extends Minecart {
    private int fuel;
    private double pushX;
    private double pushZ;
    private boolean pushing;

    public EngineMinecart(World world) {
        super(world);
        this.type = 2;
        this.drop = ItemList.minecartFurnace.id;
    }

    public EngineMinecart(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.type = 2;
        this.drop = ItemList.minecartFurnace.id;
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.pushing && this.rand.nextInt(4) == 0) {
            this.fuel--;
            if (this.fuel < 0) {
                this.pushX = this.pushZ = 0.0;
            }

            this.world.spawnParticle(ParticleType.LARGESMOKE, this.posX, this.posY + 0.8F, this.posZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void moveCartOnTrack(int x, int y, int z, double maxOffset) {
        super.moveCartOnTrack(x, y, z, maxOffset);
        double force = MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
        if (force > 0.01 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
            this.pushX /= force;
            this.pushZ /= force;
            if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
                this.pushX = 0.0;
                this.pushZ = 0.0;
            } else {
                this.pushX = this.motionX;
                this.pushZ = this.motionZ;
            }
        }
    }

    @Override
    protected void pushCart() {
        double force = MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
        if (force > 0.01) {
            this.pushing = true;
            this.pushX /= force;
            this.pushZ /= force;
            this.motionX *= 0.8;
            this.motionY *= 0.0;
            this.motionZ *= 0.8;
            this.motionX = this.motionX + this.pushX * 0.04;
            this.motionZ = this.motionZ + this.pushZ * 0.04;
        } else {
            this.motionX *= 0.9;
            this.motionY *= 0.0;
            this.motionZ *= 0.9;
        }
    }

    @Override
    protected void collide(Minecart entity, double motionX, double motionZ, double distX, double distZ) {
        if (entity.type != 2) {
            entity.motionX *= 0.2F;
            entity.motionZ *= 0.2F;
            entity.addVelocity(this.motionX + distX, 0.0, this.motionZ + distZ);
            this.motionX *= 0.95F;
            this.motionZ *= 0.95F;
        } else {
            motionX /= 2.0;
            motionZ /= 2.0;
            this.motionX *= 0.2F;
            this.motionZ *= 0.2F;
            this.addVelocity(motionX - distX, 0.0, motionZ - distZ);
            entity.motionX *= 0.2F;
            entity.motionZ *= 0.2F;
            entity.addVelocity(motionX + distX, 0.0, motionZ + distZ);
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setFloat("PushX", (float)this.pushX);
        tag.setFloat("PushZ", (float)this.pushZ);
        tag.setShort("Fuel", (short)this.fuel);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.pushX = tag.getFloat("PushX");
        this.pushZ = tag.getFloat("PushZ");
        this.fuel = tag.getShort("Fuel");
    }

    @Override
    public boolean interact(Player player) {
        if (!this.world.isOnline) {
            ItemStack stack = player.inventory.getCurrentItem();
            if (player.getUsing()) {
                stack = player.inventory.getOffHandItem();
            }

            if (stack != null && stack.itemID == ItemList.coal.id) {
                if (--stack.stackSize == 0) {
                    if (player.getUsing()) {
                        player.inventory.setSlotContents(44, null);
                    } else {
                        player.inventory.setSlotContents(player.inventory.currentSlot, null);
                    }
                }

                this.fuel += 1200;
            }

            this.pushX = this.posX - player.posX;
            this.pushZ = this.posZ - player.posZ;
        }

        return true;
    }
}
