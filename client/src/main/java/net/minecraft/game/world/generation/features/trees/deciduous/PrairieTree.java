package net.minecraft.game.world.generation.features.trees.deciduous;

public class PrairieTree extends ProceduralTree {
    public PrairieTree(int id, int meta) {
        super(id, meta, 12);
    }

    @Override
    protected void prepare() {
        this.foliageHeight = 2;
        this.foliageDensity = 1.3;
        this.heightVariance = 9;
        super.prepare();
    }
}
