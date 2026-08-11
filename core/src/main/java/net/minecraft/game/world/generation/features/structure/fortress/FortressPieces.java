package net.minecraft.game.world.generation.features.structure.fortress;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BossRoom;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeCrossing1;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeCrossing2;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeCrossing3;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeStraight;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeThrone;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeType1;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeType2;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.LeftTurn;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.MushroomStaircase;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.RightTurn;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.Staircase;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.Tower;

public class FortressPieces {
    private static final PieceWeight[] mainPieces = new PieceWeight[]{
        new PieceWeight(BridgeStraight.class, 30, 0, true),
        new PieceWeight(BridgeCrossing3.class, 10, 4),
        new PieceWeight(BridgeCrossing1.class, 10, 4),
        new PieceWeight(Tower.class, 10, 3),
        new PieceWeight(BridgeThrone.class, 5, 2),
        new PieceWeight(BossRoom.class, 5, 1)
    };
    private static final PieceWeight[] connectingPieces = new PieceWeight[]{
        new PieceWeight(BridgeType2.class, 25, 0, true),
        new PieceWeight(BridgeCrossing2.class, 15, 5),
        new PieceWeight(RightTurn.class, 5, 10),
        new PieceWeight(LeftTurn.class, 5, 10),
        new PieceWeight(Staircase.class, 10, 3, true),
        new PieceWeight(BridgeType1.class, 7, 2),
        new PieceWeight(MushroomStaircase.class, 5, 2)
    };

    private static FortressPiece getNextPiece(PieceWeight wght, List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        Class<?> class8 = wght.piece;
        Object thePiss = null;
        if (class8 == BridgeStraight.class) {
            thePiss = BridgeStraight.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeCrossing3.class) {
            thePiss = BridgeCrossing3.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeCrossing1.class) {
            thePiss = BridgeCrossing1.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == Tower.class) {
            thePiss = Tower.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeThrone.class) {
            thePiss = BridgeThrone.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BossRoom.class) {
            thePiss = BossRoom.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeType2.class) {
            thePiss = BridgeType2.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == RightTurn.class) {
            thePiss = RightTurn.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == LeftTurn.class) {
            thePiss = LeftTurn.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == Staircase.class) {
            thePiss = Staircase.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeType1.class) {
            thePiss = BridgeType1.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == BridgeCrossing2.class) {
            thePiss = BridgeCrossing2.createValidComponent(pcs, rand, x, y, z, rot, id);
        } else if (class8 == MushroomStaircase.class) {
            thePiss = MushroomStaircase.createValidComponent(pcs, rand, x, y, z, rot, id);
        }

        return (FortressPiece)thePiss;
    }

    static FortressPiece createNextComponent(PieceWeight wght, List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        return getNextPiece(wght, pcs, rand, x, y, z, rot, id);
    }

    public static PieceWeight[] getMainPieces() {
        return mainPieces;
    }

    public static PieceWeight[] getConnectingPieces() {
        return connectingPieces;
    }
}
