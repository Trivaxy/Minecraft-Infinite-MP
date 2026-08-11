package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class RightTurn extends LeftTurn {
    public RightTurn(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id, rand, bb, rot);
    }

    @Override
    public void buildComponent(LegacyStructurePiece componente, List<LegacyStructurePiece> pcs, Random rand) {
        if (this.rotation != 2 && this.rotation != 3) {
            this.getNextComponentX((EntryStairs)componente, pcs, rand, 1, 1);
        } else {
            this.getNextComponentZ((EntryStairs)componente, pcs, rand, 1, 1);
        }
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
            this.fill(world, bb, 0, 1, 1, 0, 3, 3, 0, 0, false);
        } else {
            this.fill(world, bb, 4, 1, 1, 4, 3, 3, 0, 0, false);
        }

        return true;
    }
}
