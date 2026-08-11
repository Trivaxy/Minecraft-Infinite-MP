package net.minecraft.game.entity.mob.animal.flying;

import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Ostrich extends FlyingMob {
    public Ostrich(World world) {
        super(world);
        this.texture = "/mob/ostrich.png";
        this.moveSpeed = 0.3F;
        this.setSize(0.8F, 1.8F);
        this.setHealth(16);
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 3.0F));
        this.behaviors.addTask(3, new FleeFrom(this, Wolf.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 3.0F));
        this.behaviors.addTask(4, new FleeFrom(this, Player.class, 16.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 3.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public int getSpawnID() {
        return 23;
    }

    @Override
    public float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public String getSound() {
        return "mob.ostrich";
    }

    @Override
    protected String getHurtSound() {
        return "mob.ostrichhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.ostrichdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.chimkenCooked.id : ItemList.chimkenRaw.id;
    }

    @Override
    public int specialDrop() {
        return ItemList.ostrichFeather.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(3) + 1;
    }
}
