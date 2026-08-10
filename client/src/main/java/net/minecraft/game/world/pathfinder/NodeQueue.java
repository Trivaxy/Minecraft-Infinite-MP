package net.minecraft.game.world.pathfinder;

public class NodeQueue {
    private PathNode[] nodes = new PathNode[1024];
    private int count = 0;

    public PathNode addPoint(PathNode node) {
        if (node.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }

        if (this.count == this.nodes.length) {
            PathNode[] nodes = new PathNode[this.count << 1];
            System.arraycopy(this.nodes, 0, nodes, 0, this.count);
            this.nodes = nodes;
        }

        this.nodes[this.count] = node;
        node.index = this.count;
        this.sortBack(this.count++);
        return node;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathNode dequeue() {
        PathNode currNode = this.nodes[0];
        this.nodes[0] = this.nodes[--this.count];
        this.nodes[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }

        currNode.index = -1;
        return currNode;
    }

    public void changeDistance(PathNode node, float dist) {
        float maxDist = node.distanceToTarget;
        node.distanceToTarget = dist;
        if (dist < maxDist) {
            this.sortBack(node.index);
        } else {
            this.sortForward(node.index);
        }
    }

    private void sortBack(int index) {
        PathNode node = this.nodes[index];
        float dist = node.distanceToTarget;

        while (index > 0) {
            int next = index - 1 >> 1;
            PathNode nextNode = this.nodes[next];
            if (dist >= nextNode.distanceToTarget) {
                break;
            }

            this.nodes[index] = nextNode;
            nextNode.index = index;
            index = next;
        }

        this.nodes[index] = node;
        node.index = index;
    }

    private void sortForward(int index) {
        PathNode node = this.nodes[index];
        float dist = node.distanceToTarget;

        while (true) {
            int next = 1 + (index << 1);
            int nextPlusOne = next + 1;
            if (next >= this.count) {
                break;
            }

            PathNode nextNode = this.nodes[next];
            float distToNext = nextNode.distanceToTarget;
            PathNode newNode;
            float targetDist;
            if (nextPlusOne >= this.count) {
                newNode = null;
                targetDist = Float.POSITIVE_INFINITY;
            } else {
                newNode = this.nodes[nextPlusOne];
                targetDist = newNode.distanceToTarget;
            }

            if (distToNext < targetDist) {
                if (distToNext >= dist) {
                    break;
                }

                this.nodes[index] = nextNode;
                nextNode.index = index;
                index = next;
            } else {
                if (targetDist >= dist) {
                    break;
                }

                this.nodes[index] = newNode;
                newNode.index = index;
                index = nextPlusOne;
            }
        }

        this.nodes[index] = node;
        node.index = index;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}
