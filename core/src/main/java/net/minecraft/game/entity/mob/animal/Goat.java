package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.ChargeAt;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Goat extends Animal {
    public Goat(World world) {
        super(world);
        this.setSize(0.8F, 1.1F);
        this.setHealth(16);
        this.attackStrength = 4;
        this.scoreValue = 40;
        this.knockback = 1.2F;
        this.stepHeight = 1.0F;
        this.moveSpeed = 0.23F;
        this.type = (byte)this.rand.nextInt(3);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new ChargeAt(this, this.moveSpeed * 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/goat/goat_white.png";
                break;
            case 2:
                this.texture = "/mob/goat/goat_brown.png";
                break;
            default:
                this.texture = "/mob/goat/goat_grey.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 20;
    }

    @Override
    public String getSound() {
        return "mob.goat";
    }

    @Override
    protected String getHurtSound() {
        return "mob.goathurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.goatdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.goatstep", 0.45F, 1.0F);
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
    public int specialDrop() {
        return ItemList.horn.id;
    }
}
