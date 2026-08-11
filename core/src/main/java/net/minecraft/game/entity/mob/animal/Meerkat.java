package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Meerkat extends Animal {
    public Meerkat(World world) {
        super(world);
        this.setSize(0.8F, 0.9F);
        this.setHealth(6);
        this.moveSpeed = 0.25F;
        this.texture = "/mob/meerkat.png";
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(4, new Meerkat.ScoutArea(this));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
    }

    @Override
    public int getSpawnID() {
        return 33;
    }

    @Override
    public String getSound() {
        return "mob.mice";
    }

    @Override
    protected String getHurtSound() {
        return "mob.micehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.micedeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.rabbithop", 0.75F, 1.0F);
    }

    @Override
    public int getDrop() {
        return ItemList.tail.id;
    }

    private class ScoutArea extends LookAround {
        private ScoutArea(Mob host) {
            super(host);
        }

        @Override
        public boolean canExecute() {
            return this.host.navigator.noPath() && super.canExecute();
        }

        @Override
        public void start() {
            super.start();
            this.idleTime = 100 + this.host.rand.nextInt(60);
            this.host.sitting = true;
        }

        @Override
        public void stop() {
            this.host.sitting = false;
        }
    }
}
