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

public class Prison extends StrongholdPiece {
    protected final DoorType doorType;

    public Prison(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece componente, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntryStairs)componente, pcs, rand, 1, 1);
    }

    public static Prison findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int dir, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 9, 5, 11, dir);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new Prison(id, rand, bb, dir) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatioInBB(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 8, 4, 10, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 1, 0);
        this.fill(world, bb, 1, 1, 10, 3, 3, 10, 0, 0, false);
        this.fill(world, bb, 4, 1, 1, 4, 3, 1, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 1, 3, 4, 3, 3, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 1, 7, 4, 3, 7, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 1, 9, 4, 3, 9, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 1, 4, 4, 3, 6, Stronghold.ironFenceId, Stronghold.ironFenceId, false);
        this.fill(world, bb, 5, 1, 5, 7, 3, 5, Stronghold.ironFenceId, Stronghold.ironFenceId, false);
        this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, 4, 3, 2, bb);
        this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, 4, 3, 8, bb);
        this.placeBlock(world, BlockList.doorWood.id, this.getMetadataWithOffset(BlockList.doorWood.id, 3), 4, 1, 2, bb);
        this.placeBlock(world, BlockList.doorWood.id, this.getMetadataWithOffset(BlockList.doorWood.id, 3) + 8, 4, 2, 2, bb);
        this.placeBlock(world, BlockList.doorWood.id, this.getMetadataWithOffset(BlockList.doorWood.id, 3), 4, 1, 8, bb);
        this.placeBlock(world, BlockList.doorWood.id, this.getMetadataWithOffset(BlockList.doorWood.id, 3) + 8, 4, 2, 8, bb);
        if (rand.nextBoolean()) {
            this.placeSpawner(world, bb, rand, 7, 1, 7, rand.nextInt(2));
        }

        if (rand.nextBoolean()) {
            this.placeSpawner(world, bb, rand, 7, 1, 3, rand.nextInt(2));
        }

        return true;
    }
}
