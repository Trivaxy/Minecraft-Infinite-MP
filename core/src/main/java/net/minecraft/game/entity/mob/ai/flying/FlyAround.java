package net.minecraft.game.entity.mob.ai.flying;

import java.util.Random;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.util.MoveControl;

public class FlyAround extends Action {
    protected Mob mob;
    protected float dist;
    protected float spd;

    public FlyAround(Mob parent, float dist, float spd) {
        this.mob = parent;
        this.dist = dist;
        this.spd = spd;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        MoveControl moveHelper = this.mob.moveControl;
        if (!moveHelper.getMoving()) {
            return true;
        }

        double distX = moveHelper.posX - this.mob.posX;
        double distY = moveHelper.posY - this.mob.posY;
        double distZ = moveHelper.posZ - this.mob.posZ;
        double dist = distX * distX + distY * distY + distZ * distZ;
        return dist < 1.0 || dist > 3600.0;
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }

    @Override
    public void start() {
        Random random = this.mob.rand;
        double x = this.mob.posX + (random.nextFloat() * 2.0F - 1.0F) * this.dist;
        double y = this.mob.posY + (random.nextFloat() * 2.0F - 1.0F) * this.dist;
        double z = this.mob.posZ + (random.nextFloat() * 2.0F - 1.0F) * this.dist;
        this.mob.moveControl.moveTo(x, y, z, this.spd);
    }
}
