package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class BaobabTree extends Tree {
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 8;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 7;
    public int height = 6;

    public void leavesCluster(World world, Random rand, int x, int y, int z) {
        this.setBlockIfEmpty(world, x, y, z, leavesID, 8);
        if (rand.nextBoolean()) {
            this.setBlockIfEmpty(world, x - 1, y, z, leavesID, 8);
            this.setBlockIfEmpty(world, x + 1, y, z, leavesID, 8);
            this.setBlockIfEmpty(world, x, y, z - 1, leavesID, 8);
            this.setBlockIfEmpty(world, x, y, z + 1, leavesID, 8);
        } else {
            this.setBlockIfEmpty(world, x - 1, y, z - 1, leavesID, 8);
            this.setBlockIfEmpty(world, x + 1, y, z - 1, leavesID, 8);
            this.setBlockIfEmpty(world, x - 1, y, z + 1, leavesID, 8);
            this.setBlockIfEmpty(world, x + 1, y, z + 1, leavesID, 8);
        }
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        this.height = rand.nextInt(3) + 6;
        if (y0 + this.height > world.getWorldHeight() - 2) {
            return false;
        }

        int width = 2;
        int x1 = x0 - 1;
        int x2 = x0 + width - 2;
        int z1 = z0 - 1;
        int z2 = z0 + width - 2;

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                if (!this.validGround(world, x0, y0 - 1, z0) || y0 + this.height > world.getWorldHeight() - 1) {
                    return false;
                }

                for (int y = y0 + 1; y < y0 + this.height; y++) {
                    if (world.isSolid(x, y, z)) {
                        return false;
                    }
                }
            }
        }

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                for (int y = y0; y < y0 + this.height; y++) {
                    this.setBlockIfEmpty(world, x, y, z, trunkID, 7);
                }
            }
        }

        if (rand.nextBoolean()) {
            this.setBlockIfEmpty(world, x1 - 1, y0, z1 + rand.nextInt(width), trunkID, 7);
        }

        if (rand.nextBoolean()) {
            this.setBlockIfEmpty(world, x2 + 1, y0, z1 + rand.nextInt(width), trunkID, 7);
        }

        if (rand.nextBoolean()) {
            this.setBlockIfEmpty(world, x1 + rand.nextInt(width), y0, z1 - 1, trunkID, 7);
        }

        if (rand.nextBoolean()) {
            this.setBlockIfEmpty(world, x1 + rand.nextInt(width), y0, z2 + 1, trunkID, 7);
        }

        int srcX = x1 - 1;
        int dstX = x1 - 3 - (rand.nextInt(4) == 0 ? 1 : 0);
        int srcY = y0 + this.height;
        int dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
        int srcZ = z1 + rand.nextInt(width);
        int dstZ = srcZ + rand.nextInt(3) - 1;
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        srcX = x2 + 1;
        dstX = x2 + 3 + (rand.nextInt(4) == 0 ? 1 : 0);
        dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
        srcZ = z1 + rand.nextInt(width);
        dstZ = srcZ + rand.nextInt(3) - 1;
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        srcZ = z1 - 1;
        dstZ = z1 - 3 - (rand.nextInt(4) == 0 ? 1 : 0);
        dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
        srcX = x1 + rand.nextInt(width);
        dstX = srcX + rand.nextInt(3) - 1;
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        srcZ = z2 + 1;
        dstZ = z2 + 3 + (rand.nextInt(4) == 0 ? 1 : 0);
        dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
        srcX = x1 + rand.nextInt(width);
        dstX = srcX + rand.nextInt(3) - 1;
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        dstY = y0 + this.height + 2 + (rand.nextInt(4) == 0 ? 1 : 0);
        if (rand.nextBoolean()) {
            srcX = x1;
            dstX = x1 - 1;
        } else {
            srcX = x2;
            dstX = x2 + 1;
        }

        srcZ = dstZ = z1 + rand.nextInt(width);
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        dstY = y0 + this.height + 2 + (rand.nextInt(4) == 0 ? 1 : 0);
        if (rand.nextBoolean()) {
            srcZ = z1;
            dstZ = z1 - 1;
        } else {
            srcZ = z2;
            dstZ = z2 + 1;
        }

        srcX = dstX = x1 + rand.nextInt(width);
        this.drawBresehnam(world, srcX, srcY, srcZ, dstX, dstY, dstZ, trunkID, 7, true);
        this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
        return true;
    }
}
