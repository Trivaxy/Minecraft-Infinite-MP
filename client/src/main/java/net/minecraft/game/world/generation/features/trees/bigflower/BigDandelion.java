package net.minecraft.game.world.generation.features.trees.bigflower;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class BigDandelion extends BigFlower {
    public BigDandelion() {
        this(4, true);
    }

    public BigDandelion(int minTreeHeight, boolean vinesGrow) {
        super(minTreeHeight, vinesGrow);
    }

    @Override
    protected void createShape(World world, int x, int y, int z, int height, int y0) {
        world.setBlockAndMetadata(x, y + y0, z, BlockList.wood.id, 17);
        world.setBlockAndMetadata(x, y + height, z, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 1, y + height, z, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 1, y + height, z, bigFlowerId, 0);
        world.setBlockAndMetadata(x, y + height, z - 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x, y + height, z + 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 1, y + height, z - 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 1, y + height, z + 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 1, y + height, z - 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 1, y + height, z + 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 1, y + height, z - 2, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 1, y + height, z + 2, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 1, y + height, z - 2, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 1, y + height, z + 2, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 2, y + height, z - 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x - 2, y + height, z + 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 2, y + height, z - 1, bigFlowerId, 0);
        world.setBlockAndMetadata(x + 2, y + height, z + 1, bigFlowerId, 0);
        if (y0 == height / 2) {
            if (world.rand.nextBoolean()) {
                world.setBlockAndMetadata(x - 1, y + y0, z, BlockList.wood.id, 17);
                world.setBlockAndMetadata(x + 1, y + y0 - 1, z, BlockList.wood.id, 17);
            } else {
                world.setBlockAndMetadata(x, y + y0, z - 1, BlockList.wood.id, 17);
                world.setBlockAndMetadata(x, y + y0 - 1, z + 1, BlockList.wood.id, 17);
            }
        }
    }
}
