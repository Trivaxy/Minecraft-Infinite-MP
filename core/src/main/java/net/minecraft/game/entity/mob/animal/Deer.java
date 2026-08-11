package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Deer extends Animal {
    public Deer(World world) {
        super(world);
        this.setSize(0.8F, 1.3F);
        this.setHealth(14);
        this.moveSpeed = 0.35F;
        this.scoreValue = 25;
        this.type = (byte)this.rand.nextInt(2);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(4, new FleeFrom(this, Player.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 4.0F));
        this.behaviors.addTask(4, new FleeFrom(this, Wolf.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 4.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/deer/deer.png";
        } else {
            this.texture = "/mob/deer/deerb.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 19;
    }

    @Override
    public String getSound() {
        return "mob.deer";
    }

    @Override
    protected String getHurtSound() {
        return "mob.deerhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.deerdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.goatstep", 0.35F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.muttonCooked.id : ItemList.muttonRaw.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
    }
}
