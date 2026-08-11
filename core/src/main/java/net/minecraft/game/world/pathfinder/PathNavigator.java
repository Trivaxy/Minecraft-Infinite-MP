package net.minecraft.game.world.pathfinder;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class PathNavigator {
    private Vector lastPos = Vector.createVector(0.0, 0.0, 0.0);
    private Mob subject;
    private World world;
    private Path currentPath;
    private float speed;
    private float searchRange;
    private int totalTicks;
    private int stopTicks;
    private boolean canSwim = false;
    private boolean avoidSun = false;

    public PathNavigator(Mob mob, World world, float range) {
        this.subject = mob;
        this.world = world;
        this.searchRange = range;
    }

    public void setAvoidSun(boolean state) {
        this.avoidSun = state;
    }

    public void setSpeed(float spd) {
        this.speed = spd;
    }

    public void setCanSwim(boolean state) {
        this.canSwim = state;
    }

    public Path pathToLocation(double x, double y, double z) {
        return !this.canNavigate()
            ? null
            : this.world.findPathLocation(this.subject, MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z), this.searchRange);
    }

    public boolean moveToLocation(double x, double y, double z, float spd) {
        Path path = this.pathToLocation(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z));
        return this.setPath(path, spd);
    }

    public Path pathToTarget(Mob target) {
        return !this.canNavigate() ? null : this.world.findPathEntity(this.subject, target, this.searchRange);
    }

    public boolean moveToTarget(Mob mob, float spd) {
        Path path = this.pathToTarget(mob);
        return path != null ? this.setPath(path, spd) : false;
    }

    public boolean setPath(Path path, float spd) {
        if (path == null) {
            this.currentPath = null;
            return false;
        }

        if (!path.isPathValid(this.currentPath)) {
            this.currentPath = path;
        }

        if (this.avoidSun) {
            this.removeSunnyPath();
        }

        if (this.currentPath != null && this.currentPath.getLength() != 0) {
            this.speed = spd;
            Vector pos = this.getEntityPosition();
            this.stopTicks = this.totalTicks;
            this.lastPos.xCoord = pos.xCoord;
            this.lastPos.yCoord = pos.yCoord;
            this.lastPos.zCoord = pos.zCoord;
            return true;
        } else {
            return false;
        }
    }

    public Path getPath() {
        return this.currentPath;
    }

    public void tick() {
        this.totalTicks++;
        if (!this.noPath()) {
            if (this.canNavigate()) {
                this.navigatePath();
            } else if (this.currentPath != null && this.currentPath.getCurrentIndex() < this.currentPath.getLength()) {
                Vector currentPosition = this.getEntityPosition();
                Vector targetNode = this.currentPath.getVector(this.subject, this.currentPath.getCurrentIndex());
                if (currentPosition.yCoord > targetNode.yCoord
                    && !this.subject.onGround
                    && MathHelper.floor_double(currentPosition.xCoord) == MathHelper.floor_double(targetNode.xCoord)
                    && MathHelper.floor_double(currentPosition.zCoord) == MathHelper.floor_double(targetNode.zCoord)) {
                    this.currentPath.setCurrentIndex(this.currentPath.getCurrentIndex() + 1);
                }
            }

            if (!this.noPath()) {
                Vector destination = this.currentPath.getCurrent(this.subject);
                if (destination != null) {
                    this.subject.moveControl.moveTo(destination.xCoord, destination.yCoord, destination.zCoord, this.speed);
                }
            }
        }
    }

    private void navigatePath() {
        Vector currPos = this.getEntityPosition();
        int pathLength = this.currentPath.getLength();

        for (int index = this.currentPath.getCurrentIndex(); index < this.currentPath.getLength(); index++) {
            if (this.currentPath.getNode(index).yCoord != (int)currPos.yCoord) {
                pathLength = index;
                break;
            }
        }

        float size = this.subject.width * this.subject.width;

        for (int index = this.currentPath.getCurrentIndex(); index < pathLength; index++) {
            if (currPos.squareDistanceTo(this.currentPath.getVector(this.subject, index)) < size) {
                this.currentPath.setCurrentIndex(index + 1);
            }
        }

        int var8 = (int)Math.ceil(this.subject.width);
        int heightOffset = (int)this.subject.height + 1;

        for (int newIndex = pathLength - 1; newIndex >= this.currentPath.getCurrentIndex(); newIndex--) {
            if (this.hasDirectPath(currPos, this.currentPath.getVector(this.subject, newIndex), var8, heightOffset, var8)) {
                this.currentPath.setCurrentIndex(newIndex);
                break;
            }
        }

        if (this.totalTicks - this.stopTicks > 100) {
            if (currPos.squareDistanceTo(this.lastPos) < 2.25) {
                this.clearPath();
            }

            this.stopTicks = this.totalTicks;
            this.lastPos.xCoord = currPos.xCoord;
            this.lastPos.yCoord = currPos.yCoord;
            this.lastPos.zCoord = currPos.zCoord;
        }
    }

    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPath() {
        this.currentPath = null;
    }

    private Vector getEntityPosition() {
        return Vector.createVector(this.subject.posX, this.getPathableYPos(), this.subject.posZ);
    }

    private int getPathableYPos() {
        if (this.subject.isInWater() && this.canSwim) {
            int y = (int)this.subject.boundingBox.minY;
            int block = this.world.getBlockId(MathHelper.floor_double(this.subject.posX), y, MathHelper.floor_double(this.subject.posZ));
            int yOffset = 0;

            while (block == BlockList.waterMoving.id || block == BlockList.waterStill.id) {
                block = this.world.getBlockId(MathHelper.floor_double(this.subject.posX), ++y, MathHelper.floor_double(this.subject.posZ));
                if (++yOffset > 16) {
                    return (int)this.subject.boundingBox.minY;
                }
            }

            return y;
        } else {
            return (int)(this.subject.boundingBox.minY + 0.5);
        }
    }

    private boolean canNavigate() {
        return this.subject.onGround || this.canSwim && this.isInLiquid();
    }

    private boolean isInLiquid() {
        return this.subject.isInWater() || this.subject.isInLava();
    }

    private void removeSunnyPath() {
        if (!this.world
            .isBlockExposed(MathHelper.floor_double(this.subject.posX), (int)(this.subject.boundingBox.minY + 0.5), MathHelper.floor_double(this.subject.posZ))
            )
         {
            for (int index = 0; index < this.currentPath.getLength(); index++) {
                PathNode currentnode = this.currentPath.getNode(index);
                if (this.world.isBlockExposed(currentnode.xCoord, currentnode.yCoord, currentnode.zCoord)) {
                    this.clearPath();
                    return;
                }
            }
        }
    }

    private boolean hasDirectPath(Vector current, Vector target, int x, int y, int z) {
        int currentX = MathHelper.floor_double(current.xCoord);
        int currentZ = MathHelper.floor_double(current.zCoord);
        double distX = target.xCoord - current.xCoord;
        double distZ = target.zCoord - current.zCoord;
        double dist = distX * distX + distZ * distZ;
        if (dist < 1.0E-8) {
            return false;
        }

        double range = 1.0 / Math.sqrt(dist);
        distX *= range;
        distZ *= range;
        x += 2;
        z += 2;
        if (!this.safeLocation(currentX, (int)current.yCoord, currentZ, x, y, z, current, distX, distZ)) {
            return false;
        }

        x -= 2;
        z -= 2;
        double rangeX = 1.0 / Math.abs(distX);
        double rangeZ = 1.0 / Math.abs(distZ);
        double incrX = currentX * 1 - current.xCoord;
        double incrZ = currentZ * 1 - current.zCoord;
        if (distX >= 0.0) {
            incrX++;
        }

        if (distZ >= 0.0) {
            incrZ++;
        }

        incrX /= distX;
        incrZ /= distZ;
        int offsetX = distX < 0.0 ? -1 : 1;
        int offsetZ = distZ < 0.0 ? -1 : 1;
        int targetX = MathHelper.floor_double(target.xCoord);
        int targetZ = MathHelper.floor_double(target.zCoord);
        int finalDistX = targetX - currentX;
        int finalDistZ = targetZ - currentZ;

        while (finalDistX * offsetX > 0 || finalDistZ * offsetZ > 0) {
            if (incrX < incrZ) {
                incrX += rangeX;
                currentX += offsetX;
                finalDistX = targetX - currentX;
            } else {
                incrZ += rangeZ;
                currentZ += offsetZ;
                finalDistZ = targetZ - currentZ;
            }

            if (!this.safeLocation(currentX, (int)current.yCoord, currentZ, x, y, z, current, distX, distZ)) {
                return false;
            }
        }

        return true;
    }

    private boolean safeLocation(int currX, int currY, int currZ, int targetX, int targetY, int targetZ, Vector pos, double distX, double distZ) {
        int rangeX = currX - targetX / 2;
        int rangeZ = currZ - targetZ / 2;
        if (!this.clear(rangeX, currY, rangeZ, targetX, targetY, targetZ, pos, distX, distZ)) {
            return false;
        }

        for (int x = rangeX; x < rangeX + targetX; x++) {
            for (int z = rangeZ; z < rangeZ + targetZ; z++) {
                double newX = x + 0.5 - pos.xCoord;
                double newZ = z + 0.5 - pos.zCoord;
                if (newX * distX + newZ * distZ >= 0.0) {
                    int id = this.world.getBlockId(x, currY - 1, z);
                    if (id <= 0) {
                        return false;
                    }

                    Material mat = BlockList.blocks[id].material;
                    if (mat == MaterialList.water && !this.subject.isInWater()) {
                        return false;
                    }

                    if (mat == MaterialList.lava) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean clear(int targetX, int targetY, int targetZ, int rangeX, int rangeY, int rangeZ, Vector pos, double distX, double distZ) {
        for (int x = targetX; x < targetX + rangeX; x++) {
            for (int y = targetY; y < targetY + rangeY; y++) {
                for (int z = targetZ; z < targetZ + rangeZ; z++) {
                    double x0 = x + 0.5 - pos.xCoord;
                    double z0 = z + 0.5 - pos.zCoord;
                    if (x0 * distX + z0 * distZ >= 0.0) {
                        int id = this.world.getBlockId(x, y, z);
                        if (id > 0 && BlockList.blocks[id].blocksPathfinder(this.world, x, y, z)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
