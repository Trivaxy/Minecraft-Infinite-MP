package net.minecraft.game.world.generation.features.structure.stronghold;

public final class GuaranteedPieceWeight extends PieceWeight {
    GuaranteedPieceWeight(Class<?> pieceClass, int pieceWeight, int instancesLimit) {
        super(pieceClass, pieceWeight, instancesLimit);
    }

    @Override
    public boolean continueGenerating(int type) {
        return super.continueGenerating(type) && type > 4;
    }
}
