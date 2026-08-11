package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.arrow.FlameArrow;
import net.minecraft.game.entity.arrow.FrostArrow;
import net.minecraft.game.entity.arrow.PoisonArrow;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Rock;
import net.minecraft.game.entity.projectile.Sandball;
import net.minecraft.game.entity.projectile.Snowball;
import net.minecraft.game.entity.projectile.Spit;
import net.minecraft.game.entity.projectile.ThrownPotion;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class RangedAttack extends Action {
    protected World world;
    protected Mob source;
    protected Mob target;
    protected int shootTimer = 0;
    protected int attackTimer = 0;
    protected int arrowType;
    protected int maxWaitTime;
    protected float friction;
    private int direction;
    private int[] potionIds = new int[]{13, 14, 15, 16, 18};

    public RangedAttack(Mob src, float friction, int arrow, int timer) {
        this.source = src;
        this.world = src.world;
        this.friction = friction;
        this.arrowType = arrow;
        this.maxWaitTime = timer;
        this.setMutexBits(3);
    }

    @Override
    public boolean canExecute() {
        Mob target = this.source.attackTarget;
        if (target == null) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public void start() {
        this.direction = this.source.rand.nextBoolean() ? 1 : -1;
        this.shootTimer = 20;
    }

    @Override
    public void stop() {
        this.target = null;
        this.source.moveStrafing = 0.0F;
    }

    @Override
    public void tick() {
        double maxDist = 250.0;
        if (this.arrowType < 0) {
            maxDist = 50.0;
        }

        double dist = this.source.getDistanceSq(this.target.posX, this.target.boundingBox.minY, this.target.posZ);
        boolean canSeeTarget = this.source.senses.canSee(this.target);
        if (canSeeTarget) {
            this.attackTimer++;
        } else {
            this.attackTimer = 0;
        }

        if (dist <= maxDist && this.attackTimer >= 20) {
            if (this.world.settings.getDifficulty() >= 2 && this.arrowType >= 0) {
                this.performDodge(dist, maxDist);
            } else {
                this.source.navigator.clearPath();
            }
        } else {
            this.source.navigator.moveToTarget(this.target, this.friction);
            this.source.moveStrafing = 0.0F;
        }

        this.source.lookControl.faceEntity(this.target, 30.0F, 30.0F);
        this.shootTimer = Math.max(this.shootTimer - 1, 0);
        if (this.shootTimer <= 0 && dist <= maxDist && canSeeTarget) {
            this.shootProjectile();
            this.shootTimer = this.maxWaitTime;
        }
    }

    private void performDodge(double dist, double maxDist) {
        double distX = this.target.posX - this.source.posX;
        double distZ = this.target.posZ - this.source.posZ;
        float facing = this.source.headFacing;
        this.source.headFacing = (float)(Math.atan2(distZ, distX) * 180.0 / (float) Math.PI) - 90.0F;
        float angle = (facing - this.source.headFacing + 90.0F) * (float) Math.PI / 180.0F;
        this.source.moveStrafing = this.direction * MathHelper.sin(angle) * this.source.moveForward;
        if (this.source.collidedHorizontally && this.source.onGround) {
            this.source.jump();
        }

        if (dist <= maxDist / 2.0) {
            if (this.source.rand.nextInt(25) == 0) {
                this.source.navigator.moveToTarget(this.target, -this.friction);
            }
        } else if (dist <= maxDist - 1.0 && this.source.rand.nextInt(25) == 0) {
            this.source.navigator.moveToTarget(this.target, this.friction / 2.0F);
            this.direction = this.source.rand.nextBoolean() ? 1 : -1;
        }
    }

    protected void shootProjectile() {
        double distX = this.target.posX - this.source.posX;
        double distZ = this.target.posZ - this.source.posZ;
        float yOffset = MathHelper.sqrt_double(distX * distX + distZ * distZ) * 0.2F;
        if (this.arrowType >= 0) {
            Arrow arrow = new Arrow(this.world, this.source, 1.0F);
            switch (this.arrowType) {
                case 1:
                    arrow = new FlameArrow(this.world, this.source, 1.0F);
                    break;
                case 2:
                    arrow = new FrostArrow(this.world, this.source, 1.0F);
                    break;
                case 3:
                    arrow = new PoisonArrow(this.world, this.source, 1.0F);
            }

            double distY = this.target.posY - 0.2F - arrow.posY;
            this.world.playEntitySound(this.source, "random.bow", 1.0F, 1.0F / (this.source.rand.nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntity(arrow);
            arrow.setHeading(distX, distY + yOffset, distZ, 1.0F, 1.0F);
        } else if (this.arrowType < 0) {
            Projectile projectile = new Snowball(this.world, this.source, 0);
            String snd = "random.throw";
            switch (this.arrowType) {
                case -7:
                    projectile = new ThrownPotion(this.world, this.source, this.potionIds[this.source.rand.nextInt(5)]);
                    break;
                case -6:
                    projectile = new Sandball(this.world, this.source, 6);
                    break;
                case -5:
                    projectile = new Windball(this.world, this.source, 4);
                    snd = "random.breath";
                    break;
                case -4:
                    projectile = new Spit(this.world, this.source, 2);
                    snd = "mob.llamaspit";
                    break;
                case -3:
                    projectile = new Rock(this.world, this.source, 3);
                    break;
                case -2:
                    projectile = new Fireball(this.world, this.source, 5);
            }

            double distY = this.target.posY + this.target.getEyeHeight() - 1.1 - projectile.posY;
            projectile.setHeading(distX, distY + yOffset, distZ, this.arrowType == -7 ? 0.6F : 1.6F, 12.0F);
            this.world.playEntitySound(this.source, snd, 1.0F, 1.0F / (this.source.rand.nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntity(projectile);
        }
    }
}
