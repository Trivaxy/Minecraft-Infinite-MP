package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.world.World;

public class Golem extends Monster {
    public Golem(World world) {
        super(world);
        this.setHealth(50);
        this.setSize(1.4F, 2.7F);
        this.texture = "/mob/golem.png";
        this.attackStrength = 10;
        this.knockback = 1.2F;
        this.scoreValue = 500;
        this.moveSpeed = 0.2F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Attack(this, this.moveSpeed * 2.0F, false, false));
        this.behaviors.addTask(3, new Golem.FireProjectile(this));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void knockBack(Entity entity, double force, double x) {
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public int getSpawnID() {
        return 59;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getDrop() {
        return BlockList.obsidian.id;
    }

    @Override
    public final String getSound() {
        return "mob.golem";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.golemhurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.golemhurt";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.golemstep", 1.0F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    public class FireProjectile extends Action {
        private Mob golem;
        private int attackTime;

        public FireProjectile(Golem golem) {
            this.golem = golem;
        }

        @Override
        public void tick() {
            if (this.attackTime > 0) {
                this.attackTime--;
            }

            double distX = this.golem.attackTarget.posX - this.golem.posX;
            double distZ = this.golem.attackTarget.posZ - this.golem.posZ;
            if (this.golem.getDistanceSq(this.golem.attackTarget.posX, this.golem.attackTarget.boundingBox.minY, this.golem.attackTarget.posZ) > 12.0
                && this.attackTime == 0) {
                Projectile fireball = new Fireball(this.golem.world, this.golem, 10);
                fireball.posY++;
                fireball.setPosition(this.golem.posX, this.golem.posY + this.golem.getEyeHeight(), this.golem.posZ);
                double ballHeight = this.golem.attackTarget.posY + this.golem.attackTarget.getEyeHeight() - 1.1F - fireball.posY;
                this.golem.world.playEntitySound(this.golem, "random.bow", 1.0F, 1.0F / (this.golem.rand.nextFloat() * 0.4F + 0.8F));
                this.golem.world.spawnEntity(fireball);
                fireball.setHeading(distX, ballHeight, distZ, 1.0F, 12.0F);
                this.attackTime = 60;
            }
        }

        @Override
        public boolean canExecute() {
            return this.golem.attackTarget != null && this.golem.navigator.noPath();
        }

        @Override
        public void start() {
            this.attackTime = 60;
        }
    }
}
