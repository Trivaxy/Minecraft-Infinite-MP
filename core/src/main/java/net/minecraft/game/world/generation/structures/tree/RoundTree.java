package net.minecraft.game.world.generation.structures.tree;

public class RoundTree extends ForesterTree {
    @Override
    public void prepare() {
        this.branchSlope = 0.382;
        super.prepare();
        this.foliageShape = new double[]{2.0, 3.0, 3.0, 2.5, 1.6};
        this.trunkRadius *= 0.8;
        this.trunkHeight = this.treeTrunkHeight * this.trunkHeight;
    }

    @Override
    protected double shape(int y) {
        double twigs = super.shape(y);
        if (!Double.isNaN(twigs)) {
            return twigs;
        }

        if (y < this.height * (0.282 + 0.1 * Math.sqrt(this.random.nextDouble()))) {
            return Double.NaN;
        }

        double radius = this.height / 2.0;
        double adj = this.height / 2.0 - y;
        double dist;
        if (adj == 0.0) {
            dist = radius;
        } else if (Math.abs(adj) > radius) {
            dist = 0.0;
        } else {
            dist = Math.sqrt(Math.pow(radius, 2.0) - Math.pow(adj, 2.0));
            dist *= 0.618;
        }

        return dist;
    }
}
