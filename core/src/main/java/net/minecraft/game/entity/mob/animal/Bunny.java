package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.JumpAround;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Bunny extends Animal {
    public Bunny(World world) {
        super(world);
        this.setSize(0.6F, 0.5F);
        this.setHealth(3);
        this.moveSpeed = 0.35F;
        this.type = (byte)this.rand.nextInt(4);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(2, new JumpAround(this));
        this.behaviors.addTask(4, new FleeFrom(this, Fox.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(4, new FleeFrom(this, Ocelot.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/bunny/bunny_night.png";
                break;
            case 2:
                this.texture = "/mob/bunny/bunny_winter.png";
                break;
            case 3:
                this.texture = "/mob/bunny/bunny_grey.png";
                break;
            default:
                this.texture = "/mob/bunny/bunny.png";
        }
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public int getSpawnID() {
        return 11;
    }

    @Override
    public int getDrop() {
        return ItemList.leather.id;
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (!this.world.isOnline) {
            if (this.fire > 0) {
                this.dropItem(ItemList.rabbitCooked.id, 1);
            } else {
                this.dropItem(ItemList.rabbitRaw.id, 1);
            }
        }
    }

    @Override
    public String getSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "mob.rabbithurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.rabbitdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.rabbithop", 0.55F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public double getYOffset() {
        return -0.7;
    }

    @Override
    public boolean interact(Player player) {
        this.mountEntity(player);
        player.swingItem();
        return false;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }
}
