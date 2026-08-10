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

public class Stairs extends StrongholdPiece {
    private final boolean mainStairs;
    protected DoorType doorType;

    public Stairs(World world, int id, Random rand, int x, int z) {
        super(id);
        this.mainStairs = true;
        this.rotation = rand.nextInt(4);
        this.doorType = DoorType.OPENING;
        switch (this.rotation) {
            case 0:
            case 2:
                this.boundingBox = new LegacyStructureBoundingBox(x, world.getSeaLevel(), z, x + 5 - 1, world.getSeaLevel() + 10, z + 5 - 1);
                break;
            default:
                this.boundingBox = new LegacyStructureBoundingBox(x, world.getSeaLevel(), z, x + 5 - 1, world.getSeaLevel() + 10, z + 5 - 1);
        }
    }

    public Stairs(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.mainStairs = false;
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        if (this.mainStairs) {
            StrongholdPieces.setComponentType(Crossing.class);
        }

        this.getNextComponent((EntryStairs)piece, pcs, rand, 1, 1);
    }

    public static Stairs getStrongholdStairsComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -7, 0, 5, 11, 5, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new Stairs(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 4, 10, 4, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 7, 0);
        this.placeDoor(world, rand, bb, DoorType.OPENING, 1, 1, 4);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 2, 6, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 5, 1, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 1, 6, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 5, 2, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 4, 3, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 1, 5, 3, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 2, 4, 3, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3, 3, 3, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 3, 4, 3, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3, 3, 2, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3, 2, 1, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 3, 3, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 2, 2, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 1, 1, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 1, 2, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 1, 1, 2, bb);
        this.placeBlock(world, BlockList.slabStone.id, 0, 1, 1, 3, bb);
        return true;
    }
}
