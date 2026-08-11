package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.Direction;

public class WillowTree extends Tree {
    private int height = 5;
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 15;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 16;

    public WillowTree(int height) {
        this.height = height;
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        if (!this.validGround(world, x0, y0 - 1, z0)) {
            return false;
        }

        if (y0 + this.height > world.getWorldHeight() - 2) {
            return false;
        }

        int radius = 3;
        BlockPosition src = new BlockPosition().set(x0, y0, z0);
        BlockPosition dst = new BlockPosition()
            .set(x0 + rand.nextInt((radius << 1) + 1) - radius, this.height + y0, z0 + rand.nextInt((radius << 1) + 1) - radius);
        this.bresenham(world, src, dst, trunkID, 16);

        for (int y = -1; y <= 0; y++) {
            this.squareTreeLayer(world, rand, dst.x, dst.y + y, dst.z, 3, leavesID, 15);
        }

        for (int x = -1; x <= 1; x++) {
            this.setBlockIfEmpty(world, dst.x + x, dst.y + 1, dst.z, leavesID, 15);
        }

        for (int z = -1; z <= 1; z++) {
            this.setBlockIfEmpty(world, dst.x, dst.y + 1, dst.z + z, leavesID, 15);
        }

        dst.move(4);
        BlockPosition branchCursor = new BlockPosition();

        for (int j = 0; j <= 1; j++) {
            for (int direction : Direction.HORZ_PLANE) {
                branchCursor.set(dst);
                branchCursor.move(direction, 2);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                branchCursor.move(5);
                int amp = rand.nextInt(2);
                this.setBlockIfEmpty(
                    world,
                    branchCursor.x + Direction.offsetX[direction] * amp,
                    branchCursor.y,
                    branchCursor.z + Direction.offsetZ[direction] * amp,
                    leavesID,
                    15
                );
                int sideWays = Direction.getCW(direction);
                int amount = rand.nextInt(3) - 1;
                int vx = Direction.offsetX[sideWays] * amount;
                int vz = Direction.offsetZ[sideWays] * amount;
                int vx2 = vx;
                if (rand.nextBoolean()) {
                    vx2 += vx;
                }

                int vz2 = vx;
                if (rand.nextBoolean()) {
                    vz2 += vz;
                }

                branchCursor.move(5).move(direction);
                this.setBlockIfEmpty(world, branchCursor.x + vx, branchCursor.y, branchCursor.z + vz, leavesID, 15);
                branchCursor.move(5);
                this.setBlockIfEmpty(world, branchCursor.x + vx, branchCursor.y, branchCursor.z + vz, leavesID, 15);
                int branchL = 2 + rand.nextInt(3);

                for (int i = 0; i < branchL; i++) {
                    branchCursor.move(5);
                    this.setBlockIfEmpty(world, branchCursor.x + vx2, branchCursor.y, branchCursor.z + vz2, leavesID, 15);
                }
            }

            for (int direction : Direction.HORZ_PLANE_DIAGONALS) {
                int mulX = rand.nextInt(2);
                int mulZ = 1 - mulX;
                branchCursor.set(dst);
                branchCursor.x = branchCursor.x + Direction.offsetX[direction] * (mulX + 1);
                branchCursor.z = branchCursor.z + Direction.offsetZ[direction] * (mulZ + 1);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                branchCursor.x = branchCursor.x + Direction.offsetX[direction];
                branchCursor.z = branchCursor.z + Direction.offsetZ[direction];
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                this.setBlock(world, branchCursor.move(5), leavesID, 15);
                branchCursor.x = branchCursor.x + Direction.offsetX[direction] * rand.nextInt(2);
                branchCursor.z = branchCursor.z + Direction.offsetZ[direction] * rand.nextInt(2);
                int branchL = 2 + rand.nextInt(3);

                for (int i = 0; i < branchL; i++) {
                    this.setBlock(world, branchCursor.move(5), leavesID, 15);
                }
            }
        }

        return true;
    }
}
