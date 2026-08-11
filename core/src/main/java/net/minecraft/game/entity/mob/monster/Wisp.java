package net.minecraft.game.entity.mob.monster;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.util.math.MathHelper;

public class Wisp extends Monster {
    public Wisp(World world) {
        super(world);
        this.texture = "/mob/slime/slime_blue.png";
        this.produceFootsteps = false;
        this.moveSpeed = 0.23F;
        this.scoreValue = 125;
        this.canGlow = true;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Wisp.RunAway(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(3, new Wisp.FrostAttack(this, this.moveSpeed, false, true));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new Wisp.DanceAround(this, 16.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
    }

    @Override
    public int getSpawnID() {
        return 66;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        this.freeze = 0;
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        if (this.attackTarget != null) {
            this.world
                .spawnParticle(
                    ParticleType.SPORESPINK,
                    this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                    this.posY + this.rand.nextDouble() * this.height,
                    this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                    0.0,
                    0.0,
                    0.0
                );
            this.texture = "/mob/slime/slime_pink.png";
        } else {
            this.world
                .spawnParticle(
                    ParticleType.SPORESBLUE,
                    this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                    this.posY + this.rand.nextDouble() * this.height,
                    this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                    0.0,
                    0.0,
                    0.0
                );
            this.texture = "/mob/slime/slime_blue.png";
        }
    }

    @Override
    public boolean damageEntity(DamageSource ds, int amount) {
        if (super.damageEntity(ds, amount)) {
            if (this.health <= this.maxHealth / 2) {
                this.addEffect(new EffectData(EffectList.invisibility.id, 100, 0));
                this.attackTarget = this.lastTarget = null;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.settings.getDifficulty() > 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public int getDrop() {
        return ItemList.extract.id;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void knockBack(Entity entity, double force, double knockback) {
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public String getSound() {
        return "mob.fairy";
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    private class DanceAround extends Action {
        private Mob host;
        private Mob target;
        private Path path;
        private float range;
        private float facing;

        public DanceAround(Mob host, float f) {
            this.host = host;
            this.range = f;
            this.facing = 0.02F;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            if (!(this.host.rand.nextFloat() >= this.facing) && this.host.lastTarget == null && this.host.rider == null) {
                this.target = this.host.world.getClosestPlayer(this.host, this.range);
                if (this.target == null) {
                    return false;
                }

                this.path = this.host.navigator.pathToTarget(this.target);
                return this.path != null;
            } else {
                return false;
            }
        }

        @Override
        public boolean continueExecuting() {
            return this.target.isAlive() && this.host.lastTarget == null ? !(this.host.getDistanceSqTo(this.target) > this.range * this.range) : false;
        }

        @Override
        public void start() {
            this.host.navigator.clearPath();
            this.host.navigator.setPath(this.path, this.host.moveSpeed);
        }

        @Override
        public void stop() {
            this.target = null;
            this.host.navigator.clearPath();
        }

        @Override
        public void tick() {
            if (this.target != null && !(this.host.getDistanceSqTo(this.target) > 9.0) && this.host.senses.canSee(this.target)) {
                double distX = this.target.posX - this.host.posX;
                double distZ = this.target.posZ - this.host.posZ;
                float facing = this.host.headFacing;
                this.host.headFacing = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
                float angle = (facing - this.host.headFacing + 90.0F) * (float) Math.PI / 180.0F;
                this.host.moveStrafing = MathHelper.sin(angle) * this.host.moveForward;
                this.host.lookControl.faceEntity(this.target, 30.0F, 30.0F);
            } else {
                this.host.moveStrafing = 0.0F;
            }
        }
    }

    private class FrostAttack extends Attack {
        private FrostAttack(Mob src, float spd, boolean priority, boolean sprint) {
            super(src, spd, priority, sprint);
        }

        @Override
        protected void attack() {
            super.attack();
            this.target.freeze = 100;
        }
    }

    private class RunAway extends Panic {
        private RunAway(Mob animal, float f) {
            super(animal, f);
        }

        @Override
        public boolean canExecute() {
            return this.laCreatura.health < this.laCreatura.maxHealth / 2 && super.canExecute();
        }
    }
}
