package net.minecraft.game.entity.mob.monster.elemental;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Blaze extends Monster {
    private float rotations = 0.5F;
    private int timeAttacks;
    protected boolean burns;

    public Blaze(World world) {
        super(world);
        this.texture = "/mob/elemental/fire_elemental.png";
        this.attackStrength = 6;
        this.scoreValue = 120;
        this.moveSpeed = 0.23F;
        this.produceFootsteps = false;
        this.burns = this.canGlow = true;
        this.behaviors.addTask(3, new Blaze.FireballAttack(this));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.FLAME;
    }

    @Override
    public int getSpawnID() {
        return 58;
    }

    @Override
    public String getSound() {
        return "mob.blaze.breathe";
    }

    @Override
    protected String getHurtSound() {
        return "mob.blaze.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.blaze.death";
    }

    @Override
    public boolean canBurn() {
        return !this.burns;
    }

    @Override
    public void mobTick() {
        if (!this.world.isOnline) {
            if (this.isInWater() && this.burns) {
                this.damageEntity(DamageSource.drown, 1);
            }

            this.timeAttacks--;
            if (this.timeAttacks <= 0) {
                this.timeAttacks = 100;
                this.rotations = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
            }

            if (this.attackTarget != null && this.attackTarget.posY + this.attackTarget.getEyeHeight() > this.posY + this.getEyeHeight() + this.rotations) {
                this.motionY = this.motionY + (0.3F - this.motionY) * 0.3F;
            }
        }

        if (this.rand.nextInt(24) == 0 && this.burns) {
            this.world
                .playSoundEffect(
                    this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F
                );
        }

        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        this.spawnParticles();
        super.mobTick();
    }

    protected void spawnParticles() {
        for (int i = 0; i < 2; i++) {
            this.world
                .spawnParticle(
                    ParticleType.SMOKE,
                    this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                    this.posY + this.rand.nextDouble() * this.height,
                    this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                    0.0,
                    0.0,
                    0.0
                );
        }

        this.world
            .spawnParticle(
                ParticleType.FLAME,
                this.posX + this.rand.nextDouble() * this.width * 2.0 - this.width,
                this.posY + this.rand.nextDouble() * this.height,
                this.posZ + this.rand.nextDouble() * this.width * 2.0 - this.width,
                0.0,
                0.0,
                0.0
            );
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public int getDrop() {
        return ItemList.fireball.id;
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.settings.getDifficulty() > 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    protected class FireballAttack extends Action {
        private Blaze blaze;
        private int shotCount;
        private int attackDelay;

        protected FireballAttack(Blaze blaze) {
            this.blaze = blaze;
            this.setMutexBits(3);
        }

        @Override
        public boolean canExecute() {
            return this.blaze.attackTarget != null && this.blaze.attackTarget.isAlive();
        }

        @Override
        public void start() {
            this.shotCount = 0;
        }

        @Override
        public void stop() {
            this.blaze.fire = 0;
        }

        @Override
        public void tick() {
            this.attackDelay--;
            Mob target = this.blaze.attackTarget;
            double dist = this.blaze.getDistanceSqTo(target);
            if (dist < 4.0) {
                if (this.attackDelay <= 0) {
                    this.attackDelay = 20;
                    this.blaze.damageEntity(DamageSource.mobDamage(this.blaze), this.blaze.attackStrength);
                }

                this.blaze.moveControl.moveTo(target.posX, target.posY, target.posZ, 1.0F);
            } else if (dist < 256.0) {
                double distX = target.posX - this.blaze.posX;
                double distY = target.boundingBox.minY + target.height / 2.0F - (this.blaze.posY + this.blaze.height / 2.0F);
                double distZ = target.posZ - this.blaze.posZ;
                if (this.attackDelay <= 0) {
                    this.shotCount++;
                    if (this.shotCount == 1) {
                        this.attackDelay = 60;
                        if (this.blaze.burns) {
                            this.blaze.fire = 60;
                        }
                    } else if (this.shotCount <= 4) {
                        this.attackDelay = 6;
                    } else {
                        this.attackDelay = 100;
                        this.shotCount = 0;
                        if (this.blaze.burns) {
                            this.blaze.fire = 0;
                        }
                    }

                    if (this.shotCount > 1) {
                        float offset = MathHelper.sqrt_float(MathHelper.sqrt_double(dist)) * 0.5F;
                        if (!this.blaze.burns) {
                            this.blaze.world.playEntitySound(this.blaze, "random.breath", 10.0F, 1.0F / (this.blaze.rand.nextFloat() * 0.4F + 0.8F));
                        } else {
                            this.blaze.world.playEntitySound(this.blaze, "mob.ghast.shoot", 10.0F, 1.0F / (this.blaze.rand.nextFloat() * 0.4F + 0.8F));
                        }

                        Projectile fireball = new Fireball(
                            this.blaze.world,
                            this.blaze,
                            distX + this.blaze.rand.nextGaussian() * offset,
                            distY,
                            distZ + this.blaze.rand.nextGaussian() * offset,
                            5
                        );
                        if (!this.blaze.burns) {
                            fireball = new Windball(
                                this.blaze.world,
                                this.blaze,
                                distX + this.blaze.rand.nextGaussian() * offset,
                                distY,
                                distZ + this.blaze.rand.nextGaussian() * offset,
                                4
                            );
                        }

                        fireball.posY = this.blaze.posY + this.blaze.height / 2.0F + 0.5;
                        this.blaze.world.spawnEntity(fireball);
                    }
                }

                this.blaze.lookControl.faceEntity(target, 10.0F, 10.0F);
            } else {
                this.blaze.navigator.clearPath();
                this.blaze.moveControl.moveTo(target.posX, target.posY, target.posZ, 1.0F);
            }
        }
    }
}
