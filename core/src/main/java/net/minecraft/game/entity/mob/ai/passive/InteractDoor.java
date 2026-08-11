package net.minecraft.game.entity.mob.ai.passive;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Door;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.world.pathfinder.Path;
import net.minecraft.game.world.pathfinder.PathNavigator;
import net.minecraft.game.world.pathfinder.PathNode;
import net.minecraft.util.math.MathHelper;

public abstract class InteractDoor extends Action {
    protected Mob host;
    protected int mobX;
    protected int mobY;
    protected int mobZ;
    protected Door targetDoor;
    boolean doorDetected;
    float doorX;
    float doorZ;

    public InteractDoor(Mob mob) {
        this.host = mob;
    }

    @Override
    public boolean canExecute() {
        if (!this.host.collidedHorizontally) {
            return false;
        }

        PathNavigator pathFinder = this.host.navigator;
        Path path = pathFinder.getPath();
        if (path != null && !path.isFinished()) {
            for (int index = 0; index < Math.min(path.getCurrentIndex() + 2, path.getLength()); index++) {
                PathNode point = path.getNode(index);
                this.mobX = point.xCoord;
                this.mobY = point.yCoord + 1;
                this.mobZ = point.zCoord;
                if (this.host.getDistanceSq(this.mobX, this.host.posY, this.mobZ) <= 2.25) {
                    this.targetDoor = this.findNearbyDoors(this.mobX, this.mobY, this.mobZ);
                    if (this.targetDoor != null) {
                        return true;
                    }
                }
            }

            this.mobX = MathHelper.floor_double(this.host.posX);
            this.mobY = MathHelper.floor_double(this.host.posY + 1.0);
            this.mobZ = MathHelper.floor_double(this.host.posZ);
            this.targetDoor = this.findNearbyDoors(this.mobX, this.mobY, this.mobZ);
            return this.targetDoor != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.doorDetected;
    }

    @Override
    public void start() {
        this.doorDetected = false;
        this.doorX = (float)(this.mobX + 0.5F - this.host.posX);
        this.doorZ = (float)(this.mobZ + 0.5F - this.host.posZ);
    }

    @Override
    public void tick() {
        float distX = (float)(this.mobX + 0.5F - this.host.posX);
        float distZ = (float)(this.mobZ + 0.5F - this.host.posZ);
        float dist = this.doorX * distX + this.doorZ * distZ;
        if (dist < 0.0F) {
            this.doorDetected = true;
        }
    }

    private Door findNearbyDoors(int x, int y, int z) {
        Block door = this.host.world.getBlock(x, y, z);
        return door instanceof Door && door != BlockList.doorSteel ? (Door)door : null;
    }
}
