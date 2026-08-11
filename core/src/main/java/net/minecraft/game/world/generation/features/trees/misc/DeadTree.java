package net.minecraft.game.world.generation.features.trees.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.generation.features.trees.deciduous.ProceduralTree;

public class DeadTree extends ProceduralTree {
    public DeadTree() {
        super(0, 0);
        this.height = 12;
        this.secondaryBlockToGrowOn = BlockList.sand.id;
    }

    public DeadTree(int meta) {
        super(meta, 0);
        this.height = 12;
        this.secondaryBlockToGrowOn = BlockList.sand.id;
    }

    @Override
    protected void foliageCluster(int x, int y, int z) {
    }
}
