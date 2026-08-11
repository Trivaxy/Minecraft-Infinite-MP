package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Turtle extends Animal {
    public Turtle(World world) {
        super(world);
        this.setSize(0.8F, 0.7F);
        this.moveSpeed = 0.1F;
        this.texture = "/mob/turtle.png";
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed * 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public int getSpawnID() {
        return 31;
    }

    @Override
    public String getSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "mob.turtlehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.turtledeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.rabbithop", 0.5F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public int specialDrop() {
        return ItemList.shell.id;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }
}
