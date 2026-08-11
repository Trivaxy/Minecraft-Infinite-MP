package net.minecraft.game.world.generation.features.trees.bigflower;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class BigLilac extends BigFlower {
    protected int outerMeta = 3;
    protected int innerMeta = 3;

    public BigLilac() {
        this(6, true);
    }

    public BigLilac(int minTreeHeight, boolean vinesGrow) {
        super(minTreeHeight, vinesGrow);
    }

    public BigLilac(int meta1, int meta2) {
        this(6, false);
        this.outerMeta = meta1;
        this.innerMeta = meta2;
    }

    @Override
    protected void createShape(World world, int x, int y, int z, int height, int y0) {
        world.setBlockAndMetadata(x, y + y0, z, BlockList.wood.id, 17);
        world.setBlockAndMetadata(x - 1, y + (height - 1), z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + (height - 1), z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + (height - 1), z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + (height - 1), z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 2, y + height, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 2, y + height, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height, z - 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height, z + 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 2, y + height + 1, z, bigFlowerId, this.innerMeta);
        world.setBlockAndMetadata(x + 2, y + height + 1, z, bigFlowerId, this.innerMeta);
        world.setBlockAndMetadata(x, y + height + 1, z - 2, bigFlowerId, this.innerMeta);
        world.setBlockAndMetadata(x, y + height + 1, z + 2, bigFlowerId, this.innerMeta);
        world.setBlockAndMetadata(x - 2, y + height + 1, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 2, y + height + 1, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 2, y + height + 1, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 2, y + height + 1, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 1, z - 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 1, z - 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 1, z + 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 1, z + 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 2, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 2, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 2, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 2, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 2, y + height + 2, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 2, y + height + 2, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 2, z - 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 2, z + 2, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 2, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 2, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 2, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 2, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x - 1, y + height + 3, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x + 1, y + height + 3, z, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 3, z - 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 3, z + 1, bigFlowerId, this.outerMeta);
        world.setBlockAndMetadata(x, y + height + 3, z, bigFlowerId, this.outerMeta);
    }
}
