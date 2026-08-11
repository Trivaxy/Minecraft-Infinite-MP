package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.player.Player;

public class LookAt extends Action {
    protected Mob host;
    private Entity target;
    private Class<?> targetClass;
    private float range;
    private float facing;
    private int lookTimer;

    public LookAt(Mob host, Class<?> class2, float f) {
        this.host = host;
        this.targetClass = class2;
        this.range = f;
        this.facing = 0.02F;
        this.setMutexBits(2);
    }

    public LookAt(Mob mob, Class<?> class2, float range, float angle) {
        this.host = mob;
        this.targetClass = class2;
        this.range = range;
        this.facing = angle;
        this.setMutexBits(2);
    }

    @Override
    public boolean canExecute() {
        if (!(this.host.rand.nextFloat() >= this.facing) && this.host.lastTarget == null && this.host.rider == null) {
            if (this.targetClass == Player.class) {
                this.target = this.host.world.getClosestPlayer(this.host, this.range);
            } else {
                this.target = this.host.world.getEntityOfType(this.targetClass, this.host.boundingBox.expand(this.range, 3.0, this.range), this.host);
            }

            return this.target != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return this.target.isAlive() && this.host.lastTarget == null
            ? (this.host.getDistanceSqTo(this.target) > this.range * this.range ? false : this.lookTimer > 0)
            : false;
    }

    @Override
    public void start() {
        this.lookTimer = 40 + this.host.rand.nextInt(40);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        this.host
            .lookControl
            .setLookPosition(this.target.posX, this.target.posY - this.target.getEyeHeight(), this.target.posZ, 10.0F, this.host.getFaceSpeed());
        this.lookTimer--;
    }
}
