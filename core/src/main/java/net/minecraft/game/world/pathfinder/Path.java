package net.minecraft.game.world.pathfinder;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.Vector;

public class Path {
    public final PathNode[] nodes;
    public int pathLength;
    private int pathIndex;

    public Path(PathNode[] nodes) {
        this.nodes = nodes;
        this.pathLength = nodes.length;
    }

    public boolean isFinished() {
        return this.pathIndex >= this.nodes.length;
    }

    public PathNode getTargetNode() {
        return this.pathLength > 0 ? this.nodes[this.pathLength - 1] : null;
    }

    public PathNode getNode(int id) {
        return this.nodes[id];
    }

    public int getLength() {
        return this.pathLength;
    }

    public void setLength(int length) {
        this.pathLength = length;
    }

    public int getCurrentIndex() {
        return this.pathIndex;
    }

    public void setCurrentIndex(int id) {
        this.pathIndex = id;
    }

    public Vector getVector(Entity entity, int id) {
        double x = this.nodes[id].xCoord + (int)(entity.width + 1.0F) * 0.5;
        double y = this.nodes[id].yCoord;
        double z = this.nodes[id].zCoord + (int)(entity.width + 1.0F) * 0.5;
        return Vector.createVector(x, y, z);
    }

    public Vector getPosition(Entity entity) {
        float x = this.nodes[this.pathIndex].xCoord + (int)(entity.width + 1.0F) * 0.5F;
        float y = this.nodes[this.pathIndex].yCoord;
        float z = this.nodes[this.pathIndex].zCoord + (int)(entity.width + 1.0F) * 0.5F;
        return Vector.createVector(x, y, z);
    }

    public Vector getCurrent(Entity entity) {
        return this.getVector(entity, this.pathIndex);
    }

    public boolean isPathValid(Path pathEntity1) {
        if (pathEntity1 == null) {
            return false;
        }

        if (pathEntity1.nodes.length != this.nodes.length) {
            return false;
        }

        for (int i2 = 0; i2 < this.nodes.length; i2++) {
            if (this.nodes[i2].xCoord != pathEntity1.nodes[i2].xCoord
                || this.nodes[i2].yCoord != pathEntity1.nodes[i2].yCoord
                || this.nodes[i2].zCoord != pathEntity1.nodes[i2].zCoord) {
                return false;
            }
        }

        return true;
    }

    public boolean pathExists(Vector vec3D1) {
        PathNode pathPoint2 = this.getTargetNode();
        return pathPoint2 == null ? false : pathPoint2.xCoord == (int)vec3D1.xCoord && pathPoint2.zCoord == (int)vec3D1.zCoord;
    }
}
