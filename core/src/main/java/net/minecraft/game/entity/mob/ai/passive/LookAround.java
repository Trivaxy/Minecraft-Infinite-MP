package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;

public class LookAround extends Action {
    protected Mob host;
    private double yaw;
    private double pitch;
    protected int idleTime = 0;

    public LookAround(Mob host) {
        this.host = host;
        this.setMutexBits(3);
    }

    @Override
    public boolean canExecute() {
        return this.host.rand.nextFloat() < 0.02F;
    }

    @Override
    public boolean continueExecuting() {
        return this.idleTime >= 0;
    }

    @Override
    public void start() {
        double angle = (Math.PI * 2) * this.host.rand.nextDouble();
        this.yaw = Math.cos(angle);
        this.pitch = Math.sin(angle);
        this.idleTime = 20 + this.host.rand.nextInt(20);
    }

    @Override
    public void tick() {
        this.idleTime--;
        this.host
            .lookControl
            .setLookPosition(this.host.posX + this.yaw, this.host.posY + this.host.getEyeHeight(), this.host.posZ + this.pitch, 10.0F, this.host.getFaceSpeed());
    }
}
