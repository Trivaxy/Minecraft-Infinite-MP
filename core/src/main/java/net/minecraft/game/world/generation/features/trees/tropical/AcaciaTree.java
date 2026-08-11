package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.trees.Tree;

public class AcaciaTree extends Tree {
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 5;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 4;

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        int height = 3 + rand.nextInt(3);
        if (this.validGround(world, x0, y0 - 1, z0) && y0 + height <= world.getWorldHeight() - 1) {
            for (int y = y0; y <= y0 + 1 + height; y++) {
                int radius = y == y0 ? 0 : (y == y0 + 1 + height ? 3 : 1);

                for (int x = x0 - radius; x <= x0 + radius; x++) {
                    for (int z = z0 - radius; z <= z0 + radius; z++) {
                        if (world.isSolid(x, y, z) && world.getBlockMaterial(x, y, z) != MaterialList.leaves) {
                            return false;
                        }
                    }
                }
            }

            BlockPosition src = new BlockPosition().set(x0, y0, z0);
            double tilt = rand.nextDouble() * Math.PI / 4.0;
            double angle = rand.nextInt(4) * Math.PI / 2.0;
            BlockPosition dst = this.findEndPoint(src, height, angle, tilt);
            this.bresenham(world, src, dst, trunkID, 4);
            BlockPosition branchsrc = this.findEndPoint(src, height, angle, tilt);

            double branchAngle;
            do {
                branchAngle = rand.nextInt(4) * Math.PI / 2.0;
            } while (angle == branchAngle);

            BlockPosition branchdst = this.findEndPoint(branchsrc, height, branchAngle, Math.PI / 3);
            this.bresenhamNoRotation(world, branchsrc, branchdst, trunkID, 4);
            this.circle(world, branchdst, 4 - rand.nextInt(2), leavesID, 5);
            this.circle(world, branchdst.move(4), 2, leavesID, 5);
            branchsrc = dst.copy();
            angle = branchAngle;

            do {
                branchAngle = rand.nextInt(4) * Math.PI / 4.0;
            } while (angle == branchAngle);

            branchdst = this.findEndPoint(branchsrc, height, branchAngle, (Math.PI / 4) + rand.nextDouble() * 0.3);
            this.bresenhamNoRotation(world, branchsrc, branchdst, trunkID, 4);
            this.circle(world, branchdst, 4 - rand.nextInt(2), leavesID, 5);
            this.circle(world, branchdst.move(4), 2, leavesID, 5);
            return true;
        } else {
            return false;
        }
    }
}
