package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.Direction;

public class PalmTree extends Tree {
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 4;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 3;

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        if (!this.validGround(world, x0, y0 - 1, z0)) {
            return false;
        }

        for (int y = y0 + 8; y < y0 + 10; y++) {
            for (int x = x0 - 2; x <= x0 + 2; x++) {
                for (int z = z0 - 2; z <= z0 + 2; z++) {
                    if (world.isSolid(x, y, z) && world.getBlockMaterial(x, y, z) != MaterialList.leaves) {
                        return false;
                    }
                }
            }
        }

        double arch;
        int height;
        if (rand.nextBoolean()) {
            arch = 0.1 + rand.nextDouble() * 0.35;
            height = 10;
        } else {
            arch = 0.05 + rand.nextDouble() * 0.25;
            height = 8;
        }

        BlockPosition cursor = new BlockPosition().set(x0, y0 - 1, z0);
        this.setBlock(world, cursor, BlockList.dirt.id, 0);
        int direction = Direction.HORZ_PLANE[rand.nextInt(Direction.HORZ_PLANE.length)];

        for (int y = 0; y < height; y++) {
            cursor.x = x0;
            cursor.z = z0;
            cursor.move(direction, (int)Math.floor(arch));
            this.setBlock(world, cursor.move(4), trunkID, 3);
            arch *= 1.3;
        }

        this.edgesPlusShape(world, cursor.move(5), 5, leavesID, 4);
        this.edgesPlusShape(world, cursor.move(4), 5, leavesID, 4);
        this.edgesSquareShape(world, cursor.move(4), 5, leavesID, 4);
        this.edgesPlusShape(world, cursor, 3, leavesID, 4);
        this.setBlock(world, cursor.move(4), leavesID, 4);
        this.edgesSquareShape(world, cursor, 3, leavesID, 4);
        return true;
    }

    public void edgesPlusShape(World world, BlockPosition blockPos, int w, int leavesID, int leavesMeta) {
        this.edgesPlusShape(world, blockPos.x, blockPos.y, blockPos.z, w, leavesID, leavesMeta);
    }

    public void edgesPlusShape(World world, int x, int y, int z, int w, int leavesID, int leavesMeta) {
        int offset = w >> 1;
        if ((w & 1) == 0) {
            offset--;
        }

        this.setBlockIfEmpty(world, x - offset, y, z, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x + offset, y, z, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x, y, z - offset, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x, y, z + offset, leavesID, leavesMeta);
    }

    public void edgesSquareShape(World world, BlockPosition blockPos, int w, int leavesID, int leavesMeta) {
        this.edgesSquareShape(world, blockPos.x, blockPos.y, blockPos.z, w, leavesID, leavesMeta);
    }

    public void edgesSquareShape(World world, int x, int y, int z, int w, int leavesID, int leavesMeta) {
        int offset = w >> 1;
        if ((w & 1) == 0) {
            offset--;
        }

        this.setBlockIfEmpty(world, x - offset, y, z - offset, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x + offset, y, z - offset, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x - offset, y, z + offset, leavesID, leavesMeta);
        this.setBlockIfEmpty(world, x + offset, y, z + offset, leavesID, leavesMeta);
    }
}
