package net.minecraft.game.world.generation.features.trees.bigflower;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class BigPetunia extends BigFlower {
    public BigPetunia() {
        this(6, true);
    }

    public BigPetunia(int minTreeHeight, boolean vinesGrow) {
        super(minTreeHeight, vinesGrow);
    }

    @Override
    protected void createShape(World world, int x, int y, int z, int height, int y0) {
        world.setBlockAndMetadata(x, y + y0, z, BlockList.wood.id, 17);
        world.setBlockAndMetadata(x - 1, y + (height - 1), z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + (height - 1), z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + (height - 1), z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + (height - 1), z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + (height - 1), z, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + (height - 1), z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + (height - 1), z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + (height - 1), z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + (height - 1), z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 1, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 1, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 1, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 1, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 1, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 1, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 1, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 1, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 1, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 1, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 1, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 1, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 1, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 1, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 1, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 1, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 1, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 1, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 1, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 1, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 1, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 2, y + height + 2, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z + 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 2, y + height + 2, z - 1, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 2, z - 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x + 1, y + height + 2, z + 2, bigFlowerId, 2);
        world.setBlockAndMetadata(x - 1, y + height + 2, z + 2, bigFlowerId, 2);
        if (y0 == height / 2) {
            world.setBlockAndMetadata(x + 1, y + y0 / 2, z, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x - 1, y + y0 / 2, z, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x, y + y0 / 2, z + 1, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x, y + y0 / 2, z - 1, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x + 2, y + y0 / 2 + 1, z, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x - 2, y + y0 / 2 + 1, z, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x, y + y0 / 2 + 1, z + 2, BlockList.wood.id, 17);
            world.setBlockAndMetadata(x, y + y0 / 2 + 1, z - 2, BlockList.wood.id, 17);
        }
    }
}
