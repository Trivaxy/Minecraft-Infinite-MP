package net.minecraft.game.entity.mob.animal.mounts;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Pig extends RideableMob {
    public Pig(World world) {
        super(world);
        this.setSize(0.9F, 0.9F);
        this.moveSpeed = 0.25F;
        this.rideSpeed = 0.6F;
        this.type = (byte)this.rand.nextInt(5);
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
            case 0:
                this.texture = "/mob/pig/boar.png";
                break;
            case 1:
            default:
                this.texture = "/mob/pig/pig.png";
                break;
            case 2:
                this.texture = "/mob/pig/spotted.png";
                break;
            case 3:
                this.texture = "/mob/pig/dark.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        this.type = biome.getTemperature();
        this.setType();
    }

    @Override
    public int getSpawnID() {
        return 0;
    }

    @Override
    public String getSound() {
        return "mob.pig";
    }

    @Override
    protected String getHurtSound() {
        return "mob.pig";
    }

    @Override
    protected String getDeathSound() {
        return "mob.pigdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.pigstep", 0.15F, 1.0F);
    }

    @Override
    protected void fall(float dist) {
        super.fall(dist);
        if (dist > 5.0F && this.rider instanceof Player) {
            ((Player)this.rider).triggerAchievement(AchievementList.flyPig);
        }
    }

    @Override
    public void updateAI() {
        if (this.rider != null && this.rider instanceof Player) {
            Player rider = (Player)this.rider;
            if ((rider.inventory.getCurrentItem() == null || rider.inventory.getCurrentItem().getItem() != ItemList.stickCarrot)
                && (rider.inventory.getOffHandItem() == null || rider.inventory.getOffHandItem().getItem() != ItemList.stickCarrot)) {
                this.AIOverride();
            } else {
                this.ride();
            }
        } else {
            this.AIOverride();
        }
    }

    @Override
    protected boolean validFood(Item fond) {
        return fond == ItemList.appleRed || fond == ItemList.carrot || fond == ItemList.wheat;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
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
        } else if (this.isRideable() && !this.healMob(player, heldItem)) {
            player.mountEntity(this);
            this.navigator.clearPath();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.porkCooked.id : ItemList.porkRaw.id;
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
}
