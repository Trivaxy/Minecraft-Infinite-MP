package net.minecraft.game.entity.mob.monster.skeleton;

import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class Lich extends Monster {
    protected int passiveTimer;

    public Lich(World world) {
        super(world);
        this.setSize(0.8F, 2.34F);
        this.setHealth(30);
        this.heldItem = ItemList.swordFrost;
        this.texture = "/mob/skeleton/stray.png";
        this.attackStrength = 8;
        this.moveSpeed = 0.25F;
        this.scoreValue = 200;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new FleeFrom(this, Wolf.class, 8.0F, this.moveSpeed, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed + this.moveSpeed / 2.0F, false, EffectList.slowness.id, 100, 1, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new Lich.DetectPlayer(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Lich.DetectPlayer(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.passiveTimer > 0) {
            this.passiveTimer--;
        }

        this.freeze = 0;
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public int getSpawnID() {
        return 49;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getDrop() {
        return ItemList.bone.id;
    }

    @Override
    public final String getSound() {
        return "mob.skeleton";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.skeletonhurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.skeletondeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.skeletonstep", 0.15F, 1.0F);
    }

    private class DetectPlayer extends DetectEnemy {
        private Lich lich;

        public DetectPlayer(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.lich = (Lich)target;
        }

        @Override
        public boolean canExecute() {
            return this.lich.passiveTimer <= 0 && super.canExecute();
        }
    }
}
