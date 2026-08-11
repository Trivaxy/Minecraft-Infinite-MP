package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPieces;
import net.minecraft.game.world.generation.features.structure.fortress.PieceWeight;

public class EntranceBridge extends BridgeCrossing3 {
    public PieceWeight pieceWeight;
    public List<PieceWeight> pieces = new ArrayList<>();
    public List<PieceWeight> spawners;
    public ArrayList<Object> maxPieces = new ArrayList<>();

    public EntranceBridge(Random rand, int x, int z) {
        super(rand, x, z);

        for (PieceWeight wght : FortressPieces.getMainPieces()) {
            wght.maxLimit = 0;
            this.pieces.add(wght);
        }

        this.spawners = new ArrayList<>();

        for (PieceWeight wght : FortressPieces.getConnectingPieces()) {
            wght.maxLimit = 0;
            this.spawners.add(wght);
        }
    }
}
