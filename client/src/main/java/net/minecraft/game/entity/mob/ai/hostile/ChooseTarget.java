package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;
import net.minecraft.game.entity.mob.monster.Golem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.game.world.pathfinder.PathNode;
import net.minecraft.util.math.MathHelper;

public abstract class ChooseTarget extends Action {
    protected Mob src;
    protected float dist;
    protected boolean chasing;
    private boolean searching;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    public ChooseTarget(Mob trgt, float dist, boolean check) {
        this(trgt, dist, check, false);
    }

    public ChooseTarget(Mob src, float dist, boolean chase, boolean search) {
        this.src = src;
        this.dist = dist;
        this.chasing = chase;
        this.searching = search;
    }

    @Override
    public boolean continueExecuting() {
        Mob target = this.src.attackTarget;
        if (target != null && target.isAlive() && !(this.src.getDistanceSqTo(target) > this.dist * this.dist)) {
            if (this.chasing) {
                if (!this.src.senses.canSee(target)) {
                    if (++this.z > 60) {
                        return false;
                    }
                } else {
                    this.z = 0;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    @Override
    public void stop() {
        this.src.attackTarget = null;
    }

    protected boolean foundValidTarget(Mob target, boolean attackPlayer) {
        if (target != null && target != this.src && target.isAlive() && target.world.settings.getDifficulty() != 0) {
            if (!this.src.canBeAggroed(target.getClass())) {
                return false;
            }

            if (this.src instanceof TameableMob && ((TameableMob)this.src).isTamed()) {
                if (target instanceof TameableMob && ((TameableMob)target).isTamed()) {
                    return false;
                }

                if (target == ((TameableMob)this.src).getOwner()) {
                    return false;
                }
            } else if (target instanceof Player
                && !attackPlayer
                && (
                    ((Player)target).properties.gamemode == 1
                        || ((Player)target).properties.gamemode == 3
                        || target.hasEffect(EffectList.invisibility) && !this.src.boss
                )) {
                return false;
            }

            if (this.src instanceof Golem
                && this.src.lastTarget == null
                && (
                    target.sneaking
                        || target.crawling
                        || target.motionX < 0.05F && target.motionX > -0.05F
                        || target.motionZ < 0.05F && target.motionZ > -0.05F
                )) {
                return false;
            }

            if (!this.src.withinTrackingDist(MathHelper.floor_double(target.posX), MathHelper.floor_double(target.posY), MathHelper.floor_double(target.posZ))) {
                return false;
            }

            if (this.chasing && !this.src.senses.canSee(target)) {
                return false;
            }

            if (this.searching) {
                if (--this.y <= 0) {
                    this.x = 0;
                }

                if (this.x == 0) {
                    this.x = this.isEntityValid(target) ? 1 : 2;
                }

                if (this.x == 2) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isEntityValid(Mob target) {
        this.y = 10 + this.src.rand.nextInt(5);
        Path path = this.src.navigator.pathToTarget(target);
        if (path == null) {
            return false;
        }

        PathNode point = path.getTargetNode();
        if (point == null) {
            return false;
        }

        int distX = point.xCoord - MathHelper.floor_double(target.posX);
        int distZ = point.zCoord - MathHelper.floor_double(target.posZ);
        return distX * distX + distZ * distZ <= 2.25;
    }
}
