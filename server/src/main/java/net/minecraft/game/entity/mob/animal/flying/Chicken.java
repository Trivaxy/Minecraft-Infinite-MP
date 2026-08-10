package net.minecraft.game.entity.mob.animal.flying;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.util.math.MathHelper;

public class Chicken extends FlyingMob {
    private String sndFile = "mob.chicken";
    private String hurtSnd = "mob.chickenhurt";
    public int eggTimer;

    public Chicken(World world) {
        super(world);
        this.setSize(0.6F, 0.7F);
        this.setHealth(4);
        this.eggTimer = this.rand.nextInt(6000) + 6000;
        this.moveSpeed = 0.25F;
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public void setType() {
        if (this.type == 4) {
            this.sndFile = "mob.duck";
            this.hurtSnd = "mob.duckhurt";
        } else {
            this.sndFile = "mob.chicken";
            this.hurtSnd = "mob.chickenhurt";
        }

        switch (this.type) {
            case 0:
                this.texture = "/mob/chicken/chicken_black.png";
                break;
            case 1:
            default:
                this.texture = "/mob/chicken/chicken.png";
                break;
            case 2:
                this.texture = "/mob/chicken/chicken_brown.png";
                break;
            case 3:
                this.texture = "/mob/chicken/chicken_yellow.png";
                break;
            case 4:
                this.texture = "/mob/chicken/duck.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        Biome biome = this.world.getBiomeAt(x, z);
        if (this.rand.nextInt(5) == 0) {
            this.type = 4;
        } else {
            this.type = biome.getTemperature();
        }

        this.setType();
    }

    @Override
    public int getSpawnID() {
        return 3;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.world.isOnline && --this.eggTimer <= 0) {
            this.world.playEntitySound(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(ItemList.egg.id, 1);
            this.eggTimer = this.rand.nextInt(6000) + 6000;
        }
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public String getSound() {
        return this.sndFile;
    }

    @Override
    protected String getHurtSound() {
        return this.hurtSnd;
    }

    @Override
    protected String getDeathSound() {
        return this.hurtSnd;
    }

    @Override
    public int getDrop() {
        return ItemList.feather.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("eggTime", this.eggTimer);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.eggTimer = tag.getInteger("eggTime");
    }
}
