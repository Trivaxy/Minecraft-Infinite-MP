package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.physics.Vector;
import net.minecraft.util.random.RandomPositionGenerator;

public class Panic extends Action {
    protected Mob laCreatura;
    private float friction;
    private double randomX;
    private double randomY;
    private double randomZ;
    private int panicTime = 100;

    public Panic(Mob animal, float f) {
        this.laCreatura = animal;
        this.friction = f;
        this.setMutexBits(1);
    }

    @Override
    public void tick() {
        if (this.panicTime > 0) {
            this.panicTime--;
        } else {
            this.laCreatura.lastTarget = null;
        }
    }

    @Override
    public boolean canExecute() {
        if (this.laCreatura.lastTarget == null) {
            this.panicTime = 100;
            return false;
        }

        Vector randomPos = RandomPositionGenerator.findRandomTarget(this.laCreatura, 5, 4);
        if (randomPos == null) {
            return false;
        }

        this.randomX = randomPos.xCoord;
        this.randomY = randomPos.yCoord;
        this.randomZ = randomPos.zCoord;
        return true;
    }

    @Override
    public void start() {
        this.laCreatura.navigator.moveToLocation(this.randomX, this.randomY, this.randomZ, this.friction);
    }

    @Override
    public boolean continueExecuting() {
        return !this.laCreatura.navigator.noPath();
    }
}
