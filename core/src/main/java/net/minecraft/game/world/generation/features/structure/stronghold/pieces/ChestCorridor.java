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

public class ChestCorridor extends StrongholdPiece {
    private static final LegacyStructureTreasure[] chestLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(BlockList.obsidian.id, 0, 1, 1, 10),
        new LegacyStructureTreasure(ItemList.diamond.id, 0, 1, 3, 1),
        new LegacyStructureTreasure(ItemList.ingotCopper.id, 0, 1, 5, 10),
        new LegacyStructureTreasure(ItemList.ingotIron.id, 0, 1, 3, 5),
        new LegacyStructureTreasure(ItemList.ruby.id, 0, 1, 4, 5),
        new LegacyStructureTreasure(ItemList.sapphire.id, 0, 1, 4, 5),
        new LegacyStructureTreasure(ItemList.emerald.id, 0, 1, 4, 5),
        new LegacyStructureTreasure(ItemList.bread.id, 0, 1, 3, 15),
        new LegacyStructureTreasure(ItemList.appleRed.id, 0, 1, 3, 15),
        new LegacyStructureTreasure(ItemList.pickaxeSteel.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.swordSteel.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.plateChain.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.helmetChain.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.legsChain.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.bootsChain.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.appleGold.id, 0, 1, 1, 1)
    };
    private final DoorType doorType;
    private boolean hasMadeChest;

    public ChestCorridor(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntryStairs)piece, pcs, rand, 1, 1);
    }

    public static ChestCorridor findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, 7, rot);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null && y <= 128 ? new ChestCorridor(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 4, 4, 6, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 1, 1, 0);
        this.placeDoor(world, rand, bb, DoorType.OPENING, 1, 1, 6);
        this.fill(world, bb, 3, 1, 2, 3, 1, 4, Stronghold.bricksId, Stronghold.bricksId, false);
        this.placeBlock(world, BlockList.slabStoneBrick.id, 0, 3, 1, 1, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, 0, 3, 1, 5, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, 0, 3, 2, 2, bb);
        this.placeBlock(world, BlockList.slabStoneBrick.id, 0, 3, 2, 4, bb);

        for (int i = 2; i <= 4; i++) {
            this.placeBlock(world, BlockList.slabStoneBrick.id, 0, 2, 1, i, bb);
        }

        if (!this.hasMadeChest) {
            int y = this.getYWithOffset(2);
            int x = this.getXWithOffset(3, 3);
            int z = this.getZWithOffset(3, 3);
            if (bb.isVecInside(x, y, z)) {
                this.hasMadeChest = true;
                this.placeChest(world, bb, rand, 3, 2, 3, chestLoot, 2 + rand.nextInt(2));
            }
        }

        return true;
    }
}
