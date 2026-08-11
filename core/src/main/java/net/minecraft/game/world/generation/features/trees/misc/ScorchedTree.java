package net.minecraft.game.world.generation.features.trees.misc;

import net.minecraft.game.world.generation.features.trees.deciduous.ProceduralTree;

public class ScorchedTree extends ProceduralTree {
    public ScorchedTree() {
        super(8, 12);
        this.height = 12;
    }

    @Override
    protected void foliageCluster(int x, int y, int z) {
    }
}
