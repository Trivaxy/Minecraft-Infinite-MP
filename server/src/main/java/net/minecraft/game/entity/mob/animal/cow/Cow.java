package net.minecraft.game.entity.mob.animal.cow;

import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.util.math.MathHelper;

public class Cow extends Animal {
    public Cow(World world) {
        super(world);
        this.setSize(0.9F, 1.4F);
        this.setType();
        this.moveSpeed = 0.2F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed * 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 0:
                this.texture = "/mob/cow/cow_black.png";
                break;
            case 1:
            default:
                this.texture = "/mob/cow/cow.png";
                break;
            case 2:
                this.texture = "/mob/cow/cow_bw.png";
                break;
            case 3:
                this.texture = "/mob/cow/cow_brown.png";
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
        return 2;
    }

    @Override
    public String getSound() {
        return "mob.cow";
    }

    @Override
    protected String getHurtSound() {
        return "mob.cowhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.cowhurt";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.cowstep", 0.15F, 1.0F);
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (heldItem != null && heldItem.getItem() == ItemList.shears && this.getShearedDrop() > 0) {
            if (!this.world.isOnline) {
                int count = 2 + this.rand.nextInt(4);
                if (player.getLuckModifier()) {
                    count = 3 + this.rand.nextInt(6);
                }

                for (int i = 0; i < count; i++) {
                    DroppedItem drop = this.dropItem(this.getShearedDrop(), 1, 0, 1.0F);
                    drop.motionY = drop.motionY + this.rand.nextFloat() * 0.05F;
                    drop.motionX = drop.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    drop.motionZ = drop.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                }
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
                player.inventory.getOffHandItem().damageItem(1, player);
            } else {
                player.swingItem();
                player.getCurrentEquippedItem().damageItem(1, player);
            }

            Cow cow = new Cow(this.world);
            cow.health = this.health;
            cow.setPosAndRot(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            cow.yawOffset = this.yawOffset;
            cow.type = 0;
            cow.setType();
            this.world.spawnEntity(cow);
            this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            this.setDead();
            return true;
        } else {
            return false;
        }
    }

    public int getShearedDrop() {
        return 0;
    }
}
