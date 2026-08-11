package net.minecraft.game.world.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Door;
import net.minecraft.game.block.decoration.Fence;
import net.minecraft.game.block.decoration.FenceGate;
import net.minecraft.game.block.decoration.Trapdoor;
import net.minecraft.game.block.decoration.Wall;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class PathFinder {
    private WorldSource world;
    private NodeQueue path = new NodeQueue();
    private Int2ObjectOpenHashMap<PathNode> pointMap = new Int2ObjectOpenHashMap<>();
    private PathNode[] pathOptions = new PathNode[32];

    public PathFinder(WorldSource blockAccess) {
        this.world = blockAccess;
    }

    public Path pathToEntity(Entity currentEntity, Entity targetEntity, float dist) {
        return this.pathTo(currentEntity, targetEntity.posX, targetEntity.boundingBox.minY, targetEntity.posZ, dist);
    }

    public Path pathToLocation(Entity entity, int x, int y, int z, float dist) {
        return this.pathTo(entity, x + 0.5, y + 0.5, z + 0.5, dist);
    }

    private Path pathTo(Entity entity, double x, double y, double z, float dist) {
        this.path.clearPath();
        this.pointMap.clear();
        int height = MathHelper.floor_double(entity.boundingBox.minY + 0.5);
        if (entity.isInWater()) {
            height = (int)entity.boundingBox.minY;
            int pos = this.world.getBlockId(MathHelper.floor_double(entity.posX), height, MathHelper.floor_double(entity.posZ));

            while (pos == BlockList.waterMoving.id || pos == BlockList.waterStill.id) {
                pos = this.world.getBlockId(MathHelper.floor_double(entity.posX), ++height, MathHelper.floor_double(entity.posZ));
            }
        }

        PathNode closest = this.openPoint(MathHelper.floor_double(entity.boundingBox.minX), height, MathHelper.floor_double(entity.boundingBox.minZ));
        PathNode furthest = this.openPoint(
            MathHelper.floor_double(x - entity.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(z - entity.width / 2.0F)
        );
        PathNode next = new PathNode(
            MathHelper.floor_float(entity.width + 1.0F), MathHelper.floor_float(entity.height + 1.0F), MathHelper.floor_float(entity.width + 1.0F)
        );
        return this.findPath(entity, closest, furthest, next, dist);
    }

    private Path findPath(Entity entity, PathNode currentPosition, PathNode targetPosition, PathNode next, float distance) {
        currentPosition.totalPathDistance = 0.0F;
        currentPosition.distanceToNext = currentPosition.distanceTo(targetPosition);
        currentPosition.distanceToTarget = currentPosition.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(currentPosition);
        PathNode now = currentPosition;

        while (!this.path.isPathEmpty()) {
            PathNode to = this.path.dequeue();
            if (to.equals(targetPosition)) {
                return this.pathToNode(currentPosition, targetPosition);
            }

            if (to.distanceTo(targetPosition) < now.distanceTo(targetPosition)) {
                now = to;
            }

            to.isFirst = true;
            int options = this.findPathOptions(entity, to, next, targetPosition, distance);

            for (int option = 0; option < options; option++) {
                PathNode potentialPoint = this.pathOptions[option];
                float distBetween = to.totalPathDistance + to.distanceTo(potentialPoint);
                if (!potentialPoint.isAssigned() || distBetween < potentialPoint.totalPathDistance) {
                    potentialPoint.previous = to;
                    potentialPoint.totalPathDistance = distBetween;
                    potentialPoint.distanceToNext = potentialPoint.distanceTo(targetPosition);
                    if (potentialPoint.isAssigned()) {
                        this.path.changeDistance(potentialPoint, potentialPoint.totalPathDistance + potentialPoint.distanceToNext);
                    } else {
                        potentialPoint.distanceToTarget = potentialPoint.totalPathDistance + potentialPoint.distanceToNext;
                        this.path.addPoint(potentialPoint);
                    }
                }
            }
        }

        return now == currentPosition ? null : this.pathToNode(currentPosition, now);
    }

    private int findPathOptions(Entity entity, PathNode current, PathNode target, PathNode next, float distance) {
        int index = 0;
        byte dist = 0;
        if (this.getHazardType(entity, current.xCoord, current.yCoord + 1, current.zCoord, target) == 1) {
            dist = 1;
        }

        PathNode left = this.getSafePoint(entity, current.xCoord, current.yCoord, current.zCoord + 1, target, dist);
        PathNode back = this.getSafePoint(entity, current.xCoord - 1, current.yCoord, current.zCoord, target, dist);
        PathNode front = this.getSafePoint(entity, current.xCoord + 1, current.yCoord, current.zCoord, target, dist);
        PathNode right = this.getSafePoint(entity, current.xCoord, current.yCoord, current.zCoord - 1, target, dist);
        if (left != null && !left.isFirst && left.distanceTo(next) < distance) {
            this.pathOptions[index++] = left;
        }

        if (back != null && !back.isFirst && back.distanceTo(next) < distance) {
            this.pathOptions[index++] = back;
        }

        if (front != null && !front.isFirst && front.distanceTo(next) < distance) {
            this.pathOptions[index++] = front;
        }

        if (right != null && !right.isFirst && right.distanceTo(next) < distance) {
            this.pathOptions[index++] = right;
        }

        return index;
    }

    private PathNode getSafePoint(Entity entity, int x, int y, int z, PathNode node, int index) {
        PathNode destination = null;
        int type = this.getHazardType(entity, x, y, z, node);
        if (type == 2) {
            return this.openPoint(x, y, z);
        }

        if (type == 1) {
            destination = this.openPoint(x, y, z);
        }

        if (destination == null && index > 0 && type != -3 && type != -4 && this.getHazardType(entity, x, y + index, z, node) == 1) {
            destination = this.openPoint(x, y + index, z);
            y += index;
        }

        if (destination != null) {
            int fallDist = 0;
            int danger = 0;

            while (y > 0) {
                danger = this.getHazardType(entity, x, y - 1, z, node);
                if (danger == -1) {
                    return null;
                }

                if (danger != 1) {
                    break;
                }

                if (++fallDist >= entity.maxPathFallDist()) {
                    return null;
                }

                if (--y > 0) {
                    destination = this.openPoint(x, y, z);
                }
            }

            if (danger == -2) {
                return null;
            }
        }

        return destination;
    }

    private final PathNode openPoint(int x, int y, int z) {
        int index = PathNode.getChunkLocation(x, y, z);
        PathNode node = this.pointMap.get(index);
        if (node == null) {
            node = new PathNode(x, y, z);
            this.pointMap.put(index, node);
        }

        return node;
    }

    private int getHazardType(Entity entity, int x, int y, int z, PathNode node) {
        boolean canPass = false;

        for (int x0 = x; x0 < x + node.xCoord; x0++) {
            for (int y0 = y; y0 < y + node.yCoord; y0++) {
                for (int z0 = z; z0 < z + node.zCoord; z0++) {
                    int id = this.world.getBlockId(x0, y0, z0);
                    if (id > 0) {
                        Block block = BlockList.blocks[id];
                        if (block instanceof Trapdoor) {
                            canPass = true;
                        } else if (block instanceof LiquidBlock) {
                            if (block.material == MaterialList.lava) {
                                return -1;
                            }

                            canPass = true;
                        }

                        if (block.blocksPathfinder(this.world, x0, y0, z0) && (!(block instanceof Door) || (this.world.getBlockMetadata(x0, y0, z0) & 4) == 0)) {
                            if (block instanceof Fence || block instanceof Wall || block instanceof FenceGate) {
                                return -3;
                            }

                            Material mat = block.material;
                            if (mat != MaterialList.lava) {
                                return 0;
                            }

                            if (!entity.isInLava()) {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return canPass ? 2 : 1;
    }

    private Path pathToNode(PathNode current, PathNode target) {
        int index = 1;

        for (PathNode next4 = target; next4.previous != null; next4 = next4.previous) {
            index++;
        }

        PathNode[] options = new PathNode[index];
        PathNode var7 = target;

        for (options[--index] = target; var7.previous != null; options[--index] = var7) {
            var7 = var7.previous;
        }

        return new Path(options);
    }
}
