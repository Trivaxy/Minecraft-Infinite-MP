package net.minecraft.game.world.generation.features.trees.deciduous;

public class MapleTree extends ProceduralTree {
    public MapleTree() {
        super(9, 9, 9);
    }

    @Override
    protected void prepare() {
        this.foliageDensity = 0.5;
        this.branchDensity = 0.3;
        this.branchSlope = 1.0;
        super.prepare();
    }
}
