package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.Stronghold;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class StairsStraight extends StrongholdPiece {
    private final DoorType doorType;

    public StairsStraight(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece componente, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntryStairs)componente, pcs, rand, 1, 1);
    }

    public static StairsStraight findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -7, 0, 5, 11, 8, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new StairsStraight(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 4, 10, 7, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 7, 0);
        this.placeDoor(world, rand, bb, DoorType.OPENING, 1, 1, 7);
        int meta = this.getMetadataWithOffset(BlockList.stairBrick.id, 2);

        for (int i = 0; i < 6; i++) {
            if (this.getBlock(world, 1, 6 - i, i, bb) != 0) {
                this.placeBlock(world, BlockList.stairBrick.id, meta, 1, 6 - i, 1 + i, bb);
            }

            if (this.getBlock(world, 2, 6 - i, i, bb) != 0) {
                this.placeBlock(world, BlockList.stairBrick.id, meta, 2, 6 - i, 1 + i, bb);
            }

            if (this.getBlock(world, 3, 6 - i, i, bb) != 0) {
                this.placeBlock(world, BlockList.stairBrick.id, meta, 3, 6 - i, 1 + i, bb);
            }

            if (i < 5) {
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 5 - i, 1 + i, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 2, 5 - i, 1 + i, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3, 5 - i, 1 + i, bb);
            }
        }

        return true;
    }
}
