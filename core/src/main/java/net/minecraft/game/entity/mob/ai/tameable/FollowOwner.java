package net.minecraft.game.entity.mob.ai.tameable;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;
import net.minecraft.game.world.World;
import net.minecraft.game.world.pathfinder.PathNavigator;
import net.minecraft.util.math.MathHelper;

public class FollowOwner extends Action {
    private TameableMob pet;
    private Mob owner;
    private World world;
    private PathNavigator pathfinder;
    private float friction;
    private float maxDist;
    private float minDist;
    private int cooldown;

    public FollowOwner(TameableMob mob, float spd, float min, float max) {
        this.pet = mob;
        this.world = mob.world;
        this.friction = spd;
        this.pathfinder = mob.navigator;
        this.minDist = min;
        this.maxDist = max;
        this.setMutexBits(3);
    }

    @Override
    public boolean canExecute() {
        Mob owner = this.pet.getOwner();
        if (owner != null && !this.pet.isSitting() && !(this.pet.getDistanceSqTo(owner) < this.minDist * this.minDist)) {
            this.owner = owner;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.pathfinder.noPath() && this.pet.getDistanceSqTo(this.owner) > this.maxDist * this.maxDist && !this.pet.isSitting();
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.pathfinder.clearPath();
    }

    @Override
    public void tick() {
        this.pet.lookControl.faceEntity(this.owner, 10.0F, this.pet.getFaceSpeed());
        if (!this.pet.isSitting() && --this.cooldown <= 0) {
            this.cooldown = 10;
            if (!this.pathfinder.moveToTarget(this.owner, this.friction) && this.pet.getDistanceSqTo(this.owner) >= 144.0) {
                int x = MathHelper.floor_double(this.owner.posX) - 2;
                int z = MathHelper.floor_double(this.owner.posZ) - 2;
                int y = MathHelper.floor_double(this.owner.boundingBox.minY);

                for (int xOff = 0; xOff <= 4; xOff++) {
                    for (int zOff = 0; zOff <= 4; zOff++) {
                        if ((xOff < 1 || zOff < 1 || xOff > 3 || zOff > 3)
                            && this.world.isBlockFull(x + xOff, y - 1, z + zOff)
                            && !this.world.isBlockFull(x + xOff, y, z + zOff)
                            && !this.world.isBlockFull(x + xOff, y + 1, z + zOff)) {
                            this.pet.setPosition(x + xOff + 0.5F, y, z + zOff + 0.5F);
                            this.pathfinder.clearPath();
                            return;
                        }
                    }
                }
            }
        }
    }
}
