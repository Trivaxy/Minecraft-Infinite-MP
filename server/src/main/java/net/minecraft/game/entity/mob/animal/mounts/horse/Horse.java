package net.minecraft.game.entity.mob.animal.mounts.horse;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.mounts.RideableMob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Horse extends RideableMob {
    public boolean armored = false;

    public Horse(World world) {
        super(world);
        this.setHealth(20);
        this.setSize(1.5F, 1.8F);
        this.tamedItem = ItemList.wheat.id;
        this.rideSpeed = 1.2F;
        this.moveSpeed = 0.25F;
        this.type = (byte)(this.rand.nextInt(5) + 1);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/horse/horse_creamy.png";
                break;
            case 2:
                this.texture = "/mob/horse/horse_black.png";
                break;
            case 3:
                this.texture = "/mob/horse/horse_brown.png";
                break;
            case 4:
                this.texture = "/mob/horse/horse_white.png";
                break;
            case 5:
                this.texture = "/mob/horse/horse_gray.png";
                break;
            case 6:
                this.texture = "/mob/horse/zebra.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        if (biome == BiomeList.savanna) {
            this.type = 6;
        }

        this.setType();
    }

    @Override
    public int getSpawnID() {
        return 17;
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

        if (!this.isTamed()) {
            return this.tameMob(player, heldItem);
        }

        if (heldItem != null && ItemList.items[heldItem.itemID] == ItemList.saddle && !this.isRideable()) {
            if (player.getUsing()) {
                player.inventory.setSlotContents(44, null);
            } else {
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }

            this.world.playEntitySound(player, "random.saddle", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            this.setRideable(true);
            return true;
        } else if (!this.armored && heldItem != null && ItemList.items[heldItem.itemID] == ItemList.horseArmor) {
            if (player.getUsing()) {
                player.inventory.setSlotContents(44, null);
            } else {
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }

            this.world.playEntitySound(player, "random.bundle", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            this.armored = true;
            return true;
        } else if (this.isRideable() && !this.healMob(player, heldItem)) {
            player.mountEntity(this);
            if (this instanceof Pegasus) {
                player.triggerAchievement(AchievementList.pegasus);
            }

            this.navigator.clearPath();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        Entity entity = src.getEntity();
        if (entity != null && this.armored) {
            amount /= 2;
        }

        return super.damageEntity(src, amount);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Armor", this.armored);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.armored = tag.getBoolean("Armor");
    }

    @Override
    public String getSound() {
        return "mob.horse";
    }

    @Override
    protected String getHurtSound() {
        return "mob.horsehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.horsedeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.horsestep", 0.35F, 1.0F);
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
            this.dropItem(ItemList.saddle.id, 1);
        }
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
    }

    @Override
    public double getYOffset() {
        return 1.4;
    }
}
