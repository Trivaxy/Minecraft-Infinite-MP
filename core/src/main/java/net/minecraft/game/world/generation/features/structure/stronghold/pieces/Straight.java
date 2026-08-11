package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class Straight extends StrongholdPiece {
    private final DoorType doorType;
    private final boolean expandsX;
    private final boolean expandsZ;

    public Straight(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
        this.expandsX = rand.nextInt(2) == 0;
        this.expandsZ = rand.nextInt(2) == 0;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntryStairs)piece, pcs, rand, 1, 1);
        if (this.expandsX) {
            this.getNextComponentX((EntryStairs)piece, pcs, rand, 1, 2);
        }

        if (this.expandsZ) {
            this.getNextComponentZ((EntryStairs)piece, pcs, rand, 1, 2);
        }
    }

    public static Straight findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, 7, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new Straight(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 4, 4, 6, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 1, 0);
        this.placeDoor(world, rand, bb, DoorType.OPENING, 1, 1, 6);
        this.placeRandomly(world, bb, rand, 0.1F, 1, 2, 1, BlockList.torch.id, 0);
        this.placeRandomly(world, bb, rand, 0.1F, 3, 2, 1, BlockList.torch.id, 0);
        this.placeRandomly(world, bb, rand, 0.1F, 1, 2, 5, BlockList.torch.id, 0);
        this.placeRandomly(world, bb, rand, 0.1F, 3, 2, 5, BlockList.torch.id, 0);
        if (this.expandsX) {
            this.fill(world, bb, 0, 1, 2, 0, 3, 4, 0, 0, false);
        }

        if (this.expandsZ) {
            this.fill(world, bb, 4, 1, 2, 4, 3, 4, 0, 0, false);
        }

        return true;
    }
}
