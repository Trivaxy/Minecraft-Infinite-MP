package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.ChooseTarget;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class Guardian extends Monster {
    public Guardian(World world) {
        super(world);
        this.setSize(0.8F, 2.34F);
        this.texture = "/mob/angel.png";
        this.attackStrength = 10;
        this.scoreValue = 1000;
        this.boss = true;
        this.moveSpeed = 0.23F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed, false, false));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.detections.addTask(2, new Guardian.DetectPlayer(this, Player.class, 256.0F, true));
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getSpawnID() {
        return 46;
    }

    @Override
    public void mobTick() {
        if (!this.visible) {
            super.mobTick();
            if (this.attackTarget != null
                && (this.navigator.getPath() == null || this.navigator.getPath().isFinished())
                && this.getDistanceTo(this.attackTarget) > 16.0F) {
                int facing = MathHelper.floor_double(this.attackTarget.yaw * 4.0F / 360.0F + 0.5) & 3;
                int dist = 3;
                switch (facing) {
                    case 0:
                        if (this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY),
                                        MathHelper.floor_double(this.attackTarget.posZ - dist)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY + 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ - dist)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY - 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ - dist)
                                    )
                                == BlockList.stoneBrick.id) {
                            this.setPosition(
                                MathHelper.floor_double(this.attackTarget.posX),
                                MathHelper.floor_double(this.attackTarget.posY),
                                MathHelper.floor_double(this.attackTarget.posZ - dist)
                            );
                            this.world.playRandomAmbience("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        }
                        break;
                    case 1:
                        if (this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX - dist),
                                        MathHelper.floor_double(this.attackTarget.posY),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX - dist),
                                        MathHelper.floor_double(this.attackTarget.posY + 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX - dist),
                                        MathHelper.floor_double(this.attackTarget.posY - 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == BlockList.stoneBrick.id) {
                            this.setPosition(
                                MathHelper.floor_double(this.attackTarget.posX - dist),
                                MathHelper.floor_double(this.attackTarget.posY),
                                MathHelper.floor_double(this.attackTarget.posZ)
                            );
                            this.world.playRandomAmbience("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        }
                        break;
                    case 2:
                        if (this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY),
                                        MathHelper.floor_double(this.attackTarget.posZ + dist)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY + 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ + dist)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX),
                                        MathHelper.floor_double(this.attackTarget.posY - 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ + dist)
                                    )
                                == BlockList.stoneBrick.id) {
                            this.setPosition(
                                MathHelper.floor_double(this.attackTarget.posX),
                                MathHelper.floor_double(this.attackTarget.posY),
                                MathHelper.floor_double(this.attackTarget.posZ + dist)
                            );
                            this.world.playRandomAmbience("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        }
                        break;
                    case 3:
                        if (this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX + dist),
                                        MathHelper.floor_double(this.attackTarget.posY),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX + dist),
                                        MathHelper.floor_double(this.attackTarget.posY + 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == 0
                            && this.world
                                    .getBlockId(
                                        MathHelper.floor_double(this.attackTarget.posX + dist),
                                        MathHelper.floor_double(this.attackTarget.posY - 1.0),
                                        MathHelper.floor_double(this.attackTarget.posZ)
                                    )
                                == BlockList.stoneBrick.id) {
                            this.setPosition(
                                MathHelper.floor_double(this.attackTarget.posX + dist),
                                MathHelper.floor_double(this.attackTarget.posY),
                                MathHelper.floor_double(this.attackTarget.posZ)
                            );
                            this.world.playRandomAmbience("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        }
                }
            }
        } else {
            this.jumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            float movementFactor = this.landSpeed;
            this.landSpeed = this.landSpeed * this.applySpeedModifier();
            this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
            this.landSpeed = movementFactor;
        }
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        this.setAttacked();
        if (src.getEntity() != null && src.getEntity() instanceof Player) {
            ((Player)src.getEntity()).addChatMessage(Translator.getInstance().translateKey("gui.kill"));
        }

        return false;
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "random.rockslide", 0.55F, 1.0F);
    }

    public class DetectPlayer extends ChooseTarget {
        private Mob target;
        private Class<?> targetClass;

        public DetectPlayer(Mob src, Class<?> target, float dist, boolean check) {
            this(src, target, dist, check, false);
        }

        public DetectPlayer(Mob src, Class<?> target, float dist, boolean check, boolean srch) {
            super(src, dist, check, srch);
            this.targetClass = target;
            this.dist = dist;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            if (this.targetClass == Player.class) {
                this.target = this.src.world.getClosestPlayer(this.src, this.dist);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            if (this.src.calmTime <= 0) {
                this.src.attackTarget = this.target;
            }

            super.start();
        }
    }
}
