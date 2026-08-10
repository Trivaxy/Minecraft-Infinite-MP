package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class LeftTurn extends StrongholdPiece {
    protected final DoorType doorType;

    public LeftTurn(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece componente, List<LegacyStructurePiece> pcs, Random rand) {
        if (this.rotation != 2 && this.rotation != 3) {
            this.getNextComponentZ((EntryStairs)componente, pcs, rand, 1, 1);
        } else {
            this.getNextComponentX((EntryStairs)componente, pcs, rand, 1, 1);
        }
    }

    public static LeftTurn findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, 5, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new LeftTurn(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 4, 4, 4, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 1, 0);
        if (this.rotation != 2 && this.rotation != 3) {
            this.fill(world, bb, 4, 1, 1, 4, 3, 3, 0, 0, false);
        } else {
            this.fill(world, bb, 0, 1, 1, 0, 3, 3, 0, 0, false);
        }

        return true;
    }
}
