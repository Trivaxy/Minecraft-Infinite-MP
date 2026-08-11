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

public class Moose extends Animal {
    public Moose(World world) {
        super(world);
        this.setSize(1.3F, 1.7F);
        this.setHealth(20);
        this.texture = "/mob/elkir.png";
        this.attackStrength = 5;
        this.scoreValue = 75;
        this.moveSpeed = 0.2F;
        this.knockback = 1.2F;
        this.stepHeight = 1.0F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new ChargeAt(this, this.moveSpeed * 2.0F, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public int getSpawnID() {
        return 71;
    }

    @Override
    public String getSound() {
        return "mob.moose";
    }

    @Override
    protected String getHurtSound() {
        return "mob.moosehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.moosehurt";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.goatstep", 0.45F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.muttonCooked.id : ItemList.muttonRaw.id;
    }
}
