package net.minecraft.game.world.generation.features.trees.coniferous;

import net.minecraft.game.world.generation.features.trees.deciduous.ProceduralTree;

public class GoldTree extends ProceduralTree {
    public GoldTree() {
        super(12, 16, 12);
    }

    @Override
    protected void prepare() {
        this.foliageHeight = 2;
        this.foliageDensity = 1.5;
        this.widthScale = 0.7;
        this.heightVariance = 9;
        super.prepare();
    }
}
