package net.minecraft.game.world.generation.features.trees.deciduous;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public class AspenTree extends Tree {
    private int trunkID;
    private int trunkMeta;
    private int leavesID;
    private int leavesMeta;
    private byte maxRadius;

    public AspenTree() {
        this.trunkID = BlockList.wood.id;
        this.trunkMeta = 1;
        this.leavesID = BlockList.leaves.id;
        this.leavesMeta = 2;
        this.maxRadius = 3;
    }

    public AspenTree(int trunkId, int trunkMeta, int leavesId, int leavesMeta, byte maxradi) {
        this.trunkID = trunkId;
        this.trunkMeta = trunkMeta;
        this.leavesID = leavesId;
        this.leavesMeta = leavesMeta;
        this.maxRadius = maxradi;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (y > world.getWorldHeight() - 8) {
            return false;
        }

        int underID = world.getBlockId(x, y - 1, z);
        if (underID != BlockList.grass.id && underID != BlockList.dirt.id) {
            return false;
        }

        int trunkLength = 2 + random.nextInt(2);
        int canopyHalfLength = 3 + random.nextInt(6);
        int length = trunkLength + 2 * canopyHalfLength;
        boolean keep = true;

        for (int yy = y; yy <= y + 1 + length; yy++) {
            byte rad = 1;
            if (yy == y) {
                rad = 0;
            }

            if (yy >= y + trunkLength) {
                rad = this.maxRadius;
            }

            for (int xx = x - rad; xx <= x + rad && keep; xx++) {
                for (int zz = z - rad; zz <= z + rad && keep; zz++) {
                    if (yy >= 0 && yy < world.getWorldHeight()) {
                        int blockID = world.getBlockId(xx, yy, zz);
                        if (blockID != 0 && blockID != BlockList.leaves.id && blockID != BlockList.snowLayer.id) {
                            keep = false;
                        }
                    } else {
                        keep = false;
                    }
                }
            }
        }

        if (!keep) {
            return false;
        }

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
                        world.setBlockAndMetadata(xx, yy, zz, this.leavesID, this.leavesMeta);
                    }
                }
            }

            radius += 0.2F + random.nextFloat() * 0.5F;
            if (radius > this.maxRadius) {
                radius = this.maxRadius;
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
                        world.setBlockAndMetadata(xx, yy, zz, this.leavesID, this.leavesMeta);
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
            world.setBlockAndMetadata(x, y++, z, this.trunkID, this.trunkMeta);
        }

        return true;
    }
}
