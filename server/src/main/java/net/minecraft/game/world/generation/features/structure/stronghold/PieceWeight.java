package net.minecraft.game.world.generation.features.structure.stronghold;

public class PieceWeight {
    public Class<?> pieceClass;
    public final int pieceWeight;
    public int instancesSpawned;
    public int instancesLimit;

    public PieceWeight(Class<?> pieceClass, int pieceWeight, int instancesLimit) {
        this.pieceClass = pieceClass;
        this.pieceWeight = pieceWeight;
        this.instancesLimit = instancesLimit;
    }

    public boolean continueGenerating(int id) {
        return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
    }

    public boolean continueGenerating() {
        return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
    }
}
