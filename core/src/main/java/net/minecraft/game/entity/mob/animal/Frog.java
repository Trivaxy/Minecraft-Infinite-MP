package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.passive.JumpAround;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Frog extends Animal {
    public Frog(World world) {
        super(world);
        this.setSize(0.7F, 0.4F);
        this.setHealth(8);
        this.moveSpeed = 0.35F;
        this.scoreValue = 30;
        this.type = (byte)this.rand.nextInt(4);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new JumpAround(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed, false, EffectList.poison.id, 100, 0, true));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/frog/frog_blue.png";
                break;
            case 2:
                this.texture = "/mob/frog/frog_yellow.png";
                break;
            case 3:
                this.texture = "/mob/frog/frog_black.png";
                break;
            default:
                this.texture = "/mob/frog/frog.png";
        }
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public int getSpawnID() {
        return 30;
    }

    @Override
    public int getDrop() {
        return ItemList.slimeBall.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.tongue.id;
    }

    @Override
    public String getSound() {
        return "mob.frog";
    }

    @Override
    protected String getHurtSound() {
        return "mob.froghurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.frogdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.spiderstep", 0.15F, 1.0F);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }
}
