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

public class Crossing extends StrongholdPiece {
    protected final DoorType doorType;
    private boolean continueForward;
    private boolean turnLeft;
    private boolean turnRight;
    private boolean goBack;

    public Crossing(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
        this.continueForward = rand.nextBoolean();
        this.turnLeft = rand.nextBoolean();
        this.turnRight = rand.nextBoolean();
        this.goBack = rand.nextInt(3) > 0;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        int x = 3;
        int z = 5;
        if (this.rotation == 1 || this.rotation == 2) {
            x = 8 - x;
            z = 8 - z;
        }

        this.getNextComponent((EntryStairs)piece, pcs, rand, 5, 1);
        if (this.continueForward) {
            this.getNextComponentX((EntryStairs)piece, pcs, rand, x, 1);
        }

        if (this.turnLeft) {
            this.getNextComponentX((EntryStairs)piece, pcs, rand, z, 7);
        }

        if (this.turnRight) {
            this.getNextComponentZ((EntryStairs)piece, pcs, rand, x, 1);
        }

        if (this.goBack) {
            this.getNextComponentZ((EntryStairs)piece, pcs, rand, z, 7);
        }
    }

    public static Crossing findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int typ) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -4, -3, 0, 10, 9, 11, rot);
        Crossing comp = new Crossing(typ, rand, bb, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? comp : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 9, 8, 10, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 4, 3, 0);
        if (this.continueForward) {
            this.fill(world, bb, 0, 3, 1, 0, 5, 3, 0, 0, false);
        }

        if (this.turnRight) {
            this.fill(world, bb, 9, 3, 1, 9, 5, 3, 0, 0, false);
        }

        if (this.turnLeft) {
            this.fill(world, bb, 0, 5, 7, 0, 7, 9, 0, 0, false);
        }

        if (this.goBack) {
            this.fill(world, bb, 9, 5, 7, 9, 7, 9, 0, 0, false);
        }

        this.fill(world, bb, 5, 1, 10, 7, 3, 10, 0, 0, false);
        this.fill(world, bb, 1, 2, 1, 8, 2, 6, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 1, 5, 4, 4, 9, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 8, 1, 5, 8, 4, 9, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 1, 4, 7, 3, 4, 9, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 1, 3, 5, 3, 3, 6, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 1, 3, 4, 3, 3, 4, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 1, 4, 6, 3, 4, 6, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 5, 1, 7, 7, 1, 8, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 5, 1, 9, 7, 1, 9, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 5, 2, 7, 7, 2, 7, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 4, 5, 7, 4, 5, 9, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 8, 5, 7, 8, 5, 9, BlockList.slabStone.id, BlockList.slabStone.id, false);
        this.fill(world, bb, 5, 5, 7, 7, 5, 9, BlockList.doubleSlab.id, BlockList.doubleSlab.id, false);
        this.placeBlock(world, BlockList.torch.id, 0, 6, 5, 6, bb);
        return true;
    }
}
