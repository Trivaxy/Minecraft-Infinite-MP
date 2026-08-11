package net.minecraft.game.world.generation.features.structure.stronghold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.BattleRoom;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.ChestCorridor;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Corridor;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Crossing;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.EntryStairs;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.LeftTurn;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Library;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Prison;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.RightTurn;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.RoomCrossing;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Stairs;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.StairsStraight;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.Straight;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.TreasureRoom;

public class StrongholdPieces {
    private static final PieceWeight[] pieceWeights = new PieceWeight[]{
        new PieceWeight(Straight.class, 40, 0),
        new PieceWeight(Prison.class, 5, 5),
        new PieceWeight(LeftTurn.class, 20, 0),
        new PieceWeight(RightTurn.class, 20, 0),
        new PieceWeight(RoomCrossing.class, 10, 6),
        new PieceWeight(StairsStraight.class, 5, 5),
        new PieceWeight(Stairs.class, 5, 5),
        new PieceWeight(Crossing.class, 5, 4),
        new PieceWeight(ChestCorridor.class, 5, 4),
        new GuaranteedPieceWeight(Library.class, 10, 2),
        new GuaranteedPieceWeight(TreasureRoom.class, 20, 1),
        new GuaranteedPieceWeight(BattleRoom.class, 40, 1)
    };
    private static final Class<?>[] surfaceAllowedComponents = new Class[]{
        Straight.class, LeftTurn.class, RightTurn.class, StairsStraight.class, BattleRoom.class
    };
    private static final BrickType brickTypes = new BrickType(null);
    private static List<PieceWeight> piecesList;
    private static Class<?> pieceType;
    static int totalWeight = 0;

    public static void prepareStructurePieces() {
        piecesList = new ArrayList<>();
        PieceWeight[] weights = pieceWeights;

        for (int doYouEvenLiftBro = 0; doYouEvenLiftBro < weights.length; doYouEvenLiftBro++) {
            PieceWeight pieceWeight = weights[doYouEvenLiftBro];
            pieceWeight.instancesSpawned = 0;
            piecesList.add(pieceWeight);
        }

        pieceType = null;
    }

    private static boolean canAddPieces() {
        boolean returnClause = false;
        totalWeight = 0;

        for (PieceWeight wght : piecesList) {
            if (wght.instancesLimit > 0 && wght.instancesSpawned < wght.instancesLimit) {
                returnClause = true;
            }

            totalWeight = totalWeight + wght.pieceWeight;
        }

        return returnClause;
    }

    private static StrongholdPiece getNextPiece(
        Class<?> class0, List<LegacyStructurePiece> pcs, Random rand, int startX, int startZ, int endX, int endZ, int idk
    ) {
        Object piece = null;
        if (class0 == TreasureRoom.class) {
            piece = TreasureRoom.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == BattleRoom.class) {
            piece = BattleRoom.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == Straight.class) {
            piece = Straight.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == Prison.class) {
            piece = Prison.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == LeftTurn.class) {
            piece = LeftTurn.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == RightTurn.class) {
            piece = RightTurn.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == RoomCrossing.class) {
            piece = RoomCrossing.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == StairsStraight.class) {
            piece = StairsStraight.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == Stairs.class) {
            piece = Stairs.getStrongholdStairsComponent(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == Crossing.class) {
            piece = Crossing.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == ChestCorridor.class) {
            piece = ChestCorridor.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        } else if (class0 == Library.class) {
            piece = Library.findValidPlacement(pcs, rand, startX, startZ, endX, endZ, idk);
        }

        return (StrongholdPiece)piece;
    }

    private static StrongholdPiece getNextComponent(EntryStairs start, List<LegacyStructurePiece> pcs, Random rand, int x0, int z0, int x1, int z1, int idk) {
        if (!canAddPieces()) {
            return null;
        }

        if (pieceType != null) {
            StrongholdPiece component = getNextPiece(pieceType, pcs, rand, x0, z0, x1, z1, idk);
            pieceType = null;
            if (component != null) {
                return component;
            }
        }

        int i = 0;

        while (i < 5) {
            i++;
            int next = rand.nextInt(totalWeight);

            for (PieceWeight wght : piecesList) {
                next -= wght.pieceWeight;
                if (next < 0) {
                    if (!wght.continueGenerating(idk) || wght == start.weight) {
                        break;
                    }

                    StrongholdPiece piece = getNextPiece(wght.pieceClass, pcs, rand, x0, z0, x1, z1, idk);
                    if (piece != null && (z0 < 136 || allowedOnSurface(piece))) {
                        wght.instancesSpawned++;
                        start.weight = wght;
                        if (!wght.continueGenerating()) {
                            piecesList.remove(wght);
                        }

                        return piece;
                    }
                }
            }
        }

        LegacyStructureBoundingBox bb = Corridor.getBounds(pcs, rand, x0, z0, x1, z1);
        return bb != null && bb.minY > 1 && z0 < 128 ? new Corridor(idk, rand, bb, z1) : null;
    }

    private static boolean allowedOnSurface(StrongholdPiece component) {
        return Arrays.asList(surfaceAllowedComponents).contains(component.getClass());
    }

    private static LegacyStructurePiece getNextValidComponent(
        EntryStairs start, List<LegacyStructurePiece> pcs, Random rand, int x0, int z0, int x1, int z1, int idk
    ) {
        if (idk > 50 && start.bossRoom != null) {
            return null;
        }

        if (Math.abs(x0 - start.getBoundingBox().minX) <= 112 && Math.abs(x1 - start.getBoundingBox().minZ) <= 112) {
            StrongholdPiece component = getNextComponent(start, pcs, rand, x0, z0, x1, z1, idk + 1);
            if (component != null) {
                pcs.add(component);
                start.structureList.add(component);
            }

            return component;
        } else {
            return null;
        }
    }

    static LegacyStructurePiece getNextValidComponentAccess(
        EntryStairs start, List<LegacyStructurePiece> pcs, Random rand, int x0, int z0, int x1, int z1, int idk
    ) {
        return getNextValidComponent(start, pcs, rand, x0, z0, x1, z1, idk);
    }

    public static Class<?> setComponentType(Class<?> clazz) {
        pieceType = clazz;
        return clazz;
    }

    public static BrickType getStrongholdStones() {
        return brickTypes;
    }
}
