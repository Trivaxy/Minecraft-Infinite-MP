package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.random.RandomPositionGenerator;

public class WanderAround extends Action {
    private Mob entity;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float radius;
    protected boolean stopsPeriodically;

    public WanderAround(Mob mob, float f, boolean stops) {
        this.entity = mob;
        this.radius = f;
        this.stopsPeriodically = stops;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        if (this.entity.rand.nextInt(50) != 0 && this.stopsPeriodically) {
            return false;
        }

        Vector randomPos = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
        if (randomPos == null) {
            return false;
        }

        this.targetX = randomPos.xCoord;
        this.targetY = randomPos.yCoord;
        this.targetZ = randomPos.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entity.navigator.noPath();
    }

    @Override
    public void start() {
        this.entity.navigator.moveToLocation(this.targetX, this.targetY, this.targetZ, this.radius);
    }
}
