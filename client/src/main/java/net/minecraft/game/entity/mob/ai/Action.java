package net.minecraft.game.entity.mob.ai;

public abstract class Action {
    private int mutexBits = 0;

    public abstract boolean canExecute();

    public boolean continueExecuting() {
        return this.canExecute();
    }

    public boolean isContinuous() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
    }

    public void setMutexBits(int val) {
        this.mutexBits = val;
    }

    public int getMutexBits() {
        return this.mutexBits;
    }
}
