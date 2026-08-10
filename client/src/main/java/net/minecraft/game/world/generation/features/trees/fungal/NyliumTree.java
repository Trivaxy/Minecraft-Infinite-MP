package net.minecraft.game.world.generation.features.trees.fungal;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.Direction;

public class NyliumTree extends Tree {
    private static final int leavesID = BlockList.mushroomCap.id;
    private static final int leavesMeta = 5;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 14;

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        BlockPosition origin = new BlockPosition().set(x0, y0, z0);
        int height = 6 + rand.nextInt(4);
        if (this.validGround(world, x0, y0 - 1, z0)
            && y0 + height <= world.getWorldHeight() - 1
            && world.getBlock(x0, y0 + height, z0) == null
            && (world.getBlock(x0, y0, z0) == null || world.getBlock(x0, y0, z0) == BlockList.mushroomNylium)) {
            this.buildBranch(world, rand, origin, 0, height, 0.0, 0.0);
            this.setBlockIfEmpty(world, x0, y0 - 1, z0, trunkID, 14);
            int branches = rand.nextInt(3);
            BlockPosition originBranch = new BlockPosition().set(x0, y0 + 3, z0);

            for (int i = 0; i < branches; i++) {
                this.buildBranch(
                    world, rand, originBranch, height - 10 + i, 9.0, (Math.PI * 2) * rand.nextInt(4) / 3.0, (Math.PI / 5) + rand.nextDouble() * 0.2 - 0.1
                );
            }

            return true;
        } else {
            return false;
        }
    }

    private void buildBranch(World world, Random rand, BlockPosition origin, int height, double length, double angle, double tilt) {
        BlockPosition src = origin.copy().move(4, height);
        BlockPosition dst = this.findEndPoint(src, length, angle, tilt);
        this.bresenham(world, src, dst, trunkID, 14);

        for (int direction : Direction.HORZ_PLANE) {
            this.setBlock(world, dst.copy().move(direction), trunkID, 14);
        }

        this.circle(world, dst.copy().move(5), 3, leavesID, 5, BlockList.shroomLight.id, 0);
        this.circle(world, dst, 4, leavesID, 5, BlockList.shroomLight.id, 0);
        this.circle(world, dst.copy().move(4), 2, leavesID, 5, BlockList.shroomLight.id, 0);
    }

    public void circle(World world, BlockPosition src, int rad, int blockID, int meta, int secondaryBlock, int secondaryMeta) {
        this.circle(world, src.x, src.y, src.z, rad, blockID, meta, secondaryBlock, secondaryMeta);
    }

    public void circle(World world, int x0, int y0, int z0, int rad, int blockID, int meta, int secondaryBlock, int secondaryMeta) {
        for (byte dx = 0; dx <= rad; dx++) {
            for (byte dz = 0; dz <= rad; dz++) {
                int dist = (int)(Math.max(dx, dz) + Math.min(dx, dz) * 0.5);
                if (dx == 3 && dz == 3) {
                    dist = 6;
                }

                if (dist <= rad) {
                    if (world.rand.nextInt(25) != 0) {
                        this.setBlockIfEmpty(world, x0 + dx, y0, z0 + dz, blockID, meta);
                    } else {
                        this.setBlockIfEmpty(world, x0 + dx, y0, z0 + dz, secondaryBlock, secondaryMeta);
                    }

                    if (world.rand.nextInt(25) != 0) {
                        this.setBlockIfEmpty(world, x0 + dx, y0, z0 - dz, blockID, meta);
                    } else {
                        this.setBlockIfEmpty(world, x0 + dx, y0, z0 - dz, secondaryBlock, secondaryMeta);
                    }

                    if (world.rand.nextInt(25) != 0) {
                        this.setBlockIfEmpty(world, x0 - dx, y0, z0 + dz, blockID, meta);
                    } else {
                        this.setBlockIfEmpty(world, x0 - dx, y0, z0 + dz, secondaryBlock, secondaryMeta);
                    }

                    if (world.rand.nextInt(25) != 0) {
                        this.setBlockIfEmpty(world, x0 - dx, y0, z0 - dz, blockID, meta);
                    } else {
                        this.setBlockIfEmpty(world, x0 - dx, y0, z0 - dz, secondaryBlock, secondaryMeta);
                    }
                }
            }
        }
    }
}
