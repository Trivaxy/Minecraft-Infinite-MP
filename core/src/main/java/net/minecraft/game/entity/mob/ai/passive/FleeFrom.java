package net.minecraft.game.entity.mob.ai.passive;

import java.util.List;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.animal.tameable.TameableMob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.game.world.pathfinder.PathNavigator;
import net.minecraft.util.random.RandomPositionGenerator;

public class FleeFrom extends Action {
    private Mob host;
    private Entity target;
    private Path path;
    private PathNavigator pathfinder;
    private Class<?> targetClass;
    private float range;
    private float maxSpd;
    private float minSpd;

    public FleeFrom(Mob host, Class<?> class2, float r, float max, float min) {
        this.host = host;
        this.targetClass = class2;
        this.range = r;
        this.maxSpd = max;
        this.minSpd = min;
        this.pathfinder = host.navigator;
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        if (this.targetClass == Player.class) {
            if (this.host instanceof TameableMob && ((TameableMob)this.host).isTamed()) {
                return false;
            }

            this.target = this.host.world.getClosestPlayer(this.host, this.range);
            if (this.target == null) {
                return false;
            }
        } else {
            List<Entity> entities = this.host.world.getEntitiesOfType(this.targetClass, this.host.boundingBox.expand(this.range, 3.0, this.range));
            if (entities.size() == 0) {
                return false;
            }

            this.target = entities.get(0);
        }

        Vector hostLocation = RandomPositionGenerator.findRandomTargetAwayFrom(
            this.host, 16, 7, Vector.createVector(this.target.posX, this.target.posY, this.target.posZ)
        );
        if (hostLocation != null
            && !(this.target.getDistanceSq(hostLocation.xCoord, hostLocation.yCoord, hostLocation.zCoord) < this.target.getDistanceSqTo(this.host))) {
            this.path = this.pathfinder.pathToLocation(hostLocation.xCoord, hostLocation.yCoord, hostLocation.zCoord);
            return this.path == null ? false : this.path.pathExists(hostLocation);
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.pathfinder.noPath();
    }

    @Override
    public void start() {
        this.pathfinder.setPath(this.path, this.maxSpd);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.host.getDistanceSqTo(this.target) < 49.0) {
            this.host.navigator.setSpeed(this.minSpd);
        } else {
            this.host.navigator.setSpeed(this.maxSpd);
        }
    }
}
