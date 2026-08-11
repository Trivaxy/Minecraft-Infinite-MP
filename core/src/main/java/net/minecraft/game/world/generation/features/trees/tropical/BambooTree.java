package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class BambooTree extends Tree {
    final int trunkID = BlockList.bamboo.id;
    final int trunkMeta = 0;
    final int leavesID = BlockList.leaves.id;
    final int leavesMeta = 11;
    final int maxRadius = 3;

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (y > world.getWorldHeight() - 8) {
            return false;
        }

        int underID = world.getBlockId(x, y - 1, z);
        if (underID != BlockList.grass.id && underID != BlockList.dirt.id) {
            return false;
        }

        int trunkLength = 8 + random.nextInt(4);
        int canopyHalfLength = 3 + random.nextInt(3);
        int length = trunkLength + 2 * canopyHalfLength;
        double radius = 0.0;
        int yy = y + trunkLength;

        for (int i = 0; i < canopyHalfLength; i++) {
            int rad = (int)Math.floor(radius);
            int x1 = x - rad;
            int x2 = x + rad;
            int z1 = z - rad;
            int z2 = z + rad;

            for (int xx = x1; xx <= x2; xx++) {
                for (int zz = z1; zz <= z2; zz++) {
                    if (xx != x1 && xx != x2 && zz != z1 && zz != z2 || random.nextInt(4) != 0) {
                        world.setBlockAndMetadata(xx, yy, zz, this.leavesID, 11);
                    }
                }
            }

            radius += 0.2F + random.nextFloat() * 0.5F;
            if (radius > 3.0) {
                radius = 3.0F;
            }

            yy++;
        }

        for (int i = 0; i < canopyHalfLength; i++) {
            int rad = (int)Math.floor(radius);
            int x1 = x - rad;
            int x2 = x + rad;
            int z1 = z - rad;
            int z2 = z + rad;

            for (int xx = x1; xx <= x2; xx++) {
                for (int zz = z1; zz <= z2; zz++) {
                    if (rad == 0 || xx != x1 && xx != x2 && zz != z1 && zz != z2 || random.nextInt(4) != 0) {
                        world.setBlockAndMetadata(xx, yy, zz, this.leavesID, 11);
                    }
                }
            }

            radius -= 0.2F + random.nextFloat() * 0.5F;
            if (radius < 0.0) {
                radius = 0.0;
            }

            yy++;
        }

        for (int i = 0; i < length - 3; i++) {
            world.setBlockAndMetadata(x, y++, z, this.trunkID, 0);
        }

        return true;
    }
}
