package net.minecraft.game.entity.mob.ai.hostile;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.random.RandomPositionGenerator;

public class ChangeCourse extends Action {
    private Mob target;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float friction;

    public ChangeCourse(Mob target, float spd) {
        this.target = target;
        this.friction = spd;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        if (this.target.withinTrackingDist()) {
            return false;
        }

        Position targetPos = this.target.getHomePosition();
        Vector targetVec = RandomPositionGenerator.findRandomTarget(this.target, 16, 7, Vector.createVector(targetPos.x, targetPos.y, targetPos.z));
        if (targetVec == null) {
            return false;
        }

        this.targetX = targetVec.xCoord;
        this.targetY = targetVec.yCoord;
        this.targetZ = targetVec.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.target.navigator.noPath();
    }

    @Override
    public void start() {
        this.target.navigator.moveToLocation(this.targetX, this.targetY, this.targetZ, this.friction);
    }
}
