package net.minecraft.game.entity.mob.monster.arachnid;

import net.minecraft.game.block.Block;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.LeapAt;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Spider extends Monster {
    public Spider(World world) {
        super(world);
        this.setSize(1.4F, 0.9F);
        this.setHealth(20);
        this.moveSpeed = 0.3F;
        this.scoreValue = 130;
        this.type = (byte)this.rand.nextInt(4);
        this.setType();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new FleeFrom(this, Fox.class, 8.0F, this.moveSpeed, this.moveSpeed * 2.0F));
        this.behaviors.addTask(2, new LeapAt(this, 0.4F));
        this.behaviors.addTask(3, new Spider.SpiderAttack(this, this.moveSpeed, true));
        this.behaviors.addTask(4, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(5, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(6, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new Spider.SpiderAggro(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new Spider.SpiderAggro(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/spider/spider_brown.png";
        } else {
            this.texture = "/mob/spider/spider.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 6;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.ARTHROPOD;
    }

    @Override
    public int getDrop() {
        return ItemList.silk.id;
    }

    @Override
    public final String getSound() {
        return "mob.spider";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.spider";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.spiderdeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.spiderstep", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public boolean canClimb(Block block) {
        return this.collidedHorizontally;
    }

    @Override
    public boolean canBeSlowed() {
        return false;
    }

    public class SpiderAggro extends DetectEnemy {
        public SpiderAggro(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
        }

        @Override
        public boolean canExecute() {
            return this.src.getBrightness(1.0F) >= 0.5F ? false : super.canExecute();
        }
    }

    public class SpiderAttack extends Attack {
        public SpiderAttack(Mob target, float range, boolean chase) {
            super(target, range, chase, false);
        }

        @Override
        public void tick() {
            float brightness = this.attacker.getBrightness(1.0F);
            if (brightness > 0.5F && this.attacker.rand.nextInt(100) == 0) {
                this.attacker.attackTarget = null;
            } else {
                if (this.attacker.rand.nextInt(10) == 0 && this.attacker.world.settings.getDifficulty() > 1 && this.attacker.onGround) {
                    double distX = this.target.posX - this.attacker.posX;
                    double distZ = this.target.posZ - this.attacker.posZ;
                    float dist = MathHelper.sqrt_double(distX * distX + distZ * distZ);
                    this.attacker.motionX = distX / dist * 0.5 * 0.8F + this.attacker.motionX * 0.2F;
                    this.attacker.motionZ = distZ / dist * 0.5 * 0.8F + this.attacker.motionZ * 0.2F;
                    this.attacker.lookControl.faceEntity(this.attacker.attackTarget, 30.0F, 30.0F);
                    this.attacker.jump();
                }

                if (this.target.posY > this.attacker.posY) {
                    this.attacker.moveControl.moveTo(this.target.posX, this.target.posY, this.target.posZ, this.attacker.moveSpeed);
                }

                super.tick();
            }
        }
    }
}
