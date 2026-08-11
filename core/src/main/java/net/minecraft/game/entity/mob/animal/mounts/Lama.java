package net.minecraft.game.entity.mob.animal.mounts;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.tameable.AggroWild;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Spit;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class Lama extends RideableMob {
    private int attackTime;
    public int saddle = -1;

    public Lama(World world) {
        super(world);
        this.setHealth(20);
        this.setSize(1.5F, 2.0F);
        this.moveSpeed = 0.25F;
        this.rideSpeed = 0.8F;
        this.tamedItem = ItemList.carrot.id;
        this.type = (byte)(this.rand.nextInt(5) + 1);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new RangedAttack(this, this.moveSpeed, -4, 50));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new AggroWild(this, true));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/llama/llama_creamy.png";
                break;
            case 2:
                this.texture = "/mob/llama/llama_black.png";
                break;
            case 3:
                this.texture = "/mob/llama/llama_brown.png";
                break;
            case 4:
                this.texture = "/mob/llama/llama_white.png";
                break;
            case 5:
                this.texture = "/mob/llama/llama_gray.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 72;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.attackTime > 0) {
            this.attackTime--;
        }
    }

    @Override
    protected void ride() {
        if (this.rider instanceof Player && ((Player)this.rider).swinging && this.attackTime == 0) {
            this.world.playEntitySound(this, "mob.llamaspit", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            float yawRad = (float)Math.toRadians(this.yaw);
            double offsetX = -Math.sin(yawRad) * 0.8;
            double offsetZ = Math.cos(yawRad) * 0.8;
            Projectile throwable = new Spit(this.world, this, 2);
            throwable.setPosition(this.posX + offsetX, this.posY + this.height, this.posZ + offsetZ);
            if (!this.world.isOnline) {
                this.world.spawnEntity(throwable);
            }

            this.attackTime = 50;
        }

        super.ride();
    }

    @Override
    protected boolean validFood(Item fond) {
        return fond == ItemList.appleRed || fond == ItemList.carrot;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.isTamed() && this.attackTarget == null) {
            this.tameMob(player, heldItem);
        } else {
            if (heldItem != null && heldItem.itemID == BlockList.carpet.id && !this.isRideable()) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }

                this.world.playEntitySound(player, "random.saddle", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                this.setRideable(true);
                this.saddle = heldItem.itemDmg;
                return true;
            }

            if (this.isRideable() && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ItemList.shears) {
                if (!this.world.isOnline) {
                    this.setRideable(false);
                    DroppedItem entityItem5 = this.dropItem(BlockList.carpet.id, 1, this.saddle, 1.0F);
                    entityItem5.motionY = entityItem5.motionY + this.rand.nextFloat() * 0.05F;
                    entityItem5.motionX = entityItem5.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    entityItem5.motionZ = entityItem5.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                }

                player.swingItem();
                player.getCurrentEquippedItem().damageItem(1, player);
                this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                return true;
            }

            if (this.isRideable() && !this.healMob(player, heldItem)) {
                player.mountEntity(this);
                this.navigator.clearPath();
                return true;
            }
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setShort("Decor", (short)this.saddle);
        tag.setInteger("AttackTimer", this.attackTime);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.saddle = tag.getShort("Decor");
        this.attackTime = tag.getInteger("AttackTimer");
    }

    @Override
    public String getSound() {
        return "mob.llama";
    }

    @Override
    protected String getHurtSound() {
        return "mob.llamahurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.llamadeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.llamastep", 0.25F, 1.0F);
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
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (this.isRideable()) {
            this.dropItem(BlockList.carpet.id, 1, this.saddle, 0.0F);
        }
    }

    @Override
    public double getYOffset() {
        return 1.2;
    }
}
