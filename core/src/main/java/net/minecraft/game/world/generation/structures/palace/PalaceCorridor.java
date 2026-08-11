package net.minecraft.game.world.generation.structures.palace;

public class PalaceCorridor extends PalacePiece {
    public PalaceCorridor(boolean rotated, Palace featureIcePalace) {
        super(rotated, featureIcePalace);
    }

    @Override
    public int[][][] getPiece() {
        return new int[][][]{
            {{16457, 16457, 16457}, {61523, 0, 0}, {16457, 16457, 16457}},
            {{16457, 34, 0}, {16457, 0, 0}, {16457, 34, 0}},
            {{16457, 34, 0}, {61523, 0, 0}, {16457, 34, 0}},
            {{16457, 34, 0}, {193, 0, 0}, {16457, 34, 0}},
            {{16457, 34, 0}, {61523, 0, 0}, {16457, 34, 0}},
            {{16457, 34, 0}, {16457, 0, 0}, {16457, 34, 0}},
            {{16457, 16457, 16457}, {61523, 0, 0}, {16457, 16457, 16457}}
        };
    }
}
