package net.minecraft.game.world.generation.features.structure.stronghold;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.EntryStairs;

public abstract class StrongholdPiece extends LegacyStructurePiece {
    protected StrongholdPiece(int id) {
        super(id);
    }

    protected void placeDoor(World world, Random rand, LegacyStructureBoundingBox bb, DoorType doorType, int x, int y, int z) {
        if (this.getYWithOffset(y) >= world.getSeaLevel()) {
            this.fill(world, bb, x, y, z, x + 3 - 1, y + 3 - 1, z, 0, 0, false);
        } else {
            switch (DoorFacing.doorEnum[doorType.ordinal()]) {
                case 2:
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 1, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y, z, bb);
                    this.placeBlock(world, BlockList.doorSteel.id, 0, x + 1, y, z, bb);
                    this.placeBlock(world, BlockList.doorSteel.id, 8, x + 1, y + 1, z, bb);
                    break;
                case 3:
                    this.placeBlock(world, 0, 0, x + 1, y, z, bb);
                    this.placeBlock(world, 0, 0, x + 1, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x, y, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x + 1, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x + 2, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x + 2, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.ironFenceId, Stronghold.ironFenceMeta, x + 2, y, z, bb);
                    break;
                case 4:
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 1, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y + 2, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y + 1, z, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, x + 2, y, z, bb);
                    this.placeBlock(world, BlockList.doorWood.id, 0, x + 1, y, z, bb);
                    this.placeBlock(world, BlockList.doorWood.id, 8, x + 1, y + 1, z, bb);
                    break;
                default:
                    this.fill(world, bb, x, y, z, x + 3 - 1, y + 3 - 1, z, 0, 0, false);
            }
        }
    }

    protected DoorType getRandomDoor(Random rand) {
        switch (rand.nextInt(5)) {
            case 2:
                return DoorType.WOOD_DOOR;
            case 3:
                return DoorType.GRATES;
            case 4:
                return DoorType.IRON_DOOR;
            default:
                return DoorType.OPENING;
        }
    }

    protected LegacyStructurePiece getNextComponent(EntryStairs stairs, List<LegacyStructurePiece> pcs, Random rand, int x, int z) {
        switch (this.rotation) {
            case 0:
                return StrongholdPieces.getNextValidComponentAccess(
                    stairs, pcs, rand, this.boundingBox.minX + x, this.boundingBox.minY + z, this.boundingBox.maxZ + 1, this.rotation, this.getComponentType()
                );
            case 1:
                return StrongholdPieces.getNextValidComponentAccess(
                    stairs, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + z, this.boundingBox.minZ + x, this.rotation, this.getComponentType()
                );
            case 2:
                return StrongholdPieces.getNextValidComponentAccess(
                    stairs, pcs, rand, this.boundingBox.minX + x, this.boundingBox.minY + z, this.boundingBox.minZ - 1, this.rotation, this.getComponentType()
                );
            case 3:
                return StrongholdPieces.getNextValidComponentAccess(
                    stairs, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + z, this.boundingBox.minZ + x, this.rotation, this.getComponentType()
                );
            default:
                return null;
        }
    }

    protected LegacyStructurePiece getNextComponentX(EntryStairs start, List<LegacyStructurePiece> pcs, Random rand, int x, int z) {
        switch (this.rotation) {
            case 0:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 1, this.getComponentType()
                );
            case 1:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.minZ - 1, 2, this.getComponentType()
                );
            case 2:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 1, this.getComponentType()
                );
            case 3:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.minZ - 1, 2, this.getComponentType()
                );
            default:
                return null;
        }
    }

    protected LegacyStructurePiece getNextComponentZ(EntryStairs start, List<LegacyStructurePiece> pcs, Random rand, int x, int z) {
        switch (this.rotation) {
            case 0:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 3, this.getComponentType()
                );
            case 1:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                );
            case 2:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 3, this.getComponentType()
                );
            case 3:
                return StrongholdPieces.getNextValidComponentAccess(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                );
            default:
                return null;
        }
    }

    protected static boolean canGoDeeper(LegacyStructureBoundingBox bb) {
        return bb != null && bb.minY > 10;
    }
}
