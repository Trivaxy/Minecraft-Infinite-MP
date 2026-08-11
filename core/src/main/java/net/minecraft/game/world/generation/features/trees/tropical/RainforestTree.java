package net.minecraft.game.world.generation.features.trees.tropical;

import net.minecraft.game.world.generation.features.trees.deciduous.ProceduralTree;

public class RainforestTree extends ProceduralTree {
    public RainforestTree() {
    }

    public RainforestTree(int hght) {
        this.foliageDensity = 0.3;
        this.branchDensity = 0.5;
        this.branchSlope = 1.0;
        this.foliageHeight = 3;
        this.leafMeta = 13;
        this.logMeta = 11;
        this.height = 7 + hght;
    }

    @Override
    protected float treeShape(int y) {
        float width = this.height * 0.382F;
        float topdist = (this.height - y) / (this.height * 0.2F);
        return width * (0.618F + topdist) * (0.618F + this.rnd.nextFloat()) * 0.382F;
    }
}
