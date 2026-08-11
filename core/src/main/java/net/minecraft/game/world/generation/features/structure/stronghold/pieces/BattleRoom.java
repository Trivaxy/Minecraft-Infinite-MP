package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.Stronghold;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class BattleRoom extends StrongholdPiece {
    private static final LegacyStructureTreasure[] treasureList = new LegacyStructureTreasure[]{new LegacyStructureTreasure(ItemList.key.id, 0, 1, 1, 100)};
    protected final DoorType doorType;

    public BattleRoom(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> list, Random rand) {
        if (piece != null) {
            ((EntryStairs)piece).bossRoom = this;
        }
    }

    public static BattleRoom findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x0, int y0, int z0, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x0, y0, z0, -4, -1, 0, 14, 11, 15, rot);
        if (!canGoDeeper(bb) || LegacyStructurePiece.findIntersecting(pcs, bb) != null) {
            bb = LegacyStructureBoundingBox.addToBB(x0, y0, z0, -4, -1, 0, 14, 6, 15, rot);
            if (!canGoDeeper(bb) || LegacyStructurePiece.findIntersecting(pcs, bb) != null) {
                return null;
            }
        }

        return new BattleRoom(id, rand, bb, rot);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 13, 5, 14, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 4, 1, 0);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 1, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 1, 2, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 2, 1, 1, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 2, 1, 2, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 1, 1, 3, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 3, 1, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 1, 12, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 1, 13, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 2, 1, 13, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 1, 1, 11, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 2, 1, 12, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 3, 1, 13, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 11, 1, 13, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 12, 1, 13, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 12, 1, 12, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 10, 1, 13, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 11, 1, 12, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 12, 1, 11, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 12, 1, 2, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 11, 1, 1, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 12, 1, 1, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 12, 1, 3, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 10, 1, 1, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 11, 1, 2, bb);
        this.placeSpawner(world, bb, rand, 1, 2, 1, rand.nextInt(4));
        this.placeSpawner(world, bb, rand, 12, 2, 1, rand.nextInt(4));
        this.placeSpawner(world, bb, rand, 1, 2, 13, rand.nextInt(4));
        this.placeSpawner(world, bb, rand, 12, 2, 13, rand.nextInt(4));
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 7, 1, 7, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 6, 1, 7, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 7, 1, 8, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 7, 1, 6, bb);
        this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 8, 1, 7, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 5, 1, 7, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 7, 1, 9, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 7, 1, 5, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 9, 1, 7, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 6, 1, 8, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 6, 1, 6, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 8, 1, 8, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, rand.nextInt(3), 8, 1, 6, bb);
        this.placeChest(world, bb, rand, 7, 2, 7, treasureList, 1);
        return true;
    }
}
