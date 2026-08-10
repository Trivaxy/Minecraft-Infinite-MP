package net.minecraft.game.world.generation.features.trees.deciduous;

public class CherryTree extends ProceduralTree {
    public CherryTree() {
        super(10, 10, 8);
    }

    @Override
    protected void prepare() {
        this.foliageHeight = 3;
        this.foliageDensity = 1.3;
        this.widthScale = 1.3;
        super.prepare();
    }
}
