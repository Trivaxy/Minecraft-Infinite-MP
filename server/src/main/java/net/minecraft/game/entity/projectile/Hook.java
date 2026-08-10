package net.minecraft.game.entity.projectile;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Hook extends Projectile {
    public Entity hookedEntity;
    public int blockX;
    public int blockY;
    public int blockZ;
    public double hookHeight;
    public double hookDist;
    public double hookAngle;
    public double radius = 0.0;
    public boolean returning = false;

    public Hook(World world) {
        super(world);
    }

    public Hook(World world, Mob mob) {
        super(world, mob, 0);
        this.setSize(0.6F, 0.6F);
        this.world
            .playSoundEffect(
                this.posX, this.posY, this.posZ, "random.hook", 0.5F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F
            );
        if (mob instanceof Player) {
            ((Player)mob).hook = this;
        }
    }

    @Override
    protected void init() {
        this.dataSync.addObject(10, (byte)0);
    }

    public void setReeling(boolean b) {
        this.dataSync.updateObject(10, Byte.valueOf((byte)(b ? 1 : 0)));
    }

    public boolean isReeling() {
        return this.dataSync.getObjectByte(10) == 1;
    }

    @Override
    public void tick() {
        if ((this.thrower == null || !this.thrower.isDead) && this.world.blockExists((int)this.posX, (int)this.posY, (int)this.posZ)) {
            super.tick();
            if (this.hookedEntity != null) {
                float angle = (float)((this.hookAngle + this.hookedEntity.yaw) * 3.1416F / 180.0);
                this.posX = this.hookedEntity.posX - MathHelper.sin(angle) * this.hookDist;
                this.posY = this.hookedEntity.posY + this.hookHeight;
                this.posZ = this.hookedEntity.posZ + MathHelper.cos(angle) * this.hookDist;
                this.hookedEntity.motionX *= 0.1;
                this.hookedEntity.motionZ *= 0.1;
                if (!this.hookedEntity.isAlive()) {
                    this.hookedEntity = null;
                    this.setReeling(false);
                }
            }

            if (this.isReeling()) {
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            if (this.thrower != null) {
                double dist = this.getDistanceTo(this.thrower);
                if (dist > this.radius) {
                    this.thrower.fallDistance /= 2.0F;
                }

                if (this.thrower instanceof Player) {
                    if (this.thrower.sneaking && this.radius > 0.0) {
                        this.radius -= 0.4;
                        if (this.isReeling() && this.thrower instanceof Player) {
                            this.radius--;
                            if (dist >= this.radius) {
                                double distY = this.posY - this.thrower.posY;
                                if (distY > 0.0) {
                                    this.thrower.motionY = this.thrower.motionY + Math.max(0.02, distY * 0.1);
                                }

                                double maxMotion;
                                if (this.thrower.motionY > (maxMotion = Math.min(1.0, Math.max(1.0, distY / 10.0)))) {
                                    this.thrower.motionY = maxMotion;
                                }
                            }
                        }
                    }
                } else {
                    this.radius -= 1.2;
                }

                if ((this.thrower.swing > 0.0F || this.thrower.isSprinting()) && this.radius < this.getMaxRadius()) {
                    this.radius += 0.5;
                }

                if (this.isReeling()) {
                    if (!this.world.isOnline && this.world.isAir(this.blockX, this.blockY, this.blockZ) && this.hookedEntity == null) {
                        this.setReeling(false);
                    }

                    boolean pullToShooter = false;
                    if (this.hookedEntity != null) {
                        if (this.hookedEntity instanceof Player && this.hookedEntity.sneaking) {
                            this.setDead();
                        }

                        float ridenSize = this.hookedEntity.width * 2.0F + this.hookedEntity.height;
                        float shootingSize = this.thrower.width * 2.0F + this.thrower.height + 0.2F;

                        for (int i = 1; i <= 4; i++) {
                            shootingSize += 5.0F;
                        }

                        if (shootingSize > ridenSize && this.hookedEntity.isPushable()) {
                            pullToShooter = true;
                        }
                    }

                    double pullSpeed = 0.0;
                    this.radius = Math.max(this.thrower.height, this.radius - Math.min(0.6, this.radius / 30.0));
                    pullSpeed = dist - 0.2;
                    pullSpeed -= this.radius;
                    pullSpeed = MathHelper.clamp(pullSpeed, 0.0, 0.8);
                    double pull = 1.0;
                    if (this.hookedEntity != null) {
                        pull = 1.0;
                    }

                    if (pullSpeed > pull) {
                        pullSpeed = pull;
                    }

                    Vector facingVec = Vector.createVector(this.thrower.posX - this.posX, this.thrower.posY - this.posY, this.thrower.posZ - this.posZ);
                    facingVec.normalize();
                    double var15;
                    facingVec.xCoord = facingVec.xCoord * (var15 = pullSpeed * 0.03);
                    facingVec.zCoord *= var15;
                    facingVec.yCoord *= var15;
                    if (pullToShooter) {
                        if (dist > this.radius || this.radius < 1.0) {
                            this.hookedEntity.motionX = facingVec.xCoord;
                            this.hookedEntity.motionZ = facingVec.zCoord;
                        }
                    } else {
                        this.thrower.motionX = this.thrower.motionX - facingVec.xCoord;
                        this.thrower.motionZ = this.thrower.motionZ - facingVec.zCoord;
                        this.thrower.motionY = this.thrower.motionY - facingVec.yCoord;
                        if (this.thrower.motionX > 1.0) {
                            this.thrower.motionX = 1.0;
                        }

                        if (this.thrower.motionZ > 1.0) {
                            this.thrower.motionZ = 1.0;
                        }

                        if (this.thrower.motionY > 1.0) {
                            this.thrower.motionY = 1.0;
                        }
                    }
                } else if (dist > this.getMaxRadius()) {
                    this.returning = true;
                }

                if (this.returning && !this.isReeling() && dist <= this.thrower.width + this.thrower.height) {
                    this.setDead();
                }
            }
        } else {
            this.setDead();
        }
    }

    @Override
    protected boolean impactsOnClient() {
        return true;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (!this.isReeling()) {
            if (mop.target == TargetingSystem.HitType.TILE && !this.world.isOnline) {
                this.blockX = mop.blockX;
                this.blockY = mop.blockY;
                this.blockZ = mop.blockZ;
                this.setPosition(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
                this.onGround = true;
            }

            if (mop.entityHit instanceof Entity && !this.world.isOnline) {
                if (mop.entityHit instanceof Projectile) {
                    return;
                }

                if (mop.entityHit != this.thrower) {
                    this.hookedEntity = mop.entityHit;
                    this.hookHeight = Math.min(this.hookedEntity.height, this.posY - this.hookedEntity.posY);
                    this.hookAngle = Math.atan2(this.hookedEntity.posZ - this.posZ, this.hookedEntity.posX - this.posX) * 180.0 / Math.PI + 90.0;
                    this.hookAngle = this.hookAngle - this.hookedEntity.yaw;
                    this.hookDist = Math.min(
                        this.hookedEntity.width, Math.sqrt(Math.abs(this.hookedEntity.posX - this.posX + this.hookedEntity.posZ - this.posZ))
                    );
                    this.returning = true;
                    this.onGround = true;
                    if (this.thrower instanceof Player && mop.entityHit instanceof FallingBlock) {
                        ((Player)this.thrower).triggerAchievement(AchievementList.spiderman);
                    }
                }
            }

            if (!this.world.isOnline && this.onGround) {
                this.setReeling(true);
                this.radius++;
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.setDead();
    }

    @Override
    public void setDead() {
        if (this.thrower instanceof Player) {
            ((Player)this.thrower).hook = null;
        }

        super.setDead();
    }

    public double getRadius() {
        return this.radius;
    }

    public int getMaxRadius() {
        return 40;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }
}
