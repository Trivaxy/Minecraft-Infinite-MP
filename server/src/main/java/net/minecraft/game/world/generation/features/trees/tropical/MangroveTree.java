package net.minecraft.game.world.generation.features.trees.tropical;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.Direction;

public class MangroveTree extends Tree {
    private static final int leavesID = BlockList.leaves.id;
    private static final int leavesMeta = 7;
    private static final int trunkID = BlockList.wood.id;
    private static final int trunkMeta = 6;
    private boolean generate = false;

    public MangroveTree() {
    }

    public MangroveTree(boolean forceGenerate) {
        this.generate = forceGenerate;
    }

    @Override
    public boolean place(World world, Random rand, int x0, int y0, int z0) {
        BlockPosition origin = new BlockPosition().set(x0, y0, z0);
        Material materialBeneath = world.getBlockMaterial(x0, y0 - 1, z0);
        Material materialOn = world.getBlockMaterial(x0, y0, z0);
        if (!this.generate && materialBeneath != MaterialList.water && materialOn != MaterialList.water) {
            return false;
        }

        this.buildBranch(world, rand, origin, 5, 5 + rand.nextInt(4), 0.0, 0.0);
        int branches = rand.nextInt(3);
        double angleOffs = (Math.PI * 2) * rand.nextDouble();

        for (int i = 0; i < branches; i++) {
            this.buildBranch(world, rand, origin, 7 + i, 6 + rand.nextInt(3), (Math.PI * 2) * i / 3.0 + angleOffs, Math.PI / 4);
        }

        int roots = 3 + rand.nextInt(2);
        angleOffs = (Math.PI * 2) * rand.nextDouble();

        for (int i = 0; i < roots; i++) {
            this.buildRoot(world, rand, origin, 5, 8.0, (Math.PI * 4.0 / 5.0) * i + angleOffs, (Math.PI * 3.0 / 4.0) + rand.nextDouble() * 0.1);
        }

        return true;
    }

    private void buildRoot(World world, Random rand, BlockPosition origin, int height, double length, double angle, double tilt) {
        BlockPosition src = origin.copy().move(4, height);
        BlockPosition dst = this.findEndPoint(src, length, angle, tilt);
        this.bresenham(world, src, dst, trunkID, 6);

        while (dst.y > 8 && world.getBlockMaterial(dst.x, dst.y, dst.z) == MaterialList.water) {
            dst.move(5);
            this.setBlock(world, dst, trunkID, 6);
        }
    }

    private void buildBranch(World world, Random rand, BlockPosition origin, int height, double length, double angle, double tilt) {
        BlockPosition src = origin.copy().move(4, height);
        BlockPosition dst = this.findEndPoint(src, length, angle, tilt);
        this.bresenham(world, src, dst, trunkID, 6);

        for (int direction : Direction.HORZ_PLANE) {
            this.setBlock(world, dst.copy().move(direction), trunkID, 6);
        }

        int canopy = 2 + rand.nextInt(3);
        this.circle(world, dst.copy().move(5), canopy - 1, leavesID, 7);
        this.circle(world, dst, canopy, leavesID, 7);
        this.circle(world, dst.copy().move(4), canopy - 1, leavesID, 7);
    }
}
